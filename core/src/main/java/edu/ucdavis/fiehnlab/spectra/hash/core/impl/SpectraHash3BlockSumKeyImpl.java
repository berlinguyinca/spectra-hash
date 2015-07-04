package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;

/**
 * current 3 block implmentations
 *
 * 1:   SHA-256, Top 10 Ions, 6 digits, order by intensity
 * 2:   SHA-256, Spectra, 6 digits, order from lowest to highest mass
 * 3:   version, character 0-n
 */
public class SpectraHash3BlockSumKeyImpl extends AbstractSpectralHash {

    public String generate(Spectrum spectrum) {
        return encodeTop10Ions(spectrum)+"-"+encodeSpectra(spectrum)+"-"+getVersion();
    }
}
