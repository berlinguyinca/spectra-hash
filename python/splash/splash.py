# -*- coding: utf-8 -*-

import hashlib
import string


PRECISION = 6;
PRECISION_FACTOR = 10**PRECISION
EPS = 1.0e-6;

# Separator for building spectrum strings
ION_SEPARATOR = ' '

# Full spectrum hash properties
ION_PAIR_SEPARATOR = ':'
MAX_HASH_CHARATERS_ENCODED_SPECTRUM = 20

# Histogram properties
BINS = 10
BIN_SIZE = 100

INTENSITY_MAP = string.digits + string.ascii_lowercase
FINAL_SCALE_FACTOR = len(INTENSITY_MAP) - 1


class SplashVersion1():
    def build_initial_block(self, spectrum):
        # Build initial block to indicate version and spectrum type
        return 'splash%s0' % spectrum.spectrum_type


    def encode_spectrum(self, spectrum):
        # Format m/z and intensity 
        s = [(int(mz * PRECISION_FACTOR), int(intensity * PRECISION_FACTOR)) for mz, intensity in spectrum.spectrum]

        # Sort by increasing m/z and then by decreasing intensity
        s.sort(key = lambda x: (x[0], -x[1]))

        # Build spectrum string
        s = ION_SEPARATOR.join(ION_PAIR_SEPARATOR.join(map(str, x)) for x in s).encode('utf-8')

        # Hash spectrum string using SHA256 and truncate
        return hashlib.sha256(s).hexdigest()[: MAX_HASH_CHARATERS_ENCODED_SPECTRUM]


    def calculate_histogram(self, spectrum):
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
        histogram = [int(FINAL_SCALE_FACTOR * x / max_intensity) for x in histogram[:BINS]]

        # Return histogram string with value substitutions
        return ''.join(map(INTENSITY_MAP.__getitem__, histogram))


    def splash(self, spectrum):
        return '%s-%s-%s' % (
            self.build_initial_block(spectrum),
            self.calculate_histogram(spectrum),
            self.encode_spectrum(spectrum)
        )