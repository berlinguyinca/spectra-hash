package edu.ucdavis.fiehnlab.spectra.hash.rest.dao;

import edu.ucdavis.fiehnlab.spectra.hash.core.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.impl.SpectrumImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wohlg_000 on 7/1/2015.
 */
public class Spectrum implements edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum {

    public List<Ion> getIons() {
        return ions;
    }

    public void setIons(List<Ion> ions) {
        this.ions = ions;
    }

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, Object> metaData) {
        this.metaData = metaData;
    }

    private List<Ion> ions = new ArrayList<Ion>();

    private Map<String,Object> metaData = new HashMap<String, Object>();

    public Spectrum(){

    }

    public edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum toRelative() {
        return new SpectrumImpl(getIons(),getMetaData(),getOrigin()).toRelative();
    }

    private String origin;

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}
