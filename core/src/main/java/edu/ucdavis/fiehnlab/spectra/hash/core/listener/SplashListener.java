package edu.ucdavis.fiehnlab.spectra.hash.core.listener;

import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;

/**
 * a simple listener to react to different splashing events
 */
public interface SplashListener {

    /**
     * let's listener know that a new hash was created
     * @param e
     */
    void eventReceived(SplashingEvent e);

    /**
     * notificaton that the hashing is finished
     * @param spectrum
     * @param splash
     */
    void complete(Spectrum spectrum, String splash);
}
