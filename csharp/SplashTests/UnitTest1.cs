using Microsoft.VisualStudio.TestTools.UnitTesting;

using NSSplash;
using NSSplash.impl;

namespace SplashTests {
	[TestClass]
	public class UnitTest1 {
		[TestMethod]
		public void TestIonFormatedToString() {

			Ion ion = new Ion(4.083286, 142.545026);

			Assert.AreEqual("4.083286:142.545026", ion.ToString());
		}

		[TestMethod]
		public void TestSpectrumToString() {
			string data = "4.083286:142.545026 89.396063:197.067034 422.053039:190.703821 1105.751691:76.850996 1184.133855:274.317985 1333.364023:53.292152";

			ISpectrum spect = new MSSpectrum(data);

			Assert.AreEqual("[Spectrum: Type=MS, Ions=4.083286:51.963427 89.396063:71.838904 422.053039:69.519256 1105.751691:28.015296 1184.133855:100.000000 1333.364023:19.427145]", spect.ToString());
		}

		[TestMethod]
		public void testGoodSpectrumSplash() {
			string data = "4.083286:142.545026 89.396063:197.067034 422.053039:190.703821 1105.751691:76.850996 1184.133855:274.317985 1333.364023:53.292152";

			ISpectrum spect = new MSSpectrum(data);

			Assert.AreEqual("splash10-xz05j00000-b25c1dc1550eb2e12255", new Splash().splashIt(spect));
		}
	}
}

