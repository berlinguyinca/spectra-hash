package edu.ucdavis.fiehnlab.spectra.hash.core.validation.serialize;

import edu.ucdavis.fiehnlab.spectra.hash.core.validation.serialize.Result;
import org.apache.commons.cli.CommandLine;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * writes out objects to the given stream
 */
public class Serializer {

    public PrintStream getStream() {
        return stream;
    }

    public CommandLine getCmd() {
        return cmd;
    }

    /**
     * where to write our o
     */
    private PrintStream stream;

    public Serializer(CommandLine cmd, PrintStream stream) {
        this.cmd = cmd;
        this.stream = stream;
    }

    private CommandLine cmd;

    /**
     * begins the serialization
     */
    public void init() throws Exception {

    }

    /**
     * serializes an object
     *
     * @param result
     */
    public void serialize(Result result) throws IOException {
        if (cmd.hasOption("longFormat")) {
            stream.print(result);
            stream.print(result.getSeparator());

            String blocks[] = result.getSplash().split("-");
            for (int i = 0; i < blocks.length; i++) {
                stream.print(blocks[i]);

                if (i < blocks.length - 1) {
                    stream.print(result.getSeparator());
                }
            }
            stream.println();

        } else {
            stream.println(result);
        }
    }

    /**
     * closes
     */
    public void close() throws IOException {
        stream.close();
    }

}
