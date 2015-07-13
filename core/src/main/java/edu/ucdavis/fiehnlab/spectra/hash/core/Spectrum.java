package edu.ucdavis.fiehnlab.spectra.hash.core;

import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectraType;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.Ion;

import java.util.List;
import java.util.Map;

/**
 * describes a spectrum for a splash
 */
public interface Spectrum {

    /**
     * ion of a spectra
     * @return
     */
    public List<Ion> getIons();

    /**
     * meta data of a spectra
     * @return
     */
    public Map<String,Object> getMetaData();

    /**
     * convmerts the spectrum to a relative spectra
     * @return
     * @param scale
     */
    Spectrum toRelative(int scale);

    /**
     *
     * @return
     */
    public String getOrigin();

    /**
     * what kind of a spectra do we have
     * @return
     */
    public SpectraType getType();
}
