# spectra-hash
The MoNA spectral hash, general concept

Generates spectra hash codes, based on the latests specification.

implementations:

java

java-rest (in progress)

# usage

## api:
very simple example, needs file readers and specific implementations

```
        SpectraHashMonaAlphaImpl impl = new SpectraHashMonaAlphaImpl();
        Spectrum spectrum = new SpectrumImpl(Arrays.asList(new Ion(100,1),new Ion(101,2),new Ion(102,3)));

        String hash = impl.generate(spectrum,"mona");
```

## rest service:

the rest module provides a simple web service to calculate a hash code, for a submitted spectra.

to convert a given spectra to the rest server, you will need to request it the following way:

```
Request method:	POST
Request path:	/generate/DATABASE_NAME
Headers:		Content-Type=application/json
Body:
{
    "ions": [
        {
            "mass": 100,
            "intensity": 1
        },
        {
            "mass": 101,
            "intensity": 2
        },
        {
            "mass": 102,
            "intensity": 3
        }
    ],
    "metaData": {
        
    }
}
```

this will return you the generated spectra-hash in the latest official version.
