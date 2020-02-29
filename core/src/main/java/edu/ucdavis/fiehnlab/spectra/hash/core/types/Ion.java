package edu.ucdavis.fiehnlab.spectra.hash.core.types;

/**
 * defines a basic ion for a spectra key
 */
public class Ion implements Comparable<Ion> {
    private static String SEPERATOR = ":";
    private static int PRECESSION = 6;

    private Double mass;
    private Double intensity;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ion)) return false;

        Ion ion = (Ion) o;

        if (!getMass().equals(ion.getMass())) return false;
        return getIntensity().equals(ion.getIntensity());

    }

    @Override
    public int hashCode() {
        int result = getMass().hashCode();
        result = 31 * result + getIntensity().hashCode();
        return result;
    }

    public Ion() {}

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

    public String toString() {
        return String.format("%."+PRECESSION+"f",this.getMass()) + SEPERATOR + String.format("%." + PRECESSION + "f", this.getIntensity());
    }

    public int compareTo(Ion o) {
        return getMass().compareTo(o.getMass());
    }
}
