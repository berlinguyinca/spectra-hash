package edu.ucdavis.fiehnlab.spectra.hash.core.util;

import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectraType;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectrumImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * little utility class to help with converting spectra
 */
public class SpectraUtil {

    /**
     * converts a string to a spectrum object for us
     *
     * @param spectra
     * @param type
     * @return
     */
    public static Spectrum convertStringToSpectrum(String spectra, SpectraType type, String origin) {
        if (!spectra.matches("^(?:\\d+?(?:\\.\\d+?)?):(?:\\d+?(?:\\.\\d+?)?)(?: (?:\\d+?(?:\\.\\d+?)?):(?:\\d+?(?:\\.\\d+?)?))*?$")) {
            throw new IllegalArgumentException("sorry, your provided spectra string, did not match the exspected pattern!");
        }


        String[] pairs = spectra.split(" ");

        List<Ion> ionList = new ArrayList<Ion>(200);

        for (String pair : pairs) {
            String[] p = pair.split(":");

            Double m = Double.parseDouble(p[0]);
            Double intensity = Double.parseDouble(p[1]);

            ionList.add(new Ion(m, intensity));

        }

        SpectrumImpl impl = new SpectrumImpl(ionList, type);
        impl.setOrigin(origin);

        return impl;
    }

    public static Spectrum convertStringToSpectrum(String spectra, SpectraType type) {
        return convertStringToSpectrum( spectra,  type, "unknown");
    }
}
