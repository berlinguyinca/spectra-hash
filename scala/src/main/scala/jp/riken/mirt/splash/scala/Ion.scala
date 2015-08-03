package jp.riken.mirt.splash

import edu.ucdavis.fiehnlab.spectra.hash.core

/**
 * An immutable wrapper around the Java implementation of Ion
 */
trait Ion extends core.types.Ion {
  def setMass(mass: Double) = Ion(mass, super.getIntensity)
  def setIntensity(intensity: Double) = Ion(super.getMass, intensity)
}

object Ion {
  def apply(mass: Double, intensity: Double) = new core.types.Ion(mass, intensity) with Ion
}

