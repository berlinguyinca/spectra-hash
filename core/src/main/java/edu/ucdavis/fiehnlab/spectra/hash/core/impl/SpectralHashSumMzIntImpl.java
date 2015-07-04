package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.SpectraHash;
import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * implementation for the sum of multiplied m/z and intensities
 */
public class SpectralHashSumMzIntImpl extends AbstractSpectralHash implements SpectraHash {

    /**
     * generates our hash key
     *
     * @param spectrum
     * @return
     */
    public String generate(Spectrum spectrum) {
        //convert to relative
        spectrum = spectrum.toRelative();

        //get ions
        List<Ion> ions = spectrum.getIons();

        // sort by intensity in decreasing order 
	    Collections.sort(ions, new Comparator<Ion>() {
		    public int compare(Ion o1, Ion o2) {
			    return o2.getIntensity().compareTo(o1.getIntensity());
		    }
        });
        
        int ionCount = 0;

        double hashSum = 0.0;
        
        for (Ion ion : ions) {
            
            hashSum += ion.getMass() * ion.getIntensity();
            
            ionCount++;
            if (ionCount > 100) break;
        }

        String hashSumString = String.valueOf(Math.round(hashSum));
        return "sum" + String.format("%1$64s", hashSumString).replace(' ', '0');
    }

}
