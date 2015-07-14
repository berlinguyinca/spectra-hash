package edu.ucdavis.fiehnlab.spectra.hash.core.util;

import edu.ucdavis.fiehnlab.spectra.hash.core.Splash;
import edu.ucdavis.fiehnlab.spectra.hash.core.SplashFactory;
import edu.ucdavis.fiehnlab.spectra.hash.core.impl.SplashVersion1;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectraType;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectrumImpl;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: wohlgemuth
 * Date: 7/14/15
 * Time: 2:19 PM
 */
public class SplashUtilTest {

    @Test
    public void testSplash() throws Exception {

        String splashUtilGenerated = SplashUtil.splash("127:1 128:2", SpectraType.MS);

        Splash splash = SplashFactory.create();

        String splashDirectlyCreated = splash.splashIt(new SpectrumImpl(Arrays.asList(new Ion(127, 1), new Ion(128, 2)), SpectraType.MS));

        assertEquals(splashDirectlyCreated, splashUtilGenerated);

    }
}