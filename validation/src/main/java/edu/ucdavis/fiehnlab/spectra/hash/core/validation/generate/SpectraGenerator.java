package edu.ucdavis.fiehnlab.spectra.hash.core.validation.generate;

import edu.ucdavis.fiehnlab.spectra.hash.core.Splash;
import edu.ucdavis.fiehnlab.spectra.hash.core.SplashFactory;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectraType;
import edu.ucdavis.fiehnlab.spectra.hash.core.util.SplashUtil;
import edu.ucdavis.fiehnlab.spectra.hash.core.validation.serialize.Result;
import edu.ucdavis.fiehnlab.spectra.hash.core.validation.serialize.Serializer;
import org.apache.commons.cli.CommandLine;

import java.util.Arrays;
import java.util.Iterator;

/**
 * simple generator to provide us with spectra
 */
public class SpectraGenerator {

    /**
     * generates n spectra for us
     *
     * @param type
     * @param count
     * @param seperator
     * @param cmd
     */
    public int generate(SpectraType type, int count, String seperator, CommandLine cmd, Serializer serializer) throws Exception {

        String spectra = "1:1 2:2";
        String splash = SplashUtil.splash(spectra, type);

        serializer.serialize(new Result(splash, spectra, "generated", type, seperator));
        return 1;
    }
}
