package edu.ucdavis.fiehnlab.spectra.hash.rest;

import edu.ucdavis.fiehnlab.spectra.hash.core.types.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.Splash;
import edu.ucdavis.fiehnlab.spectra.hash.core.SpectraHashFactory;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectraType;
import edu.ucdavis.fiehnlab.spectra.hash.rest.dao.Spectrum;
import edu.ucdavis.fiehnlab.spectra.hash.rest.dao.ValidationRequest;
import edu.ucdavis.fiehnlab.spectra.hash.rest.dao.ValidationResponse;
import org.apache.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * simple rest service, which hashes submitted spectra data, as well as validate provided spectra hashes against the current reference implementation
 */
@org.springframework.web.bind.annotation.RestController
public class RestController {

    private Splash spectraHash;

    private Logger logger = Logger.getLogger(getClass());

    public RestController() {
        spectraHash = SpectraHashFactory.create();
        logger.info("created hash generator of type: " + spectraHash);
    }

    /**
     * converts a spectra to the hash code
     */
    @RequestMapping(value = "/splash/it", method = RequestMethod.POST)
    public String convert(@RequestBody Spectrum spectrum) throws NoSuchAlgorithmException {

        try {
            logger.info("received spectrum: " + spectrum);

            String hash = spectraHash.splashIt(spectrum);
            logger.info("generated hash: " + hash);
            return hash;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/splash/it/example")
    public Spectrum splashRequestExample() {
        Spectrum spectrum = new Spectrum();
        spectrum.setIons(Arrays.asList(new Ion(100, 1), new Ion(101, 2), new Ion(102, 3)));
        spectrum.setOrigin("where do I come from or null");
        spectrum.setType(SpectraType.MS);

        return spectrum;
    }

    /**
     * converts a spectra to the hash code
     */
    @RequestMapping(value = "/splash/validate", method = RequestMethod.POST)
    public ValidationResponse validate(@RequestBody ValidationRequest validationRequest) throws NoSuchAlgorithmException {

        try {
            String reference = spectraHash.splashIt(validationRequest.getSpectrum());
            String provided = validationRequest.getSplash();

            ValidationResponse validationResponse = new ValidationResponse();
            validationResponse.setRequest(validationRequest);
            validationResponse.setRefrenceSplash(reference);
            validationResponse.setValidationSuccessful(reference.equals(provided));

            return validationResponse;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/splash/validate/example")
    public ValidationRequest splashValidationExample() {
        Spectrum spectrum = new Spectrum();
        spectrum.setIons(Arrays.asList(new Ion(100, 1), new Ion(101, 2), new Ion(102, 3)));
        spectrum.setOrigin("where do I come from or null");
        spectrum.setType(SpectraType.MS);

        ValidationRequest request = new ValidationRequest();
        request.setSpectrum(spectrum);
        request.setSplash(spectraHash.splashIt(spectrum));

        return request;
    }


}
