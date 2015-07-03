package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.SpectraHash;
import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.HashingEvent;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.HashingListener;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * implementation for the 4 block version
 * <p>
 * AAAAAA-BBBBBB-CCCCC-D
 * <p>
 * first:   hashed spectra, rounded to N digits, ion pairs seperated by ":" and spectra seperated by ' '
 * ions are sorted from 0 to n
 * <p>
 * second:  hashed top10 ions, rounded to N digits, ions seperated by ','
 * third:   hashed origin
 * 4th:     version of the HashKey
 */
public class SpectralHash4KeyImpl extends AbstractSpectralHash implements SpectraHash {

    /**
     * generates our hash key
     *
     * @param spectrum
     * @return
     */
    public String generate(Spectrum spectrum) {
        //convert to relative
        spectrum = spectrum.toRelative();

        //get ions
        List<Ion> ions = spectrum.getIons();

        //build the actual hash
        StringBuilder completeHash = new StringBuilder();

        completeHash.append(encodeSpectra(spectrum));
        completeHash.append("-");
        completeHash.append(encodeTop10Ions(spectrum));
        completeHash.append("-");
        completeHash.append(thirdBlock(spectrum));
        completeHash.append("-");
        completeHash.append(getVersion());

        String hash = completeHash.toString();
        this.notifyListenerHashComplete(spectrum, hash);
        return hash;
    }

    private String thirdBlock( Spectrum spectrum) {
        String hash = DigestUtils.md5Hex(spectrum.getOrigin());
        this.notifyListener(new HashingEvent(hash, spectrum.getOrigin(), 2, spectrum));
        return hash;
    }

}
