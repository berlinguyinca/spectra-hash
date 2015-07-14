package edu.ucdavis.fiehnlab.spectra.hash.core;

import edu.ucdavis.fiehnlab.spectra.hash.core.listener.SplashListener;

/**
 * declares an easy to use api to calculate a spectra hash
 */
public interface Splash {

    /**
     * generates a new spectra hash based on the given spectrum
     * @return
     */
    String splashIt(Spectrum spectrum);

    /**
     * adds an optional listener to the
     * @param listener
     */
    void addListener(SplashListener listener);
}
