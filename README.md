# spectra-hash
The splash

this library is the current reference implementation for the splash. Splash stands for the spectra hash code and is an unique identifier independent of aquisition or processing. It basically tries to ensure that you can easily tell if two spectra are identical, similar or very different. Based on several criteria.

You can access it as a REST service, at http://splash.fiehnlab.ucdavis.edu

Or use one of the availalbe implementations.

This documentation is for the Java version only. Please be aware that this repository also includes, the REST interface, which wraps around the Java API

implementations:

java

# usage

## api:
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

## rest service:

the documentation for the REST service, is available as a dedicated index page, once you start the REST server

## building

```
mvn clean install
```

will build your project, run all the tests and you can find the build jar files, in the target directories of the project.