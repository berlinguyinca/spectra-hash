package edu.ucdavis.fiehnlab.spectra.hash.core.sort;

import edu.ucdavis.fiehnlab.spectra.hash.core.types.Ion;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: wohlgemuth
 * Date: 7/20/15
 * Time: 10:22 AM
 */
public class MassComperator implements Comparator<Ion> {
    @Override
    public int compare(Ion o1, Ion o2) {
        return o1.getMass().compareTo(o2.getMass());
    }
}
