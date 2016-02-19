package edu.ucdavis.fiehnlab.spectra.hash.utilities.evaluation

import java.io.File
import edu.ucdavis.fiehnlab.index._

import akka.actor.ActorSystem
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


    val indexes:List[Index] = new IndexBuilder().build()

    val parser = new FileParser()

    //defined handler to index all our found spectra
    val handler = new SpectraReadEventHandler {
      override def readSpectra(spectrum: Spectrum): Unit = {
        indexes.par.foreach(_.index(spectrum))
      }
    }

    //generate our indexes, from all the defined files
    applicationArguments.getOptionValues("library").foreach(x => parser.parseFile(new File(x), handler))


    indexes.foreach(x => logger.debug(x.toString + " contains  " + x.size + " spectra"))

    val resolver = new FetchSpectraForInChIKey(system)

    applicationArguments.getOptionValues("inchiKeys").foreach {
      x => {
        for (line <- Source.fromFile(new File(x)).getLines()) {
          logger.info("read inchi key: " + line)

          val spectraRetrievedResult:List[SpectraRetrievedResult] = resolver.resolve(line)

          logger.info("found " + spectraRetrievedResult.size + " possible matches")
          spectraRetrievedResult.foreach{
            y => searchIndex(indexes,y)
          }
        }
      }

    }

    //our work is done
    system.shutdown()
  }

  def searchIndex(indexes:List[Index], spectra:SpectraRetrievedResult) : Unit = {

    val spectrum = Utilities.convertStringToSpectrum(spectra.spectrum,spectra.splash,spectra.inchiKey)

    indexes.foreach { idx =>

      val hits:Int = idx.search(spectrum,new CompositeSimilarity,0.7).size()

      logger.info( idx.getClass + " hits: " + hits)
    }
  }
  def usage(): Unit = {

    println("Usage:")
    println("--library=FILE the libraries to compare our tests again, can be supplied several times or as directory of libraries")
    println("--inchiKeys=FILE the file containing all our inchi keys to evaluate")
    println("--output=FILE where to store our results in form of a csv file")


    System.exit(-1)

  }
}