package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.types.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectraType;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectrumImpl;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

/**
 *  */
public class SpectrumImplTest {

    @Test
    public void testGetIons() throws Exception {
        Ion ion = new Ion(120, 111);
        ArrayList<Ion> list = new ArrayList<Ion>();
        list.add(ion);
        SpectrumImpl impl = new SpectrumImpl(list, "test", SpectraType.MS);

        assertTrue(impl.getIons() != null);
        assertTrue(impl.getIons().iterator().next().equals(ion));
    }
}
