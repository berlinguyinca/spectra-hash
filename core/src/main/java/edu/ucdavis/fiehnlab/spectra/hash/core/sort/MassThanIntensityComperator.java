package edu.ucdavis.fiehnlab.spectra.hash.core.sort;

import edu.ucdavis.fiehnlab.spectra.hash.core.types.Ion;

import java.util.Comparator;

/**
 * sorts first by mass ( smaller to larger), than by intensity (larger to smaller)
 */
public class MassThanIntensityComperator implements Comparator<Ion> {
    public int compare(Ion o1, Ion o2) {
        int result = o1.getMass().compareTo(o2.getMass());

        if (result == 0) {
            result = o2.getIntensity().compareTo(o1.getIntensity());
        }

        return result;
    }
}
