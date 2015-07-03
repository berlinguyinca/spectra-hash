package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.SpectraHash;
import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.HashingEvent;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.HashingListener;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 *
 */
public abstract class AbstractSpectralHash implements SpectraHash{

    private Integer precission;


    /**
     * registered listeneres
     */
    private ConcurrentLinkedDeque<HashingListener> listeners = new ConcurrentLinkedDeque<HashingListener>();

    /**
     * adds a new listener
     * @param listener
     */
    public void addListener(HashingListener listener) {
        this.listeners.add(listener);
    }

    /**
     * notify listeners
     * @param e
     */
    protected void notifyListener(HashingEvent e) {
        for (HashingListener listener : listeners) {
            listener.eventReceived(e);
        }

    }

    /**
     * notify listeneres that the hash is complete
     * @param spectrum
     * @param hash
     */
    protected void notifyListenerHashComplete(Spectrum spectrum, String hash) {
        for (HashingListener listener : listeners) {
            listener.hashingComplete(spectrum, hash);
        }
    }


    /**
     * encodes the actual spectra
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
            buffer.append(String.format("%."+getPrecission() +"f", ions.get(i).getMass()));
            buffer.append(":");
            buffer.append(String.format("%."+getPrecission() +"f", ions.get(i).getIntensity()));

            //add our separator
            if (i < ions.size() - 1) {
                buffer.append(" ");
            }
        }


        //notify obsers in case they want to know about progress of the hashing
        String block = buffer.toString();
        String hash = DigestUtils.sha256Hex(block);
        this.notifyListener(new HashingEvent(hash, block, 1, spectrum));
        return hash;
    }

    /**
     * encodes the top ten ions
     * @param spectrum
     * @return
     */
    protected String encodeTop10Ions(Spectrum spectrum) {
        StringBuilder buffer = new StringBuilder();
        List<Ion> ions = spectrum.getIons();

        //sort by intensity
        Collections.sort(ions, new Comparator<Ion>() {
            public int compare(Ion o1, Ion o2) {
                return o2.getIntensity().compareTo(o1.getIntensity());
            }
        });

        //build the second string
        for (int i = 0; i < ions.size(); i++) {
            buffer.append(String.format("%.6f", ions.get(i).getMass()));

            if (i == 10) {
                //we only want top 10 ions
                break;
            }

            if (i < ions.size() - 1) {
                buffer.append(",");
            }

        }

        String block = buffer.toString();
        String hash = DigestUtils.sha1Hex(block);
        this.notifyListener(new HashingEvent(hash, block, 0, spectrum));
        return hash;
    }


    /**
     * get version of the string
     * @return
     */
    protected String getVersion() {
        return "0";
    }

    public Integer getPrecission() {
        return precission;
    }

    public void setPrecission(Integer precission) {
        this.precission = precission;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() +"/" + precission;
    }
}
