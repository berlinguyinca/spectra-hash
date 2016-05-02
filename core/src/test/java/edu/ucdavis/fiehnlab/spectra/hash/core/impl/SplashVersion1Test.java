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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests all asspects of the Splash Version 2 and ensures that the generated keys, meets the exspectations. It also ensures
 * that there are no duplicated found in a limited data set, from different sources.
 * <p/>
 * DataSets for Sources are based on:
 * <p/>
 * partial massbank
 * partial binbase
 * partial binbase annotations
 */
public class SplashVersion1Test extends AbstractSpectraHashImplTester {

    public static final String MONA_TESTDATA_1 = "/mona/spectra-hash-test-spectra.txt";
    public static final String MONA_TESTDATA_2 = "/mona/spectra-hash-min-spectra.txt";
    public static final String BINBASE_TESTDATA_1 = "/binbase/bins.spectra";
    public static final String BINBASE_TESTDATA_2 = "/binbase/alanine.bin.annotations";

    /**
     * how is our splash id supposed to look
     */
    private static final String REGEX = "splash[1-5][0-9a-z]-[0-9a-z]{4}-[0-9]{10}-[a-z0-9]{20}";


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
    SplashVersion1 getHashImpl() {
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

        Pattern pattern = Pattern.compile(REGEX);

        int lines = 0;
        while (scanner.hasNextLine()) {
            lines++;
            String[] content = scanner.nextLine().split("\t");

            assertEquals(content.length, 2);
            String hash = content[1];

            //check if it matches the exspected pattern
            assertTrue(pattern.matcher(hash).matches());

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
                        assertEquals("99000000:100 100000000:100", e.getRawValue());
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
                        assertEquals("99000000:100 100000000:100", e.getRawValue());
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
    public void testThirdBlockGenerationNormalizationFrom0to1000() {

        Splash splash = getHashImpl();


        Spectrum spectrum = new SpectrumImpl(Arrays.asList(new Ion(130, 0), new Ion(99.0, 25), new Ion(100.0, 50), new Ion(125, 100)), SpectraType.MS);


        final Collection<Boolean> results = new ArrayList<Boolean>();

        splash.addListener(new SplashListener() {
            public void eventReceived(SplashingEvent e) {

                switch (e.getBlock()) {
                    case THIRD:
                        assertEquals("99000000:25 100000000:50 125000000:100 130000000:0", e.getRawValue());
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
     * tests if we keep very very small values as well as 0 values in our spectra. This is in regard to us keeping only digitis and so causing smaller values to disapeer and be represented as 0
     */
    @Test
    public void testSplashItRetentionOfVerySmallValuesAndZeroValues() {

        Splash splash = getHashImpl();


        Spectrum spectrum = new SpectrumImpl(Arrays.asList(
                new Ion(100.0, 1000000),
                new Ion(101.0, 0.00001),
                new Ion(102.0, 0.00001)

        ), SpectraType.MS);


        final Collection<Boolean> results = new ArrayList<Boolean>();

        splash.addListener(new SplashListener() {
            public void eventReceived(SplashingEvent e) {

                switch (e.getBlock()) {
                    case THIRD:
                        assertEquals("100000000:100 101000000:0 102000000:0", e.getRawValue());
                        results.add(true);
                        break;

                }

            }

            public void complete(Spectrum spectrum, String splash) {

            }
        });

        splash.splashIt(spectrum);

        assertTrue(results.size() == 1);
    }

    /**
     * ensures that nobody tries to specify negative ions for the specta
     */
    @Test
    public void testSplashItCantContainNegativeNumbersInASpectrum() {

        Splash splash = getHashImpl();

        boolean success = true;

        try {
            Spectrum spectrum = new SpectrumImpl(Arrays.asList(
                    new Ion(100.0, -5),
                    new Ion(-2, 0.00001),
                    new Ion(102.0, 3)

            ), SpectraType.MS);


            splash.splashIt(spectrum);
            success = false;
        } catch (Exception e) {
            //exspected
        } finally {
            assertTrue(success);
        }
    }


    /**
     * ensure that histogram wrapping works correctly
     */
    @Test
    public void testSplashHistogramWrapping() {

        Splash splash = getHashImpl();

        Spectrum[] spectra = new Spectrum[]{
                new SpectrumImpl(Arrays.asList(
                        new Ion(50, 100),
                        new Ion(150, 20)
                ), SpectraType.MS),

                new SpectrumImpl(Arrays.asList(
                        new Ion(50, 100),
                        new Ion(150, 20),
                        new Ion(1050, 100),
                        new Ion(1150, 20)
                ), SpectraType.MS),

                new SpectrumImpl(Arrays.asList(
                        new Ion(50, 100),
                        new Ion(150, 20),
                        new Ion(1050, 100),
                        new Ion(1150, 20),
                        new Ion(2050, 100),
                        new Ion(2150, 20)
                ), SpectraType.MS)
        };

        for (Spectrum spectrum : spectra) {
            String hash = splash.splashIt(spectrum);
            String histogram = hash.split("-")[2];

            assert histogram.equals("9100000000");
        }
    }


    /**
     * ensure that ions on the bin boundaries are binned separately
     */
    @Test
    public void testSplashHistogramBinBoundaries() {

        Splash splash = getHashImpl();

        Spectrum[] spectra = new Spectrum[]{
                new SpectrumImpl(Arrays.asList(
                        new Ion(99.9995, 100),
                        new Ion(100.0001, 100)
                ), SpectraType.MS),

                new SpectrumImpl(Arrays.asList(
                        new Ion(1099.9995, 100),
                        new Ion(1100.0001, 100)
                ), SpectraType.MS)
        };

        for (Spectrum spectrum : spectra) {
            String hash = splash.splashIt(spectrum);
            String histogram = hash.split("-")[2];

            assert (histogram).equals("9900000000");
        }
    }

}
