package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.sort.IonComperator;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.Splash;
import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.SplashingEvent;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.SplashListener;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.SplashBlock;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * the reference implementation of the Spectral Hash Key
 */
public final class SplashVersion1 implements Splash {

    /**
     * how to scale the spectra
     */
    public static final int scalingOfRelativeIntensity = 1000;

    /**
     * how should ions in the string representation be separeted
     */
    private static final String ION_SEPERATOR = " ";

    /**
     * how many character should be in the top ion block. Basically this reduces the SHA256 code down
     * to a fixed length of N characater
     */
    private static final int maxCharactertop10IonBlockTruncation = 10;

    /**
     * how many character should be in the spectra block. Basically this reduces the SHA256 code down
     * to a fixed length of N characater
     */
    private static final int maxCharactersForSpectraBlockTruncation = 20;

    /**
     * max fixedPrecissionOfMassesAndIntensities
     */
    private static final int fixedPrecissionOfMassesAndIntensities = 6;

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
        return String.format("%." + fixedPrecissionOfMassesAndIntensities + "f", value);
    }

    /**
     * encodes the actual spectra
     *
     * @param spectrum
     * @return
     */
    protected String encodeSpectra(Spectrum spectrum) {

        List<Ion> ions = spectrum.getIons();

        StringBuilder buffer = new StringBuilder();

        //sort by mass
        Collections.sort(ions, new Comparator<Ion>() {
            public int compare(Ion o1, Ion o2) {
                return o1.getMass().compareTo(o2.getMass());
            }
        });


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
        this.notifyListener(new SplashingEvent(hash, block, SplashBlock.THIRD, spectrum));
        return hash;
    }

    /**
     * encodes the top ten ions
     *
     * @param spectrum
     * @return
     */
    protected String encodeTop10Ions(Spectrum spectrum) {
        StringBuilder buffer = new StringBuilder();
        List<Ion> ions = spectrum.getIons();

        //sort by intensity max to min, secondary will be larger mass to smaller mass for identical intensities
        Collections.sort(ions, new IonComperator());

        //build the second string
        for (int i = 0; i < ions.size(); i++) {
            buffer.append(formatNumber(ions.get(i).getMass()));

            if (i == 10 - 1) {
                //we only want top 10 ions
                break;
            }

            if (i < ions.size() - 1) {
                buffer.append(ION_SEPERATOR);
            }

        }

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
        //second block

        buffer.append(encodeTop10Ions(spectrum).substring(0, maxCharactertop10IonBlockTruncation));
        buffer.append("-");

        //third block

        buffer.append(encodeSpectra(spectrum).substring(0, maxCharactersForSpectraBlockTruncation));
        buffer.append("-");

        //forth block
        buffer.append(calculateSum(spectrum));

        return buffer.toString();
    }

    private String buildFirstBlock(Spectrum spectrum) {
        String splash = "splash" + spectrum.getType().getIdentifier() + getVersion();

        this.notifyListener(new SplashingEvent(splash, splash, SplashBlock.FIRST, spectrum));

        return splash;
    }

    /**
     * calculates a total sum, with no digits, padded by 10 digits
     *
     * @param spectrum
     * @return
     */
    protected String calculateSum(Spectrum spectrum) {
        int ionCount = 0;

        double hashSum = 0.0;

        List<Ion> ions = spectrum.getIons();

        //sort by intensity max to min
        Collections.sort(ions, new IonComperator());

        for (Ion ion : ions) {
            hashSum += ion.getMass() * ion.getIntensity();

            ionCount++;
            if (ionCount > calculatedSumMaxIonsCount - 1) break;
        }

        String sum = String.format("%0" + calculatedSumMaxDigitPadding + "d", (long)hashSum);

        this.notifyListener(new SplashingEvent(sum, String.format("%d", (long)hashSum), SplashBlock.FOURTH, spectrum));

        return sum;
    }


    /**
     * @return
     */
    protected char getVersion() {
        return '0';
    }
}
