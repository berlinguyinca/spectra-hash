package edu.ucdavis.fiehnlab.spectra.hash.core;

import edu.ucdavis.fiehnlab.spectra.hash.core.impl.SplashVersion1;
import edu.ucdavis.fiehnlab.spectra.hash.core.impl.SplashVersion2;


/**
 * simple factory to create new hashes
 */
public class SplashFactory {

    public static Splash create() {
        return new SplashVersion2();
    }

    /**
     * the first official splash release
     *
     * @return
     */
    public static Splash createVersion1Splash() {
        return new SplashVersion1();
    }

    /**
     * the second official splash release
     *
     * @return
     */
    public static Splash createVersion2Splash() {
        return new SplashVersion2();
    }
}
