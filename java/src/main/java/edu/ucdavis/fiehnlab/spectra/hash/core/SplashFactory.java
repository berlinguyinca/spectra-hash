package edu.ucdavis.fiehnlab.spectra.hash.core;

import edu.ucdavis.fiehnlab.spectra.hash.core.impl.*;


/**
 * simple factory to create new hashes
 */
public class SplashFactory {

    public static Splash create() {
        return createVersion1Splash();
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
