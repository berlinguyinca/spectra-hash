# SPLASH

## The Spectral Hash

The SPLASH (SPectraL hASH) is an unambiguous, database-independent spectral identifier, just as the [InChIKey](http://www.inchi-trust.org/technical-faq/#2.7) is designed to serve as a unique identifier for chemical structures.  It contains separate blocks that define different layers of information, separated by dashes. For example, the full SPLASH of a [caffeine mass spectrum](http://massbank.eu/MassBank/jsp/FwdRecord.jsp?id=PR100026) above is `splash10-0002-0900000000-b112e4e059e1ecf98c5f`. The first block is the SPLASH identifier, the second and third are summary blocks, and the fourth is the unique hash block.

This repository contains:

*  The current reference implementations for the SPLASH written in Java
* Additional implementations in C++, Python, C# and R
* API wrappers in Scala and JavaScript
* A Java validation tool 
* A web service with REST endpoints written in Java (accessible at http://splash.fiehnlab.ucdavis.edu) with Docker build files

SPLASH has been published in Nature Biotechnology.  If using SPLASH, please cite:

> Wohlgemuth, Gert, et al., *SPLASH, a Hashed Identifier for Mass Spectra*. Nature Biotechnology **34**, 1099-101 (2016). [doi:10.1038/nbt.3689](http://www.nature.com/nbt/journal/v34/n11/full/nbt.3689.html)

## Contents

* [Java API](#java-api)
* [C# API](csharp)
* [C++ API](cpp)
* [Python API](python)
* [R API](splashR)
* [JavaScript API](javascript)
* [Scala API](scala)
* [Validation Tool](#validation-tool)
* [REST Service](#rest-service)




## Java API

### Building

To build the project, run all tests and install the SPLASH jar files in the local Maven repository, simply run:

    mvn clean install


### Maven Dependency

The reference implementation jar file is available from the [Fiehn Lab Public Maven Repository](http://gose.fiehnlab.ucdavis.edu:55000/content/groups/public/edu/ucdavis/fiehnlab/splash/core/1.8/core-1.8.jar):

    <repository>
        <id>releases</id>
        <name>Fiehnlab Public Maven Repository</name>
        <url>http://gose.fiehnlab.ucdavis.edu:55000/content/groups/public</url>
    </repository>

To use SPLASH in a Maven project, add the following section to the project's `pom.xml` file:

    <dependency>
        <groupId>edu.ucdavis.fiehnlab.splash</groupId>
        <artifactId>core</artifactId>
        <version>1.8</version>
    </dependency>


### Usage

To generate a SPLASH for the following [mass spectrum of caffeine](http://www.massbank.jp/jsp/FwdRecord.jsp?id=PR100026) with MassBank identifier PR100026,

```
m/z        intensity
138.0641   71.59
195.0815   261.7
```

build a `Spectrum` object from a list of ion-intnsity pairs and use the `SplashFactory` to utilize the default SPLASHer:

```java
Splash splash = SplashFactory.create();
Spectrum spectrum = new SpectrumImpl(Arrays.asList(new Ion(138.0641, 71.59), new Ion(195.0815, 261.7)), SpectraType.MS);
String splash = splash.splashIt(spectrum);
```

Alternatively, you can also utilize the `SplashUtil` class to directly SPLASH a spectrum in a string representation:

```java
String splash = SplashUtil.splash("138.0641:71.59 195.0815:261.7", SpectraType.MS);
```

We are also providing an easy way to connect a listener to the splashing algorithm, so that you can inspect the different blocks, before they are hashed. This can be done with directly adding a SplashListener to your Splash instance or alternativly using the util like this

```java
String splash = SplashUtils.splash("138.0641:71.59 195.0815:261.7", SpectraType.MS, new SplashListener() {
    @Override
    public void eventReceived(SplashingEvent e) {}

    @Override
    public void complete(Spectrum spectrum, String splash) {}
});
```


## Validation Tool

In addition to the reference implementations, we provide a simple validation tool that is used to ensure consistent and accurate SPLASH generation between tools and implementations.  The latest jar file is available from the [Fiehn Lab Public Maven Repository](http://gose.fiehnlab.ucdavis.edu:55000/content/groups/public/edu/ucdavis/fiehnlab/splash/validation/1.8/validation-1.8.jar).  

to run this tool (from the sources) please clone and build the project and afterwards run

```
java -jar validation-1.8.jar
```

For example, to validate a file against thr reference implementation, run the following from the project root:

```
java -jar validation/target/validation-1.8.jar -c -s 2 -t ms ./base-dataset/spectra/notsplashed/test-set-v1.csv base-dataset/spectra/test-set-with-splash-v1.csv
```

The specified flags indicate:

* k = column number of the generated SPLASH
* o = column number of the spectrum ID/origin
* s = column number of the full mass spectrum in single-line string representation
* t = type of spectrum to SPLASH (default: MS)
* T = column delimiter
* X = enable debug messages

The format for single-line string spectral representation follows:

```
ion:intensity ion:intensity ...
```

## REST Service

The documentation for the REST service is available as a dedicated index page  once you start the REST server using:

```
java -jar web/target/web-1.8.jar
```

If you like to use the official REST API, you can find it at: http://splash.fiehnlab.ucdavis.edu


# Contributing

If you would like to contribute to this project, please feel submit issues or create a pull request. 