package edu.ucdavis.fiehnlab.spectra.hash.rest;

import edu.ucdavis.fiehnlab.spectra.hash.core.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.SpectraHash;
import edu.ucdavis.fiehnlab.spectra.hash.core.SpectraHashFactory;
import edu.ucdavis.fiehnlab.spectra.hash.core.impl.SpectrumImpl;
import edu.ucdavis.fiehnlab.spectra.hash.rest.dao.Spectrum;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * simple rest service, which hashes submitted spectra data
 */
@org.springframework.web.bind.annotation.RestController
public class RestController {

    private SpectraHash spectraHash;

    private Logger logger = Logger.getLogger(getClass());

    public RestController() throws NoSuchAlgorithmException {
        spectraHash = SpectraHashFactory.create();
        logger.info("created hash generator of type: " + spectraHash);
    }
    /**
     * converts a spectra to the hash code
     */
    @RequestMapping(value = "/generate/{origin}", method = RequestMethod.POST)
    public String convert(@RequestBody Spectrum spectrum, @PathVariable("origin") String origin) throws NoSuchAlgorithmException {

        try {
            logger.info("received spectrum: " + spectrum);
            logger.info("definied origin: " + origin);

            String hash = spectraHash.generate(spectrum);
            logger.info("generated hash: " + hash);
            return hash;

        }
        catch (Exception e){
            logger.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/show")
    public Spectrum show(){
        Spectrum spectrum = new Spectrum();
        spectrum.setIons(Arrays.asList(new Ion(100, 1), new Ion(101, 2), new Ion(102, 3)));

        return spectrum;
    }
}
