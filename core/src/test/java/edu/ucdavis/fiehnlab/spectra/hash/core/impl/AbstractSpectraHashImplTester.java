package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.SpectraHash;
import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import edu.ucdavis.fiehnlab.spectra.hash.core.io.SpectraHandler;
import edu.ucdavis.fiehnlab.spectra.hash.core.io.SpectrumReader;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.HashingListener;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by wohlg_000 on 7/3/2015.
 */
public abstract class AbstractSpectraHashImplTester {

    public static final String MONA_TESTDATA_1 = "/mona/spectra-hash-test-spectra.txt";
    public static final String MONA_TESTDATA_2 = "/mona/spectra-hash-min-spectra.txt";
    public static final String BINBASE_TESTDATA_1 = "/binbase/bins.spectra";
    public static final String BINBASE_TESTDATA_2 = "/binbase/alanine.bin.annotations";
    public static final String BINBASE_TESTDATA_3 = "/binbase/local/all.bin.annotations";


    /**
     * get our impl to test
     * @return
     */
    abstract SpectraHash getHashImpl();

    private void runTest(String fileName, String inputFile) throws IOException {
        final FileWriter out = new FileWriter("target/" + fileName);
        final SpectrumReader reader = new SpectrumReader();

        reader.readSpectrum(new InputStreamReader(getClass().getResourceAsStream(inputFile)), new SpectraHandler() {
            public void handle(Spectrum s) {
                String hash = getHashImpl().generate(s);

                try {
                    out.write(s.getOrigin() + "\t" + hash + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        out.flush();
        out.close();
    }


    /**
     * test different implementations of the hashing algo
     * with spectrum: (100, 1) (101, 2) (102, 3)
     *
     * @param impl
     */
    public void testDefault(SpectraHash impl, String expected) {
        Spectrum spectrum = new SpectrumImpl(Arrays.asList(new Ion(100, 1), new Ion(101, 2), new Ion(102, 3)), "mona");

        String hash = impl.generate(spectrum);

        assertEquals(expected, hash);

    }


    @Test
    public void testBinBaseAllBinSpectraHash() throws IOException {
        runTest("binbase.hash-" + this.getHashImpl().toString(), (BINBASE_TESTDATA_1));
    }

    @Test
    public void testBinBaseAlanineSpectraHash() throws IOException {
        runTest("binbase.alanine.annotations-" + this.getHashImpl().toString(), (BINBASE_TESTDATA_2));
    }


    @Ignore
    @Test
    public void testBinBaseAllAnnotationSpectraHash() throws IOException {
        runTest("binbase.all.annotations-" + this.getHashImpl().toString(), (BINBASE_TESTDATA_3));
    }

    @Test
    public void testMonaSpectraHash1() throws IOException {
        runTest("mona.hash-" + this.getHashImpl().toString(), MONA_TESTDATA_1);
    }

    @Test
    public void testMonaSpectraHash2() throws IOException {
        runTest("mona-2.hash-" + this.getHashImpl().toString(), (MONA_TESTDATA_2));
    }
}
