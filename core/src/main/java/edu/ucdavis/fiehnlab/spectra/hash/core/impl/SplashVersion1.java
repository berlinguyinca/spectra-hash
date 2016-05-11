package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import edu.ucdavis.fiehnlab.spectra.hash.core.Splash;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.SplashBlock;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.SplashListener;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.SplashingEvent;
import edu.ucdavis.fiehnlab.spectra.hash.core.sort.IntensityThenMassComparator;
import edu.ucdavis.fiehnlab.spectra.hash.core.sort.MassThenIntensityComparator;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectraType;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectrumImpl;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * the reference implementation of the Spectral Hash Key
 */
public class SplashVersion1 implements Splash {
    private static final int PREFILTER_BASE = 3;
    private static final int PREFILTER_LENGTH = 10;
    private static final int PREFILTER_BIN_SIZE = 5;

    private static final int SIMILARITY_BASE = 10;
    private static final int SIMILARITY_LENGTH = 10;
    private static final int SIMILARITY_BIN_SIZE = 100;

    private static final char[] BASE_36_MAP = new char[] {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c',
            'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    /**
     * how to scale the spectrum
     */
    public static final int scalingOfRelativeIntensity = 100;

    /**
     * how should ions in the string representation be separeted
     */
    private static final String ION_SEPERATOR = " ";

    /**
     * how many character should be in the spectrum block. Basically this reduces the SHA256 code down
     * to a fixed length of N characater
     */
    private static final int maxCharactersForSpectrumBlockTruncation = 20;

    /**
     * Fixed precission of masses
     */
    private static final int fixedPrecissionOfMasses = 6;

    /**
     * factor to scale m/z floating point values
     */
    private static final long MZ_PRECISION_FACTOR = (long)Math.pow(10, fixedPrecissionOfMasses);

    /**
     * Fixed precission of intensites
     */
    private static final int fixedPrecissionOfIntensities = 0;

    /**
     * factor to scale m/z floating point values
     */
    private static final long INTENSITY_PRECISION_FACTOR = (long)Math.pow(10, fixedPrecissionOfIntensities);

    /**
     * Correction factor to avoid floating point issues between implementations
     * and processor architectures
     */
    private static final double EPS_CORRECTION = 1.0e-7;

    /**
     * registered listeneres
     */
    private ConcurrentLinkedDeque<SplashListener> listeners = new ConcurrentLinkedDeque<SplashListener>();

    /**
     * adds a new listener
     *
     * @param listener listener
     */
    public void addListener(SplashListener listener) {
        this.listeners.add(listener);
    }

    /**
     * notify listeners
     *
     * @param e event
     */
    protected void notifyListener(SplashingEvent e) {
        for (SplashListener listener : listeners) {
            listener.eventReceived(e);
        }

    }

    /**
     * notify listeneres that the hash is complete
     *
     * @param spectrum spectrum
     * @param hash computed splash
     */
    protected void notifyListenerHashComplete(Spectrum spectrum, String hash) {
        for (SplashListener listener : listeners) {
            listener.complete(spectrum, hash);
        }
    }

    /**
     * formats a m/z value to our defined fixedPrecissionOfMasses
     *
     * @param value m/z value
     * @return formatted m/z value
     */
    String formatMZ(double value) {
        return String.format("%d", (long)((value + EPS_CORRECTION) * MZ_PRECISION_FACTOR));
    }

    /**
     * formats an intensity value to our defined fixedPrecissionOfIntensites
     *
     * @param value intensity value
     * @return formatted intensity value
     */
    String formatIntensity(double value) {
        return String.format("%d", (long)((value + EPS_CORRECTION) * INTENSITY_PRECISION_FACTOR));
    }

    /**
     * encodes the actual spectrum
     *
     * @param spectrum spectrum
     * @return spectral hash
     */
    protected String encodeSpectrum(Spectrum spectrum) {

        List<Ion> ions = spectrum.getIons();

        StringBuilder buffer = new StringBuilder();

        //sort by mass
        Collections.sort(ions, new MassThenIntensityComparator());


        //build the first string
        for (int i = 0; i < ions.size(); i++) {
            buffer.append(formatMZ(ions.get(i).getMass()));
            buffer.append(":");
            buffer.append(formatIntensity(ions.get(i).getIntensity()));

            //add our separator
            if (i < ions.size() - 1) {
                buffer.append(ION_SEPERATOR);
            }
        }


        //notify observers in case they want to know about progress of the hashing
        String block = buffer.toString();
        String hash = DigestUtils.sha256Hex(block).substring(0, maxCharactersForSpectrumBlockTruncation);
        this.notifyListener(new SplashingEvent(hash, block, SplashBlock.THIRD, spectrum));
        return hash;
    }


    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "-" + fixedPrecissionOfMasses;
    }

    /**
     * calculates our spectral hash
     *
     * @param spectrum spectrum
     * @return computed splash
     */
    public final String splashIt(Spectrum spectrum) {
        if (spectrum.getType() == SpectraType.MS) {
            for (Ion ion : spectrum.getIons()) {
                if (ion.getIntensity() < 0) {
                    throw new RuntimeException("ion's need to have an intensity larger than zero");
                }
                if (ion.getMass() < 0) {
                    throw new RuntimeException("ion's need to have an mass larger than zero");
                }
            }
        }

        //convert the spectrum to relative values
        spectrum = spectrum.toRelative(scalingOfRelativeIntensity);

        StringBuilder buffer = new StringBuilder();

        //first block
        buffer.append(buildFirstBlock(spectrum));
        buffer.append("-");

        //prefilter block
        Spectrum filteredSpectrum = filterSpectrum(spectrum, 10, 0.1);
        String prefilterHistogram = calculateHistogramBlock(filteredSpectrum, PREFILTER_BASE, PREFILTER_LENGTH, PREFILTER_BIN_SIZE);

        buffer.append(translateBase(prefilterHistogram, PREFILTER_BASE, 36, 4));
        buffer.append("-");

        //similarity block
        buffer.append(calculateHistogramBlock(spectrum, SIMILARITY_BASE, SIMILARITY_LENGTH, SIMILARITY_BIN_SIZE));
        buffer.append("-");

        //hash block
        buffer.append(encodeSpectrum(spectrum));

        return buffer.toString();
    }

    private String buildFirstBlock(Spectrum spectrum) {
        String splash = "splash" + spectrum.getType().getIdentifier() + getVersion();

        this.notifyListener(new SplashingEvent(splash, splash, SplashBlock.FIRST, spectrum));

        return splash;
    }


    /**
     * @return splash version
     */
    protected char getVersion() {
        return '0';
    }

    /**
     * Calculates a spectral histogram using the following steps:
     *   1. Bin spectrum into a histogram based on BIN_SIZE, extending the
     *      histogram size as needed to accommodate large m/z values
     *   2. Normalize the histogram, scaling to INITIAL_SCALE_FACTOR
     *   3. Wrap the histogram by summing normalized intensities to reduce
     *      the histogram to BINS bins
     *   4. Normalize the reduced histogram, scaling to FINAL_SCALE_FACTOR
     *   5. Convert/truncate each intensity value to a 2-digit integer value,
     *      concatenated into the final string histogram representation
     *
     * @param spectrum spectrum
     * @return histogram
     */
    protected String calculateHistogramBlock(Spectrum spectrum, int base, int length, int binSize) {
        double[] binnedIons = new double[length];

        double maxIntensity = 0;

        // Bin ions using the histogram wrapping strategy
        for (Ion ion : spectrum.getIons()) {
            int bin = (int)(ion.getMass() / binSize) % length;
            binnedIons[bin] += ion.getIntensity();

            if (binnedIons[bin] > maxIntensity) {
                maxIntensity = binnedIons[bin];
            }
        }

        // Normalize the histogram and scale to the provided base
        for (int i = 0; i < length; i++) {
            binnedIons[i] = (base - 1) * binnedIons[i] / maxIntensity;
        }

        // Build histogram string
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int bin = (int)(EPS_CORRECTION + binnedIons[i]);
            result.append(BASE_36_MAP[bin]);
        }


        String data = result.toString();

        this.notifyListener(new SplashingEvent(data, data, SplashBlock.SECOND, spectrum));

        return result.toString();
    }


    /**
     * Filters spectrum by number of highest abundance ions
     * @param s spectrum
     * @param topIons number of top ions to retain
     * @return filtered spectrum
     */
    protected Spectrum filterSpectrum(Spectrum s, int topIons) {
        return filterSpectrum(s, topIons, -1);
    }

    /**
     * Filters spectrum by base peak percentage
     * @param s spectrum
     * @param basePeakPercentage percentage of base peak above which to retain
     * @return filtered spectrum
     */
    protected Spectrum filterSpectrum(Spectrum s, double basePeakPercentage) {
        return filterSpectrum(s, -1, basePeakPercentage);
    }

    /**
     * Filters spectrum by number of highest abundance ions and by base peak percentage
     * @param s spectrum
     * @param topIons number of top ions to retain
     * @param basePeakPercentage percentage of base peak above which to retain
     * @return filtered spectrum
     */
    protected Spectrum filterSpectrum(Spectrum s, int topIons, double basePeakPercentage) {
        List<Ion> ions = s.getIons();

        // Find base peak intensity
        double basePeakIntensity = 0.0;

        for(Ion ion: ions) {
            if (ion.getIntensity() > basePeakIntensity)
                basePeakIntensity = ion.getIntensity();
        }

        // Filter by base peak percentage if needed
        if (basePeakPercentage >= 0) {
            List<Ion> filteredIons = new ArrayList<Ion>();

            for(Ion ion : ions) {
                if (ion.getIntensity() + EPS_CORRECTION >= basePeakPercentage * basePeakIntensity)
                    filteredIons.add(new Ion(ion.getMass(), ion.getIntensity()));
            }

            ions = filteredIons;
        }

        // Filter by top ions if necessary
        if (topIons > 0 && ions.size() > topIons) {
            Collections.sort(ions, new IntensityThenMassComparator());

            ions = ions.subList(0, topIons);
        }

        return new SpectrumImpl(ions, s.getOrigin(), s.getType());
    }

    /**
     * Translate a number in string format from one numerical base to another
     * @param number number in string format
     * @param initialBase base in which the given number is represented
     * @param finalBase base to translate the number to, up to 36
     * @param fill minimum length of string
     */
    protected String translateBase(String number, int initialBase, int finalBase, int fill) {
        int n = Integer.parseInt(number, initialBase);

        StringBuilder result = new StringBuilder();

        while (n > 0) {
            result.insert(0, BASE_36_MAP[n % finalBase]);
            n /= finalBase;
        }

        while (result.length() < fill) {
            result.insert(0, '0');
        }

        return result.toString();
    }
}
