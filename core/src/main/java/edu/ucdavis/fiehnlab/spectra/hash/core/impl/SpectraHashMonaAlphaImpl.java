package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Collections;
import java.util.List;

/**
 * the currently utilized hash in MoNA alpha
 */
public class SpectraHashMonaAlphaImpl extends AbstractSpectralHash {

    public SpectraHashMonaAlphaImpl()  {
    }

    /**
     * hashes a simple spectra as done in mona right now
     * @param spectrum
     * @return
     */
    public String calculateHash(Spectrum spectrum) {

        StringBuffer buffer = new StringBuffer();

        List<Ion> ions = spectrum.getIons();

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
