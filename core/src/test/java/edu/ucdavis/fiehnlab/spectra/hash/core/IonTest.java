package edu.ucdavis.fiehnlab.spectra.hash.core;

import static org.junit.Assert.assertEquals;

/**
 * test case for 1 ion
 */
public class IonTest {

    @org.junit.Test
    public void testToString() throws Exception {
        Ion ion = new Ion(100.0222222,122.011111);

	    assertEquals("test if the toString method generates the right Ion representation", "100.022222:122.011111", ion.toString());
    }

    @org.junit.Test
    public void testCompare(){
        Ion a = new Ion(1,1);
        Ion b = new Ion(2,1);

        assertEquals(-1,a.compareTo(b));
    }
}