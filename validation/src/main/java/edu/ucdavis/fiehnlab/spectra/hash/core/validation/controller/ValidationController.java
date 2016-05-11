package edu.ucdavis.fiehnlab.spectra.hash.core.validation.controller;

import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.SplashListener;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.SplashingEvent;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectraType;
import edu.ucdavis.fiehnlab.spectra.hash.core.util.SplashUtil;
import edu.ucdavis.fiehnlab.spectra.hash.core.validation.serialize.*;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;

import java.io.*;
import java.util.Scanner;

/**
 * a small util to print out validation and processing information, while doing splashing in comparrison of different api's
 */
@Controller
public class ValidationController implements CommandLineRunner {

    private static Logger logger = Logger.getLogger("validation");

    /**
     * utilized FORMAT for the output of strings
     */
    public static String FORMAT = "%1$40s";


    /**
     * provides us with a serializer, based on the command line options
     *
     * @param cmd
     * @return
     */
    protected Serializer createSerialzier(CommandLine cmd, File input) throws Exception {

        //get our output stream
        PrintStream out = null;
        if (cmd.hasOption("output")) {
            out = System.out;
        } else {
            try {
                File file = null;


                file = new File(cmd.getArgs()[1]);

                if (file.isDirectory()) {
                    file = new File(file, "splashed_" + input.getName());
                }

                status(cmd, "storing result at: " + file + "\n");
                out = new PrintStream(new FileOutputStream(file));

            } catch (IndexOutOfBoundsException e) {
                throw new ParseException("please provide a filename for the output file");
            }
        }

        //exspected datatype for serialization
        Class<? extends Result> type;
        if (cmd.hasOption("create")) {
            type = Result.class;
        } else {
            type = ValidationResult.class;
        }

        //we only want to write duplicates out
        if (cmd.hasOption("duplicates")) {
            return new FindDuplicatesSerializer(cmd, out, type);
        }
        //we want to sort the result
        else if (cmd.hasOption("sort")) {
            return new SortSerializer(cmd, out, type);
        }
        //we don't care about the order and just serialize it
        else {
            return new Serializer(cmd, out);
        }
    }

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

            displayUtilizedOptions(cmd);

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


            int hashes = 0;


            hashes = processFile(cmd, seperator, columnSplash, columnSpectra, columnOrigin, msType);

            //only show statistics, if we save the output in a file
            status(cmd, "finished processing, processing took: " + String.format("%.2f", (double) (System.currentTimeMillis() - time) / 1000.0) + " s.\n");
            status(cmd, "processed " + hashes + " spectra\n");
            status(cmd, "average time including io to splash a spectra is " + String.format("%.2f", (double) (System.currentTimeMillis() - time) / (double) hashes) + " ms\n");


        } catch (Exception e) {
            System.out.println("\nwe encountered an error: " + e.getMessage() + "\n");
            HelpFormatter formatter = new HelpFormatter();
            formatter.setArgName("value");

            formatter.printHelp("splash", "\n\nplease use the following options\n\n", options, "\n\n", true);

            e.printStackTrace(System.out);
        }

    }

    /**
     * little method to show us the options we provided
     *
     * @param cmd
     */
    protected void displayUtilizedOptions(CommandLine cmd) {
        String formatOption = "%1$5s";
        String formatOptionValue = "%1$-20s";
        String formatOptionDesc = "%1$-90s";


        status(cmd, "utilized options\n\n");

        for (Option o : cmd.getOptions()) {

            if (cmd.hasOption(o.getOpt())) {

                if (o.getValue() != null) {
                    status(cmd, String.format(formatOption, o.getOpt() + ":") + String.format(formatOptionValue, o.getValue()) + String.format(formatOptionDesc, o.getDescription()) + "\n");
                } else {
                    status(cmd, String.format(formatOption, o.getOpt() + ":") + String.format(formatOptionValue, "") + String.format(formatOptionDesc, o.getDescription()) + "\n");
                }
            }
        }

        status(cmd, "\n");
        status(cmd, "provided arguments\n\n");

        for (String o : cmd.getArgList()) {
            status(cmd, String.format(formatOption, "") + String.format(formatOptionValue, "") + String.format(formatOptionDesc, o) + "\n");

        }

        status(cmd, "\n");
    }

    /**
     * process the actual file for us
     *
     * @param cmd
     * @param seperator
     * @param columnSplash
     * @param columnSpectra
     * @param columnOrigin
     * @param msType
     * @throws FileNotFoundException
     */
    private int processFile(CommandLine cmd, String seperator, int columnSplash, int columnSpectra, int columnOrigin, SpectraType msType) throws Exception {

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

        if (inputFile.isDirectory()) {


            int counter = 0;
            for (File file : inputFile.listFiles()) {

                Serializer serializer = createSerialzier(cmd, file);
                serializer.init();
                counter += splashFile(cmd, seperator, columnSplash, columnSpectra, columnOrigin, msType, serializer, file);
            }

            return counter;
        } else {

            Serializer serializer = createSerialzier(cmd, inputFile);
            serializer.init();

            return splashFile(cmd, seperator, columnSplash, columnSpectra, columnOrigin, msType, serializer, inputFile);
        }

    }

    private int splashFile(CommandLine cmd, String seperator, int columnSplash, int columnSpectra, int columnOrigin, SpectraType msType, Serializer stream, File inputFile) throws Exception {
        Scanner scanner = new Scanner(inputFile);

        status(cmd, "current file: " + inputFile + "\n");

        long time = System.currentTimeMillis();
        int counter = 0;
        int counterValid = 0;
        int interval = 10000;

        int errorCounter = 0;

        try {
            while (scanner.hasNextLine()) {

                counter++;
                String line = scanner.nextLine();

                if (!line.isEmpty()) {

                    try {
                        String[] columns = line.split(seperator);


                        if (cmd.hasOption("debugExtraFine")) {
                            status(cmd, "read line " + counter + "\n");
                            for (int i = 0; i < columns.length; i++) {
                                status(cmd, "column " + (i + 1) + " contains: " + columns[i].substring(0, Math.min(columns[i].length(), 50)) + "\n");
                            }
                            status(cmd, "\n");
                        }
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

                            boolean valid = validateIt(splash, spectra, origin, msType, stream, seperator, cmd, !scanner.hasNextLine());

                            if (valid) {
                                counterValid++;
                            }
                            if (counter % interval == 0) {
                                status(cmd, "splashes valid: " + String.format("%.2f", (double) counterValid / (double) counter * 100) + "%, " + (counter - counterValid) + " invalid\n");
                            }

                        } else {

                            if (counter % interval == 0) {
                                status(cmd, "splashing, ");
                            }
                            splashIt(spectra, origin, msType, stream, seperator, cmd, !scanner.hasNextLine());
                        }

                        if ((counter % interval) == 0) {
                            status(cmd, "processed " + counter + " spectra, " + String.format("%.2f", (double) (System.currentTimeMillis() - time) / (double) counter) + " ms average time to splash a spectra\n");
                            status(cmd, "errors discovered in file: " + String.format("%.2f", ((double) errorCounter / (double) counter * 100)) + "%\n");

                        }
                    } catch (Exception e) {
                        errorCounter++;
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
        } finally {
            status(cmd, "errors discovered in file: " + errorCounter + "\n");

        }

        stream.close();

        return counter;
    }

    /**
     * provides us with simple status messages and use for debuggingt
     *
     * @param cmd
     * @param message
     */
    public static void status(CommandLine cmd, String message) {
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
    private void splashIt(String spectra, String origin, SpectraType msType, Serializer stream, String seperator, CommandLine cmd, boolean last) throws Exception {

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
    private boolean validateIt(String splash, String spectra, String origin, SpectraType msType, Serializer stream, String seperator, final CommandLine cmd, boolean last) throws Exception {
        String code = SplashUtil.splash(spectra, msType, new Listener(cmd));

        boolean valid = (splash.equals(code));

        if (cmd.hasOption("debug") && !valid) {

            status(cmd, "reference validation result" + "\n");

            //splash it again, this time with a listener
            code = SplashUtil.splash(spectra, msType, new Listener(cmd));

            status(cmd, String.format(FORMAT, "valid: ") + valid + "\n");

            String[] reference = code.split("-");
            String[] provided = splash.split("-");

            status(cmd, String.format(FORMAT, "reference: ") + code + "\n");
            status(cmd, String.format(FORMAT, "provided: ") + splash + "\n");
            status(cmd, String.format(FORMAT, "origin: ") + origin + "\n");


            status(cmd, String.format(FORMAT, "first block identical: ") + reference[0].equals(provided[0]) + "\n");
            status(cmd, String.format(FORMAT, "second block identical: ") + reference[1].equals(provided[1]) + "\n");
            status(cmd, String.format(FORMAT, "third block identical: ") + reference[2].equals(provided[2]) + "\n");
            status(cmd, String.format(FORMAT, "fourth block identical: ") + reference[3].equals(provided[3]) + "\n");
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
    private void serializeResult(Result result, Serializer stream, CommandLine cmd) throws Exception {
        stream.serialize(result);
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

        options.addOption("D", "duplicates", false, "only output discovered duplicates, careful it can be slow!");
        options.addOption("S", "sort", false, "sorts the output by given column. Columns can be 'splash' or 'origin' or 'spectra', careful it can be slow!");
        options.addOption("SD", "sortDirectory", true, "specify which directory should be used for temporary data, during sorting or duplicate searches");

        options.addOption("X", "debug", false, "displays additional debug information, cut to 50 char for strings");
        options.addOption("XX", "debugExact", false, "displays additional debug information, complete printout");
        options.addOption("XXX", "debugExtraFine", false, "displays even more debug information");

        options.addOption("c", "create", false, "computes a validation file with the default splash implementation, instead of validation the file");
        options.addOption("v", "validate", false, "validates a provided validation file, if not specified this is the default option");

        options.addOption("t", "type", true, "what kind of spectra type is it, options is MS | IR | UV | NMR | RAMAN");


        options.addOption("T", "separator", true, "what is the separator between columns");
        options.addOption("O", "output", false, "output will be system out, instead of a file");
        options.addOption("I", "ignoreErrors", false, "errors in spectra, will be ignored, but displayed");
        options.addOption("IS", "ignoreErrorsSuppressed", false, "errors in spectra, will be ignored and not shown");
        options.addOption("L", "longFormat", false, "utilizes the long serialization format");


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

            if (cmd.hasOption("debugExact") || cmd.hasOption("debugExtraFine")) {
                status(cmd, String.format(FORMAT, e.getBlock() + " raw : ") + e.getRawValue() + "\n");
                status(cmd, String.format(FORMAT, e.getBlock() + " processed : ") + e.getProcessedValue() + "\n");
            } else if (cmd.hasOption("debug")) {
                status(cmd, String.format(FORMAT, e.getBlock() + " raw : ") + e.getRawValue().substring(0, Math.min(e.getRawValue().length(), 90)) + "\n");
                status(cmd, String.format(FORMAT, e.getBlock() + " processed : ") + e.getProcessedValue().substring(0, Math.min(e.getProcessedValue().length(), 90)) + "\n");
            }
        }

        public void complete(Spectrum spectrum, String splash) {
            if (cmd.hasOption("debugExact") | cmd.hasOption("debug")) {
                status(cmd, "\n\n");
            }
        }
    }


}
