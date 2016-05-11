#!/usr/bin/env python

from __future__ import print_function

import sys


if __name__ == '__main__':
    filename = sys.argv[1]
    basename, extension = '.'.join(filename.split('.')[:-1]), filename.split('.')[-1]

    if basename == '':
        basename = filename
        extension = 'csv'

    with open(filename, 'rb') as fin, open(basename + '_exact.'+ extension, 'w') as fout_exact, \
         open(basename + '_exact.'+ extension, 'w') as fout_exact, \
         open(basename + '_highsim.'+ extension, 'w') as fout_highsim, \
         open(basename + '_midsim.'+ extension, 'w') as fout_midsim:

        for i, line in enumerate(fin):
            stats = line.strip().split(',')

            if len(stats) != 22:
                break

            if stats[2] == 'true':
                print(line.strip(), file = fout_exact)
        
            elif float(stats[3]) >= 0.9 or float(stats[4]) >= 0.9 or float(stats[5]) >= 0.9 or float(stats[6]) >= 0.9:
                print(line.strip(), file = fout_highsim)
                
            elif float(stats[3]) >= 0.75 or float(stats[4]) >= 0.75 or float(stats[5]) >= 0.75 or float(stats[6]) >= 0.75:
                print(line.strip(), file = fout_midsim)

        if i % 100000 == 0:
            print(i)