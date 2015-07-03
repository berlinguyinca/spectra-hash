package edu.ucdavis.fiehnlab.spectra.hash.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * a simple spectrum
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
     */
    Spectrum toRelative();

    /**
     *
     * @return
     */
    public String getOrigin();
}
