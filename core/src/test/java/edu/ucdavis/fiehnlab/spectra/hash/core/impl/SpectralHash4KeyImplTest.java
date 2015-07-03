package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.listener.HashingEvent;
import edu.ucdavis.fiehnlab.spectra.hash.core.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.HashingListener;
import org.junit.Test;

import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import static org.junit.Assert.assertEquals;

/**
 * Created by wohlg_000 on 7/2/2015.
 */
public class SpectralHash4KeyImplTest {

    @Test
    public void testGenerate() throws Exception {


        SpectralHash4KeyImpl impl = new SpectralHash4KeyImpl();

        impl.addListener(new HashingListener() {
            public void eventReceived(HashingEvent e) {
                System.out.println(e.getBlock() + ":" + e.getRawValue());
                System.out.println(e.getBlock() + ":" + e.getHashedValue());
            }

            public void hashingComplete(Spectrum spectrum, String hash) {

            }

        });

        Spectrum spectrum = new SpectrumImpl(Arrays.asList(new Ion(100, 1), new Ion(101, 2), new Ion(102, 3)),"mona");

        String hash = impl.generate(spectrum);

        System.out.println(hash);
        System.out.println(hash.length());

        assertEquals("f2beea5e3f67cf14b2bbcec6a8637b8618c9ce7374f1fafa6e6e67cdd5c52e38-5d20a4d7ee6c49d52f70734bae8bae0f1bab67a4-4af5cab77c62eaec5f87b570f2d2b127-0",hash);
    }
}