package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by wohlg_000 on 6/30/2015.
 */
public class SpectraHashMonaAlphaImplTest {

    @Test
    public void testGenerate() throws Exception {

        SpectraHashMonaAlphaImpl impl = new SpectraHashMonaAlphaImpl();
        Spectrum spectrum = new SpectrumImpl(Arrays.asList(new Ion(100,1),new Ion(101,2),new Ion(102,3)));

        String hash = impl.generate(spectrum,"mona", new HashMap<String, Object>());

        System.out.println(hash);

        assertEquals("mona-AAC9C7920625D1CB861051373ADC63D8DBB2BB84-0".toLowerCase(),hash);

    }
}