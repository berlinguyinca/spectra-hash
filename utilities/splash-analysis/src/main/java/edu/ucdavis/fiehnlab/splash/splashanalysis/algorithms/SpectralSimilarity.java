package edu.ucdavis.fiehnlab.splash.splashanalysis.algorithms;

import java.util.Map;

/**
 * Created by sajjan on 10/27/15.
 */
public class SpectralSimilarity {
    public static double dotProductSimilarity(Map<Integer, Double> a, Map<Integer, Double> b) {
        return transformedDotProductSimilarity(a, b, 0, 1);
    }

    public static double steinProductSimilarity(Map<Integer, Double> a, Map<Integer, Double> b) {
        return transformedDotProductSimilarity(a, b, 1.3, 0.53);
    }

    /**
     * http://www.ncbi.nlm.nih.gov/pmc/articles/PMC3418476/
     * @param a
     * @param b
     * @param n
     * @param m
     * @return
     */
    public static double transformedDotProductSimilarity(Map<Integer, Double> a, Map<Integer, Double> b, double n, double m) {
        double numerator = 0, normA = 0, normB = 0;

        for(Integer k : a.keySet()) {
            normA += Math.pow(Math.pow(k / 1000.0, n) * Math.pow(a.get(k), m), 2);
        }

        for(Integer k : b.keySet()) {
            normB += Math.pow(Math.pow(k / 1000.0, n) * Math.pow(b.get(k), m), 2);
        }

        for(Integer k : a.keySet()) {
            if(b.containsKey(k))
                numerator += Math.pow(k / 1000.0, n) * Math.pow(k / 1000.0, n) * Math.pow(a.get(k), m) * Math.pow(b.get(k), m);
        }

        return numerator / Math.sqrt(normA) / Math.sqrt(normB);
    }
}
