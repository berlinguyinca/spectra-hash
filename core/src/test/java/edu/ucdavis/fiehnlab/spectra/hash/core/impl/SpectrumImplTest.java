package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.Ion;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 *  */
public class SpectrumImplTest {

    @Test
    public void testGetIons() throws Exception {
        Ion ion= new Ion(120,111);
        ArrayList<Ion> list = new ArrayList<Ion>();
        list.add(ion);
        SpectrumImpl impl = new SpectrumImpl(list,"mona");

        assertTrue(impl.getIons() != null);
        assertTrue(impl.getIons().iterator().next().equals(ion));
    }
}