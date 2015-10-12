# -*- coding: utf-8 -*-

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