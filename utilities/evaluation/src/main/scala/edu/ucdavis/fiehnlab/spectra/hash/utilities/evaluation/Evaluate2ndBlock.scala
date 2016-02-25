package edu.ucdavis.fiehnlab.spectra.hash.utilities.evaluation

import java.io.{Writer, FileWriter, File}
import akka.actor.Actor.Receive
import edu.ucdavis.fiehnlab.index._

import akka.actor._
import edu.ucdavis.fiehnlab.Spectrum
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

    if (!applicationArguments.containsOption("inchiKeys")) {
      usage()
    }

    if (!applicationArguments.containsOption("output")) {
      usage()
    }


    val indexes: List[Index] = new IndexBuilder().build()

    val parser = new FileParser()


    //defined handler to index all our found spectra
    val handler = new SpectraReadEventHandler {
      override def readSpectra(spectrum: Spectrum): Unit = {
        indexes.par.foreach(_.index(spectrum))
      }
    }

    //generate our indexes, from all the defined files
    applicationArguments.getOptionValues("library").par.foreach(x => parser.parseFile(new File(x), handler))


    indexes.par.foreach(x => logger.info(x.toString + " contains  " + x.size + " spectra"))

    logger.info(indexes.size + " different index methods will be compared")

    val resolver = new FetchSpectraForInChIKey(system)

    val writer = new FileWriter(new File(applicationArguments.getOptionValues("output").head))
    val finishActor = system.actorOf(Props(new FinishActor(writer)))

    applicationArguments.getOptionValues("inchiKeys").foreach {
      x => {
        for (line <- Source.fromFile(new File(x)).getLines()) {
          val data = line.split("\t")
          logger.info("read inchi key: " + data(1))


          val begin = System.currentTimeMillis()
          try {
            val spectraRetrievedResult: Set[SpectraRetrievedResult] = resolver.resolve(data(1)).toSet

            logger.info("found " + spectraRetrievedResult.size + " spectra for this compound")
            spectraRetrievedResult.foreach {
              y => searchIndex(indexes, y,finishActor)
            }
            logger.info("finished index search")
          }
          catch {
            case e: Exception => logger.error("retrieval error for key: " + data(1) + " - " + e.getMessage)
          }

          val duration = (System.currentTimeMillis() - begin)/1000

          logger.info("duration: " + duration + "s")
        }
      }

    }

    finishActor ! PoisonPill
    //our work is done
    system.shutdown()
  }

  def searchIndex(indexes: List[Index], spectra: SpectraRetrievedResult,finish:ActorRef): Unit = {

    val spectrum = Utilities.convertStringToSpectrum(spectra.spectrum, spectra.splash, spectra.inchiKey)

    indexes.foreach { idx =>

      logger.debug("evaluating index " + idx)
      val before: Long = System.currentTimeMillis()
      val hits: Int = idx.search(spectrum, new CompositeSimilarity, 0.7).size()

      val duration:Long = System.currentTimeMillis() - before
      finish ! (idx,hits,spectra,duration, idx.size)
    }
  }

  def usage(): Unit = {

    println("Usage:")
    println("--library=FILE the libraries to compare our tests again, can be supplied several times or as directory of libraries")
    println("--inchiKeys=FILE the file containing all our inchi keys to evaluate")
    println("--output=FILE where to store our results in form of a csv file")


    System.exit(-1)

  }

  class FinishActor(val writer:Writer) extends Actor{
    override def receive: Receive = {

      case x:(Index,Int,SpectraRetrievedResult,Long,Int) =>
        writer.write(x._3.inchiKey)
        writer.write("\t")
        writer.write(x._3.splash)
        writer.write("\t")
        writer.write(x._1.toString)
        writer.write("\t")

        writer.write(x._2.toString)
        writer.write("\t")
        writer.write(x._4.toString)
        writer.write("\t")
        writer.write(x._5.toString)

        writer.write("\n")
        writer.flush()
    }
  }
}