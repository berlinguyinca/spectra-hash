#!/usr/bin/env python

from __future__ import print_function
import hashlib, sys


PRECISION = 6;
EPS = 1.0e-6;

# Value to scale relative spectra
RELATIVE_INTENSITY_SCALE = 1000.0

# Separator for building spectrum strings
ION_SEPARATOR = ' '

# Full spectrum hash properties
ION_PAIR_SEPARATOR = ':'
MAX_HASH_CHARATERS_ENCODED_SPECTRUM = 20

# Top ions block properties
MAX_TOP_IONS = 10;
MAX_HASH_CHARACTERS_TOP_IONS = 10

# Spectrum sum properties
SPECTRUM_SUM_PADDING = 10
SPECTRUM_SUM_MAX_IONS = 100


def build_first_block(spectrum, spectrum_type):
	return 'splash%s0' % spectrum_type

def encode_top_ions(spectrum, spectrum_type):
	spectrum = sorted(spectrum, key = lambda x: (-x[1], x[0]))[: MAX_TOP_IONS]
	s = ION_SEPARATOR.join(('%0.'+ str(PRECISION) +'f') % mz for mz, _ in spectrum)
	
	return hashlib.sha256(s).hexdigest()[: MAX_HASH_CHARACTERS_TOP_IONS]

def encode_spectrum(spectrum, spectrum_type):
	spectrum = sorted(spectrum, key = lambda x: x[0])
	s = ION_SEPARATOR.join(ION_PAIR_SEPARATOR.join(map(lambda s: ('%0.'+ str(PRECISION) +'f') % s, x)) for x in spectrum)
	
	return hashlib.sha256(s).hexdigest()[: MAX_HASH_CHARATERS_ENCODED_SPECTRUM]
	

def calculate_sum(spectrum, spectrum_type):
	spectrum = sorted(spectrum, key = lambda x: (-x[1], x[0]))[: SPECTRUM_SUM_MAX_IONS]
	return ('%.0f' % sum(mz * intensity for mz, intensity in spectrum)).zfill(MAX_HASH_CHARACTERS_TOP_IONS)


def splash_it(spectrum, spectrum_type):
	return '%s-%s-%s-%s' % (
		build_first_block(spectrum, spectrum_type),
		encode_top_ions(spectrum, spectrum_type),
		encode_spectrum(spectrum, spectrum_type),
		calculate_sum(spectrum, spectrum_type)
	)


if __name__ == '__main__':
	for line in sys.stdin:
		# Handle input of the form [id],[spectrum string]
		spectrum_id, spectrum_string = line.strip().split(',')
		spectrum = [list(map(float, x.split(':'))) for x in spectrum_string.split()]

		# Normalize spectrum
		max_intensity = max(intensity for _, intensity in spectrum)
		spectrum = [(mz, intensity / max_intensity * RELATIVE_INTENSITY_SCALE) for mz, intensity in spectrum]

		# Print the spectrum id with the calculated splash id
		print('%s,%s' % (spectrum_id, splash_it(spectrum, '1')))
