package edu.ucdavis.fiehnlab.spectra.hash.core.sort;

import edu.ucdavis.fiehnlab.spectra.hash.core.types.Ion;

import java.util.Comparator;

/**
 * sorts ions first by intensity, secondary by mass
 */
public class IntensityThenMassComparator implements Comparator<Ion> {
    public int compare(Ion o1, Ion o2) {
        int result = o2.getIntensity().compareTo(o1.getIntensity());

        if (result == 0) {
            result = o1.getMass().compareTo(o2.getMass());
        }

        return result;
    }
}
