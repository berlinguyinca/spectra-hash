using System;

namespace NSSplash {
	public sealed class Ion : IComparable {
		private double mz = 0.0;
		private double intensity = 0.0;

		public double MZ { 
			get { return mz; } 
			set { mz = value; } 
		}

		public double Intensity { 
			get { return intensity; }
			set { intensity = value; }
		}

		public Ion (double mz, double intensity) {
			this.mz = mz;
			this.intensity = intensity;
		}

		//returning ion in mz:intensity format with 6 decimals
		public override string ToString() {
			return (String.Format("{0,5:F6}:{1,5:F6}", mz, intensity));
		}

		//compares by mz value
		public int CompareTo(Object other) {
			if (this.GetType () != other.GetType ()) {
				throw new ArgumentException (String.Format("Can't compare {0} with {1}.", this.GetType (), other.GetType ()));
			}

			Ion otherCpy = (Ion)other;

			if (this.intensity < otherCpy.intensity) {
				return -1;
			} else if (this.intensity > otherCpy.intensity) {
				return 1;
			} else {
				return otherCpy.mz.CompareTo(this.mz);
			}
		}
	}
}

