# -*- coding: utf-8 -*-

from __future__ import division
import re


class Spectrum:
    # Value to scale relative spectra
    RELATIVE_INTENSITY_SCALE = 100.0

    # Regular expression to match a spectrum string of the form:
    #     [m/z]:[intensity][ ]...
    DECIMAL_REGEX = r'[+-]?\d+(\.\d+)?([Ee][+-]?\d+)?'
    ION_REGEX = DECIMAL_REGEX +':'+ DECIMAL_REGEX
    SPECTRUM_REGEX = '^'+ ION_REGEX +'(\s'+ ION_REGEX +')*$'

    
    def __init__(self, spectrum, spectrum_type):
        self.spectrum = self.parse_spectrum(spectrum)
        self.spectrum_type = spectrum_type
    

    def parse_spectrum(self, spectrum):
        """Parse the provided mass spectrum into the internal format and normalize the spectrum"""
        
        # Handle the spectrum string format
        if type(spectrum) is str and re.match(self.SPECTRUM_REGEX, spectrum.strip()):
            # Split the spectrum into m/z and intensity pairs as floats
            spectrum = [list(map(float, x.split(':'))) for x in spectrum.strip().split()]

            return self.normalize_spectrum(spectrum)
        
        # Handle the internal format    
        elif type(spectrum) is list and all(type(x) is tuple and len(x) == 2 for x in spectrum):
            return self.normalize_spectrum(spectrum)
        
        # Otherwise, throw an invalid format exception
        else:
            raise ValueError('Invalid spectrum format')


    def normalize_spectrum(self, spectrum):
        """Normalize intensities to the constant RELATIVE_INTENSITY_SCALE value"""

        # Compute the maxmimum intensity
        max_intensity = max(intensity for _, intensity in spectrum)

        # Normalize spectrum
        return [(mz, intensity / max_intensity * self.RELATIVE_INTENSITY_SCALE) for mz, intensity in spectrum]
