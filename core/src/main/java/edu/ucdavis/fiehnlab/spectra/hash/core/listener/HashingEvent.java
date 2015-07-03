package edu.ucdavis.fiehnlab.spectra.hash.core.listener;

import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;

/**
 * used for internal notifications of building blocks
 */
public class HashingEvent {

    private final Spectrum spectrum;
    private final String hashedValue;

    public HashingEvent(String hashedValue, String rawValue, int block, Spectrum spectrum) {
        this.hashedValue = hashedValue;
        this.rawValue = rawValue;
        this.block = block;
        this.spectrum = spectrum;
    }

    private final String rawValue;

    public int getBlock() {
        return block;
    }


    public String getRawValue() {
        return rawValue;
    }

    public String getHashedValue() {
        return hashedValue;
    }

    private final int block;


    public Spectrum getSpectrum() {
        return spectrum;
    }
}
