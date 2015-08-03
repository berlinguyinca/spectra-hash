package jp.riken.mirt.splash

import edu.ucdavis.fiehnlab.spectra.hash.core
import scala.collection.JavaConversions._
import scala.language.implicitConversions

object JavaConversions {
  implicit def ionJavaToScalaConverter(ion: core.types.Ion): Ion = ion

  implicit def ionSeqToSpectrumImpl(ions: Seq[Ion]): Spectrum = v1.SpectrumImpl(ions, core.types.SpectraType.MS)
}

