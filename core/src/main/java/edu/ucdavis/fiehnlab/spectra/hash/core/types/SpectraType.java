package edu.ucdavis.fiehnlab.spectra.hash.core.types;

/**
 * an enumaration with the different valid spectra types
 */
public enum SpectraType {
    MS('1'),
    NMR('2'),
    UV('3'),
    IR('4'),
    RAMAN('5');

    /**
     * identifier has to be a single character
     */
    private final char identifier;

    /**
     * creates a new spectra type with the assoicated reference number
     *
     * @param referenceNumber
     */
    SpectraType(char referenceNumber) {
        this.identifier = referenceNumber;
    }

    /**
     * access to it's identifier
     *
     * @return
     */
    public char getIdentifier() {
        return this.identifier;
    }
}
