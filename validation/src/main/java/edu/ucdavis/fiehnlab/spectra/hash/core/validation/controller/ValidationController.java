package edu.ucdavis.fiehnlab.spectra.hash.core.validation.controller;

import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import edu.ucdavis.fiehnlab.spectra.hash.core.Splash;
import edu.ucdavis.fiehnlab.spectra.hash.core.SplashFactory;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.SplashListener;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.SplashingEvent;
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
     * utilized format for the output of strings
     */
    private String format = "%1$30s";

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

            formatter.printHelp("splash", "\n\nplease use the following options\n\n", options, "\n\n", true);

            e.printStackTrace(System.out);
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
    private int processFile(CommandLine cmd, String seperator, int columnSplash, int columnSpectra, int columnOrigin, PrintStream stream, SpectraType msType) throws Exception {


        if (cmd.hasOption("create")) {
            status(cmd, "splashing your data...\n");

        } else {
            status(cmd, "validating your splashes...\n");

        }
        File inputFile = null;
        try {
            inputFile = new File(cmd.getArgs()[0]);

            status(cmd, "reading file: " + inputFile + "\n");

        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            throw new ParseException("please provide an input file, as first argument");
        }
        Scanner scanner = new Scanner(inputFile);

        long time = System.currentTimeMillis();
        int counter = 0;
        int counterValid = 0;
        int interval = 10000;
        while (scanner.hasNextLine()) {

            counter++;
            String line = scanner.nextLine();

            if (!line.isEmpty()) {

                try {
                    String[] columns = line.split(seperator);


                    String spectra = null;

                    try {
                        spectra = columns[columnSpectra - 1];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new ParseException("sorry, we did not find a spectra, did you specify the right column?");
                    }

                    String origin = "unknown";

                    if (columnOrigin != -1) {
                        try {
                            origin = columns[columnOrigin - 1];
                        } catch (ArrayIndexOutOfBoundsException e) {
                            throw new ParseException("sorry, we did not find an origin, did you specify the right column?");
                        }

                    }


                    if (!cmd.hasOption("create")) {
                        String splash = null;

                        try {
                            splash = columns[columnSplash - 1];
                        } catch (ArrayIndexOutOfBoundsException e) {
                            throw new ParseException("sorry, we did not find a splash, did you specify the right column?");
                        }

                        boolean valid = validateIt(splash, spectra, origin, msType, stream, seperator, cmd);

                        if (valid) {
                            counterValid++;
                        }
                        if (counter % interval == 0) {
                            status(cmd, "splashes valid: " + (double) counterValid / (double) counter * 100 + "%, ");
                        }

                    } else {

                        if (counter % interval == 0) {
                            status(cmd, "splashing, ");
                        }
                        splashIt(spectra, origin, msType, stream, seperator, cmd);
                    }

                    if ((counter % interval) == 0) {
                        status(cmd, "processed " + counter + " spectra, " + (double) (System.currentTimeMillis() - time) / (double) counter + " ms average time to splash a spectra\n");
                    }
                } catch (Exception e) {
                    if (cmd.hasOption("ignoreErrorsSuppressed")) {

                        //nothign to see here
                    } else if (cmd.hasOption("ignoreErrors")) {
                        status(cmd, "encountered error, ignoring it!\n");
                        status(cmd, "error was: " + e.getMessage() + "\n");
                        status(cmd, "line was: " + line + "\n");
                    } else {
                        throw e;
                    }
                }
            }
        }

        stream.flush();
        stream.close();

        return counter;

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


        String code = SplashUtil.splash(spectra, msType, new Listener(cmd));


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

        if (cmd.hasOption("debug") && !valid) {

            status(cmd, String.format(format, "reference validation result") + "\n");

            //splash it again, this time with a listener
            code = SplashUtil.splash(spectra, msType, new Listener(cmd));

            status(cmd, String.format(format, "valid: ") + valid + "\n");

            String[] reference = code.split("-");
            String[] provided = splash.split("-");

            status(cmd, String.format(format, "reference: ") + code + "\n");
            status(cmd, String.format(format, "provided: ") + splash + "\n");
            status(cmd, String.format(format, "origin: ") + origin + "\n");


            status(cmd, String.format(format, "first block identical: ") + reference[0].equals(provided[0]) + "\n");
            status(cmd, String.format(format, "second block identical: ") + reference[1].equals(provided[1]) + "\n");
            status(cmd, String.format(format, "third block identical: ") + reference[2].equals(provided[2]) + "\n");
            status(cmd, String.format(format, "fourth block identical: ") + reference[3].equals(provided[3]) + "\n");
            status(cmd, "\n");
        }

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

        //options.addOption("D", "duplicates", false, "only output discovered duplicates, careful it can be slow!");
        //options.addOption("S", "sort", true, "sorts the output by given column. Columns can be 'splash' or 'origin', careful it can be slow!");
        options.addOption("X", "debug", false, "displays additional debug information, cut to 50 char for strings");
        options.addOption("XX", "debugExact", false, "displays additional debug information, complete printout");


        options.addOption("c", "create", false, "computes a validation file with the default splash implementation, instead of validation the file");
        options.addOption("t", "type", true, "what kind of spectra type is it, options is MS | IR | UV | NMR | RAMAN");


        options.addOption("T", "separator", true, "what is the separator between columns");
        options.addOption("O", "output", false, "output will be system out, instead of a file");
        options.addOption("I", "ignoreErrors", false, "errors in spectra, will be ignored, but displayed");
        options.addOption("IS", "ignoreErrorsSuppressed", false, "errors in spectra, will be ignored and not shown");


        return options;
    }


    /**
     * a simple listener for debug purposes
     */
    class Listener implements SplashListener {

        public Listener(CommandLine cmd) {
            this.cmd = cmd;
        }

        private CommandLine cmd;

        /**
         * in case we have
         *
         * @param e
         */
        public void eventReceived(SplashingEvent e) {

            if (cmd.hasOption("debugExact")) {
                status(cmd, String.format(format, e.getBlock() + " raw : ") + e.getRawValue() + "\n");
                status(cmd, String.format(format, e.getBlock() + " processed : ") + e.getProcessedValue() + "\n");
            } else if (cmd.hasOption("debug")) {
                status(cmd, String.format(format, e.getBlock() + " raw : ") + e.getRawValue().substring(0, Math.min(e.getRawValue().length(), 90)) + "\n");
                status(cmd, String.format(format, e.getBlock() + " processed : ") + e.getProcessedValue().substring(0, Math.min(e.getProcessedValue().length(), 90)) + "\n");
            }
        }

        public void complete(Spectrum spectrum, String splash) {
            if (cmd.hasOption("debugExact") | cmd.hasOption("debug")) {
                status(cmd, "\n\n");
            }
        }
    }
}
