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
 * Sorts all the generated results, utilizing an external sort algorithm
 */
public class SortSerializer extends Serializer {

    private final Class<? extends Result> type;
    private ExternalMergeSort.Serializer<Result> serializer;

    private OutputStream temp;

    private File tempFile;

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

        ExternalMergeSort<Result> sort = generateSorter();

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

    /**
     * generates and configures our external sorter
     *
     * @return
     * @throws FileNotFoundException
     */
    protected ExternalMergeSort<Result> generateSorter() throws FileNotFoundException {
        if (getCmd().hasOption("sortDirectory")) {

            File file = new File(getCmd().getOptionValue("sortDirectory"));

            if (file.exists() && file.isDirectory()) {


                return ExternalMergeSort.newSorter(serializer, new Comparator<Result>() {
                    public int compare(Result o1, Result o2) {
                        return o1.getSplash().compareTo(o2.getSplash());
                    }
                }).withChunkSize(1000)
                        .withMaxOpenFiles(10)
                        .withCleanup(true)
                        .withDistinct(false)
                        .withTempDirectory(file)
                        .build();

            } else {
                throw new FileNotFoundException("sorry your given file doesn't exist or is not a directory: " + file + " please specify a valid -SD option");
            }
        } else {
            return ExternalMergeSort.newSorter(serializer, new Comparator<Result>() {
                public int compare(Result o1, Result o2) {
                    return o1.getSplash().compareTo(o2.getSplash());
                }
            }).withChunkSize(1000)
                    .withMaxOpenFiles(10)
                    .withCleanup(true)
                    .withDistinct(false)
                    .build();
        }
    }

    /**
     * does the actual serialization of a single data set
     * @param sortedData
     */
    protected void serializeSortedData(Result sortedData) throws IOException {
        super.serialize(sortedData);
    }

    @Override
    public void init() throws Exception {
        tempFile = File.createTempFile("splash-sort", "tmp");
        tempFile.deleteOnExit();
        temp = new BufferedOutputStream(new FileOutputStream(tempFile));
        serializer = new KryoSerializer(type);
    }

    @Override
    public void serialize(Result result) throws IOException {
        serializer.writeValues(Arrays.asList(result).iterator(), temp);
    }
}
