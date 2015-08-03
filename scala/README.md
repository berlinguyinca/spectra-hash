*splash-scala* is a set of wrappers and convenience functions for the spectral hash code generator written in Scala. 

### Basic usage

```scala
// Directly using the Java libraries
import jp.riken.mirt.splash._
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectraType

val factory = SplashFactory.create
val spectrum = v1.SpectrumImpl(Seq(Ion(100.0, 50)), SpectraType.MS)
val splash = factory.splashIt(spectrum)

// With Scala helpers (implicit conversions)
import jp.riken.mirt.splash._
import jp.riken.mirt.splash.JavaConversions._
import scala.language.implicitConversions

val spectrum: Spectrum  = Seq(Ion(100.0, 50))
val splash: String = spectrum.splashIt
```

### Tests

Run the following goals to test using a basic test suite: `mvn clean scala:compile scala:testCompile test`

*Validation testing to be implemented.*
