using System;
using System.Text;
using System.Collections.Generic;
using System.Security.Cryptography;
using System.Linq;
using System.Numerics;

namespace NSSplash {
	public class Splash : ISplash {
		private const string PREFIX = "splash";
		private const int VERSION = 0;
		private const int FACTOR = 1000000;


		public string splashIt(Spectrum spectrum) {

			// check spectrum var
			if (spectrum == null) {
				throw new ArgumentNullException ("The spectrum can't be null");
			}

			StringBuilder hash = new StringBuilder();

			//creating first block 'splash<type><version>'
			hash.Append(getFirstBlock(spectrum.Type));
			hash.Append('-');

			//creating second block 'top 10 peak hash' (SHA256)
			hash.Append(getTop10Hash(spectrum));
			hash.Append('-');

			//create the 3rd block
			hash.Append(getSpectrumBlock(spectrum));
			hash.Append('-');

			//create version block
			hash.Append(getSumBlock(spectrum));

			return hash.ToString();

		}

		private string getFirstBlock(SpectrumType specType) {
			return(PREFIX + (int)specType + VERSION);
		}

		private string getTop10Hash(Spectrum spec) {
			List<Ion> ions = spec.getIonsByIntensity();

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
		private string getSpectrumBlock(Spectrum spec) {
			List<Ion> ions = spec.getIonsByMZ();

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
		private string getSumBlock(Spectrum spec) {
			BigInteger bisum = 0;
			List<Ion> ions = spec.getIonsByIntensity();

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

		private string formatNumber(double number) {
			return String.Format("{0}", (long)(number * FACTOR));
		}
	}
}

