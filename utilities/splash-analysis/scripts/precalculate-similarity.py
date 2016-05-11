from __future__ import print_function
import math, sys

from splash import SplashVersion1, Spectrum, SpectrumType
import string
import collections


EPS_CORRECTION = 1.0e-7


def binbase_similarity(a, b):
    same_spectra = []
    i, j = 0, 0

    while i < len(a) and j < len(b):
        if a[i][0] < b[j][0]:
            i += 1
        elif a[i][0] > b[j][0]:
            j += 1
        else:
            same_spectra.append((a[i][0], a[i][1] / 10.0, b[j][1] / 10.0))
            i += 1
            j += 1

    similarity_sum = 0
    normalization_sum = 0

    for i, (mz, intensity_a, intensity_b) in enumerate(same_spectra):
        similarity_sum += mz * (intensity_a * intensity_b)**0.5

        if i > 0:
            unk = intensity_a / same_spectra[i - 1][1]
            lib = intensity_b / same_spectra[i - 1][2]

            normalization_sum += unk / lib if unk <= lib else lib / unk

    sum_a = sum(mz * intensity / 10.0 for mz, intensity in a if intensity > 0)
    sum_b = sum(mz * intensity / 10.0 for mz, intensity in b if intensiZty > 0)

    f1 = similarity_sum / (sum_a * sum_b)**0.5
    f2 = 1.0 / len(same_spectra) * normalization_sum

    return (1000.0 / (len(a) + len(same_spectra)) * (len(a) * f1) + (len(same_spectra) * f2))

def bin_spectrum(s):
    histogram = []

    binsize = 0.5

    for mz, intensity in s:
        idx = int(mz // binsize)

        while idx >= len(histogram):
            histogram.append(0.0)

        histogram[idx] += intensity

    return histogram

def norm(s):
    histogram = []

    binsize = 0.5

    for mz, intensity in s:
        idx = int(mz // binsize)

        while idx >= len(histogram):
            histogram.append(0.0)

        histogram[idx] += intensity

    return sum(x * x for x in histogram)**0.5



def similarity(a, b):
    histogram_a, histogram_b = [], []

    binsize = 1

    for mz, intensity in a:
        idx = int(mz // binsize)

        while idx >= len(histogram_a):
            histogram_a.append(0.0)

        histogram_a[idx] += intensity

    for mz, intensity in b:
        idx = int(mz // binsize)

        while idx >= len(histogram_b):
            histogram_b.append(0.0)

        histogram_b[idx] += intensity

    while len(histogram_a) != len(histogram_b):
        if len(histogram_a) > len(histogram_b):
            histogram_b.append(0.0)
        else:
            histogram_a.append(0.0)


    return int(1000 * sum(x * y for x, y in zip(histogram_a, histogram_b)) / norm(a) / norm(b))


def calculate_histogram(spectrum):
    BINS = 10
    BIN_SIZE = 100

    INTENSITY_MAP = string.digits + string.ascii_lowercase
    FINAL_SCALE_FACTOR = len(INTENSITY_MAP) - 1

    histogram = [0.0 for _ in range(BINS)]

    # Bin ions
    for mz, intensity in spectrum.spectrum:
        idx = int(mz / BIN_SIZE)

        while len(histogram) <= idx:
            histogram.append(0.0) 

        histogram[idx] += intensity

    # Wrap the histogram
    for i in range(BINS, len(histogram)):
        histogram[i % BINS] += histogram[i]

    # Normalize the histogram
    max_intensity = max(histogram[:BINS])
    histogram = [int(EPS_CORRECTION + FINAL_SCALE_FACTOR * x / max_intensity) for x in histogram[:BINS]]

    # Return histogram string with value substitutions
    return ''.join(map(INTENSITY_MAP.__getitem__, histogram))


def calculate_long_histogram(spectrum):
    BINS = 20
    BIN_SIZE = 50

    INTENSITY_MAP = string.digits + string.ascii_lowercase
    FINAL_SCALE_FACTOR = len(INTENSITY_MAP) - 1
    histogram = [0.0 for _ in range(BINS)]

    # Bin ions
    for mz, intensity in spectrum.spectrum:
        idx = int(mz / BIN_SIZE)

        while len(histogram) <= idx:
            histogram.append(0.0) 

        histogram[idx] += intensity

    # Wrap the histogram
    for i in range(BINS, len(histogram)):
        histogram[i % BINS] += histogram[i]

    # Normalize the histogram
    max_intensity = max(histogram[:BINS])
    histogram = [int(EPS_CORRECTION + FINAL_SCALE_FACTOR * x / max_intensity) for x in histogram[:BINS]]

    # Return histogram string with value substitutions
    return ''.join(map(INTENSITY_MAP.__getitem__, histogram))


def calculate_sum(spectrum):
    SPECTRUM_SUM_MAX_IONS = 100
    s = sorted(spectrum.spectrum, key = lambda x: (-x[1], x[0]))[: SPECTRUM_SUM_MAX_IONS]
    return str(int(sum(mz * intensity for mz, intensity in s) + EPS_CORRECTION))

def calculate_precision_sum(spectrum):
    SPECTRUM_SUM_MAX_IONS = 100
    s = sorted(spectrum.spectrum, key = lambda x: (-x[1], x[0]))[: SPECTRUM_SUM_MAX_IONS]
    return str(int(sum(mz * intensity * 1000 for mz, intensity in s) + EPS_CORRECTION))



get_bin = lambda x, size : int((x + (size / 2) + 1.0e-15) / size)

def bin_spectrum_nominal(spectrum):
    bins = collections.defaultdict(int)

    for mz, intensity in spectrum.spectrum:
        bins[get_bin(mz, 1) * 1000] += intensity

    return ' '.join('%d:%.6f' % (k, v) for k, v in bins.items())

def bin_spectrum_accurate(spectrum):
    bins = collections.defaultdict(int)

    for mz, intensity in spectrum.spectrum:
        bins[get_bin(mz, 0.001)] += intensity

    return ' '.join('%d:%.6f' % (k, v) for k, v in bins.items())


if __name__ == '__main__':
    splasher = SplashVersion1()

    for i, line in enumerate(sys.stdin):
        line = line.strip()
        origin, spectrum_string = line.split(',')

        if spectrum_string:
            spectrum = Spectrum(spectrum_string, SpectrumType.MS)

            print(origin, spectrum_string,
                  bin_spectrum_nominal(spectrum),
                  bin_spectrum_accurate(spectrum),
                  splasher.encode_spectrum(spectrum),
                  splasher.calculate_histogram(spectrum), \
                  calculate_long_histogram(spectrum), calculate_sum(spectrum), \
                  calculate_precision_sum(spectrum), sep = ',')

        if i % 1000 == 0:
            print(i, file = sys.stderr)