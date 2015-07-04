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

    List<Spectrum> getBinBaseSpectra(){
        final List<Spectrum> data = new ArrayList<Spectrum>();

        SpectrumReader reader = new SpectrumReader();

        reader.readSpectrum(new InputStreamReader(getClass().getResourceAsStream("/binbase/bins.spectra")), new SpectraHandler() {
            public void handle(Spectrum spectrum) {
                data.add(spectrum);
            }
        });

        return data;

    }

    List <Spectrum> getMonaSpectra(){
        final List<Spectrum> data = new ArrayList<Spectrum>();

        SpectrumReader reader = new SpectrumReader();

        reader.readSpectrum(new InputStreamReader(getClass().getResourceAsStream("/mona/spectra-hash-test-spectra.txt")), new SpectraHandler() {
            public void handle(Spectrum spectrum) {
                data.add(spectrum);
            }
        });

        return data;
    }

    /**
     * get our impl to test
     * @return
     */
    abstract SpectraHash getHashImpl();

    @Test
    public void testBinBaseSpectraHash() throws IOException {
        FileWriter out = new FileWriter("binbase.hash-" + this.getHashImpl().getClass().getSimpleName());

        int i = 0;

        for(Spectrum s : getBinBaseSpectra()){
            String hash = getHashImpl().generate(s);

            out.write(i + "\t" + hash + "\n");
            i++;
        }

        out.flush();
        out.close();

    }


    @Test
    public void testMonaSpectraHash() throws IOException {
        FileWriter out = new FileWriter("mona.hash-" + this.getHashImpl().getClass().getSimpleName());

        int i = 0;

        for(Spectrum s : getMonaSpectra()){
            String hash = getHashImpl().generate(s);

            out.write(i + "\t" + hash + "\n");
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

        System.out.println(hash);
        System.out.println(hash.length());

        assertEquals(expected, hash);

    }

}
