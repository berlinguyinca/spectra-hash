package edu.ucdavis.fiehnlab.spectra.hash.core;

import java.util.Map;

/**
 * declares an easy to use api to calculate a spectra hash
 */
public interface SpectraHash {

    /**
     * generates a new spectra hash based on the given spectrum and the origin
     * @param spectrum
     * @param origin
     * @return
     */
    String generate(Spectrum spectrum, String origin, Map<String,Object> metaData );
}
