package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;

/**
 * current 3 block implmentations
 *
 * 1:   version + SHA-256, Top 10 Ions, 6 digits, order by intensity. truncated to 10 digits
 * 2:   SHA-256, Spectra, 6 digits, order from lowest to highest mass, truncated to 20 digits
 * 3:   SUM, sum of ions limited to 10 digits
 */
public class SpectraHash3BlockSumTruncatedKeyImpl extends AbstractSpectralHash {

    /**
     * max amount of padding for the sum
     */
    private int maxDigits = 10;

    /**
     * max amount of ions
     */
    private int maxIons = 100;

    /**
     * calculates our hash
     * @param spectrum
     * @return
     */
    public String calculateHash(Spectrum spectrum) {
        return getVersion()+encodeTop10Ions(spectrum).substring(0,10)+"-"+encodeSpectra(spectrum).substring(0,20)+"-"+calculateSum(spectrum);
    }

    /**
     * calculates a total sum, with no digits, padded by 10 digits
     * @param spectrum
     * @return
     */
    protected String calculateSum(Spectrum spectrum) {
        int ionCount = 0;

        double hashSum = 0.0;

        for (Ion ion : spectrum.getIons()) {

            hashSum += ion.getMass() * ion.getIntensity();

            ionCount++;
            if (ionCount > maxIons) break;
        }

        return String.format("%0"+maxDigits+".0f", hashSum);
    }


    /**
     * get version of the string
     * padded with 3 digits
     * @return
     */
    protected String getVersion() {
        return "splash"+String.format("%03d",0);
    }

}
