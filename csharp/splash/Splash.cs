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


		public string splashIt(ISpectrum spectrum, string sim) {

			// check spectrum var
			if (spectrum == null) {
				throw new ArgumentNullException ("The spectrum can't be null");
			}

			StringBuilder hash = new StringBuilder();

			//creating first block 'splash<type><version>'
			hash.Append(getFirstBlock(spectrum.getSpectrumType()));
			hash.Append('-');

			//creating second block 'top 10 peak hash' (SHA256)
			hash.Append(getTop10Hash(spectrum));
			hash.Append('-');

			//create the 3rd block
			hash.Append(getSpectrumBlock(spectrum));
			hash.Append('-');

			//create similarity block
			string fourth = string.Empty;
			switch (sim) {
				case "hist":
					fourth = getHistoBlock(spectrum);
					break;
				case "whist":
					fourth = getHistoBlock(spectrum, true);
					break;
				case "esblock":
					fourth = getESBlock(spectrum);
					break;
				case "wesblock":
					fourth = getESBlock(spectrum, true);
					break;
				default:
					fourth = getSumBlock(spectrum);
					break;
			}

			hash.Append(fourth);

			return hash.ToString();

		}

		private string getFirstBlock(SpectrumType specType) {
			return(PREFIX + (int)specType + VERSION);
		}

		private string getTop10Hash(ISpectrum spec) {
			List<Ion> ions = spec.getSortedIonsByIntensity();

			if(ions.Count > 10) {
				ions.RemoveRange(10, ions.Count - 10);
			}

			StringBuilder strIons = new StringBuilder();
			foreach(Ion i in ions) {
				strIons.Append(formatNumber(i.MZ));
				strIons.Append(" ");
			}


			//string to hash
			strIons.Remove(strIons.Length -1, 1);

			byte[] message = Encoding.UTF8.GetBytes(strIons.ToString());

			SHA256Managed hashString = new SHA256Managed();

			hashString.ComputeHash(message);

			string hash = BitConverter.ToString(hashString.Hash).Replace("-","").ToLower();
			hash = hash.Replace("-","").Substring(0,10);

//			Console.WriteLine("2nd block raw: {0}\n2rd block pro: {1}", strIons, hash);

			return hash;
		}


		//calculate the hash for the whole spectrum
		private string getSpectrumBlock(ISpectrum spec) {
			List<Ion> ions = spec.getSortedIonsByMZ();

			StringBuilder strIons = new StringBuilder();
			foreach(Ion i in ions) {
				strIons.Append(String.Format("{0}:{1}", formatNumber(i.MZ), formatNumber(i.Intensity)));
				strIons.Append(" ");
			}

			//string to hash
			strIons.Remove(strIons.Length -1, 1);

			byte[] message = Encoding.UTF8.GetBytes(strIons.ToString());

			SHA256Managed hashString = new SHA256Managed();

			hashString.ComputeHash(message);

			string hash = BitConverter.ToString(hashString.Hash);
			hash = hash.Replace("-","").Substring(0,20).ToLower();

//			Console.WriteLine("3nd block raw: {0}\n3rd block pro: {1}", strIons, hash);

			return hash;
		}

		//calculate Sum(mz*intensity) for top 100 ions (sorted by intensity desc)
		private string getSumBlock(ISpectrum spec) {
			BigInteger bisum = 0;
			List<Ion> ions = spec.getSortedIonsByIntensity();

			if(ions.Count > 100) {
				ions.RemoveRange(100, ions.Count - 100);
			}

			foreach(Ion i in ions) {
				bisum = BigInteger.Add(bisum, BigInteger.Multiply(new BigInteger(i.MZ * FACTOR), new BigInteger(i.Intensity * FACTOR)));
			}

			long sum = (long)BigInteger.Divide(bisum, new BigInteger(1000000000000));

//			Console.WriteLine(String.Format("Sum: {0}", sum));

			return String.Format("{0:D10}", (long)sum);
		}

		// calculates a histogram of the spectrum. If weighted, it sums the mz * intensities for the peaks in each bin
		private string getHistoBlock(ISpectrum spec, bool weighted = false) {
			string histogram = string.Empty;
			int[] data = {0,0,0,0,0,0,0,0,0,0};

			int index = 0;
			spec.getSortedIonsByMZ().ForEach( ion => {
				if(ion.MZ <= 100.0) {
					index = 0;
				} else if(ion.MZ <= 150.0) {
					index = 1;
				} else if(ion.MZ <= 200.0) {
					index = 2;
				} else if(ion.MZ <= 250.0) {
					index = 3;
				} else if(ion.MZ <= 300.0) {
					index = 4;
				} else if(ion.MZ <= 400.0) {
					index = 5;
				} else if(ion.MZ <= 500.0) {
					index = 6;
				} else if(ion.MZ <= 600.0) {
					index = 7;
				} else if(ion.MZ <= 700.0) {
					index = 8;
				} else {
					index = 9;
				}

				if(weighted) {
					data[index] += (int)(ion.Intensity * ion.MZ);
				} else {
					data[index] += (int)ion.Intensity;
				}
			});

			Console.WriteLine("size: {0}", data.Length );

			int max = data.Max();
			foreach(int bin in data) {
				int newbin = (int)Math.Ceiling((double)(bin * 9 / max));
				histogram = string.Concat(histogram, newbin.ToString());
			}

			return histogram;
		}

		private string getESBlock(ISpectrum spec, bool weighted = false) {
			string block = string.Empty;
			int[] data = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

			spec.getSortedIonsByMZ().ForEach(ion => {
				int full = (int)ion.MZ;
				int i = full % 10;

				if(weighted) {
					data[i] += (int)(ion.Intensity * ion.MZ);
				} else {
					data[i] += (int)ion.Intensity;
				}
			});

			int max = data.Max();
			foreach(int bin in data) {
				int newbin = (int)Math.Ceiling((double)(bin  * 9/ max));
				block = string.Concat(block, newbin.ToString());
			}

			return block;
		}

		private string formatNumber(double number) {
			return String.Format("{0}", (long)(number * FACTOR));
		}
	}
}

