package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.SpectraHash;
import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import edu.ucdavis.fiehnlab.spectra.hash.core.io.SpectraHandler;
import edu.ucdavis.fiehnlab.spectra.hash.core.io.SpectrumReader;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.geirove.exmeso.CloseableIterator;
import org.geirove.exmeso.ExternalMergeSort;
import org.geirove.exmeso.jackson.JacksonSerializer;
import org.geirove.exmeso.kryo.KryoSerializer;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by wohlg_000 on 7/3/2015.
 */
public abstract class AbstractSpectraHashImplTester {

    public static final String MONA_TESTDATA_1 = "/mona/spectra-hash-test-spectra.txt";
    public static final String MONA_TESTDATA_2 = "/mona/spectra-hash-min-spectra.txt";
    public static final String BINBASE_TESTDATA_1 = "/binbase/bins.spectra";
    public static final String BINBASE_TESTDATA_2 = "/binbase/alanine.bin.annotations";
    public static final String BINBASE_TESTDATA_3 = "/binbase/local/all.spectra";

    /**
     * get our impl to test
     *
     * @return
     */
    abstract SpectraHash getHashImpl();

    /**
     * checks that duplicates are not removed in the output files
     * @throws IOException
     */
    @Test
    public void testSorterGeneratingDuplicates() throws IOException {
        String file = "duplicates" + this.getHashImpl().toString();
        runTest(file,"/tenIdenticalSpectra");

        file = "target/"+file;

        int counter = 0;

        Scanner scanner = new Scanner(new File(file));

        while(scanner.hasNextLine()){
            scanner.nextLine();
            counter++;
        }

        assertEquals(10,counter);
        scanner.close();
    }
    /**
     * runs our actual tests on the input and output files
     *
     * @param resultFile
     * @param inputFile
     * @throws IOException
     */
    private File runTest(String resultFile, String inputFile) throws IOException {
        final File tempFile = File.createTempFile("temp", "tmp");
        tempFile.deleteOnExit();
        final FileOutputStream temp = new FileOutputStream(tempFile);
        final ExternalMergeSort.Serializer<TestSpectraImpl> serializer = new KryoSerializer<TestSpectraImpl>(TestSpectraImpl.class);

        final SpectrumReader reader = new SpectrumReader();
        reader.readSpectrum(new InputStreamReader(getClass().getResourceAsStream(inputFile)), new SpectraHandler() {

            public void begin() throws IOException {
            }

            /**
             * generate hash and serialize as json for later usage
             * @param s
             */
            public void handle(Spectrum s) {
                String hash = getHashImpl().generate(s);

                TestSpectraImpl impl = new TestSpectraImpl(s, hash);

                try {
                    serializer.writeValues(Arrays.asList(impl).iterator(),temp);
                } catch (Exception e) {
                    e.printStackTrace();
                    fail(e.getMessage());
                }
            }

            public void done() throws IOException {
            }
        });

       return sortByHash(resultFile, tempFile, serializer);

    }

    /**
     * writes our generated hashes out and sorts them
     * @param fileName
     * @param tempFile
     * @param serializer
     * @throws IOException
     */
    private File sortByHash(String fileName, File tempFile, ExternalMergeSort.Serializer<TestSpectraImpl> serializer) throws IOException {
        ExternalMergeSort<TestSpectraImpl> sort = ExternalMergeSort.newSorter(serializer, new Comparator<TestSpectraImpl>() {
            public int compare(TestSpectraImpl o1, TestSpectraImpl o2) {
                return o1.getHash().compareTo(o2.getHash());
            }
        }).withChunkSize(1000)
                .withMaxOpenFiles(10)
                .withCleanup(true)
                .withTempDirectory(new File("target"))
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
        try {
            final File out = new File("target/" + fileName);

            PrintStream output = new PrintStream(new FileOutputStream(out));

            boolean first = true;

            TestSpectraImpl last = null;
            while(sorted.hasNext()){

                if(last == null){
                    last = sorted.next();
                }

                if(sorted.hasNext()){
                    TestSpectraImpl current = sorted.next();

                    //onlywrite duplicates and since it's sorted we should be all good
                    if(current.getHash().equals(last.getHash())){
                        if(first) {
                            output.print(last.getOrigin());
                            output.print("\t");
                            output.print(last.getHash());
                            output.print("\n");

                            first = false;
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

            System.out.println(out + ": " + out.length());
            return out;
        } finally {
            sorted.close();
        }

    }


    /**
     * test different implementations of the hashing algo
     * with spectrum: (100, 1) (101, 2) (102, 3)
     *
     * @param impl
     */
    public void testDefault(SpectraHash impl, String expected) {

        ArrayList list = new ArrayList();
        list.add(new Ion(100, 1));
        list.add( new Ion(101, 2));
        list.add(new Ion(102, 3));

        Spectrum spectrum = new SpectrumImpl(list, "mona");

        String hash = impl.generate(spectrum);

        assertEquals(expected, hash);

    }


    @Test
    public void testBinBaseAllBinSpectraHash() throws IOException {
        runTest("binbase.hash-" + this.getHashImpl().toString(), (BINBASE_TESTDATA_1));
    }

    @Test
    public void testBinBaseAlanineSpectraHash() throws IOException {
        runTest("binbase.alanine.annotations-" + this.getHashImpl().toString(), (BINBASE_TESTDATA_2));
    }

    @Test
    public void testBinBaseAllAnnotationSpectraHash() throws IOException {
        runTest("binbase.all.annotations-" + this.getHashImpl().toString(), (BINBASE_TESTDATA_3));
    }

    @Test
    public void testMonaSpectraHash1() throws IOException {
        runTest("mona.hash-" + this.getHashImpl().toString(), MONA_TESTDATA_1);
    }

    @Test
    public void testMonaSpectraHash2() throws IOException {
        runTest("mona-2.hash-" + this.getHashImpl().toString(), (MONA_TESTDATA_2));
    }
}
