package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import edu.ucdavis.fiehnlab.spectra.hash.core.Splash;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.SplashListener;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.SplashingEvent;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectraType;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectrumImpl;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * tests a limited data set for duplicates clashes and expectations of the generated specta string
 */
public class SplashVersion1Test extends AbstractSpectraHashImplTester {

    public static final String MONA_TESTDATA_1 = "/mona/spectra-hash-test-spectra.txt";
    public static final String MONA_TESTDATA_2 = "/mona/spectra-hash-min-spectra.txt";
    public static final String BINBASE_TESTDATA_1 = "/binbase/bins.spectra";
    public static final String BINBASE_TESTDATA_2 = "/binbase/alanine.bin.annotations";


    /**
     * checks that duplicates are not removed in the output files
     *
     * @throws IOException
     */
    @Test
    public void testSorterGeneratingDuplicates() throws IOException {
        String file = "duplicates" + this.getHashImpl().toString();
        TestResult result = runTest(file, "/tenIdenticalSpectra", SpectraType.MS, true);

        int counter = 0;

        Scanner scanner = new Scanner(result.file);

        while (scanner.hasNextLine()) {
            scanner.nextLine();
            counter++;
        }

        assertEquals(result.duplicates, counter);
        scanner.close();
    }

    @Override
    Splash getHashImpl() {
        return new SplashVersion1();
    }

    @Test
    public void testBinBaseAllBinSpectraHash() throws IOException {
        TestResult result = runTest("binbase.hash-" + this.getHashImpl().toString(), (BINBASE_TESTDATA_1), SpectraType.MS, false);
        assertTrue(result.duplicates == 0);

        verifyGeneralSplashLayout(result, SpectraType.MS);
    }

    @Test
    public void testBinBaseAlanineSpectraHash() throws IOException {
        TestResult result = runTest("binbase.alanine.annotations-" + this.getHashImpl().toString(), (BINBASE_TESTDATA_2), SpectraType.MS, false);
        assertTrue(result.duplicates == 0);

        verifyGeneralSplashLayout(result, SpectraType.MS);
    }

    @Test
    public void testMonaSpectraHash1() throws IOException {
        TestResult result = runTest("mona.hash-" + this.getHashImpl().toString(), MONA_TESTDATA_1, SpectraType.MS, false);
        assertTrue(result.duplicates == 0);

        verifyGeneralSplashLayout(result, SpectraType.MS);
    }

    @Test
    public void testMonaSpectraHash2() throws IOException {
        TestResult result = runTest("mona-2.hash-" + this.getHashImpl().toString(), (MONA_TESTDATA_2), SpectraType.MS, false);
        assertTrue(result.duplicates == 0);

        verifyGeneralSplashLayout(result, SpectraType.MS);
    }

    /**
     * checks all the keys in the given result set to match certain exspectations.
     *
     * @param result
     * @throws FileNotFoundException
     */
    private void verifyGeneralSplashLayout(TestResult result, SpectraType type) throws FileNotFoundException {
        File file = result.file;

        assertTrue(result.linesRead > 0);

        Scanner scanner = new Scanner(file);


        int lines = 0;
        while (scanner.hasNextLine()) {
            lines++;
            String[] content = scanner.nextLine().split("\t");

            assertEquals(content.length, 2);
            String hash = content[1];

            String[] blocks = hash.split("-");

            assertEquals(blocks.length, 4);

            assertEquals(blocks[0], "splash" + type.getIdentifier() + "0");
            assertEquals(blocks[1].length(), 10);
            assertEquals(blocks[2].length(), 20);
            assertEquals(blocks[3].length(), 10);

            //simple test for overflow conditions
            assertTrue(blocks[3].startsWith("0"));
        }

        assertEquals(lines, result.linesRead);
    }

    @Test
    public void testFirstBlockGeneration() {

        Splash splash = getHashImpl();


        Spectrum spectrum = new SpectrumImpl(Arrays.asList(new Ion(100.0, 50)), SpectraType.MS);


        final Collection<Boolean> results = new ArrayList<Boolean>();

        splash.addListener(new SplashListener() {
            public void eventReceived(SplashingEvent e) {

                switch (e.getBlock()) {
                    case FIRST:
                        assertEquals(e.getProcessedValue(), "splash10");
                        results.add(true);
                }
            }

            public void complete(Spectrum spectrum, String splash) {

            }
        });

        splash.splashIt(spectrum);

        assertTrue(results.size() == 1);
    }

    /**
     * test for block of 1 mass, to ensure we format the digits precise enough
     */
    @Test
    public void testSecondBlockGenerationOneMass() {

        Splash splash = getHashImpl();


        Spectrum spectrum = new SpectrumImpl(Arrays.asList(new Ion(100.0, 50)), SpectraType.MS);


        final Collection<Boolean> results = new ArrayList<Boolean>();

        splash.addListener(new SplashListener() {
            public void eventReceived(SplashingEvent e) {

                switch (e.getBlock()) {
                    case SECOND:
                        assertEquals(e.getRawValue(), "100.000000");
                        results.add(true);
                }
            }

            public void complete(Spectrum spectrum, String splash) {

            }
        });

        splash.splashIt(spectrum);

        assertTrue(results.size() == 1);
    }


    /**
     * tests for a block of 2 masses, with same intensity to ensure that the sorting is correct
     */
    @Test
    public void testSecondBlockGenerationTwoMassesSmallerSecondLargerFirst() {

        Splash splash = getHashImpl();


        Spectrum spectrum = new SpectrumImpl(Arrays.asList(new Ion(100.0, 50), new Ion(99.0, 50)), SpectraType.MS);


        final Collection<Boolean> results = new ArrayList<Boolean>();

        splash.addListener(new SplashListener() {
            public void eventReceived(SplashingEvent e) {

                switch (e.getBlock()) {
                    case SECOND:
                        assertEquals("99.000000 100.000000", e.getRawValue());
                        results.add(true);
                }
            }

            public void complete(Spectrum spectrum, String splash) {

            }
        });

        splash.splashIt(spectrum);

        assertTrue(results.size() == 1);
    }


    /**
     * tests for a block of 2 masses, with same intensity to ensure that the sorting is correct
     */
    @Test
    public void testSecondBlockGenerationTwoMassesLargerSecondSmallerFirst() {

        Splash splash = getHashImpl();


        Spectrum spectrum = new SpectrumImpl(Arrays.asList(new Ion(99.0, 50), new Ion(100.0, 50)), SpectraType.MS);


        final Collection<Boolean> results = new ArrayList<Boolean>();

        splash.addListener(new SplashListener() {
            public void eventReceived(SplashingEvent e) {

                switch (e.getBlock()) {
                    case SECOND:
                        assertEquals("99.000000 100.000000", e.getRawValue());
                        results.add(true);
                }
            }

            public void complete(Spectrum spectrum, String splash) {

            }
        });

        splash.splashIt(spectrum);

        assertTrue(results.size() == 1);
    }


    /**
     * test if the generation of the string for block 3 is complete and as expected
     */
    @Test
    public void testThirdBlockGenerationTwoMassesLargerSecondSmallerFirst() {

        Splash splash = getHashImpl();


        Spectrum spectrum = new SpectrumImpl(Arrays.asList(new Ion(100, 50), new Ion(99.0, 50)), SpectraType.MS);


        final Collection<Boolean> results = new ArrayList<Boolean>();

        splash.addListener(new SplashListener() {
            public void eventReceived(SplashingEvent e) {

                switch (e.getBlock()) {
                    case THIRD:
                        assertEquals("99.000000:1000.000000 100.000000:1000.000000", e.getRawValue());
                        results.add(true);
                }
            }

            public void complete(Spectrum spectrum, String splash) {

            }
        });

        splash.splashIt(spectrum);

        assertTrue(results.size() == 1);
    }


    /**
     * test if the generation of the string for block 3 is complete and as expected
     */
    @Test
    public void testThirdBlockGenerationTwoMassesSmallerFirstLargerSecond() {

        Splash splash = getHashImpl();


        Spectrum spectrum = new SpectrumImpl(Arrays.asList(new Ion(99.0, 50), new Ion(100.0, 50)), SpectraType.MS);


        final Collection<Boolean> results = new ArrayList<Boolean>();

        splash.addListener(new SplashListener() {
            public void eventReceived(SplashingEvent e) {

                switch (e.getBlock()) {
                    case THIRD:
                        assertEquals("99.000000:1000.000000 100.000000:1000.000000", e.getRawValue());
                        results.add(true);
                }
            }

            public void complete(Spectrum spectrum, String splash) {

            }
        });

        splash.splashIt(spectrum);

        assertTrue(results.size() == 1);
    }


    /**
     * test if the generation of the string for block 3 is complete and as expected
     */
    @Test
    public void testThirdBlockGenerationNormalization() {

        Splash splash = getHashImpl();


        Spectrum spectrum = new SpectrumImpl(Arrays.asList(new Ion(99.0, 25), new Ion(100.0, 50), new Ion(125, 100)), SpectraType.MS);


        final Collection<Boolean> results = new ArrayList<Boolean>();

        splash.addListener(new SplashListener() {
            public void eventReceived(SplashingEvent e) {

                switch (e.getBlock()) {
                    case THIRD:
                        assertEquals("99.000000:250.000000 100.000000:500.000000 125.000000:1000.000000", e.getRawValue());
                        results.add(true);
                }
            }

            public void complete(Spectrum spectrum, String splash) {

            }
        });

        splash.splashIt(spectrum);

        assertTrue(results.size() == 1);
    }

    @Test
    public void testFourthBlockGenerationSumCalculation3Ions() {

        Splash splash = getHashImpl();


        Spectrum spectrum = new SpectrumImpl(Arrays.asList(new Ion(99.0, 25), new Ion(100.0, 50), new Ion(125, 100)), SpectraType.MS);


        final Collection<Boolean> results = new ArrayList<Boolean>();

        splash.addListener(new SplashListener() {
            public void eventReceived(SplashingEvent e) {

                switch (e.getBlock()) {
                    case FOURTH:
                        assertEquals("0000199750", e.getRawValue());
                        results.add(true);
                }
            }

            public void complete(Spectrum spectrum, String splash) {

            }
        });

        splash.splashIt(spectrum);

        assertTrue(results.size() == 1);
    }

    /**
     * tests for overflow conditions, needed to calculate how many digits we actually need to support
     * assuming we have no mz over 5000.
     */
    @Test
    public void testFourthBlockGenerationSumCalculationOverflow() {

        Splash splash = getHashImpl();


        List<Ion> ionList = new ArrayList<Ion>();

        for (double i = 1; i < 500000; i++) {
            ionList.add(new Ion(i/100, 100 * i + 0.01 * i));
        }
        Spectrum spectrum = new SpectrumImpl(ionList, SpectraType.MS);


        final Collection<Boolean> results = new ArrayList<Boolean>();

        splash.addListener(new SplashListener() {
            public void eventReceived(SplashingEvent e) {

                switch (e.getBlock()) {
                    case FOURTH:
                        assertTrue(
                                e.getRawValue().length() <= 10
                        );
                        results.add(true);
                }
            }

            public void complete(Spectrum spectrum, String splash) {

            }
        });

        splash.splashIt(spectrum);

        assertTrue(results.size() == 1);
    }


}