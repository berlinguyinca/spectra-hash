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
    int BINS = 10;
    int BIN_SIZE = 100;
    int FINAL_SCALE_FACTOR = 35;

    char[] INTENSITY_MAP = new char[] {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
            'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };



    /**
     * how to scale the spectrum
     */
    public static final int scalingOfRelativeIntensity = 1000;

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
     * max fixedPrecissionOfMassesAndIntensities
     */
    private static final int fixedPrecissionOfMassesAndIntensities = 6;

    /**
     * factor to scale floating point values
     */
    private static final long PRECISION_FACTOR = (long)Math.pow(10, fixedPrecissionOfMassesAndIntensities);

    /**
     * max amount of padding for the sum
     */
    private static final int calculatedSumMaxDigitPadding = 10;

    /**
     * max amount of ions
     */
    private static final int calculatedSumMaxIonsCount = 100;

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
     * formats a number to our defined fixedPrecissionOfMassesAndIntensities
     *
     * @param value
     * @return
     */
    String formatNumber(double value) {
        return String.format("%d", (long)(value * PRECISION_FACTOR));
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
            buffer.append(formatNumber(ions.get(i).getMass()));
            buffer.append(":");
            buffer.append(formatNumber(ions.get(i).getIntensity()));

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
        return this.getClass().getSimpleName() + "-" + fixedPrecissionOfMassesAndIntensities;
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

        //third block
        buffer.append(encodeSpectrum(spectrum).substring(0, maxCharactersForSpectrumBlockTruncation));
        buffer.append("-");

        //forth block
        buffer.append(calculateHistogramBlock(spectrum));

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

        // Max intensity value
        double maxIntensity = 0;

        // Bin ions
        for (Ion ion : spectrum.getIons()) {
            int index = (int)(ion.getMass() / BIN_SIZE);

            // Add bins as needed
            while (binnedIons.size() <= index) {
                binnedIons.add(0.0);
            }

            double value = binnedIons.get(index) + ion.getIntensity();
            binnedIons.set(index, value);

            if (value > maxIntensity) {
                maxIntensity = value;
            }
        }

        // Wrap the histogram
        for (int i = BINS; i < binnedIons.size(); i++) {
            double value = binnedIons.get(i % BINS) + binnedIons.get(i);
            binnedIons.set(i % BINS, value);
        }

        // Normalize the histogram
        maxIntensity = 0;

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
            binnedIons.set(i, FINAL_SCALE_FACTOR * binnedIons.get(i) / maxIntensity);
        }

        // Build histogram string
        StringBuffer result = new StringBuffer();

        for (int i = 0; i < BINS; i++) {
            result.append(INTENSITY_MAP[binnedIons.get(i).intValue()]);
        }

        return result.toString();
    }
}
