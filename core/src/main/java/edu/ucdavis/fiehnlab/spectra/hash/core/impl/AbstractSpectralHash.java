package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.SpectraHash;
import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.HashingEvent;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.HashingListener;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 *
 */
public abstract class AbstractSpectralHash implements SpectraHash{
    /**
     * registered listeneres
     */
    private ConcurrentLinkedDeque<HashingListener> listeners = new ConcurrentLinkedDeque<HashingListener>();

    /**
     * adds a new listener
     * @param listener
     */
    public void addListener(HashingListener listener) {
        this.listeners.add(listener);
    }

    /**
     * notify listeners
     * @param e
     */
    protected void notifyListener(HashingEvent e) {
        for (HashingListener listener : listeners) {
            listener.eventReceived(e);
        }

    }

    /**
     * notify listeneres that the hash is complete
     * @param spectrum
     * @param hash
     */
    protected void notifyListenerHashComplete(Spectrum spectrum, String hash) {
        for (HashingListener listener : listeners) {
            listener.hashingComplete(spectrum, hash);
        }
    }
}
