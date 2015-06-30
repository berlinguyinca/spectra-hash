package edu.ucdavis.fiehnlab.spectra.hash.core.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.security.NoSuchAlgorithmException;

/**
 * provides us with different HashCode impls
 */
public class Hasher {

    private Hasher() {

    }

    /**
     * generates a new instance of a hasher
     *
     * @return
     */
    public static Hasher createInstance() throws NoSuchAlgorithmException {
        Hasher hasher = new Hasher();
        return hasher;
    }

    /**
     * hashes the given string
     *
     * @param o
     * @return
     */
    public String hash(Object o) {
        return DigestUtils.sha1Hex(o.toString());
    }
}
