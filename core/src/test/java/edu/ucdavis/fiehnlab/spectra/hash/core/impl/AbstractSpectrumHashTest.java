package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by diego on 7/3/15.
 */
public class AbstractSpectrumHashTest {

	/**
	 * test different implementations of the hashing algo
	 * with spectrum: (100, 1) (101, 2) (102, 3)
	 *
	 * @param impl
	 */
	public void testDefault(AbstractSpectralHash impl, String expected) {
		Spectrum spectrum = new SpectrumImpl(Arrays.asList(new Ion(100, 1), new Ion(101, 2), new Ion(102, 3)), "mona");

		String hash = impl.generate(spectrum);

		System.out.println(hash);
		System.out.println(hash.length());

		assertEquals(expected, hash);

	}
}
