package edu.ucdavis.fiehnlab.spectra.hash.core.impl;

import edu.ucdavis.fiehnlab.spectra.hash.core.Spectrum;
import edu.ucdavis.fiehnlab.spectra.hash.core.Splash;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.SplashListener;
import edu.ucdavis.fiehnlab.spectra.hash.core.listener.SplashingEvent;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.Ion;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectraType;
import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectrumImpl;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: wohlgemuth
 * Date: 7/20/15
 * Time: 10:34 AM
 */
public class SplashHistVersion1Test extends AbstractSpectraHashImplTester
{


    public static final String MONA_TESTDATA_1 = "/mona/spectra-hash-test-spectra.txt";
    public static final String MONA_TESTDATA_2 = "/mona/spectra-hash-min-spectra.txt";
    public static final String BINBASE_TESTDATA_1 = "/binbase/bins.spectra";
    public static final String BINBASE_TESTDATA_2 = "/binbase/alanine.bin.annotations";

    /**
     * how is our splash id supposed to look
     */
    private static final String REGEX = "splash[1-5][0-9a-z]-[a-z0-9]{10}-[a-z0-9]{20}-[0-9]{10}";


    /**
     * checks that duplicates are not removed in the output files
     *
     * @throws IOException
     */
    @Test
    public void testSorterGeneratingDuplicates() throws IOException {
        String file = "duplicates" + this.getHashImpl().toString();
        TestResult result = runTest(file+this.getHashImpl().toString(), "/tenIdenticalSpectra", SpectraType.MS, true);

        int counter = 0;

        Scanner scanner = new Scanner(result.file);

        while (scanner.hasNextLine()) {
            scanner.nextLine();
            counter++;
        }

        assertEquals(result.duplicates, counter);
        scanner.close();
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
                        assertEquals(e.getRawValue(), "100000000");
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
                        assertEquals("99000000 100000000", e.getRawValue());
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
                        assertEquals("99000000 100000000", e.getRawValue());
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
    public void testSecondBlockGenerationThreeMassesThreeDifferentIntensities() {

        Splash splash = getHashImpl();


        Spectrum spectrum = new SpectrumImpl(Arrays.asList(new Ion(100, 1), new Ion(101,2), new Ion(102,3)), SpectraType.MS);


        final Collection<Boolean> results = new ArrayList<Boolean>();

        splash.addListener(new SplashListener() {
            public void eventReceived(SplashingEvent e) {

                switch (e.getBlock()) {
                    case SECOND:
                        assertEquals("102000000 101000000 100000000", e.getRawValue());
                        results.add(true);
                }
            }

            public void complete(Spectrum spectrum, String splash) {
                System.out.println("generated splash: " + splash);
            }
        });

        String splashHash = splash.splashIt(spectrum);

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
                        assertEquals("99000000:1000000000 100000000:1000000000", e.getRawValue());
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
                        assertEquals("99000000:1000000000 100000000:1000000000", e.getRawValue());
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
                        assertEquals("99000000:250000000 100000000:500000000 125000000:1000000000 130000000:0", e.getRawValue());
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
     * tests if we only use the 100 ions for the sum calculations
     * as we are supposed too, instead of all the ions
     */
    @Test
    public void testSecondBlockGenerationBeingLimitedToTop10Ions() {

        Splash splash = getHashImpl();

        final List<Ion> ionsSpectrumWith10Ions = new ArrayList<Ion>();
        final List<Ion> ionsSpectrumWith12Ions = new ArrayList<Ion>();


        //first 100 ions are the largest
        for (int i = 0; i < 10; i++) {
            ionsSpectrumWith12Ions.add(new Ion(i, 100));
            ionsSpectrumWith10Ions.add(new Ion(i, 100));

        }

        //second ions are lower, they should not really be part of the calculation
        for (int i = 0; i < 2; i++) {
            ionsSpectrumWith10Ions.add(new Ion(100 + i, 10));
        }


        final Collection<Boolean> results = new ArrayList<Boolean>();

        splash.addListener(new SplashListener() {
            public void eventReceived(final SplashingEvent e) {

                switch (e.getBlock()) {
                    case SECOND:

                        Splash splash1 = getHashImpl();
                        splash1.addListener(new SplashListener() {
                            public void eventReceived(SplashingEvent e2) {

                                switch (e2.getBlock()) {
                                    case SECOND:

                                        assertEquals(e2.getRawValue(), e.getRawValue());
                                        assertEquals(e2.getProcessedValue(), e.getProcessedValue());

                                        results.add(true);

                                }
                            }

                            public void complete(Spectrum spectrum, String splash) {

                            }
                        });

                        final Spectrum spectrumWith12Ions = new SpectrumImpl(ionsSpectrumWith12Ions, SpectraType.MS);
                        splash1.splashIt(spectrumWith12Ions);
                }
            }

            public void complete(Spectrum spectrum, String splash) {

            }
        });

        final Spectrum spectrumWith10Ions = new SpectrumImpl(ionsSpectrumWith10Ions, SpectraType.MS);
        splash.splashIt(spectrumWith10Ions);

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
                        assertEquals("100000000:1000000000 101000000:0 102000000:0", e.getRawValue());
                        results.add(true);
                        break;
                    case SECOND:
                        assertEquals("100000000 101000000 102000000", e.getRawValue());
                        results.add(true);
                        break;

                }

            }

            public void complete(Spectrum spectrum, String splash) {

            }
        });

        splash.splashIt(spectrum);

        assertTrue(results.size() == 2);
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


    @Test
    public void testCalculate4thBlock() throws Exception {

        SplashHistVersion1 splash = getHashImpl();

        Spectrum spectrum = new SpectrumImpl(Arrays.asList(new Ion(50, 50),new Ion(150, 100),new Ion(250, 25)), SpectraType.MS);

        String block = splash.calculate4thBlock(spectrum);

        assertEquals("4920000000",block);

    }

    @Override
    SplashHistVersion1 getHashImpl() {
        return new SplashHistVersion1();
    }
}