package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import edu.ucdavis.fiehnlab.spectra.hash.core.SpectraHash;
import org.junit.Test;

import edu.ucdavis.fiehnlab.spectra.hash.core.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;

/**
 * 
 */
public class SpectralHashSumMzIntImplTest extends AbstractSpectraHashImplTester {

    @Override
    SpectraHash getHashImpl() {
        return new SpectralHashSumMzIntImpl();
    }
}