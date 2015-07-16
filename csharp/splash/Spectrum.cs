using System;
using System.Collections.Generic;
using System.Text;
using System.Linq;

namespace NSSplash {
	
	public sealed class Spectrum {
		private SpectrumType type;
		public SpectrumType Type { 
			get { return type; }
			set { type = value; }
		}

		private List<Ion> ions = new List<Ion>();
		public List<Ion> Ions {
			get { return ions; }
			set { ions = value; }
		}

		public Spectrum (string data, SpectrumType type) {
			//checke data has data 
			if ("" == data) {
				throw new ArgumentException ("The spectrum data can't be null or empty.");
			}

			this.type = type;

			ions = new List<Ion>();
			Array splitData = data.Split (' ');
			foreach(string ion in splitData) {
				//get m/z
				double mz = Double.Parse(String.Format("{0:F6}", ion.Split(':')[0]));

				//get intensity
				double intensity = Double.Parse(String.Format("{0:F6}", ion.Split(':')[1]));

				Ion newIon = new Ion(mz, intensity);
				Ions.Add(newIon);
			}

			Ions = this.normalizeIons();
		}

		public override string ToString() {
			StringBuilder ionList = new StringBuilder();
			foreach(Ion ion in Ions) {
				ionList.Append(ion);
				ionList.Append(' ');
			}

			if(ionList.Length > 1) {
				ionList.Remove(ionList.Length - 1, 1);
			}

			return string.Format("[Spectrum: Type={0}, Ions={1}]", Type, ionList.ToString());
		}

		public List<Ion> getIonsByIntensity(bool asc = false) {
			List<Ion> sorted = Ions.OrderByDescending(i => i.Intensity).ThenBy(m => m.MZ).ToList();

			if(asc) { 
				sorted.Reverse();
			}

			return sorted;
		}

		public List<Ion> getIonsByMZ(bool desc = false) {
			List<Ion> sorted = Ions.OrderBy(i => i.MZ).ThenByDescending(i => i.Intensity).ToList();

			if(desc) {
				sorted.Reverse();
			}

			return sorted;
		}

		private List<Ion> normalizeIons() {
			double maxInt = Ions.Max(ion => ion.Intensity);
			Ions.ForEach(i => i.Intensity = i.Intensity / maxInt * 1000);

			return Ions;
		}
	}
}

