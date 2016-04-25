using Microsoft.VisualStudio.TestTools.UnitTesting;

using NSSplash;
using NSSplash.impl;

namespace SplashTests {
	[TestClass]
	public class UnitTest1 {
		[TestMethod]
		public void TestIonFormatedToString()
		{

			Ion ion = new Ion(4.083286, 142.545026);

			Assert.AreEqual("4.083286:142.545026", ion.ToString());
		}

		[TestMethod]
		public void TestSpectrumToString()
		{
			string data = "130.0:0 99.0:25 100.0:50 125:100";

			ISpectrum spect = new MSSpectrum(data);

			Assert.AreEqual("[Spectrum: Type=MS, Ions=130.000000:0.000000 99.000000:25.000000 100.000000:50.000000 125.000000:100.000000]", spect.ToString());
		}

		[TestMethod]
		public void testMonaData() {
			string case1 = "66.0463:2.1827 105.0698:7.9976 103.0541:4.5676 130.065:8.6025 93.0572:0.2544 79.0542:4.4657 91.0541:2.5671 131.0728:2.6844 115.0541:1.3542 65.0384:0.6554 94.0412:0.5614 116.0494:1.2008 95.049:2.1338 117.0572:100 89.0385:11.7808 77.0385:3.3802 90.0463:35.6373 132.0806:2.343 105.0446:1.771";
			string case2 = "303.07:100 662.26:1.2111 454.91:1.2023 433.25:0.8864 432.11:2.308 592.89:3.9052 259.99:0.6406 281.14:1.2549 451.34:1.1847 499.85:1.2374 482.14:2.4133 450:23.5191 483:1.0004 285.25:1.448 253.1:46.5731 254.11:3.247 259.13:6.9241 304.14:17.2795";

			Assert.AreEqual("splash10-014i-4900000000-889a38f7ace2626a0435", new Splash().splashIt(new MSSpectrum(case1)));
			Assert.AreEqual("splash10-0udi-0049200000-ef488ecacceeaaadb4a2", new Splash().splashIt(new MSSpectrum(case2)));
		}
	}
}

