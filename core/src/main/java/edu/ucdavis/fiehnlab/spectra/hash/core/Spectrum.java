package edu.ucdavis.fiehnlab.spectra.hash.core;

import edu.ucdavis.fiehnlab.spectra.hash.core.types.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectraType;

import java.util.List;

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
     * convmerts the spectrum to a relative spectra
     * @return
     * @param scale
     */
    Spectrum toRelative(int scale);

    /**
     *
     * @return
     */
    String getOrigin();

    /**
     * what kind of a spectra do we have
     * @return
     */
    public SpectraType getType();
}
