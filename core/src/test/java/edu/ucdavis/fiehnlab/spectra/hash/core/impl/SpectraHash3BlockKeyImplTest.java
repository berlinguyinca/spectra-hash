package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.SpectraHash;
import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.HashingEvent;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.HashingListener;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by wohlg_000 on 7/3/2015.
 */
public class SpectraHash3BlockKeyImplTest extends AbstractSpectraHashImplTester {

    @Override
    SpectraHash getHashImpl() {
        return new SpectraHash3BlockKeyImpl();
    }
}