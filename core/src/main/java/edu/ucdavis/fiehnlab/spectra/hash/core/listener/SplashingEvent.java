package edu.ucdavis.fiehnlab.spectra.hash.core.listener;

import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;

/**
 * simple event, recevied by SplashListener
 */
public class SplashingEvent {

    private final Spectrum spectrum;
    private final String processedValue;

    public SplashingEvent(String processedValue, String rawValue, SplashBlock block, Spectrum spectrum) {
        this.processedValue = processedValue;
        this.rawValue = rawValue;
        this.block = block;
        this.spectrum = spectrum;
    }

    private final String rawValue;

    public SplashBlock getBlock() {
        return block;
    }


    public String getRawValue() {
        return rawValue;
    }

    public String getProcessedValue() {
        return processedValue;
    }

    private final SplashBlock block;


    public Spectrum getSpectrum() {
        return spectrum;
    }
}
