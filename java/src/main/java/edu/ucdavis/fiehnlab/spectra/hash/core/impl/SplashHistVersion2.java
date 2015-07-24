package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import edu.ucdavis.fiehnlab.spectra.hash.core.sort.IonMZComperator;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.Ion;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: wohlgemuth
 * Date: 7/20/15
 * Time: 10:14 AM
 */
public class SplashHistVersion2 extends SplashVersion1 {

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

        //how to scale
        int scale = 9;

        Map<Integer, Double> binnedIons = new HashMap<Integer, Double>();

        //max value
        double max = 0;

        //bin our ions
        for (Ion ion : ionList) {

            int index = getBin(ion);

            if (!binnedIons.containsKey(index)) {
                binnedIons.put(index, 0.0);
            }

            /**
             * multiply mass * intensity to and than add it
             */
            double value = ((ion.getIntensity()) * (ion.getMass()) + binnedIons.get(index));

            if (value > max) {
                max = value;
            }
            binnedIons.put(index, value);
        }


        //normalize them

        StringBuffer result = new StringBuffer();

        for (int i = 0; i < 10; i++) {
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

    /**
     * finds the right bin for this ion
     * @param ion
     * @return
     */
    private int getBin(Ion ion) {
        int index = 0;
        if (ion.getMass() > 0 && ion.getMass() <= 100) {
            index = 0;
        } else if (ion.getMass() > 100 && ion.getMass() <= 200) {
            index = 1;
        } else if (ion.getMass() > 200 && ion.getMass() < 300) {
            index = 2;
        } else if (ion.getMass() > 300 && ion.getMass() <= 400) {
            index = 3;
        } else if (ion.getMass() > 400 && ion.getMass() <= 500) {
            index = 4;
        } else if (ion.getMass() > 500 && ion.getMass() <= 600) {
            index = 5;
        } else if (ion.getMass() > 600 && ion.getMass() <= 700) {
            index = 6;
        } else if (ion.getMass() > 700 && ion.getMass() <= 800) {
            index = 7;
        } else if (ion.getMass() > 800 && ion.getMass() <= 900) {
            index = 8;
        } else if (ion.getMass() > 900) {
            index = 9;
        }
        return index;
    }
}
