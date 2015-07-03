package edu.ucdavis.fiehnlab.spectra.hash.core.listener;

import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;

/**
 * Created by wohlg_000 on 7/2/2015.
 */
public interface HashingListener {

    /**
     * let's listener know that a new hash was created
     * @param e
     */
    void eventReceived(HashingEvent e);

    /**
     * notificaton that the hashing is finished
     * @param spectrum
     * @param hash
     */
    void hashingComplete(Spectrum spectrum, String hash);
}
