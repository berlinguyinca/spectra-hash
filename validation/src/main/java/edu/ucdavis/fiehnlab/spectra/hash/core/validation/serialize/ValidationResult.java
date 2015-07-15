package edu.ucdavis.fiehnlab.spectra.hash.core.validation.serialize;

import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectraType;

/**
 * Created with IntelliJ IDEA.
 * User: wohlgemuth
 * Date: 7/15/15
 * Time: 12:30 PM
 */
public class ValidationResult extends Result {

    boolean valid;

    public String getSplashToValidate() {
        return splashToValidate;
    }

    public void setSplashToValidate(String splashToValidate) {
        this.splashToValidate = splashToValidate;
    }

    String splashToValidate;

    public ValidationResult() {

    }

    @Override
    public String toString() {
        return this.getSplash() + this.getSeparator() + this.getOrigin() + this.getSeparator() + this.isValid() + this.getSeparator() + this.getSplashToValidate() + this.getSeparator() + this.getSpectra();
    }

    public ValidationResult(String code, String spectra, String origin, SpectraType msType, String seperator, boolean valid, String splash) {
        super(code, spectra, origin, msType, seperator);
        this.valid = valid;
        this.splash = splash;

    }

    public boolean isValid() {

        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
