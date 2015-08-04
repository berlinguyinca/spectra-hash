package jp.riken.mirt.splash.v1

import edu.ucdavis.fiehnlab.spectra.hash.core
import jp.riken.mirt.splash.{ Ion, Spectrum }
import jp.riken.mirt.splash.JavaConversions._
import scala.collection.JavaConversions._

/**
 * Immutable wrapper for [[edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectrumImpl]]
 */
trait SpectrumImpl extends core.types.SpectrumImpl with Spectrum 

/**
 * Factory object for the Java implementation of [[edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectrumImpl]]
 */
object SpectrumImpl {
  def apply(ions: Seq[Ion], stype: core.types.SpectraType, origin: Option[String] = None): SpectrumImpl = {
    val _origin = origin.getOrElse("unknown")
    new core.types.SpectrumImpl(ions, _origin, stype) with SpectrumImpl
  }
}

