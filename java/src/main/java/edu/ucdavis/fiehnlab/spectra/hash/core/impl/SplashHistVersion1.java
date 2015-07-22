package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import edu.ucdavis.fiehnlab.spectra.hash.core.sort.IonComperator;
import edu.ucdavis.fiehnlab.spectra.hash.core.sort.IonMZComperator;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.Ion;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: wohlgemuth
 * Date: 7/20/15
 * Time: 10:14 AM
 */
public class SplashHistVersion1 extends SplashVersion1 {

    /**
     * calculates a histogram and returns it
     *
     * @param spectrum
     * @return
     */
    @Override
    protected String calculate4thBlock(Spectrum spectrum) {

        List<Ion> ionList = spectrum.getIons();

        Collections.sort(ionList, new IonMZComperator());

        //how many bins we want
        int bins = 10;
        //size of our bins
        int binSize = 1000/bins;

        //how to scale
        int scale = 9;

        Map<Integer, Double> binnedIons = new HashMap<Integer, Double>();

        //max value
        double max = 0;

        //bin our ions
        for (Ion ion : ionList) {

            for (int i = 1; i <= bins; i++) {

                if (ion.getMass() > i * binSize - binSize - 1 && ion.getMass() < i * binSize) {

                    if (!binnedIons.containsKey(i)) {
                        binnedIons.put(i, 0.0);
                    }

                    double value = ion.getIntensity() + binnedIons.get(i);

                    if (value > max) {
                        max = value;
                    }
                    binnedIons.put(i, value);
                }
            }
        }

        //normalize them

        StringBuffer result = new StringBuffer();

        for (int i = 1; i <= bins; i++) {
            if (binnedIons.containsKey(i)) {
                double value = binnedIons.get(i);

                value = value / max * scale;

                result.append((int) value);
            } else {
                result.append(0);
            }
        }

        return result.toString();
    }
}
