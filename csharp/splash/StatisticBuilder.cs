using System;

namespace NSSplash {
	public class StatisticBuilder {
		long count = 0;
		double sumTime = 0;

		public StatisticBuilder() {}

		public void addTime(double time) {
			sumTime += time;
			count++;
		}

		public string getTimeData() {
			return String.Format("It took {0:F2}s to hash {1} spectra including IO. Average: {2:F2}ms", sumTime/1000, count, sumTime/count);
		}
	}
}

