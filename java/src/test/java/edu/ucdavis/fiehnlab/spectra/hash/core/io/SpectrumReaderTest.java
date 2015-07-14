package edu.ucdavis.fiehnlab.spectra.hash.core.io;

import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectraType;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;

/**
 * simple test of a spectrum reader
 */
public class SpectrumReaderTest {

    Spectrum spectrum = null;

    @Test
    public void testReadSpectrum() throws Exception {

        SpectrumReader reader = new SpectrumReader();

        reader.readSpectrum(new StringReader("100:1 101:2 103:3"), new SpectraHandler() {
            public void begin() throws IOException {

            }

            public void handle(Spectrum s) {
                spectrum = s;
            }

            public void done() throws IOException {

            }
        }, SpectraType.MS);

        assertNotNull(spectrum);


    }


    @Test
    public void testReadSpectrum2() throws Exception {

        SpectrumReader reader = new SpectrumReader();

        reader.readSpectrum(new StringReader("test\t100:1 101:2 103:3"), new SpectraHandler() {
            public void begin() throws IOException {

            }

            public void handle(Spectrum s) {
                spectrum = s;
            }

            public void done() throws IOException {

            }
        }, SpectraType.MS);

        assertNotNull(spectrum);

        assertEquals("test", spectrum.getOrigin());

    }

    @Test
    public void testReadSpectrum3() throws Exception {

        SpectrumReader reader = new SpectrumReader();

        reader.readSpectrum(new StringReader("test\t100:1 101:2 103:3\t1"), new SpectraHandler() {
            public void begin() throws IOException {

            }

            public void handle(Spectrum s) {
                spectrum = s;
            }

            public void done() throws IOException {

            }
        }, SpectraType.MS);

        assertNotNull(spectrum);

        assertEquals("test_1", spectrum.getOrigin());

    }


}