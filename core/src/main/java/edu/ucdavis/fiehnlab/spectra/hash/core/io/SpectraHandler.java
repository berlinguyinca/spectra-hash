package edu.ucdavis.fiehnlab.spectra.hash.core.io;

import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;

import java.io.IOException;

/**
 * simple handler to work on received spectra
 */
public interface SpectraHandler {

    /**
     * beginning to read
     */
    void begin() throws IOException;
    /**
     * a spectrum was found, lets do something with it
     * @param spectrum
     */
    void handle(Spectrum spectrum);

    /**
     * no more data
     */
    void done() throws IOException;
}
