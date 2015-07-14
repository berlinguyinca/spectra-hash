# spectra-hash
The splash

this library is the current reference implementation for the splash. Splash stands for the spectra hash code and is an unique identifier independent of aquisition or processing. It basically tries to ensure that you can easily tell if two spectra are identical, similar or very different. Based on several criteria.

You can access it as a REST service, at http://splash.fiehnlab.ucdavis.edu

Or use one any of the available implementations, which should have been validated against the REST validation service.

# usage

## java api:
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

## scala api:

TODO

## C# api:

TODO

## C api:

TODO

## Python api:

TODO

## rest service:

the documentation for the REST service, is available as a dedicated index page, once you start the REST server. If you like to use the official webservice, you can find it at http://splash.fiehnlab.ucdavis.edu

## building

```
mvn clean install
```

will build your project, run all the tests and you can find the build jar files, in the target directories of the project.

##Contributing

if you like to contribute to this project, please feel free to contact me.