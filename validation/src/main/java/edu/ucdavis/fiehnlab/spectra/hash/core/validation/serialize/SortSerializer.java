package edu.ucdavis.fiehnlab.spectra.hash.core.validation.serialize;

import edu.ucdavis.fiehnlab.spectra.hash.core.validation.controller.ValidationController;
import org.apache.commons.cli.CommandLine;
import org.geirove.exmeso.CloseableIterator;
import org.geirove.exmeso.ExternalMergeSort;
import org.geirove.exmeso.kryo.KryoSerializer;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wohlgemuth
 * Date: 7/16/15
 * Time: 12:37 PM
 */
public class SortSerializer extends Serializer {

    private final Class<? extends Result> type;
    private ExternalMergeSort.Serializer<Result> serializer;

    OutputStream temp;

    File tempFile;

    public SortSerializer(CommandLine cmd, PrintStream stream, Class<? extends Result> type) throws Exception {
        super(cmd, stream);
        this.type = type;
    }

    /**
     * closes the temp stream and sorts our data based on values
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {

        ValidationController.status(getCmd(), "sorting your result set\n");

        long time = System.currentTimeMillis();

        temp.flush();
        temp.close();

        ExternalMergeSort<Result> sort = ExternalMergeSort.newSorter(serializer, new Comparator<Result>() {
            public int compare(Result o1, Result o2) {
                return o1.getSplash().compareTo(o2.getSplash());
            }
        }).withChunkSize(1000)
                .withMaxOpenFiles(10)
                .withCleanup(true)
                .withDistinct(false)
                .build();

        List<File> sortedChunks;
        InputStream input = new FileInputStream(tempFile);
        try {
            sortedChunks = sort.writeSortedChunks(serializer.readValues(input));
        } finally {
            input.close();
        }

        CloseableIterator<Result> sorted = sort.mergeSortedChunks(sortedChunks);

        while (sorted.hasNext()) {

            Result sortedData = sorted.next();
            serializeSortedData(sortedData);
        }


        ValidationController.status(getCmd(), "sorting took: " + String.format("%.2f", (double) (System.currentTimeMillis() - time) / 1000.0) + "s\n");

        super.close();

    }

    protected void serializeSortedData(Result sortedData) {
        getStream().println(sortedData);
    }

    @Override
    public void init() throws Exception {
        tempFile = File.createTempFile("splash-sort", "tmp");
        tempFile.deleteOnExit();
        temp = new BufferedOutputStream(new FileOutputStream(tempFile));
        serializer = new KryoSerializer(type);
    }

    @Override
    public void serialize(Result result) throws Exception {
        serializer.writeValues(Arrays.asList(result).iterator(), temp);
    }
}
