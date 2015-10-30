package edu.ucdavis.fiehnlab.splash.splashanalysis.algorithms;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sajjan on 10/27/15.
 */
public class HistogramSimilarity {
    private static int HISTOGRAM_BASE = 36;


    private static int[] histogramStringToInt(String histogram) {
        int[] bins = new int[histogram.length()];

        for(int i = 0; i < histogram.length(); i++) {
            bins[i] = Integer.valueOf(String.valueOf(histogram.charAt(i)), HISTOGRAM_BASE);
        }

        return bins;
    }


    public static int manhattanDistance(String a, String b) {
        assert(a.length() == b.length());

        int[] binsA = histogramStringToInt(a);
        int[] binsB = histogramStringToInt(b);

        int distance = 0;

        for(int i = 0; i < a.length(); i++) {
            distance += Math.abs(binsA[i] - binsB[i]);
        }

        return distance;
    }

    public static int cyclicManhattanDistance(String a, String b) {
        assert(a.length() == b.length());

        int[] binsA = histogramStringToInt(a);
        int[] binsB = histogramStringToInt(b);

        int distance = 0;

        for(int i = 0; i < a.length(); i++) {
            distance += Math.min(
                    Math.abs(binsA[i] - binsB[i]),
                    Math.min(
                            Math.abs(binsA[i] + HISTOGRAM_BASE - binsB[i]),
                            Math.abs(binsA[i] - binsB[i] - HISTOGRAM_BASE)
                    )
            );
        }

        return distance;
    }

    public static int levenshteinDistance(String a, String b) {
        assert(a.length() == b.length());

        int[] binsA = histogramStringToInt(a);
        int[] binsB = histogramStringToInt(b);

        int distance = 0;

        for(int i = 0; i < a.length(); i++) {
            if(binsA[i] != binsB[i])
                distance++;
        }

        return distance;
    }

    public static double chiSquaredDistance(String a, String b) {
        assert(a.length() == b.length());

        int[] binsA = histogramStringToInt(a);
        int[] binsB = histogramStringToInt(b);

        double sumA = 0, sumB = 0;
        double distance = 0;

        for(int i = 0; i < a.length(); i++) {
            sumA += binsA[i];
            sumB += binsB[i];
        }

        for(int i = 0; i < a.length(); i++) {
            double binA = binsA[i] / sumA;
            double binB = binsB[i] / sumB;

            if(binA + binB > 0)
                distance += Math.pow(binA - binB, 2) / (binA + binB);
        }

        return distance / 2.0;
    }

    public static double bhattacharyyaDistance(String a, String b) {
        assert(a.length() == b.length());

        int[] binsA = histogramStringToInt(a);
        int[] binsB = histogramStringToInt(b);

        double sumA = 0, sumB = 0;
        double distance = 0;

        for(int i = 0; i < a.length(); i++) {
            sumA += binsA[i];
            sumB += binsB[i];
        }

        for(int i = 0; i < a.length(); i++)
            distance += Math.sqrt((binsA[i] / sumA) * (binsB[i] / sumB));

        return (distance > 0 ? -Math.log(distance) : 0);
    }

    public static double dotProductSimilarity(String a, String b) {
        assert(a.length() == b.length());

        Map<Integer, Double> mapA = new HashMap<Integer, Double>();
        Map<Integer, Double> mapB = new HashMap<Integer, Double>();

        int[] binsA = histogramStringToInt(a);
        int[] binsB = histogramStringToInt(b);

        for(int i = 0; i < a.length(); i++) {
            mapA.put(i, (double)binsA[i]);
            mapB.put(i, (double)binsB[i]);
        }

        return SpectralSimilarity.dotProductSimilarity(mapA, mapB);
    }
}
