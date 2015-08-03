package jp.riken.mirt.splash

import edu.ucdavis.fiehnlab.spectra.hash.core
import jp.riken.mirt.splash.JavaConversions._
import scala.collection.JavaConversions._

trait Spectrum extends core.Spectrum {
  lazy val splashIt: String = { 
    val ions: Seq[Ion] = getIons().map(ion => Ion(ion.getMass, ion.getIntensity)) 
    SplashFactory.create().splashIt(ions)
  }
}

