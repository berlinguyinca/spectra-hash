package jp.riken.mirt.splash

import org.scalatest._

class LibrarySuite extends FlatSpec with Matchers {
  "The splash library wrapper" should "emulate the Java usage" in {
    import jp.riken.mirt.splash._
    import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectraType
    
    val factory = SplashFactory.create
    val spectrum = v1.SpectrumImpl(Seq(Ion(100.0, 50)), SpectraType.MS)
    val splash = factory.splashIt(spectrum)

    splash should equal("splash10-0udi-0900000000-cc0ed451a7eca3bcb4a6")
  }

  it should "be able to work using Scala-specific usage" in {
    import jp.riken.mirt.splash._
    import jp.riken.mirt.splash.JavaConversions._

    val spectrum: Spectrum  = Seq(Ion(100.0, 50))
    val splash: String = spectrum.splashIt

    splash should equal("splash10-0udi-0900000000-cc0ed451a7eca3bcb4a6")
  }
}
