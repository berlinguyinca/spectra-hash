package edu.ucdavis.fiehnlab.spectra.hash.rest.dao;

/**
 * defines the response from the validation
 */
public class ValidationResponse {
    String referenceSplash;

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

    public String getReferenceSplash() {
        return referenceSplash;
    }

    public void setReferenceSplash(String refrenceSplash) {
        this.referenceSplash = refrenceSplash;
    }

    ValidationRequest request;
}
