package edu.ucdavis.fiehnlab.spectra.hash.core.types;

import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;

import java.util.*;

/**
 * simple implementation for a spectrum
 */
public class SpectrumImpl implements Spectrum {

    private String origin;
    private List<Ion> ions;
    private SpectraType type;

    public SpectrumImpl(List<Ion> ions,  String origin, SpectraType type) {
        this.ions = ions;
        this.origin = origin;
        this.type = type;
    }


    protected SpectrumImpl(){

    }

    public SpectrumImpl(List<Ion> ions, SpectraType ms) {
        this(ions,"unknown",ms);

    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public SpectraType getType() {
        return type;
    }

    public void setType(SpectraType type) {
        this.type = type;
    }

    public List<Ion> getIons() {
        return ions;
    }

    public void setIons(List<Ion> ions) {
        this.ions = ions;
    }


    public Spectrum toRelative(int scale) {
        double max = 0;

        for (Ion ion : getIons()) {
            if (ion.getIntensity() > max) {
                max = ion.getIntensity();
            }
        }

        ArrayList<Ion> ions = new ArrayList<Ion>(getIons().size());
        for (Ion ion : getIons()) {
            ions.add(new Ion(ion.getMass(), ion.getIntensity() / max * scale));
        }
        return new SpectrumImpl(ions, getOrigin(), getType());
    }

}
