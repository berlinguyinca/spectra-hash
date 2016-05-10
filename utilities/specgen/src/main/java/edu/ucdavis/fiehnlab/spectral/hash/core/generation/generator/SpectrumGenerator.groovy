package edu.ucdavis.fiehnlab.spectral.hash.core.generation.generator

import edu.ucdavis.fiehnlab.spectra.hash.core.types.*

/**
 * Created by diego on 7/21/15.
 */
class SpectrumGenerator {
	private List<String> spectra = new ArrayList<String>()

	public List<String> getSpectra() {
		return this.spectra
	}

	public int generate(boolean accMz, int peaks, int specs, long seed) {
		int c = 0

		while(c < specs) {
			def ions
			if(seed) {
				ions = createSpectrum(accMz, peaks, seed+c)
			} else {
				ions = createSpectrum(accMz, peaks, System.nanoTime())
			}
//			println "ions: ${ions}"
			spectra.add(ions)
			c++
		}
//		println "spectra: ${spectra.toListString()}"

		return spectra.size()
	}

	private String createSpectrum(boolean accMz, int peaks, long seed) {
		List<Ion> ions = new ArrayList<Ion>()
		StringBuilder sIons= new StringBuilder()
		Random rnd = new Random(seed)

		int p=0

		while(p < peaks * 1.2) {
			Ion ion
			if(accMz) {
				ion = new Ion(rnd.nextDouble() * 2000, Math.abs(rnd.nextGaussian() * 100))
			} else {
				ion = new Ion(rnd.nextInt(2000), Math.abs(rnd.nextGaussian() * 100).intValue())
			}
			ions.add(ion)

			p++
		}
		ions.sort()

		ions.each { ion ->
			sIons.append(ion).append(" ")
		}

//		println "sorted spectra: ${ions.toListString()}"

		return sIons.toString().trim()
	}
}
