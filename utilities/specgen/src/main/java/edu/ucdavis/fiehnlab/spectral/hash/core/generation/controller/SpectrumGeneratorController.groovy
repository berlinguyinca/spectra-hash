package edu.ucdavis.fiehnlab.spectral.hash.core.generation.controller

import edu.ucdavis.fiehnlab.spectral.hash.core.generation.generator.SpectrumGenerator
import org.apache.commons.cli.*
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Controller

/**
 * Created by diego on 7/20/15.
 */
@Controller
class SpectrumGeneratorController implements CommandLineRunner {

	/*
	 * starts the application checking for options and calling the actual generator with provided settings
	 *
	 * @param strings command line options to parse
	 * @throws Exception
	 */

	void run(String... strings) throws Exception {
		Options options = getOptions()
		HelpFormatter formatter = new HelpFormatter()
		formatter.setArgName("value")

		try {

			CommandLineParser parser = new DefaultParser()
			CommandLine cmd = parser.parse(options, strings, true)

//			displayUtilizedOptions(cmd)

			if (cmd.hasOption("h")) {
				formatter.printHelp("specgen", "\nplease use the following options\n", options, "\n", true)
			} else {

				// check if accurate mass needed
				boolean accurateMass = cmd.hasOption("a")

				// check for naming
				String name = ""
				if(cmd.hasOption("n")) {
					name = "${cmd.getOptionValue("n")}-"
				}

				// checking the number of spectra to generate
				int spectraCount = 1;
				if (cmd.hasOption("c")) {
					spectraCount = Integer.parseInt(cmd.getOptionValue("c"))
					if (spectraCount < 1 || spectraCount > 100) {
						throw new ParseException("Invalid number of spectra, please use a value between 1 and 100.")
					}
				}

				// checking number of peaks to add to the spectrum
				int peaks = 5;
				if (cmd.hasOption("p")) {
					peaks = Integer.parseInt(cmd.getOptionValue("p"))
					if (peaks < 1 || peaks > 250) {
						throw new ParseException("Invalid number of peaks, please use a value between 1 and 250.")
					}
				}

				// check for a seed value
				long seed = 0
				if(cmd.hasOption("s")) {
					seed = Integer.parseInt(cmd.getOptionValue("s"))
				}

				long time = System.currentTimeMillis()

				// start generation...

				SpectrumGenerator gen = new SpectrumGenerator()
				gen.generate(accurateMass, peaks, spectraCount, seed)

				List<String> spectra = gen.getSpectra()

				int idx = 1

				println "Generator settings:\n" +
						"\tNumber of spectra = $spectraCount\n" +
						"\tAccurate mass = $accurateMass\n" +
						"\tNumber of peaks/spectrum = $peaks\n" +
						"\tPrefix = '$name'\n" +
						"\tSeed = $seed\n" +
						"\nSpectra:\n"

				spectra.each { spec ->
					println "\t$name$idx,$spec"
					idx++
				}

			}
		} catch (Exception e) {
			System.err.println("\nwe encountered an error: ${e.getMessage()}\n${e.stackTrace}")

			formatter.printHelp("specgen", "\nplease use the following options\n", options, "\n", true)

			System.err.println(e.localizedMessage + "\n" + e.stackTrace)
		}
	}

	/**
	 * little method to show us the options we provided
	 *
	 * @param cmd
	 */
	protected void displayUtilizedOptions(CommandLine cmd) {
		String formatOption = "%0\$-5s"
		String formatOptionValue = "%0\$-10s"
		String formatOptionDesc = "%0\$-90s"

		status(cmd, "utilized options\n");

		for (Option o : cmd.getOptions()) {
			if (cmd.hasOption(o.getOpt())) {
				if (o.getValue() != null) {
					status(cmd, "${String.format(formatOption, o.getOpt())}: ${String.format(formatOptionValue, o.getValue())} ${String.format(formatOptionDesc, o.getDescription())}\n")
				} else {
					status(cmd, "${String.format(formatOption, o.getOpt())}  ${String.format(formatOptionValue, "")} ${String.format(formatOptionDesc, o.getDescription())}\n")
				}
			}
		}

		status(cmd, "\n")
	}

	/**
	 * provides us with simple status messages and use for debuggingt
	 *
	 * @param cmd
	 * @param message
	 */
	public static void status(CommandLine cmd, String message) {
		if (!cmd.hasOption("output")) {
			System.out.print(message)
		} else {
			println message
		}
	}

	/**
	 * list of possible options
	 *
	 * @return
	 */
	private Options getOptions() {

		Options options = new Options()

		options.addOption("a", "accurate", false, "generates accurate mass data. (default: nominal mass)")
		options.addOption("p", "peaks", true, "generates a spectrum with at least <value> fragments. Valid range [1 - 250]. (default: 5)")
		options.addOption("c", "spectra-count", true, "generates <value> spectra. Valid range [1 - 100]. (default: 1)")
		options.addOption("n", "name", true, "adds a name to each generated spectrum. If empty the name is only an index.")
		options.addOption("s", "seed", true, "sets the seed for the spectra generation. Use in case you want to re-generate a previous set.")
		options.addOption("h", "help", false, "prints this help.")

		return options;
	}
}
