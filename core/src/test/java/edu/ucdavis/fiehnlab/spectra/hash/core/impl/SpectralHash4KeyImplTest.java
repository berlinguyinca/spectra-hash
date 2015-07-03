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

    @Test
    public void testGenerate() throws Exception {


        SpectraHash impl =getHashImpl();

        impl.addListener(new HashingListener() {
            public void eventReceived(HashingEvent e) {
                System.out.println(e.getBlock() + ":" + e.getRawValue());
                System.out.println(e.getBlock() + ":" + e.getHashedValue());
            }

            public void hashingComplete(Spectrum spectrum, String hash) {

            }

        });

//	    testDefault(impl, "f2beea5e3f67cf14b2bbcec6a8637b8618c9ce7374f1fafa6e6e67cdd5c52e38-5d20a4d7ee6c49d52f70734bae8bae0f1bab67a4-4af5cab77c62eaec5f87b570f2d2b127-0");
    }

    @Override
    SpectraHash getHashImpl() {
        return  new SpectralHash4KeyImpl();
    }
}