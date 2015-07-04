package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;

/**
 * implementation for the 4 block version
 * <p/>
 * AAAAAA-BBBBBB-CCCCC-D
 * <p/>
 * first:   hashed spectra, rounded to N digits, ion pairs seperated by ":" and spectra seperated by ' '
 * ions are sorted from 0 to n
 * <p/>
 * second:  hashed top10 ions, rounded to N digits, ions seperated by ','
 * third:   hashed origin
 * 4th:     version of the HashKey
 */
public class SpectralHashTruncatedKeyImpl extends AbstractSpectralHash {

	/**
	 * generates our hash key
	 *
	 * @param spectrum
	 * @return
	 */
	public String calculateHash(Spectrum spectrum) {
		return encodeTop10Ions(spectrum).substring(0, 10) + "-" + encodeSpectra(spectrum).substring(0, 15) + "-" + getVersion();
	}

}
