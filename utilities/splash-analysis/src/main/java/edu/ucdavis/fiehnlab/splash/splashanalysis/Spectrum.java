package edu.ucdavis.fiehnlab.splash.splashanalysis;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sajjan on 10/27/15.
 */
public class Spectrum {
    public String origin;
    public String spectrum;
    public Map<Integer, Double> nominalBinnedSpectrum;
    public Map<Integer, Double> accurateBinnedSpectrum;
    public String hash;
    public String shortHistogram;
    public String longHistogram;
    public long sum;
    public long preciseSum;


    public Spectrum(String input) {
        String[] data = input.split(",");

        this.origin = data[0];
        this.spectrum = data[1];
        this.nominalBinnedSpectrum = this.parseBinnedSpectrumString(data[2]);
        this.accurateBinnedSpectrum = this.parseBinnedSpectrumString(data[3]);
        this.hash = data[4];
        this.shortHistogram = data[5];
        this.longHistogram = data[6];
        this.sum = Long.parseLong(data[7]);
        this.preciseSum = Long.parseLong(data[8]);
    }

    public Map<Integer, Double> parseBinnedSpectrumString(String s) {
        Map<Integer, Double> data = new HashMap<Integer, Double>();

        for(String ion : s.split(" ")) {
            String[] x = ion.split(":");
            data.put(Integer.parseInt(x[0]), Double.parseDouble(x[1]));
        }

        return data;
    }
}
