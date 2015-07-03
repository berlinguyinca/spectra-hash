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

        completeHash.append(firstBlock(spectrum));
        completeHash.append("-");
        completeHash.append(secondBlock(spectrum));
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

    private String firstBlock(Spectrum spectrum) {

        List<Ion> ions = spectrum.getIons();

        StringBuilder first = new StringBuilder();

        //sort by mass
        Collections.sort(ions, new Comparator<Ion>() {
            public int compare(Ion o1, Ion o2) {
                return o1.getMass().compareTo(o2.getMass());
            }
        });


        //build the first string
        for (int i = 0; i < ions.size(); i++) {
            first.append(String.format("%.6f", ions.get(i).getMass()));
            first.append(":");
            first.append(String.format("%.6f", ions.get(i).getIntensity()));

            //add our seperator
            if (i < ions.size() - 1) {
                first.append(" ");
            }
        }


        //notify obsers in case they want to know about progress of the hashing
        String block = first.toString();
        String hash = DigestUtils.sha256Hex(block);
        this.notifyListener(new HashingEvent(hash, block, 1, spectrum));
        return hash;
    }

    private String secondBlock(Spectrum spectrum) {
        StringBuilder second = new StringBuilder();
        List<Ion> ions = spectrum.getIons();

        //sort by intensity
        Collections.sort(ions, new Comparator<Ion>() {
            public int compare(Ion o1, Ion o2) {
                return o2.getIntensity().compareTo(o1.getIntensity());
            }
        });

        //build the second string
        for (int i = 0; i < ions.size(); i++) {
            second.append(String.format("%.6f", ions.get(i).getMass()));

            if (i == 10) {
                //we only want top 10 ions
                break;
            }

            if (i < ions.size() - 1) {
                second.append(",");
            }

        }

        String block = second.toString();
        String hash = DigestUtils.sha1Hex(block);
        this.notifyListener(new HashingEvent(hash, block, 0, spectrum));
        return hash;
    }

    public String getVersion() {
        return "0";
    }
}
