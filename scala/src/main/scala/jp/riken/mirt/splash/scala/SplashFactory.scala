package jp.riken.mirt.splash

import edu.ucdavis.fiehnlab.spectra.hash.core

trait SplashFactory {
  def create() = core.SplashFactory.create()
}

object SplashFactory extends SplashFactory

