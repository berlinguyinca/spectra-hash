package edu.ucdavis.fiehnlab.spectra.hash.core.io;

import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectraType;
import edu.ucdavis.fiehnlab.spectra.hash.core.util.SpectraUtil;

import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * reads a spectrum from the file input stream and notifies our spectrum handler
 */
public class SpectrumReader {

    private Logger logger = Logger.getLogger(getClass().getName());

    /**
     * read a spectrum and notify our handler
     *
     * @param reader
     * @param handler
     */
    public void readSpectrum(Reader reader, SpectraHandler handler, SpectraType spectraType) throws IOException {
        Scanner scanner = new Scanner(reader);

        handler.begin();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            String origin = "unknown";

            //in case some people use several spaces instead of a tab...
            line = line.replaceAll(" {2,}", "\t");

            //in case people use csv instead of a tab
            line = line.replaceAll(",", "\t");

            if (line.contains("\t")) {
                String t[] = line.split("\t");

                origin = t[0];
                line = t[1];

                if (t.length == 3) {
                    origin = origin + "_" + t[2];
                }
            }

            if (line.contains(":") && line.contains(" ")) {

                Spectrum spectrum = SpectraUtil.convertStringToSpectrum(line,spectraType,origin);

                handler.handle(spectrum);
            }
        }

        handler.done();
    }

}
