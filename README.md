# The splash

a spectral hash code

this library is the current reference implementation for the splash. Splash stands for the spectra hash code and is an unique identifier independent of acquisition or processing. It basically tries to ensure that you can easily tell if two spectra are identical, similar or very different. Based on several criteria.

You can access it as a REST service, at http://splash.fiehnlab.ucdavis.edu

Or use one any of the available implementations, which should have been validated against the REST validation service.

# usage

## java api:
The reference implementation jar file can be downloaded from: http://gose.fiehnlab.ucdavis.edu:55000/content/groups/public/edu/ucdavis/fiehnlab/splash/core/1.6/core-1.6.jar

The Maven dependency information is:
```
    <dependency>
        <groupId>edu.ucdavis.fiehnlab.splash</groupId>
        <artifactId>core</artifactId>
        <version>1.4</version>
    </dependency>
```

The Maven repository information is:
```
	<repository>
	    <id>releases</id>
	    <name>Fiehnlab Public Maven Repository</name>
	    <url>http://gose.fiehnlab.ucdavis.edu:55000/content/groups/public</url>
	</repository>
```

To generate a new splash, please utilize the following:


```
    Splash splash = SplashFactory.create();
    Spectrum spectrum = new SpectrumImpl(Arrays.asList(new Ion(100.0, 50)), SpectraType.MS);
    String splash = splash.splashIt(spectrum);
```

Alternatively, you can also utilize the following code, to directly splash a spectra, if it's accessible as a string representation.

```
    String splash = SplashUtils.splash("10:123.12 12:123.11 13:22 14:212",SpectraType.MS);
```

We are also providing an easy way to connect a listener to the splashing algorithm, so that you can inspect the different blocks, before they are hashed. This can be done with directly adding a SplashListener to your Splash instance or alternativly using the util like this

```
    String splash = SplashUtils.splash("10:123.12 12:123.11 13:22 14:212", SpectraType.MS, new SplashListener(){
            @Override
            public void eventReceived(SplashingEvent e) {
            }

            @Override
            public void complete(Spectrum spectrum, String splash) {
                
            }
        });
```

## C# api:

To generate a splash you need to add a reference to the assembly `Splash.dll` to your project then add the following 'using' statement:
```
using NSSplash;
```

To get the hash for a given spectrum you can call:
```
	Splash splasher = new Splash();
	string hash = splasher.splashIt(new Spectrum("5.0000001:1.0 5.0000005:0.5 10.02395773287:2.0 11.234568:.10", SpectrumType.MS));
```

## JavaScript api:

Usage:
    Include splash.js script in index.html

```
    generateSplash(spectra) - generates Splash Key
    validateSplash(spectra) - validates Splash Key
```

    Spectra must be in valid JSON object. Refer to main splash site for examples
        http://splash.fiehnlab.ucdavis.edu/

```
    Testing
        Install Jasmine and Karma then run "karma start" from command line
```

## R api:

To generate a splash you need to source the R functions, and call `getSplash()`
on a dataframe or matrix with two numeric columns containing m/z and intensity:

```
    ## The caffeine example from the paper
    library(splashR)

    ## The caffeine example from the paper
    caffeine <- cbind(mz=c(138.0641, 195.0815),
                      intensity=c(71.59, 261.7))
    getSplash(caffeine)
```

Installing the R package can be done with the devtools package directly from within
R:

```
    > library(devtools)
    > install_github("berlinguyinca/spectra-hash", subdir="splashR")           
```

## Python api:

TODO

## C++ api:

TODO

## Scala api:

```scala
import jp.riken.mirt.splash._
import jp.riken.mirt.splash.JavaConversions._

val spectrum: Spectrum  = Seq(Ion(100.0, 50))
val splash: String = spectrum.splashIt
```

See [subproject documentation](scala).

## rest service:

the documentation for the REST service, is available as a dedicated index page, once you start the REST server. If you like to use the official webservice, you can find it at http://splash.fiehnlab.ucdavis.edu


## validation tool:

As part of the splash specificitation, we are providing a simple validation tool, in the validation folder.

The latest jar can be found at: http://gose.fiehnlab.ucdavis.edu:55000/content/groups/public/edu/ucdavis/fiehnlab/splash/validation/1.4/validation-1.4.jar

to run this tool (from the sources) please clone and build the project and afterwards run

```
java -jar validation-1.4.jar
```

this will present you with the usage for this tool. 

### validation example

An example to validate a file against the reference implementation and saving the output to a file would be
(run from the root of the project)

```
java -jar validation/target/validation-1.4.jar -c -s 2 -t ms ./base-dataset/spectra/notsplashed/test-set-v1.csv base-dataset/spectra/test-set-with-splash-v1.csv
```

The specified flags in the example mean:

* k = which column is your generated splash
* o = which column is your optional origin
* s = which column is your spectra
* t = what is your spectra type
* T = what is your seperator, ',' in this case
* X = debug messages

The input and output files are specified as arguments.

* input.csv your input file
* output.csv your output file

The format for a spectrum must be:

```
ion:intensity ion:intensity
```

you can also use the same tool, to easily splash a file of spectra, using the reference algorithm. To only report duplicates, sort the output, etc.

# building

## Java/Scala

```
mvn clean install
```

will build your project, run all the tests and you can find the build jar files, in the target directories of the project.

## C# 

### Requirements:
    - Mono MDK (Download from: http://www.mono-project.com/download/)
    - (Optional) MonoDevelop IDE (Download from: http://www.monodevelop.com/download/)
### Building:
The easiest way to build the project is using MonoDevelop, open the IDE and load the solution (<download folder>/csharp/splash.sln).
On the 'Solution Explorer' (left panel), right click 'splash' and select 'Build splash', if there are no errors you will see a 'Build successful' message.

## Python

TODO

## C++

TODO

# Contributing

if you like to contribute to this project, please feel free to contact me.
