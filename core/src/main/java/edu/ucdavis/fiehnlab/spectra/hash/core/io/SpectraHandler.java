package edu.ucdavis.fiehnlab.spectra.hash.core.io;

import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;

/**
 * simple handler to work on received spectra
 */
public interface SpectraHandler {

    /**
     * a spectrum was found, lets do something with it
     * @param spectrum
     */
    void handle(Spectrum spectrum);
}
