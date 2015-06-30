package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * simple implementation for a spectrum
 */
public class SpectrumImpl implements Spectrum {

    private List<Ion> ions;

    public SpectrumImpl(List<Ion> ions){
        this.ions = ions;
    }
    public List<Ion> getIons() {
        return ions;
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
        return new SpectrumImpl(ions);
    }

}
