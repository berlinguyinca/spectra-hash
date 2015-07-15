package edu.ucdavis.fiehnlab.spectra.hash.core.validation.serialize;

import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectraType;

/**
 * Created with IntelliJ IDEA.
 * User: wohlgemuth
 * Date: 7/15/15
 * Time: 12:30 PM
 */
public class Result {

    String splash;

    String spectra;

    String origin;

    SpectraType type;

    public Result(String splash, String spectra, String origin, SpectraType type, String separator) {
        this.splash = splash;
        this.spectra = spectra;
        this.origin = origin;
        this.type = type;
        this.separator = separator;
    }

    public Result() {
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public SpectraType getType() {
        return type;
    }

    public void setType(SpectraType type) {
        this.type = type;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getSpectra() {
        return spectra;
    }

    public void setSpectra(String spectra) {
        this.spectra = spectra;
    }

    public String getSplash() {
        return splash;
    }

    public void setSplash(String splash) {
        this.splash = splash;
    }

    String separator;


    @Override
    public String toString() {
        return this.getSplash() + this.getSeparator() + this.getOrigin() + this.getSeparator() + this.getSpectra();
    }
}
