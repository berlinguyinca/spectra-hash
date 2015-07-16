package edu.ucdavis.fiehnlab.spectra.hash.core.validation.serialize;

import edu.ucdavis.fiehnlab.spectra.hash.core.validation.serialize.Result;
import org.apache.commons.cli.CommandLine;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * writes out objects
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
    public void serialize(Result result) throws Exception {
        stream.print(result);
    }

    /**
     * closes
     */
    public void close() throws IOException {
        stream.close();
    }

}
