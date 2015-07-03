package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.SpectraHash;
import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * the currently utilized hash in MoNA alpha
 */
public class SpectraHashMonaAlphaImpl extends AbstractSpectralHash {

    public SpectraHashMonaAlphaImpl() throws NoSuchAlgorithmException {
    }

    /**
     * hashes a simple spectra as done in mona right now
     * @param spectrum
     * @param origin
     * @return
     */
    public String generate(Spectrum spectrum) {

        StringBuffer buffer = new StringBuffer();

        List<Ion> ions = spectrum.toRelative().getIons();

        Collections.sort(ions);

        for(Ion ion:spectrum.getIons()){
            buffer.append(ion.getMass());
            buffer.append("-");
        }

        buffer.delete(buffer.lastIndexOf("-"),buffer.length());

        for(Ion ion:spectrum.getIons()){
            buffer.append(ion.getIntensity());
            buffer.append("-");
        }

        buffer.delete(buffer.lastIndexOf("-"),buffer.length());

        return spectrum.getOrigin() + "-"+ DigestUtils.sha1Hex(buffer.toString().trim())+"-0";
    }
}
