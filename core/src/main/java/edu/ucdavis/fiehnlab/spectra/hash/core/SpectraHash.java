package edu.ucdavis.fiehnlab.spectra.hash.core;

import edu.ucdavis.fiehnlab.spectra.hash.core.listener.HashingListener;

import java.util.Map;

/**
 * declares an easy to use api to calculate a spectra hash
 */
public interface SpectraHash {

    /**
     * generates a new spectra hash based on the given spectrum and the origin
     * @param spectrum
     * @return
     */
    String generate(Spectrum spectrum);

    /**
     * adds an optional listener to the
     * @param listener
     */
    void addListener(HashingListener listener);
}
