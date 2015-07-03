package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.SpectraHash;
import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import edu.ucdavis.fiehnlab.spectra.hash.core.io.SpectraHandler;
import edu.ucdavis.fiehnlab.spectra.hash.core.io.SpectrumReader;
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

    private List<Spectrum> getSpectrums(String fileName) {
        final List<Spectrum> data = new ArrayList<Spectrum>();

        SpectrumReader reader = new SpectrumReader();

        reader.readSpectrum(new InputStreamReader(getClass().getResourceAsStream(fileName)), new SpectraHandler() {
            public void handle(Spectrum spectrum) {
                data.add(spectrum);
            }
        });

        return data;
    }

    List <Spectrum> getMonaSpectraTest1(){
        return getSpectrums(MONA_TESTDATA_1);
    }


    List <Spectrum> getMonaSpectraTest2(){
        return getSpectrums(MONA_TESTDATA_2);
    }


    /**
     * get our impl to test
     * @return
     */
    abstract SpectraHash getHashImpl();

    private void runTest(String fileName, List<Spectrum> data) throws IOException {
        FileWriter out = new FileWriter("target/" + fileName);

        int i = 0;

        for(Spectrum s : data){
            String hash = getHashImpl().generate(s);

            out.write(s.getOrigin() + "\t" + i + "\t" + hash + "\n");
            i++;
        }

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
        runTest("binbase.hash-" + this.getHashImpl().toString(), getSpectrums(BINBASE_TESTDATA_1));
    }

    @Test
    public void testBinBaseAlanineSpectraHash() throws IOException {
        runTest("binbase.alanine.annotations-" + this.getHashImpl().getClass().getSimpleName(), getSpectrums(BINBASE_TESTDATA_2));

    }

    @Test
    public void testMonaSpectraHash1() throws IOException {
        runTest("mona.hash-" + this.getHashImpl().getClass().getSimpleName(), getMonaSpectraTest1());
    }

    @Test
    public void testMonaSpectraHash2() throws IOException {
        runTest("mona-2.hash-" + this.getHashImpl().getClass().getSimpleName(), getSpectrums(MONA_TESTDATA_2));
    }

}
