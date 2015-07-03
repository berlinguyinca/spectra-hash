package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import java.util.Arrays;

import org.junit.Test;

import edu.ucdavis.fiehnlab.spectra.hash.core.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;

/**
 * 
 */
public class SpectralHashSumMzIntImplTest {

    @Test
    public void testGenerate() throws Exception {


        SpectralHashSumMzIntImpl impl = new SpectralHashSumMzIntImpl();


        Spectrum spectrum = new SpectrumImpl(Arrays.asList(new Ion(100, 1), new Ion(101, 2), new Ion(102, 3)),"mona");

        String hash = impl.generate(spectrum);

        System.out.println(hash);
        System.out.println(hash.length());

    }
}