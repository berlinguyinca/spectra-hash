package edu.ucdavis.fiehnlab.spectra.hash.utilities.evaluation

import java.io.{Writer, FileWriter, File}
import java.util
import akka.actor.Actor.Receive
import edu.ucdavis.fiehnlab.index._

import akka.actor._
import edu.ucdavis.fiehnlab.index.histogram.HistogramIndex
import edu.ucdavis.fiehnlab.math.histogram.Histogram
import edu.ucdavis.fiehnlab.{ComputationalResult, Spectrum}
import edu.ucdavis.fiehnlab.io.{SpectraReadEventHandler, FileParser}
import edu.ucdavis.fiehnlab.math.similarity.CompositeSimilarity
import edu.ucdavis.fiehnlab.splash.resolver.SpectraRetrievedResult
import edu.ucdavis.fiehnlab.util.Utilities
import org.apache.log4j.Logger
import org.springframework.boot.{ApplicationArguments, ApplicationRunner, SpringApplication}
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.{ComponentScan, Configuration}
import org.springframework.stereotype.Component
import collection.JavaConversions._
import scala.io.Source

/**
  * Created by wohlgemuth on 2/19/16.
  *
  * is utilized to evaluate the different properties for the 2nd block and find the best possible representation
  */
object Evaluate2ndBlock {

  def main(args: Array[String]): Unit = {

    val app = new SpringApplication(classOf[EvaluationConfig])
    app.setWebEnvironment(false)
    app.run(args: _*)
  }

}

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = Array("edu.ucdavis.fiehnlab.spectra.hash.utilities.evaluation"))
class EvaluationConfig

@Component
class EvaluationStartup extends ApplicationRunner {

  implicit val system = ActorSystem()

  val logger: Logger = Logger.getLogger(getClass)

  override def run(applicationArguments: ApplicationArguments): Unit = {

    if (!applicationArguments.containsOption("library")) {
      usage()
    }

    if (!applicationArguments.containsOption("inchiKeys") && !applicationArguments.containsOption("spectra")) {
      usage()
    }

    if (!applicationArguments.containsOption("output")) {
      usage()
    }


    val builder = new IndexBuilder
    val indexes: List[Index] = builder.buildBestIndexes

    val linearIndex = builder.buildLinear

    val parser = new FileParser()


    //defined handler to index all our found spectra
    val handler = new SpectraReadEventHandler {
      override def readSpectra(spectrum: Spectrum): Unit = {
        indexes.par.foreach(_.index(spectrum))
        linearIndex.index(spectrum)

      }
    }

    //generate our indexes, from all the defined files
    applicationArguments.getOptionValues("library").par.foreach(x => parser.parseFile(new File(x), handler))


    indexes.par.foreach(x => logger.info(x.toString + " contains  " + x.size + " spectra"))

    logger.info(indexes.size + " different index methods will be compared")

    val resolver = new FetchSpectraForInChIKey(system)

    val writer = new FileWriter(new File(applicationArguments.getOptionValues("output").head))
    val finishActor = system.actorOf(Props(new FinishActor(writer)))
    val missingActor = system.actorOf(Props(new MissingActor(new FileWriter("missingSpectra.txt"))))
    val similarActor = system.actorOf(Props(new SimilarActor(new FileWriter("similarSpectra.txt"))))


    applicationArguments.getOptionValues("inchiKeys").foreach 0
      x => {
        for (line <- Source.fromFile(new File(x)).getLines()) {
          val data = line.split("\t")
          logger.info("read inchi key: " + data(1))


          val begin = System.currentTimeMillis()
          try {
            val spectraRetrievedResult: Set[SpectraRetrievedResult] = resolver.resolve(data(1)).toSet


            logger.info("found " + spectraRetrievedResult.size + " spectra for this compound")
            spectraRetrievedResult.foreach {

              y =>
                searchIndex(indexes, y, finishActor, missingActor, similarActor, linearIndex)
            }
            logger.info("finished index search")
          }
          catch {
            case e: Exception => logger.error("retrieval error for key: " + data(1) + " - " + e.getMessage)
          }

          val duration = (System.currentTimeMillis() - begin) / 1000

          logger.info("duration: " + duration + "s")
        }
      }

    }

    finishActor ! PoisonPill
    missingActor ! PoisonPill

    //our work is done
    system.shutdown()
  }

  def searchIndex(indexes: List[Index], spectra: SpectraRetrievedResult, finish: ActorRef, missing: ActorRef, similar: ActorRef, referenceIndex: Index): Unit = {

    val referenceResult = referenceIndex.search(Utilities.convertStringToSpectrum(spectra.spectrum, spectra.splash, spectra.inchiKey), new CompositeSimilarity, 0.7)
    similar ! SimilarValueData(referenceIndex, referenceResult.toSet, spectra)

    indexes.foreach { idx =>
      val result = searchIndex(spectra, finish, idx)
      similar ! SimilarValueData(idx, result.toSet, spectra)

      val missingSpectra: Set[ComputationalResult] = referenceResult.toSet diff result.toSet

      if (missingSpectra.nonEmpty) {
        logger.info("missing spectra in this index: " + missingSpectra.size)
        missing ! MissingValueData(idx, missingSpectra, spectra)
      }
    }
  }

  def searchIndex(spectra: SpectraRetrievedResult, finish: ActorRef, idx: Index): util.Collection[ComputationalResult] = {
    val spectrum = Utilities.convertStringToSpectrum(spectra.spectrum, spectra.splash, spectra.inchiKey)

    logger.debug("evaluating index " + idx)
    val before: Long = System.currentTimeMillis()
    val result = idx.search(spectrum, new CompositeSimilarity, 0.7)

    val duration: Long = System.currentTimeMillis() - before
    finish !FinishResult(idx, result.size(), spectra, duration, idx.get(spectrum).size)

    result
  }

  def usage(): Unit = {
    println("Usage:")
    println("--library=FILE the libraries to compare our tests again, can be supplied several times or as directory of libraries")
    println("--inchiKeys=FILE the file containing all our inchi keys to evaluate")
    println("--output=FILE where to store our results in form of a csv file")

    System.exit(-1)
  }

  class FinishActor(val writer: Writer) extends Actor {
    override def receive: Receive = {

      case x: FinishResult =>
        writer.write(x.spectra.inchiKey)
        writer.write("\t")
        writer.write(x.spectra.splash)
        writer.write("\t")
        writer.write(x.index.toString)
        writer.write("\t")

        writer.write(x.count.toString)
        writer.write("\t")
        writer.write(x.duration.toString)
        writer.write("\t")
        writer.write(x.indexSize.toString)

        writer.write("\n")
        writer.flush()
    }
  }

  class MissingActor(val writer: Writer) extends Actor {
    override def receive: Receive = {

      case x: MissingValueData =>
        x.set.foreach { missing =>

          writer.write(x.spectra.inchiKey)
          writer.write("\t")
          writer.write(x.spectra.splash)
          writer.write("\t")
          writer.write(x.index.toString)
          writer.write("\t")
          writer.write(missing.hit.splash)
          writer.write("\t")
          writer.write(missing.unknown.splash)
          writer.write("\t")
          writer.write(missing.score.toString)

          //special check for histogram based indexes
          x.index match {
            case idx:HistogramIndex =>
              val histogram = idx.histogram

              writer.write("\t")
              writer.write(histogram.generate(missing.hit))
              writer.write("\t")
              writer.write(histogram.generate(missing.unknown))
              writer.write("\t")
              writer.write(histogram.toString)

            case _ =>
              writer.write("\t")
              writer.write("\t")
              writer.write("\t")
          }
          writer.write("\n")
          writer.flush()
        }
    }
  }

  class SimilarActor(val writer: Writer) extends Actor {
    override def receive: Receive = {

      case x: SimilarValueData =>
        x.set.foreach { missing =>

          writer.write(x.spectra.inchiKey)
          writer.write("\t")
          writer.write(x.spectra.splash)
          writer.write("\t")
          writer.write(x.index.toString)
          writer.write("\t")
          writer.write(missing.hit.splash)
          writer.write("\t")
          writer.write(missing.unknown.splash)
          writer.write("\t")
          writer.write(missing.score.toString)

          //special check for histogram based indexes
          x.index match {
            case idx:HistogramIndex =>
              val histogram = idx.histogram

              writer.write("\t")
              writer.write(histogram.generate(missing.hit))
              writer.write("\t")
              writer.write(histogram.generate(missing.unknown))
              writer.write("\t")
              writer.write(histogram.toString)

            case _ =>
              writer.write("\t")
              writer.write("\t")
              writer.write("\t")
          }
          writer.write("\n")
          writer.flush()
        }
    }
  }

  case class FinishResult(index:Index, count:Int, spectra:SpectraRetrievedResult,duration:Long, indexSize:Int)
  case class MissingValueData(index:Index, set:Set[ComputationalResult], spectra:SpectraRetrievedResult)
  case class SimilarValueData(index:Index, set:Set[ComputationalResult], spectra:SpectraRetrievedResult)
}