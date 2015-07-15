package edu.ucdavis.fiehnlab.spectra.hash.core.validation.controller;

import edu.ucdavis.fiehnlab.spectra.hash.core.Splash;
import edu.ucdavis.fiehnlab.spectra.hash.core.SplashFactory;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectraType;
import edu.ucdavis.fiehnlab.spectra.hash.core.util.SplashUtil;
import edu.ucdavis.fiehnlab.spectra.hash.core.validation.serialize.Result;
import edu.ucdavis.fiehnlab.spectra.hash.core.validation.serialize.ValidationResult;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * a controller todo the actual validation for us
 */
@Controller
public class ValidationController implements CommandLineRunner {

    private Logger logger = Logger.getLogger("validation");

    /**
     * parses our input and processes the data
     *
     * @param strings
     * @throws Exception
     */
    public void run(String... strings) throws Exception {

        Options options = getOptions();

        try {

            CommandLineParser parser = new DefaultParser();


            CommandLine cmd = parser.parse(options, strings, true);
            String seperator = ",";

            if (cmd.hasOption("separator")) {
                seperator = cmd.getOptionValue("separator");
            }


            int columnSplash = -1;

            if (cmd.hasOption("splash")) {
                columnSplash = Integer.parseInt(cmd.getOptionValue("splash"));
            } else {
                if (!cmd.hasOption("create")) {
                    throw new ParseException("please provide the information in which column the splash can be found");
                } else {
                    logger.info("in creation mode, no splash needed at this point");
                }
            }

            int columnSpectra = 0;

            if (cmd.hasOption("spectra")) {
                columnSpectra = Integer.parseInt(cmd.getOptionValue("spectra"));
            } else {
                throw new ParseException("please provide the information in which column the spectra can be found");
            }

            int columnOrigin = -1;

            if (cmd.hasOption("origin")) {
                columnOrigin = Integer.parseInt(cmd.getOptionValue("origin"));
            }

            PrintStream stream = null;

            if (cmd.hasOption("output")) {
                stream = System.out;
            } else {
                try {
                    File file = new File(cmd.getArgs()[1]);

                    logger.info("writing result to: " + file);
                    stream = new PrintStream(new FileOutputStream(file));
                } catch (IndexOutOfBoundsException e) {
                    throw new ParseException("please provide a filename for the output file");
                }
            }

            SpectraType msType = null;

            if (cmd.hasOption("type")) {

                String value = cmd.getOptionValue("type");

                if (value.toLowerCase().equals("ms")) {
                    msType = SpectraType.MS;
                } else if (value.toLowerCase().equals("nmr")) {
                    msType = SpectraType.NMR;
                } else if (value.toLowerCase().equals("uv")) {
                    msType = SpectraType.UV;
                } else if (value.toLowerCase().equals("ir")) {
                    msType = SpectraType.IR;
                } else if (value.toLowerCase().equals("raman")) {
                    msType = SpectraType.RAMAN;
                } else {
                    throw new ParseException("you provided an invalid spectra type, " + value);
                }

            } else {
                throw new ParseException("you need to provide a valid spectra type");
            }


            long time = System.currentTimeMillis();
            int hashes = processFile(cmd, seperator, columnSplash, columnSpectra, columnOrigin, stream, msType);

            //only show statistics, if we save the output in a file
            if (!cmd.hasOption("output")) {
                System.out.println("finished processing, processing took: " + (System.currentTimeMillis() - time) / 1000 / 60 + " min.");
                System.out.println("processed " + hashes + " spectra");
                System.out.println("average time including io to splash a spectra is " + (double) (System.currentTimeMillis() - time) / (double) hashes + " ms");

            } else {
                logger.info("finished processing, processing took: " + (System.currentTimeMillis() - time) / 1000 + " seconds");
                logger.info("processed " + hashes + " spectra");
                logger.info("average time including io to splash a spectra is " + (double) (System.currentTimeMillis() - time) / (double) hashes + " ms");

            }
        } catch (Exception e) {
            System.out.println("\nwe encountered an error: " + e.getMessage() + "\n");
            HelpFormatter formatter = new HelpFormatter();
            formatter.setArgName("value");

            formatter.printHelp("splash", "\n\nplease use the following options\n\n", options, "", true);
        }

    }

    /**
     * process the actual file for us
     *
     * @param cmd
     * @param seperator
     * @param columnSplash
     * @param columnSpectra
     * @param columnOrigin
     * @param stream
     * @param msType
     * @throws FileNotFoundException
     */
    private int processFile(CommandLine cmd, String seperator, int columnSplash, int columnSpectra, int columnOrigin, PrintStream stream, SpectraType msType) throws FileNotFoundException, ParseException {

        status(cmd,"processing your data...\n");
        try {
            File inputFile = new File(cmd.getArgs()[0]);


            Scanner scanner = new Scanner(inputFile);

            long time = System.currentTimeMillis();
            int counter = 0;
            int counterValid = 0;
            int interval = 10000;
            while (scanner.hasNextLine()) {

                counter++;
                String line = scanner.nextLine();

                String[] columns = line.split(seperator);


                String spectra = columns[columnSpectra - 1];
                String origin = "unknown";

                if (columnOrigin != -1) {
                    origin = columns[columnOrigin - 1];
                }

                if (!cmd.hasOption("create")) {
                    String splash = columns[columnSplash - 1];

                    boolean valid = validateIt(splash, spectra, origin, msType, stream, seperator, cmd);

                    if (valid) {
                        counterValid++;

                        if (counter % interval == 0) {
                            status(cmd, "splashes valid: " + (double)counterValid/(double)counter*100 + "%, " );
                        }
                    }
                } else {
                    splashIt(spectra, origin, msType, stream, seperator, cmd);
                }

                if ((counter % interval) == 0) {
                    status(cmd, "processed " + counter + " spectra " + (double) (System.currentTimeMillis() - time) / (double) counter + " ms average time to splash a spectra\n");
                }
            }

            stream.flush();

            return counter;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ParseException("please provide an input file, as first argument");
        }
    }

    private void status(CommandLine cmd, String message) {
        if (!cmd.hasOption("output")) {
            System.out.print(message);
        } else {
            logger.info(message);
        }
    }

    /**
     * computes new splashes
     *
     * @param spectra
     * @param origin
     * @param msType
     * @param stream
     * @param seperator
     * @param cmd
     */
    private void splashIt(String spectra, String origin, SpectraType msType, PrintStream stream, String seperator, CommandLine cmd) {


        String code = SplashUtil.splash(spectra, msType);


        serializeResult(new Result(code, spectra, origin, msType, seperator), stream, cmd);

    }

    /**
     * validates the provided splash
     *
     * @param splash
     * @param spectra
     * @param origin
     * @param msType
     * @param stream
     * @param seperator
     * @param cmd
     */
    private boolean validateIt(String splash, String spectra, String origin, SpectraType msType, PrintStream stream, String seperator, CommandLine cmd) {

        String code = SplashUtil.splash(spectra, msType);


        boolean valid = (splash.equals(code));


        serializeResult(new ValidationResult(code, spectra, origin, msType, seperator, valid, splash), stream, cmd);

        return valid;
    }

    /**
     * takes care of writing out data
     *
     * @param result
     * @param stream
     * @param cmd
     */
    private void serializeResult(Result result, PrintStream stream, CommandLine cmd) {

        if (cmd.hasOption("duplicates")) {

        } else {
            stream.println(result.toString());
        }
    }

    /**
     * list of possible options
     *
     * @return
     */
    private Options getOptions() {

        Options options = new Options();

        options.addOption("k", "splash", true, "which columns contains the splash");
        options.addOption("s", "spectra", true, "which column contains the spectra");
        options.addOption("o", "origin", true, "which column contains the origin information");

        options.addOption("d", "duplicates", false, "only output discovered, careful very very slow!");

        options.addOption("c", "create", false, "computes a validation file with the default splash implementation, instead of validation the file");
        options.addOption("t", "type", true, "what kind of spectra type is it, options is MS | IR | UV | NMR | RAMAN");


        options.addOption("T", "separator", true, "what is the separator between columns");
        options.addOption("O", "output", false, "output will be system out, instead of a file");


        return options;
    }

}
