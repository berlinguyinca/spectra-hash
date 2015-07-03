package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import edu.ucdavis.fiehnlab.spectra.hash.core.io.SpectraHandler;
import edu.ucdavis.fiehnlab.spectra.hash.core.io.SpectrumReader;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wohlg_000 on 7/3/2015.
 */
public abstract class AbstractSpectraHashImplTester {

    List<Spectrum> getBinBaseSpectra(){
        final List<Spectrum> data = new ArrayList<Spectrum>();

        SpectrumReader reader = new SpectrumReader();

        reader.readSpectrum(new InputStreamReader(getClass().getResourceAsStream("/binbase/binbase.spectra")), new SpectraHandler() {
            public void handle(Spectrum spectrum) {
                data.add(spectrum);
            }
        });

        return data;

    }

    List <Spectrum> getMonaSpectra(){
        final List<Spectrum> data = new ArrayList<Spectrum>();

        SpectrumReader reader = new SpectrumReader();

        reader.readSpectrum(new InputStreamReader(getClass().getResourceAsStream("/binbase/binbase.spectra")), new SpectraHandler() {
            public void handle(Spectrum spectrum) {
                data.add(spectrum);
            }
        });

        return data;
    }
}
