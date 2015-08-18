package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import edu.ucdavis.fiehnlab.spectra.hash.core.sort.IonMZComperator;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.Ion;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: wohlgemuth
 * Date: 7/20/15
 * Time: 10:14 AM
 */
public class SplashHistVersion3 extends SplashVersion1 {
	int BINS = 10;
	int BIN_SIZE = 25;
	int INITIAL_SCALE_FACTOR = 9;
	int FINAL_SCALE_FACTOR = 99;

    /**
     * calculates a histogram and returns it
     *
     * @param spectrum
     * @return
     */
    @Override
    protected String calculate4thBlock(Spectrum spectrum) {
        List<Double> binnedIons = new ArrayList<Double>(BINS);

        // Max intensity value
        double maxIntensity = 0;

        // Bin ions
        for (Ion ion : spectrum.getIons()) {
            int index = (int)(ion.getMass() / BIN_SIZE);

	        // Add bins as needed
	        while (binnedIons.size() <= index) {
		        binnedIons.add(0.0);
	        }

	        double value = binnedIons.get(index) + ion.getIntensity();
	        binnedIons.set(index, value);

	        if (value > maxIntensity) {
		        maxIntensity = value;
	        }
        }

	    // Normalize the histogram
	    for (int i = 0; i < binnedIons.size(); i++) {
		    binnedIons.set(i, INITIAL_SCALE_FACTOR * binnedIons.get(i) / maxIntensity);
	    }

	    // Wrap the histogram
	    maxIntensity = 0;

	    for (int i = BINS; i < binnedIons.size(); i++) {
		    double value = binnedIons.get(i % BINS) + binnedIons.get(i);
		    binnedIons.set(i % BINS, value);

		    if (value > maxIntensity) {
			    maxIntensity = value;
		    }
	    }

	    // Renormalize the histogram
	    for (int i = 0; i < BINS; i++) {
		    binnedIons.set(i, FINAL_SCALE_FACTOR * binnedIons.get(i) / maxIntensity);
	    }

        // Build histogram string
        StringBuffer result = new StringBuffer();

        for (int i = 0; i < BINS; i++) {
	        result.append(String.format("%02d", binnedIons.get(i).intValue()));
        }

        return result.toString();
    }
}
