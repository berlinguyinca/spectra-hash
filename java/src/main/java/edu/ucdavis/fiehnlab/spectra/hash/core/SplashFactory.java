package edu.ucdavis.fiehnlab.spectra.hash.core;

import edu.ucdavis.fiehnlab.spectra.hash.core.impl.SplashHistVersion1;
import edu.ucdavis.fiehnlab.spectra.hash.core.impl.SplashHistVersion2;
import edu.ucdavis.fiehnlab.spectra.hash.core.impl.SplashHistVersion3;
import edu.ucdavis.fiehnlab.spectra.hash.core.impl.SplashVersion1;


/**
 * simple factory to create new hashes
 */
public class SplashFactory {

    public static Splash create() {
        return new SplashHistVersion3();
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
