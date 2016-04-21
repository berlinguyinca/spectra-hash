package edu.ucdavis.fiehnlab.spectra.hash.core.util;

import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectraType;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: wohlgemuth
 * Date: 7/14/15
 * Time: 2:15 PM
 */
public class SpectraUtilTest {

    @Test
    public void testConvertStringToSpectrum() throws Exception {
        Spectrum spectrum = SpectraUtil.convertStringToSpectrum("127:12 128:12.12 129:123", SpectraType.MS);

        assertTrue(spectrum.getIons().contains(new Ion(127,12)));
        assertTrue(spectrum.getIons().contains(new Ion(128,12.12)));
        assertTrue(spectrum.getIons().contains(new Ion(129,123)));
    }
}
