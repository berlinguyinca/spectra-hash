//
//  Splash.cs
//
//  Author:
//       Diego Pedrosa <dpedrosa@ucdavis.edu>
//
//  Copyright (c) 2015 Diego Pedrosa
//
//  This library is free software; you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as
//  published by the Free Software Foundation; either version 2.1 of the
//  License, or (at your option) any later version.
//
//  This library is distributed in the hope that it will be useful, but
//  WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//  Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public
//  License along with this library; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
using System;
using System.Text;
using System.Collections.Generic;
using System.Security.Cryptography;
using System.Linq;
using System.Numerics;
using NSSplash.impl;

namespace NSSplash {
	public class Splash : ISplash {
		private const string PREFIX = "splash";
		private const int VERSION = 0;
		private const int FACTOR = 1000000;
		private int BINS = 10;
		private int BIN_SIZE = 100;
		private int INITIAL_SCALE_FACTOR = 9;
		private int FINAL_SCALE_FACTOR = 9;


		//public string splashIt(ISpectrum spectrum, string sim) {
		public string splashIt(ISpectrum spectrum) {

			// check spectrum var
			if (spectrum == null) {
				throw new ArgumentNullException("The spectrum can't be null");
			}

			StringBuilder hash = new StringBuilder();

			//creating first block 'splash<type><version>'
			hash.Append(getFirstBlock(spectrum.getSpectrumType()));
			hash.Append('-');

			//create the spetrum block
			hash.Append(getSpectrumBlock(spectrum));
			hash.Append('-');

			//create histogram block
			string histogram = getHistoBlock(spectrum);
			hash.Append(histogram);

			return hash.ToString();

		}

		private string getFirstBlock(SpectrumType specType) {
			return (PREFIX + (int)specType + VERSION);
		}

		//calculate the hash for the whole spectrum
		private string getSpectrumBlock(ISpectrum spec) {
			List<Ion> ions = spec.getSortedIonsByMZ();

			StringBuilder strIons = new StringBuilder();
			foreach (Ion i in ions) {
				strIons.Append(String.Format("{0}:{1}", formatNumber(i.MZ), formatNumber(i.Intensity)));
				strIons.Append(" ");
			}

			//string to hash
			strIons.Remove(strIons.Length - 1, 1);

			byte[] message = Encoding.UTF8.GetBytes(strIons.ToString());

			SHA256Managed hashString = new SHA256Managed();
			hashString.ComputeHash(message);

			string hash = BitConverter.ToString(hashString.Hash);
			hash = hash.Replace("-", "").Substring(0, 20).ToLower();

			//			Console.WriteLine("3nd block raw: {0}\n3rd block pro: {1}", strIons, hash);

			return hash;
		}

		// calculates a histogram of the spectrum. If weighted, it sums the mz * intensities for the peaks in each bin
		private string getHistoBlock(ISpectrum spec) {
			List<double> binnedIons = new List<double>();
			double maxIntensity = 0;

			// initioalize and populate bins
			foreach (Ion i in spec.getSortedIonsByMZ()) {
				int index = (int)(i.MZ / BIN_SIZE);

				while (binnedIons.Count <= index) {
					binnedIons.Add(0.0);
				}

				double value = binnedIons[index] + i.Intensity;
				binnedIons[index] = value;

				if (value > maxIntensity) {
					maxIntensity = value;
				}

			}

			// Wrap the bins
			for (int i = BINS; i < binnedIons.Count; i++) {
				double value = binnedIons[i % BINS] + binnedIons[i];
				binnedIons[i % BINS] = value;
			}

			// Normalize
			maxIntensity = 0;
			for (int i = 0; i < BINS; i++) {
				if (i < binnedIons.Count) {
					if (binnedIons[i] > maxIntensity) {
						maxIntensity = binnedIons[i];
					}
				} else {
					binnedIons.Add(0.0);
				}
			}

			for (int i = 0; i < binnedIons.Count; i++) {
				binnedIons[i] = FINAL_SCALE_FACTOR * binnedIons[i] / maxIntensity;
			}

			StringBuilder histogram = new StringBuilder();

			foreach (double bin in binnedIons.GetRange(0, BINS)) {
				histogram.Append((int)bin);
			}

			return histogram.ToString();
		}

		private string formatNumber(double number) {
			return String.Format("{0}", (long)(number * FACTOR));
		}
	}
}

