*splash-scala* is a set of wrappers and convenience functions for the spectral hash code generator written in Scala. 

### Basic usage

```scala
// Directly using the Java libraries
import jp.riken.mirt.splash._
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectraType

val factory = SplashFactory.create
val spectrum = v1.SpectrumImpl(Seq(Ion(100.0, 50)), SpectraType.MS)
val splash = factory.splashIt(spectrum)

// With implicit classes
import jp.riken.mirt.splash.utils._
import scala.collection.JavaConversions._

val splash = Seq(Ion(100.0, 50)).splashIt // Uses SpectraType.MS by default
```

