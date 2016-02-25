package edu.ucdavis.fiehnlab.spectra.hash.utilities.evaluation

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.util.Timeout
import edu.ucdavis.fiehnlab.splash.resolver.{MonaJSONFormat, Spectrum, SpectraRetrievedResult}
import spray.client.pipelining._
import spray.http.HttpRequest
import scala.concurrent.duration._
import spray.httpx.SprayJsonSupport._

import scala.concurrent.{Await, Future}
import MonaJSONFormat._

/**
  * Created by wohlgemuth on 2/19/16.
  */
class FetchSpectraForInChIKey(system: ActorSystem) {


  implicit val timeout = Timeout(6000, TimeUnit.MINUTES)

  import system.dispatcher

  val pipeline: HttpRequest => Future[List[SpectraRetrievedResult]] = sendReceive(system, system.dispatcher, timeout) ~> unmarshal[List[SpectraRetrievedResult]]

  val host = "http://resolver-mona.apps.fiehnlab.ucdavis.edu/rest/spectra/";
//  val host = "http://127.0.0.1:8080/rest/spectra/";

  /**
    * resolves all the spectra for a given inchi key
    *
    * @param key
    * @return
    */
  def resolve(key: String): List[SpectraRetrievedResult] = {

    val response = pipeline(Get(s"${host}${key}"))

    Await.result(response,900 seconds)

  }
}
