package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.SpectraHash;

/**
 * Created by wohlg_000 on 7/3/2015.
 */
public class SpectraHash3BlockKeyImplPrec4test extends AbstractSpectraHashImplTester {

    @Override
    SpectraHash getHashImpl() {
        SpectraHash3BlockKeyImpl impl = new SpectraHash3BlockKeyImpl();
        impl.setPrecission(4);
        return impl;
    }



}