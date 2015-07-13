package edu.ucdavis.fiehnlab.spectra.hash.core;

import edu.ucdavis.fiehnlab.spectra.hash.core.impl.SplashVersion1;


/**
 * simple factory to create new hashes
 */
public class SpectraHashFactory {

    public static Splash create() {
        return new SplashVersion1();
    }

    /**
     * the first official splash release
     *
     * @return
     */
    public static Splash createVersion1Splash() {
        return new SplashVersion1();
    }
}
