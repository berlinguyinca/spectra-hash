from __future__ import print_function

import sys


if __name__ == '__main__':
    accurate_origins = set(map(str.strip, open('accurate-origins').readlines()))


    with open('highsim_accurate_56.csv', 'wb') as f1_56, open('highsim_accurate.csv', 'wb') as f1, open('highsim_nominal.csv', 'wb') as f2:
        for i, line in enumerate(sys.stdin):
            stats = line.strip().split(',')

            if len(stats) != 22:
                continue

            if stats[0] in accurate_origins and stats[1] in accurate_origins:
                if float(stats[5]) > 0.8 or float(stats[6]) > 0.8:
                    print(line.strip(), file = f1_56)
                else:
                    print(line.strip(), file = f1)
            else:
                print(line.strip(), file = f2)

            if i % 10000 == 0:
                print(i, file = sys.stderr)



header_rows =[
    'origin1' ,'origin2', 'hash_match',
    'nominal_sim', 'nominal_stein_sim', 'accurate_sim', 'accurate_stein_sim',
    'shist_manhattan', 'shist_cmanhattan', 'shist_levenshtein', 'shist_chi2', 'shist_bhattacharyya', 'shist_sim',
    'lhist_manhattan', 'lhist_cmanhattan', 'lhist_levenshtein', 'lhist_chi2', 'lhist_bhattacharyya', '', 'lhist_sim',
    'sum_sim', 'asum_sim'
]