package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.SpectraHash;
import org.junit.Test;

/**
 * Created by wohlg_000 on 6/30/2015.
 */
public class SpectraHashMonaAlphaImplTest extends AbstractSpectraHashImplTester {

    @Override
    SpectraHash getHashImpl() {
        return new SpectraHashMonaAlphaImpl();
    }
}