package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;

import java.util.*;

/**
 * simple implementation for a spectrum
 */
public class SpectrumImpl implements Spectrum {

    public SpectrumImpl(List<Ion> ions, String mona) {
        this.ions = ions;
        this.origin = mona;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    private String origin;

    private List<Ion> ions;

    private Map<String,Object> metaData;

    public SpectrumImpl(List<Ion> ions){
        this(ions,new HashMap<String,Object>(),"");
    }

    public SpectrumImpl(List<Ion> ions, Map<String, Object> metaData,String origin) {
        this.ions = ions;
        this.metaData = metaData;
        this.origin = origin;
    }

    public List<Ion> getIons() {
        return ions;
    }

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public Spectrum toRelative() {
        double max = 0;

        for(Ion ion : getIons()){
            if(ion.getIntensity() > max){
                max = ion.getIntensity();
            }
        }

        List<Ion> ions = new ArrayList<Ion>(getIons().size());
        for(Ion ion : getIons()){
            ions.add(new Ion(ion.getMass(),ion.getIntensity()/max*100));
        }
        return new SpectrumImpl(ions,getMetaData(),getOrigin());
    }

}
