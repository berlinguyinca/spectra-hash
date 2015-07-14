package edu.ucdavis.fiehnlab.spectra.hash.core.util;

import edu.ucdavis.fiehnlab.spectra.hash.core.Splash;
import edu.ucdavis.fiehnlab.spectra.hash.core.SplashFactory;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectraType;


/**
 * a simple splash util, to ease the splashing of data
 */
public class SplashUtil {

    /**
     * quick splashing of the latest splash implementation
     *
     * @param spectra
     * @param type
     * @return
     */
    public static String splash(String spectra, SpectraType type) {

        Splash s = SplashFactory.create();

        return s.splashIt(SpectraUtil.convertStringToSpectrum(spectra, type));
    }
}
