package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.sort.IonComperator;
import edu.ucdavis.fiehnlab.spectra.hash.core.sort.MassThanIntensityComperator;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.Splash;
import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.SplashingEvent;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.SplashListener;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.SplashBlock;
import org.apache.commons.codec.digest.DigestUtils;

import java.lang.Math;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * the reference implementation of the Spectral Hash Key
 */
public class SplashVersion1 implements Splash {
    private static final int BINS = 10;
    private static final int BIN_SIZE = 100;

    private static final char[] INTENSITY_MAP = new char[] {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c',
            'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    private static final int FINAL_SCALE_FACTOR = 35;

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
     * @param listener
     */
    public void addListener(SplashListener listener) {
        this.listeners.add(listener);
    }

    /**
     * notify listeners
     *
     * @param e
     */
    protected void notifyListener(SplashingEvent e) {
        for (SplashListener listener : listeners) {
            listener.eventReceived(e);
        }

    }

    /**
     * notify listeneres that the hash is complete
     *
     * @param spectrum
     * @param hash
     */
    protected void notifyListenerHashComplete(Spectrum spectrum, String hash) {
        for (SplashListener listener : listeners) {
            listener.complete(spectrum, hash);
        }
    }

    /**
     * formats a m/z value to our defined fixedPrecissionOfMasses
     *
     * @param value
     * @return
     */
    String formatMZ(double value) {
        return String.format("%d", (long)((value + EPS_CORRECTION) * MZ_PRECISION_FACTOR));
    }

    /**
     * formats an intensity value to our defined fixedPrecissionOfIntensites
     *
     * @param value
     * @return
     */
    String formatIntensity(double value) {
        return String.format("%d", (long)((value + EPS_CORRECTION) * INTENSITY_PRECISION_FACTOR));
    }

    /**
     * encodes the actual spectrum
     *
     * @param spectrum
     * @return
     */
    protected String encodeSpectrum(Spectrum spectrum) {

        List<Ion> ions = spectrum.getIons();

        StringBuilder buffer = new StringBuilder();

        //sort by mass
        Collections.sort(ions, new MassThanIntensityComperator());


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
        String hash = DigestUtils.sha256Hex(block);
        this.notifyListener(new SplashingEvent(hash, block, SplashBlock.SECOND, spectrum));
        return hash;
    }


    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "-" + fixedPrecissionOfMasses;
    }

    /**
     * calculates our spectral hash
     *
     * @param spectrum
     * @return
     */
    public final String splashIt(Spectrum spectrum) {

        for (Ion ion : spectrum.getIons()) {
            if (ion.getIntensity() < 0) {
                throw new RuntimeException("ion's need to have an intensity larger than zero");
            }
            if (ion.getMass() < 0) {
                throw new RuntimeException("ion's need to have an mass larger than zero");
            }

        }

        //convert the spectrum to relative values
        spectrum = spectrum.toRelative(scalingOfRelativeIntensity);

        StringBuffer buffer = new StringBuffer();

        //first block
        buffer.append(buildFirstBlock(spectrum));
        buffer.append("-");

        //second block
        buffer.append(calculateHistogramBlock(spectrum));
        buffer.append("-");

        //third block
        buffer.append(encodeSpectrum(spectrum).substring(0, maxCharactersForSpectrumBlockTruncation));

        return buffer.toString();
    }

    private String buildFirstBlock(Spectrum spectrum) {
        String splash = "splash" + spectrum.getType().getIdentifier() + getVersion();

        this.notifyListener(new SplashingEvent(splash, splash, SplashBlock.FIRST, spectrum));

        return splash;
    }


    /**
     * @return
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
     * @param spectrum
     * @return histogram
     */
    protected String calculateHistogramBlock(Spectrum spectrum) {
        List<Double> binnedIons = new ArrayList<Double>();

        // Bin ions
        for (Ion ion : spectrum.getIons()) {
            int index = (int)(ion.getMass() / BIN_SIZE);

            // Add bins as needed
            while (binnedIons.size() <= index) {
                binnedIons.add(0.0);
            }

            double value = binnedIons.get(index) + ion.getIntensity();
            binnedIons.set(index, value);
        }

        // Wrap the histogram
        for (int i = BINS; i < binnedIons.size(); i++) {
            double value = binnedIons.get(i % BINS) + binnedIons.get(i);
            binnedIons.set(i % BINS, value);
        }

        // Normalize the histogram
        double maxIntensity = 0;

        for (int i = 0; i < BINS; i++) {
            if (i < binnedIons.size()) {
                if (binnedIons.get(i) > maxIntensity) {
                    maxIntensity = binnedIons.get(i);
                }
            } else {
                binnedIons.add(0.0);
            }
        }


        for (int i = 0; i < BINS; i++) {
            binnedIons.set(i, EPS_CORRECTION + FINAL_SCALE_FACTOR * binnedIons.get(i) / maxIntensity);
        }

        // Build histogram string
        StringBuffer result = new StringBuffer();

        for (int i = 0; i < BINS; i++) {
            int bin = (int)(EPS_CORRECTION + binnedIons.get(i));
            result.append(INTENSITY_MAP[bin]);
        }

        return result.toString();
    }
}
