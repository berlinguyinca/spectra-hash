package edu.ucdavis.fiehnlab.spectra.hash.core.util;

import edu.ucdavis.fiehnlab.spectra.hash.core.Splash;
import edu.ucdavis.fiehnlab.spectra.hash.core.SplashFactory;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.SplashListener;
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

    /**
     * allows you to add a listener to see what is happening
     * @param spectra
     * @param type
     * @param listener
     * @return
     */
    public static String splash(String spectra, SpectraType type,SplashListener listener) {

        Splash s = SplashFactory.create();
        s.addListener(listener);

        return s.splashIt(SpectraUtil.convertStringToSpectrum(spectra, type));
    }

}
