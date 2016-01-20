package edu.ucdavis.fiehnlab.spectra.hash.core.util;

import edu.ucdavis.fiehnlab.spectra.hash.core.Splash;
import edu.ucdavis.fiehnlab.spectra.hash.core.SplashFactory;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectraType;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectrumImpl;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

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

    @Test
    public void testSplashA(){

        System.out.println(SplashUtil.splash("195.0815:100 138.0641:27.3558", SpectraType.MS));
        System.out.println(SplashUtil.splash("393.2185:0.8805 320.1282:0.3235 436.2676:0.9633 501.2415:1.7095 121.0284:0.6362 128.1434:7.3477 278.0813:0.4691 114.1278:13.6642 336.1592:2.9277 142.1591:51.3807 435.2639:13.8641 293.1428:0.1688 161.0597:1.7983 309.1353:1.1935 388.1216:0.7916 72.0808:0.7937 350.1764:0.3301 58.0652:31.7421 130.1593:0.6701 324.1605:0.955 112.1119:1.7335 216.1014:0.4709 215.0937:1.3168 310.1439:1.1029 170.1903:12.1627 70.0651:0.5082 214.0861:0.2892 86.0964:2.5646 366.1912:0.2921 252.0326:0.6765 557.3059:11.0359 379.201:1.7066 421.2477:0.3226 195.9654:0.2215 100.1122:100 308.1273:0.7764 294.0792:8.4579", SpectraType.MS));
        System.out.println(SplashUtil.splash("773.5054:30.03 415.1457:20.02 771.4897:30.03 791.5159:30.03 379.1246:50.0501 397.1352:100 1225.8917:10.01 1165.8706:50.0501 789.5003:30.03 393.373:60.0601 391.3574:60.0601", SpectraType.MS));


    }
}
