package edu.ucdavis.fiehnlab.spectra.hash.core;

import edu.ucdavis.fiehnlab.spectra.hash.core.impl.SpectraHashMonaAlphaImpl;

import java.security.NoSuchAlgorithmException;

/**
 * simple factory to create new hashes
 */
public class SpectraHashFactory {

    public static SpectraHash create() throws NoSuchAlgorithmException {
        return new SpectraHashMonaAlphaImpl();
    }
}
