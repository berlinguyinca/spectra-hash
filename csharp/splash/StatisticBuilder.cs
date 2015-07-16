using System;

namespace NSSplash {
	public class StatisticBuilder {
		long count = 0;
		long sumTime = 0;

		public StatisticBuilder() {
		}

		public void addTime(long time) {
			sumTime += time;
			count++;
		}

		public string getTimeData() {
			return String.Format("It took {0}s to hash {1} spectra. Average: {2}s", sumTime/1000, count, sumTime/(1000*count));
		}
	}
}

