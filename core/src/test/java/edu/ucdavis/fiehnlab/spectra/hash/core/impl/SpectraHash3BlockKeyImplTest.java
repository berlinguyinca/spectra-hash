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
public class SpectraHash3BlockKeyImplTest {

    @Test
    public void testGenerate() throws Exception {


        SpectraHash impl = new SpectraHash3BlockKeyImpl();

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

        assertEquals("5d20a4d7ee6c49d52f70734bae8bae0f1bab67a4-16840fdada28160115bf616ff1bf716ef9eba88a83ebc259c2fc9a601bb66f14-0",hash);

    }
}