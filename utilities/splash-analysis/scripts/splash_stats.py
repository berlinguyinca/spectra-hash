import pandas as pd
import numpy as np
import matplotlib
import matplotlib.pyplot as plt
from matplotlib.backends.backend_pdf import PdfPages


header_rows =[
    'origin1' ,'origin2', 'hash_match',
    'nominal_sim', 'nominal_stein_sim', 'accurate_sim', 'accurate_stein_sim',
    'shist_manhattan', 'shist_cmanhattan', 'shist_levenshtein', 'shist_chi2', 'shist_bhattacharyya', 'shist_sim',
    'lhist_manhattan', 'lhist_cmanhattan', 'lhist_levenshtein', 'lhist_chi2', 'lhist_bhattacharyya', '', 'lhist_sim',
    'sum_sim', 'asum_sim'
]

FILENAME = 'highsim_accurate_56.csv'
FILENAME = '20151105/highsim'

df = pd.read_csv(FILENAME, names = header_rows, index_col = [0, 1])


def bin_data(x, y, bins = 25, err_scale = 0.25):
	t = np.linspace(0.95, 1.0, bins + 1)

	y_mean, y_std = [], []

	for i in range(bins):
		data = y[(x >= t[i]) & (x < t[i + 1])]
		y_mean.append(np.mean(data))
		y_std.append(np.std(data) / len(data)**err_scale)

	t = [(t[i] + t[i + 1]) / 2 for i in range(bins)]
	return t, np.array(y_mean), np.array(y_std)




with PdfPages('plots.pdf') as pdf:
	plt.figure()
	plt.title('Similarity Distribution')
	plt.xlabel('Spectral Similarity Score')
	plt.ylabel('N')

	plt.hist(df.accurate_sim, bins = 15, histtype = 'step', label = 'Dot Product')
	plt.hist(df.accurate_stein_sim, bins = 15, histtype = 'step', label = 'Transformed Dot Product - Short Histogram')

	min(min(df.accurate_sim), min(df.accurate_stein_sim))

	plt.xlim((min(min(df.accurate_sim), min(df.accurate_stein_sim)), max(max(df.accurate_sim), max(df.accurate_stein_sim))))
	plt.legend(loc = 'upper left', fontsize = 'x-small')
	pdf.savefig()


	plt.figure()
	plt.title('Levenshtein Distance')
	plt.xlabel('Spectral Similarity Score')
	plt.ylabel('Levenshtein Distance')

	t, y, std = bin_data(df.accurate_sim, df.shist_levenshtein, 25)
	plt.errorbar(t, y, yerr = std, label = 'Dot Product - Short Histogram')

	t, y, std = bin_data(df.accurate_stein_sim, df.shist_levenshtein, 25)
	plt.errorbar(t, y, yerr = std, label = 'Transformed Dot Product - Short Histogram')

	t, y, std = bin_data(df.accurate_sim, df.lhist_levenshtein, 25)
	plt.errorbar(t, y, yerr = std, label = 'Dot Product - Long Histogram')

	t, y, std = bin_data(df.accurate_stein_sim, df.lhist_levenshtein, 25)
	plt.errorbar(t, y, yerr = std, label = 'Transformed Dot Product - Long Histogram')

	plt.xlim((min(t), max(t)))
	plt.legend(loc = 'upper right', fontsize = 'x-small')
	pdf.savefig()



	plt.figure()
	plt.title('Manhattan Distance')
	plt.xlabel('Spectral Similarity Score')
	plt.ylabel('Manhattan Distance')

	t, y, std = bin_data(df.accurate_sim, df.shist_manhattan, 25)
	plt.errorbar(t, y, yerr = std, label = 'Dot Product - Short Histogram')

	t, y, std = bin_data(df.accurate_stein_sim, df.shist_manhattan, 25)
	plt.errorbar(t, y, yerr = std, label = 'Transformed Dot Product - Short Histogram')

	t, y, std = bin_data(df.accurate_sim, df.lhist_manhattan, 25)
	plt.errorbar(t, y, yerr = std, label = 'Dot Product - Long Histogram')

	t, y, std = bin_data(df.accurate_stein_sim, df.lhist_manhattan, 25)
	plt.errorbar(t, y, yerr = std, label = 'Transformed Dot Product - Long Histogram')

	plt.xlim((min(t), max(t)))
	plt.legend(loc = 'upper right', fontsize = 'x-small')
	pdf.savefig()



	plt.figure()
	plt.title('Manhattan Similarity')
	plt.xlabel('Spectral Similarity Score')
	plt.ylabel('Manhattan Similarity')

	t, y, std = bin_data(df.accurate_sim, 1 - df.shist_manhattan / 36 / 10, 25)
	plt.errorbar(t, y, yerr = std, label = 'Dot Product - Short Histogram')

	t, y, std = bin_data(df.accurate_stein_sim, 1 - df.shist_manhattan / 36 / 10, 25)
	plt.errorbar(t, y, yerr = std, label = 'Transformed Dot Product - Short Histogram')

	t, y, std = bin_data(df.accurate_sim, 1 - df.lhist_manhattan / 36 / 20, 25)
	plt.errorbar(t, y, yerr = std, label = 'Dot Product - Long Histogram')

	t, y, std = bin_data(df.accurate_stein_sim, 1 - df.lhist_manhattan / 36 / 20, 25)
	plt.errorbar(t, y, yerr = std, label = 'Transformed Dot Product - Long Histogram')

	plt.xlim((min(t), max(t)))
	plt.legend(loc = 'lower right', fontsize = 'x-small')
	pdf.savefig()



	plt.figure()
	plt.title(r'$\chi^2$ Distance')
	plt.xlabel('Spectral Similarity Score')
	plt.ylabel(r'$\chi^2$ Distance')

	t, y, std = bin_data(df.accurate_sim, df.shist_chi2, 25)
	plt.errorbar(t, y, yerr = std, label = 'Dot Product - Short Histogram')

	t, y, std = bin_data(df.accurate_stein_sim, df.shist_chi2, 25)
	plt.errorbar(t, y, yerr = std, label = 'Transformed Dot Product - Short Histogram')

	t, y, std = bin_data(df.accurate_sim, df.lhist_chi2, 25)
	plt.errorbar(t, y, yerr = std, label = 'Dot Product - Long Histogram')

	t, y, std = bin_data(df.accurate_stein_sim, df.lhist_chi2, 25)
	plt.errorbar(t, y, yerr = std, label = 'Transformed Dot Product - Long Histogram')

	plt.xlim((min(t), max(t)))
	plt.legend(loc = 'upper right', fontsize = 'x-small')
	pdf.savefig()



	plt.figure()
	plt.title('Bhattacharyya Distance')
	plt.xlabel('Spectral Similarity Score')
	plt.ylabel('Bhattacharyya Distance')

	t, y, std = bin_data(df.accurate_sim, df.shist_bhattacharyya, 25)
	plt.errorbar(t, y, yerr = std, label = 'Dot Product - Short Histogram')

	t, y, std = bin_data(df.accurate_stein_sim, df.shist_bhattacharyya, 25)
	plt.errorbar(t, y, yerr = std, label = 'Transformed Dot Product - Short Histogram')

	t, y, std = bin_data(df.accurate_sim, df.lhist_bhattacharyya, 25)
	plt.errorbar(t, y, yerr = std, label = 'Dot Product - Long Histogram')

	t, y, std = bin_data(df.accurate_stein_sim, df.lhist_bhattacharyya, 25)
	plt.errorbar(t, y, yerr = std, label = 'Transformed Dot Product - Long Histogram')

	plt.xlim((min(t), max(t)))
	plt.legend(loc = 'upper right', fontsize = 'x-small')
	pdf.savefig()



	plt.figure()
	plt.title('Histogram Dot Product')
	plt.xlabel('Spectral Similarity Score')
	plt.ylabel('Histogram Dot Product')

	t, y, std = bin_data(df.accurate_sim, df.shist_sim, 25)
	plt.errorbar(t, y, yerr = std, label = 'Dot Product - Short Histogram')

	t, y, std = bin_data(df.accurate_stein_sim, df.shist_sim, 25)
	plt.errorbar(t, y, yerr = std, label = 'Transformed Dot Product - Short Histogram')

	t, y, std = bin_data(df.accurate_sim, df.lhist_sim, 25)
	plt.errorbar(t, y, yerr = std, label = 'Dot Product - Long Histogram')

	t, y, std = bin_data(df.accurate_stein_sim, df.lhist_sim, 25)
	plt.errorbar(t, y, yerr = std, label = 'Transformed Dot Product - Long Histogram')

	plt.xlim((min(t), max(t)))
	plt.legend(loc = 'lower right', fontsize = 'x-small')
	pdf.savefig()




	plt.figure()
	plt.title('Spectrum Sum Difference')
	plt.xlabel('Spectral Similarity Score')
	plt.ylabel('Spectrum Sum Difference')

	t, y, std = bin_data(df.accurate_sim, df.sum_sim, 25, 0.125)
	plt.errorbar(t, y, yerr = std, label = 'Dot Product - Sum')

	t, y, std = bin_data(df.accurate_stein_sim, df.sum_sim, 25, 0.125)
	plt.errorbar(t, y, yerr = std, label = 'Transformed Dot Product - Sum')

	plt.xlim((min(t), max(t)))
	plt.legend(loc = 'upper right', fontsize = 'x-small')
	pdf.savefig()




	nist_msms_28047 = '78.9591:999.00 80.9633:2.80 96.9697:57.34 134.9854:5.19 150.9803:6.39 152.9959:171.43 153.9992:5.59 171.0064:17.48 237.2223:7.69 238.2259:1.10 255.2330:162.44 256.2363:25.67 257.2393:1.80'
	nist_msms_28272 = '56.3603:1.80 78.9590:999.00 79.9631:2.70 80.9632:6.69 83.0502:4.60 96.9696:69.93 121.1020:6.79 134.9854:8.69 135.1180:9.19 147.1177:2.70 150.9801:7.59 152.9958:346.15 153.9992:19.08 155.0001:2.90 161.1333:2.80 163.0761:2.00 171.0062:66.43 172.0093:2.00 209.0222:2.00 227.0320:7.39 237.2224:7.09 255.2327:425.67 256.2361:162.14 257.2394:5.99 283.2425:4.30'

	nist_msms_27496 = '81.0696:6.19 83.0488:1.60 93.0695:2.00 95.0852:8.39 97.0644:46.25 97.1009:4.30 107.0853:4.50 109.0645:8.59 109.1009:14.09 119.0852:3.30 121.0646:2.70 121.1010:8.39 123.0801:3.20 123.1167:2.80 125.0958:4.70 127.1114:4.80 131.0852:3.10 133.1009:4.30 135.1166:6.79 137.0957:3.30 143.0852:2.30 143.1064:3.70 145.1009:5.00 147.0801:2.00 147.1166:5.59 149.0959:4.80 149.1323:3.10 157.1009:7.39 159.1165:6.59 161.0958:6.19 161.1322:3.90 163.1114:5.89 163.1478:1.80 167.1427:2.60 171.1166:3.30 173.0957:4.70 173.1321:4.40 175.1113:8.29 175.1477:2.60 177.1270:46.95 177.1632:2.90 185.1320:3.20 187.1479:2.10 189.1271:3.40 189.1634:2.50 191.1427:1.70 191.1790:4.60 199.1479:1.60 201.1633:3.20 211.1477:1.90 215.1429:2.80 219.1740:4.20 229.1584:3.20 235.1688:3.00 237.1844:10.09 239.1792:1.70 241.1581:2.20 243.1738:7.79 249.1844:3.10 251.1790:4.20 253.1948:5.89 255.1737:5.79 257.1895:3.70 259.2053:3.40 267.1740:5.49 269.1895:52.45 271.2051:18.88 277.2156:43.66 279.2100:1.80 283.2053:4.00 285.2207:7.79 287.2001:1.80 289.2157:3.10 295.2049:4.20 297.2206:29.77 299.2359:5.19 309.2566:1.70 317.2466:6.39 337.2516:3.60 349.2881:4.80 355.2625:7.49 359.2725:3.40 367.2988:83.82 377.2832:89.21 395.2937:432.37 413.3042:999.00'
	nist_msms_27497 = '67.0538:3.30 69.0695:5.59 79.0537:2.80 81.0695:32.77 83.0487:11.79 83.0850:7.39 91.0536:2.40 93.0694:10.69 95.0852:47.15 97.0644:141.06 97.1008:24.58 105.0694:9.39 107.0852:26.67 109.0645:36.46 109.1009:56.04 111.0800:2.30 111.1162:3.50 119.0853:20.18 121.0645:13.49 121.1009:35.66 123.0801:17.18 123.1166:14.69 125.0958:14.39 127.1113:12.79 131.0852:13.89 133.1009:22.88 135.0799:2.10 135.1166:32.57 137.0958:14.89 137.1319:4.10 139.1112:3.20 143.0851:10.99 143.1063:10.29 145.1009:25.87 147.0800:10.09 147.1166:25.27 149.0958:22.08 149.1321:19.28 151.1112:5.59 155.0849:6.99 157.1008:23.68 159.1165:28.27 161.0958:25.37 161.1322:17.78 163.1114:25.07 163.1476:9.59 165.1268:7.09 167.1425:5.99 169.1007:8.19 171.1164:15.88 173.0958:21.18 173.1321:22.08 175.1114:43.86 175.1476:14.09 177.1270:193.71 177.1632:14.89 183.1163:6.89 185.1319:13.99 187.1112:7.29 187.1476:12.49 189.1270:18.18 189.1632:12.79 191.1425:10.79 191.1791:19.88 195.1374:5.00 197.1321:7.39 199.1476:8.29 201.1268:7.09 201.1634:14.29 203.1425:5.00 203.1786:2.40 205.1581:3.10 209.1322:2.00 211.1476:8.49 213.1634:5.49 215.1427:9.09 217.1581:4.20 219.1738:8.99 223.1689:3.80 225.1633:4.50 227.1426:7.29 229.1584:22.08 231.1739:6.49 235.1688:10.49 237.1634:2.10 237.1844:21.68 239.1789:9.89 241.1582:11.59 241.1946:7.29 243.1739:21.38 249.1846:7.29 251.1789:16.68 253.1946:20.48 255.1738:19.58 257.1894:10.99 259.2051:9.99 263.2000:4.20 267.1737:17.18 269.1895:152.65 270.1975:4.00 271.2051:54.15 273.2209:2.00 277.2156:127.37 279.2101:9.19 281.1896:4.10 281.2259:4.60 283.2050:13.49 285.1842:3.30 285.2207:22.08 287.2001:7.09 287.2363:4.30 289.2156:7.49 295.2049:13.29 297.2207:82.62 299.2362:12.59 309.2569:7.19 311.2362:2.90 313.2159:2.00 313.2515:2.10 317.2469:10.29 329.2466:2.20 331.2626:2.80 337.2519:10.09 349.2880:15.08 355.2624:21.28 359.2728:8.79 367.2988:249.65 377.2832:168.03 395.2937:641.26 413.3042:999.00'

	plt.figure()
	ax = plt.subplot(2, 1, 1)
	plt.title('High Spectral Similarity, Low Histogram Similarity')

	for ion in nist_msms_27496.split():
		mz, intensity = map(float, ion.split(':'))
		plt.plot([mz, mz], [0, intensity], 'b-')

	ax.text(100, 900, 'NIST14 MSMS 27496', fontsize = 14)

	text = 'Dot Product: 0.954908\n'
	text += 'Transformed Dot Product: 0.972003\n\n'

	text += 'Manhattan Distance: 67\n'
	text += 'Manhattan Similarity: 0.813888\n'
	text += 'Histogram Dot Product: 0.849184\n\n'

	text += 'Long Histogram Manhattan Distance: 70\n'
	text += 'Long Histogram Manhattan Similarity: 0.902777\n'
	text += 'Long Histogram Histogram Dot Product: 0.884707\n'

	ax.text(100, 200, text, fontsize = 8)
	
	plt.ylim((0, 1050))
	plt.ylabel('Intensity')


	ax = plt.subplot(2, 1, 2)

	for ion in nist_msms_27497.split():
		mz, intensity = map(float, ion.split(':'))
		plt.plot([mz, mz], [0, intensity], 'b-')
	
	ax.text(100, 900, 'NIST14 MSMS 27497', fontsize = 14)

	plt.ylim((0, 1050))
	plt.xlabel('m/z')
	plt.ylabel('Intensity')

	pdf.savefig()