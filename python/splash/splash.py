import hashlib
import re



PRECISION = 6;
PRECISION_FACTOR = 10**PRECISION
EPS = 1.0e-6;

# Value to scale relative spectra
RELATIVE_INTENSITY_SCALE = 1000.0

# Separator for building spectrum strings
ION_SEPARATOR = ' '

# Full spectrum hash properties
ION_PAIR_SEPARATOR = ':'
MAX_HASH_CHARATERS_ENCODED_SPECTRUM = 20

# Histogram properties
BINS = 10
BIN_SIZE = 100

FINAL_SCALE_FACTOR = 35
INTENSITY_MAP = [
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c',
    'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
    'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
]



class SpectrumType:
    MS = 1
    NMR = 2
    UV = 3
    IR = 4
    RAMEN = 5

    def get(spectrum_type):
        if spectrum_type.lower() == 'ms':
            return SpectrumType.MS
        elif spectrum_type.lower() == 'nmr':
            return SpectrumType.NMR
        elif spectrum_type.lower() == 'uv':
            return SpectrumType.UV
        elif spectrum_type.lower() == 'ir':
            return SpectrumType.IR
        elif spectrum_type.lower() == 'ramen':
            return SpectrumType.RAMEN
        else:
            return None



class Spectrum:
    # Regular expression to match a spectrum string of the form:
    #     [m/z]:[intensity][ ]...
    SPECTRUM_REGEX = r'^((\d*\.?\d+(?:e-?\d+)?):(\d*\.?\d+(?:e-?\d+)?)\s?)+$'

    
    def __init__(self, spectrum, spectrum_type):
        self.spectrum = self.parse_spectrum(spectrum)
        self.spectrum_type = spectrum_type
    
    def parse_spectrum(self, spectrum):
        """Parse the provided mass spectrum into the internal format"""
        
        # Handle the spectrum string format
        if type(spectrum) is str and re.match(Spectrum.SPECTRUM_REGEX, spectrum):
            # Split the spectrum into m/z and intensity pairs as floats
            spectrum = [list(map(float, x.split(':'))) for x in spectrum.split()]

            # Normalize spectrum
            max_intensity = max(intensity for _, intensity in spectrum)
            return [(mz, intensity / max_intensity * RELATIVE_INTENSITY_SCALE) for mz, intensity in spectrum]
        
        # Handle the internal format    
        elif type(spectrum) is list and all(type(x) is tuple and len(x) == 2 for x in spectrum):
            return spectrum
        
        # Otherwise, throw an invalid format exception
        else:
            raise ValueError('Invalid spectrum format')
            


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