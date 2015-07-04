package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.SpectraHash;

import static org.junit.Assert.*;

/**
 * Created by wohlg_000 on 7/3/2015.
 */
public class SpectraHash3BlockSumKeyImplTest extends AbstractSpectraHashImplTester{

    @Override
    SpectraHash getHashImpl() {
        return new SpectraHash3BlockSumKeyImpl();
    }
}