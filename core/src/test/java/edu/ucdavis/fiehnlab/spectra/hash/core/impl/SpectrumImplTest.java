package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.Ion;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 *  */
public class SpectrumImplTest {

    @Test
    public void testGetIons() throws Exception {
        Ion ion= new Ion(120,111);
        SpectrumImpl impl = new SpectrumImpl(Arrays.asList(ion));

        assertTrue(impl.getIons() != null);
        assertTrue(impl.getIons().iterator().next().equals(ion));
    }
}