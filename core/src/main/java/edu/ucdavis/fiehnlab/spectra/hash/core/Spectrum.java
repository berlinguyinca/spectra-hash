package edu.ucdavis.fiehnlab.spectra.hash.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * a simple spectrum
 */
public interface Spectrum {

    public List<Ion> getIons();

    /**
     * convmerts the spectrum to a relative spectra
     * @return
     */
    Spectrum toRelative();
}
