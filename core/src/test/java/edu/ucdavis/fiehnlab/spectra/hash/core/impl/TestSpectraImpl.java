package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import junit.framework.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wohlg_000 on 7/6/2015.
 */
public class TestSpectraImpl extends SpectrumImpl{

    public void setHash(String hash) {
        this.hash = hash;
    }

    private  String hash;

    public TestSpectraImpl(){};
    public TestSpectraImpl(Spectrum s, String hash){
        super((ArrayList<Ion>) s.getIons(),s.getMetaData(),s.getOrigin());
        this.hash = hash;
    }

    public String getHash() {
        return hash;
    }
}
