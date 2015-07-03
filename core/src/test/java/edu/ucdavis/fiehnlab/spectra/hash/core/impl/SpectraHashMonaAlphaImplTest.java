package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.SpectraHash;
import org.junit.Test;

/**
 * Created by wohlg_000 on 6/30/2015.
 */
public class SpectraHashMonaAlphaImplTest extends AbstractSpectraHashImplTester {

    @Test
    public void testGenerate() throws Exception {

        SpectraHash impl = getHashImpl();

//	    testDefault(impl, "mona-aac9c7920625d1cb861051373adc63d8dbb2bb84-0");
    }

    @Override
    SpectraHash getHashImpl() {
        return new SpectraHashMonaAlphaImpl();
    }
}