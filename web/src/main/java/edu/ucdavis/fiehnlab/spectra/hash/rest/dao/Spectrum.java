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



    private List<Ion> ions = new ArrayList<Ion>();

    public Spectrum(){

    }

    public edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum toRelative(int scale) {
        return new SpectrumImpl(getIons(),getOrigin(),getType()).toRelative(100);
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
