package edu.ucdavis.fiehnlab.spectra.hash.rest.dao;

/**
 * defines how a validation request should look
 */
public class ValidationRequest {

    private Spectrum spectrum;

    private String splash;

    public ValidationRequest(){

    }

    public Spectrum getSpectrum() {
        return spectrum;
    }

    public void setSpectrum(Spectrum spectrum) {
        this.spectrum = spectrum;
    }

    public String getSplash() {
        return splash;
    }

    public void setSplash(String splash) {
        this.splash = splash;
    }
}
