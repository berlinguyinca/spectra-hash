#!/usr/bin/env python
# -*- coding: utf-8 -*-

from __future__ import division, print_function
from splash import SplashVersion1, Spectrum, SpectrumType

import argparse 
import sys
import time



SPLASH_BANNER = """
   .---. ,---.  ,-.      .--.     .---. .-. .-. 1.4
  ( .-._)| .-.\ | |     / /\ \   ( .-._)| | | |    
 (_) \   | |-' )| |    / /__\ \ (_) \   | `-' |    
 _  \ \  | |--' | |    |  __  | _  \ \  | .-. |    
( `-'  ) | |    | `--. | |  |)|( `-'  ) | | |)|    
 `----'  /(     |( __.'|_|  (_) `----'  /(  (_)    
        (__)    (_)                    (__)
"""



def create_splash(input_file, output_file, separator, spectrum_type, spectrum_col, origin_col):
    start_time = time.time()
    splasher = SplashVersion1()

    with open(input_file, 'r') as f, \
        (open(output_file, 'w') if output_file is not None else sys.stdout) as fout:

        for i, line in enumerate(f):
            # Handle input
            line = line.strip().split(separator)

            origin = line[origin_col - 1]
            spectrum_string = line[spectrum_col - 1]

            spectrum = Spectrum(spectrum_string, spectrum_type)
            splash_code = splasher.splash(spectrum)

            # Print the spectrum id with the calculated splash id
            print(splash_code, *line, sep = separator, file = fout)

            if (i + 1) % 10000 == 0:
                print('processed %d spectra, %.2f ms average time to splash a spectrum' % (i + 1, 1000 * (time.time() - start_time) / (i + 1)), file = sys.stderr)

    print('finished processing, processing took: %.2f s' % (time.time() - start_time), file = sys.stderr)
    print('processed %d spectra' % (i + 1), file = sys.stderr)
    print('average time including io to splash a spectra is %.2f ms' % (1000 * (time.time() - start_time) / (i + 1)), file = sys.stderr)



if __name__ == '__main__':
    print(SPLASH_BANNER, file = sys.stderr)

    # Define arguments
    parser = argparse.ArgumentParser(description = '')

    parser.add_argument('-s', '--spectrum', type = int, help = 'column containing the soectrum sting')
    parser.add_argument('-o', '--origin', type = int, help = 'column containing the origin information')

    parser.add_argument('-t', '--type', choices = ['MS', 'IR', 'UV', 'NMR', 'RAMAN'], default = 'MS', \
        required = True, help = 'spectrum type (default: %(default)s)')
    parser.add_argument('-T', '--separator', default = ',', help = 'separator character between columns (default: %(default)s)')

    parser.add_argument('input_file', help = 'input file for processing')
    parser.add_argument('output_file', nargs = '?', help = 'output file')

    args = parser.parse_args()


    # Validate arguments
    if args.spectrum is None or args.origin is None:
        raise argparse.ArgumentTypeError('Please specify the origin and spectrum column numbers')

    spectrum_type = SpectrumType.get(args.type)
    create_splash(args.input_file, args.output_file, args.separator, spectrum_type, args.spectrum, args.origin)