# -*- coding: utf-8 -*-

import re


class Spectrum:
    # Value to scale relative spectra
    RELATIVE_INTENSITY_SCALE = 100.0

    # Regular expression to match a spectrum string of the form:
    #     [m/z]:[intensity][ ]...
    SPECTRUM_REGEX = r'^((\d*\.?\d+(?:e-?\d+)?):(\d*\.?\d+(?:e-?\d+)?)\s?)+$'

    
    def __init__(self, spectrum, spectrum_type):
        self.spectrum = self.parse_spectrum(spectrum)
        self.spectrum_type = spectrum_type
    

    def parse_spectrum(self, spectrum):
        """Parse the provided mass spectrum into the internal format"""
        
        # Handle the spectrum string format
        if type(spectrum) is str and re.match(self.SPECTRUM_REGEX, spectrum):
            # Split the spectrum into m/z and intensity pairs as floats
            spectrum = [list(map(float, x.split(':'))) for x in spectrum.split()]

            # Normalize spectrum
            max_intensity = max(intensity for _, intensity in spectrum)
            return [(mz, intensity / max_intensity * self.RELATIVE_INTENSITY_SCALE) for mz, intensity in spectrum]
        
        # Handle the internal format    
        elif type(spectrum) is list and all(type(x) is tuple and len(x) == 2 for x in spectrum):
            return spectrum
        
        # Otherwise, throw an invalid format exception
        else:
            raise ValueError('Invalid spectrum format')