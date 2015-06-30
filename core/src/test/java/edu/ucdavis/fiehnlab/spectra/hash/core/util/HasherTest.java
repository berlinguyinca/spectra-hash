package edu.ucdavis.fiehnlab.spectra.hash.core.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by wohlg_000 on 6/30/2015.
 */
public class HasherTest {

    @Test
    public void testHash() throws Exception {

        String content = Hasher.createInstance().hash("test");

        assertEquals("A94A8FE5CCB19BA61C4C0873D391E987982FBBD3".toLowerCase(),content);
    }
}