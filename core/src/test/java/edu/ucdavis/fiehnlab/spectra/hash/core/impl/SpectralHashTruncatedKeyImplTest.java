package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.SpectraHash;

/**
 * Created by wohlg_000 on 7/2/2015.
 */
public class SpectralHashTruncatedKeyImplTest extends AbstractSpectraHashImplTester {

	@Override
	SpectraHash getHashImpl() {
		return new SpectralHashTruncatedKeyImpl();
	}
}