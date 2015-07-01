# spectra-hash
The MoNA spectral hash, general concept

Generates spectra hash codes, based on the latests specification.

implementations:

java

java-rest (in progress)

# usage

very simple example, needs file readers and specific implementations

        SpectraHashMonaAlphaImpl impl = new SpectraHashMonaAlphaImpl();
        Spectrum spectrum = new SpectrumImpl(Arrays.asList(new Ion(100,1),new Ion(101,2),new Ion(102,3)));

        String hash = impl.generate(spectrum,"mona");
