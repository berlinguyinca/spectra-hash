package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.types.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectrumImpl;

import java.util.ArrayList;

/**
 * simple implementations needed for testing
 */
public class TestSpectraImpl extends SpectrumImpl {

    public void setHash(String hash) {
        this.hash = hash;
    }

    private String hash;

    public TestSpectraImpl() {
    }

    public TestSpectraImpl(Spectrum s, String hash) {
        super((ArrayList<Ion>) s.getIons(), s.getOrigin(), s.getType());
        this.hash = hash;
    }

    public String getHash() {
        return hash;
    }
}
