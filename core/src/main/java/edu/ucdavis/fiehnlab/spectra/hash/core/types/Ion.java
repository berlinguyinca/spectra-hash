package edu.ucdavis.fiehnlab.spectra.hash.core.types;

/**
 * defines a basic ion for a spectra key
 */
public class Ion implements Comparable<Ion>{
    private static String SEPERATOR = ":";
    private static int PRECESSION = 6;

    private Double mass;

    public Ion(){}

    public Ion(double mass, double intensity) {
        this.mass = mass;
        this.intensity = intensity;
    }

    public Double getIntensity() {
        return intensity;
    }

    public void setIntensity(Double intensity) {
        this.intensity = intensity;
    }

    public Double getMass() {
        return mass;
    }

    public void setMass(Double mass) {
        this.mass = mass;
    }

    private Double intensity;

    public String toString(){
        return String.format("%."+PRECESSION+"f",this.getMass()) + SEPERATOR + String.format("%." + PRECESSION + "f", this.getIntensity());
    }

    public int compareTo(Ion o) {
        return getMass().compareTo(o.getMass());
    }
}
