# -*- coding: utf-8 -*-

from __future__ import division
import hashlib
import string

from .spectrum import Spectrum


EPS_CORRECTION = 1.0e-7

# Full spectrum hash properties
ION_SEPARATOR = ' '
ION_PAIR_SEPARATOR = ':'
MAX_HASH_CHARATERS_ENCODED_SPECTRUM = 20

MZ_PRECISION = 6
MZ_PRECISION_FACTOR = 10**MZ_PRECISION

INTENSITY_PRECISION = 0
INTENSITY_PRECISION_FACTOR = 10**INTENSITY_PRECISION

# Prefilter properties
PREFILTER_BASE = 3
PREFILTER_LENGTH = 10
PREFILTER_BIN_SIZE = 5

# Similarity histogram properties
SIMILARITY_BASE = 10
SIMILARITY_LENGTH = 10
SIMILARITY_BIN_SIZE = 100

INTENSITY_MAP = string.digits + string.ascii_lowercase
FINAL_SCALE_FACTOR = len(INTENSITY_MAP) - 1


class SplashVersion1():
    def build_initial_block(self, spectrum):
        # Build initial block to indicate version and spectrum type
        return 'splash%s0' % spectrum.spectrum_type

    def format_mz(self, x):
        return int((x + EPS_CORRECTION) * MZ_PRECISION_FACTOR)

    def format_intensity(self, x):
        return int((x + EPS_CORRECTION) * INTENSITY_PRECISION_FACTOR)


    def encode_spectrum(self, spectrum):
        # Format m/z and intensity 
        s = [(self.format_mz(mz), self.format_intensity(intensity)) for mz, intensity in spectrum.spectrum]

        # Sort by increasing m/z and then by decreasing intensity
        s.sort(key = lambda x: (x[0], -x[1]))

        # Build spectrum string
        s = ION_SEPARATOR.join(ION_PAIR_SEPARATOR.join(map(str, x)) for x in s).encode('utf-8')

        # Hash spectrum string using SHA256 and truncate
        return hashlib.sha256(s).hexdigest()[: MAX_HASH_CHARATERS_ENCODED_SPECTRUM]


    def calculate_histogram(self, spectrum, base, length, bin_size):
        # Define the empty histogram
        histogram = [0.0 for _ in range(length)]

        # Bin ions using the histogram wrapping strategy
        for mz, intensity in spectrum.spectrum:
            idx = int(mz / bin_size) % length
            histogram[idx] += intensity

        # Normalize the histogram and scale to the provided base
        max_intensity = max(histogram)
        histogram = [int(EPS_CORRECTION + (base - 1) * x / max_intensity) for x in histogram]

        # Return histogram string with value substitutions
        return ''.join(map(INTENSITY_MAP.__getitem__, histogram))

    def filter_spectrum(self, s, top_ions = None, base_peak_percentage = None):
        spectrum = s.spectrum

        # Filter first by base peak percentage if meeded
        if base_peak_percentage is not None:
            base_peak_intensity = max(intensity for mz, intensity in spectrum)

            spectrum = [(mz, intensity) for mz, intensity in spectrum \
                if intensity + EPS_CORRECTION >= base_peak_percentage * base_peak_intensity]

        # Filter by top ions if needed
        if top_ions is not None:
            spectrum = sorted(spectrum, key = lambda x: (-x[1], x[0]))[: top_ions]

        return Spectrum(spectrum, s.spectrum_type)

    def translate_base(self, s, initial_base, final_base, fill_length):
        n = int(s, initial_base)
        digits = []

        while n > 0:
            digits.append(n % final_base)
            n //= final_base

        # Build string in final base and fill to the desired length
        return (''.join(map(INTENSITY_MAP.__getitem__, reversed(digits)))).zfill(fill_length)


    def splash(self, spectrum):
        # Filtered spectrum for the prefilter block
        filtered_spectrum = self.filter_spectrum(spectrum, 10, 0.1)

        return '-'.join([
            # Initial splash block
            self.build_initial_block(spectrum),

            # Prefilter block
            self.translate_base(
                self.calculate_histogram(filtered_spectrum, PREFILTER_BASE, PREFILTER_LENGTH, PREFILTER_BIN_SIZE), 
                PREFILTER_BASE, 36, 4
            ),

            # Similarity histogram block
            self.calculate_histogram(spectrum, SIMILARITY_BASE, SIMILARITY_LENGTH, SIMILARITY_BIN_SIZE),

            # Exact hash block
            self.encode_spectrum(spectrum)
        ])