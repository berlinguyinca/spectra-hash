package edu.ucdavis.fiehnlab.spectra.hash.rest.dao;

import edu.ucdavis.fiehnlab.spectra.hash.core.types.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectraType;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectrumImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * simple persistent spectra model
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

    public edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum toRelative(int scale) {
        return new SpectrumImpl(getIons(),getMetaData(),getOrigin(),getType()).toRelative(1000);
    }

    private String origin;

    public String getOrigin() {
        return origin;
    }

    public void setType(SpectraType type) {
        this.type = type;
    }

    private SpectraType type;

    public SpectraType getType() {
        return type;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}
