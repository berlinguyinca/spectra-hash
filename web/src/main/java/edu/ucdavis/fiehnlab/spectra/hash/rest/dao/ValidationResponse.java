package edu.ucdavis.fiehnlab.spectra.hash.rest.dao;

/**
 * defines the response from the validation
 */
public class ValidationResponse {

    String refrenceSplash;

    boolean validationSuccessful;

    public boolean isValidationSuccessful() {
        return validationSuccessful;
    }

    public void setValidationSuccessful(boolean validationSuccessful) {
        this.validationSuccessful = validationSuccessful;
    }

    public ValidationRequest getRequest() {
        return request;
    }

    public void setRequest(ValidationRequest request) {
        this.request = request;
    }

    public String getRefrenceSplash() {
        return refrenceSplash;
    }

    public void setRefrenceSplash(String refrenceSplash) {
        this.refrenceSplash = refrenceSplash;
    }

    ValidationRequest request;
}
