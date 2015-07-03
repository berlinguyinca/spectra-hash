package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.SpectraHash;
import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.HashingEvent;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.HashingListener;
import org.junit.Test;

/**
 * Created by wohlg_000 on 7/2/2015.
 */
public class SpectralHash4KeyImplTest extends AbstractSpectraHashImplTester {

    @Override
    SpectraHash getHashImpl() {
        return  new SpectralHash4KeyImpl();
    }
}