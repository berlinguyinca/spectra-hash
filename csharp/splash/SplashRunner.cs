//
//  SplashRunner.cs
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
using NSSplash.impl;
using System.Security.Cryptography;
using System.Diagnostics;
using System.IO;
using System.Text;

namespace NSSplash {
	class SplashRunner {
//		private static int LIMIT = 10000;
		private static int UPDATE_INTERVAL = 10000;
		Splash splasher;

		public static void Main(string[] args) {
			SplashRunner app = new SplashRunner();

			string error = "\nPlease provide a filename (<name>.csv) with spectra to hash.\nFile should contain a list of coma separated values in the form 'identifier,spectrum' each on a separate line.\n";

			if(args.Length > 0) {
				if(args[0].EndsWith(".csv") && (new FileInfo(args[0])).Exists) {
					app.hashFile(args[0]);
				} else {
					string fname = String.Format("{0}.csv", args[0]);
					FileInfo fin = new FileInfo(fname);

					if(fin.Exists) {
						app.hashFile(fname);
					} else {
						Console.WriteLine(error);
					}
				}
			} else {
				Console.WriteLine(error);
			}
		}

		public SplashRunner() {
			splasher = new Splash();
		}

		public void hashFile(string filename) {
			StatisticBuilder stats = new StatisticBuilder();
			DateTime sTime, eTime;
			int count = 0;

			FileInfo file = new FileInfo(String.Format("{0}-csharp.csv", filename.Substring(0,filename.LastIndexOf('.'))));
			if(file.Exists) {
				file.Delete();
			}

			sTime = DateTime.Now;

			using (StreamReader sr = File.OpenText(filename)) {
				string s = String.Empty;


				using(StreamWriter fout = new StreamWriter(File.OpenWrite(file.Name))) {
					StringBuilder result = new StringBuilder();
					fout.AutoFlush = true;

					while ((s = sr.ReadLine()) != null)	{
						string[] input = s.Split(',');
						DateTime psTime = DateTime.Now;
						string hash = splasher.splashIt(new MSSpectrum(input[1]));
						DateTime peTime = DateTime.Now;
						TimeSpan lap = new TimeSpan();

						if(count % UPDATE_INTERVAL == 0) {
							lap = DateTime.Now.Subtract(sTime);
							Console.WriteLine("Elapsed {2:F2}s, average {3:F2}ms, this item: {4:F2}ms - {0} [{1}]", input[0], count, lap.TotalSeconds, lap.TotalMilliseconds/(count+1), peTime.Subtract(psTime).TotalMilliseconds);
						}

						result.Append(input[0]).Append(",").Append(hash).Append(",").Append(input[1]);
						fout.WriteLine(String.Format(result.ToString()));
						result.Clear();

						eTime = DateTime.Now;
						stats.addTime(lap.TotalMilliseconds);
						count++;
					}

					fout.Flush();
				}
			}

			Console.WriteLine(stats.getTimeData());
		}
	}
}
	