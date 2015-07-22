package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.types.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.Splash;
import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import edu.ucdavis.fiehnlab.spectra.hash.core.io.SpectraHandler;
import edu.ucdavis.fiehnlab.spectra.hash.core.io.SpectrumReader;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectraType;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectrumImpl;
import junit.framework.TestResult;
import org.geirove.exmeso.CloseableIterator;
import org.geirove.exmeso.ExternalMergeSort;
import org.geirove.exmeso.kryo.KryoSerializer;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.util.*;

import static org.junit.Assert.*;

/**
 * general testing strategy for splash implementations
 */
public abstract class AbstractSpectraHashImplTester{

    /**
     * get our impl to test
     *
     * @return
     */
    abstract Splash getHashImpl();

    /**
     * runs our actual tests on the input and output files
     *
     * @param resultFile
     * @param inputFile
     * @throws IOException
     */
    protected TestResult runTest(String resultFile, String inputFile, SpectraType spectraType, boolean onlyDuplicates) throws IOException {
        final File tempFile = File.createTempFile("splash", "tmp");
        tempFile.deleteOnExit();
        final OutputStream temp = new BufferedOutputStream(new FileOutputStream(tempFile));
        final ExternalMergeSort.Serializer<TestSpectraImpl> serializer = new KryoSerializer<TestSpectraImpl>(TestSpectraImpl.class);

        final SpectrumReader reader = new SpectrumReader();

        reader.readSpectrum(new InputStreamReader(getClass().getResourceAsStream(inputFile)), new SpectraHandler() {

            public void begin() throws IOException {
            }

            /**
             * splashIt hash and serialize as json for later usage
             * @param s
             */
            public void handle(Spectrum s) {
                String hash = getHashImpl().splashIt(s);

                TestSpectraImpl impl = new TestSpectraImpl(s, hash);

                try {
                    serializer.writeValues(Arrays.asList(impl).iterator(), temp);
                } catch (Exception e) {
                    e.printStackTrace();
                    fail(e.getMessage());
                }
            }

            public void done() throws IOException {
                temp.flush();
                temp.close();
            }
        }, spectraType);

        return sortByHash(resultFile, tempFile, serializer, onlyDuplicates);

    }

    /**
     * sorts all hashes,
     * finds duplicates
     * saves this as file
     * returns a result with the written file and how many duplicates were found
     * <p/>
     * is implemented as external sorting, since this will allow us to support large test sets
     *
     * @param fileName
     * @param tempFile
     * @param serializer
     * @throws IOException
     */
    private TestResult sortByHash(String fileName, File tempFile, ExternalMergeSort.Serializer<TestSpectraImpl> serializer, boolean duplicatesOnly) throws IOException {
        ExternalMergeSort<TestSpectraImpl> sort = ExternalMergeSort.newSorter(serializer, new Comparator<TestSpectraImpl>() {
            public int compare(TestSpectraImpl o1, TestSpectraImpl o2) {
                return o1.getHash().compareTo(o2.getHash());
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

        CloseableIterator<TestSpectraImpl> sorted = sort.mergeSortedChunks(sortedChunks);

        int counter = 0;
        try {
            final File out = new File("target/" + fileName);

            PrintStream output = new PrintStream(new FileOutputStream(out));

            boolean first = true;

            int lines = 0;
            TestSpectraImpl last = null;
            while (sorted.hasNext()) {

                if (last == null) {
                    last = sorted.next();
                    lines++;

                    if (!duplicatesOnly) {
                        output.print(last.getOrigin());
                        output.print("\t");
                        output.print(last.getHash());
                        output.print("\n");
                    }
                }

                if (sorted.hasNext()) {
                    lines++;
                    TestSpectraImpl current = sorted.next();

                    if (duplicatesOnly) {
                        //onlywrite duplicates and since it's sorted we should be all good
                        if (current.getHash().equals(last.getHash())) {
                            counter++;
                            if (first) {
                                output.print(last.getOrigin());
                                output.print("\t");
                                output.print(last.getHash());
                                output.print("\n");

                                first = false;
                                counter++;
                            }

                            output.print(current.getOrigin());
                            output.print("\t");
                            output.print(current.getHash());
                            output.print("\n");
                        }
                    } else {
                        if (current.getHash().equals(last.getHash())) {
                            counter++;
                        }
                        output.print(current.getOrigin());
                        output.print("\t");
                        output.print(current.getHash());
                        output.print("\n");
                    }
                }
            }

            output.flush();
            output.close();

            TestResult result = new TestResult();
            result.file = out;
            result.duplicates = counter;
            result.linesRead = lines;

            return result;
        } finally {
            sorted.close();
        }

    }

    /**
     * simpleclass to store test results
     */
    protected class TestResult {
        /**
         * link to the file, containg output information
         */
        public File file;

        /**
         * how many duplicates
         */
        public int duplicates;

        /**
         * how many lines did we read
         */
        public int linesRead;
    }
}
