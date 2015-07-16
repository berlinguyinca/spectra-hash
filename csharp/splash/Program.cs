using System;
using NSSplash;
using System.Security.Cryptography;
using System.Diagnostics;
using System.IO;
using System.Text;

namespace NSSplash {
	class SplashRunner {
		private static int LIMIT = 10000;
		Splash splasher;

		public static void Main(string[] args) {
			SplashRunner app = new SplashRunner();

			app.process(args);
		}

		public SplashRunner() {
			splasher = new Splash();
		}

		public void process(string[] args) {
			string res = "";
			string validator = "";
			DateTime sTime;


			sTime = DateTime.Now;
			res = splasher.splashIt(new Spectrum("5.0000001:1.0 5.0000005:0.5 10.02395773287:2.0 11.234568:.10", SpectrumType.MS));
//			validator = "splash10-03cde00561-af1f5ec36b0457d92eee-0000014336";
			validator = "splash10-03cde00561-af1f5ec36b0457d92eee-0000014335";

//			Console.WriteLine("mine: " + res);
//			Console.WriteLine("gert: " + validator);
			Console.WriteLine(SHA256.Equals(res, validator)?"YAY!!! They match":"BOOO!! Try again...");
//			Console.WriteLine();

			res = splasher.splashIt(new Spectrum("100:1 101:2 102:3", SpectrumType.MS));
//			validator = "splash10-72a283bc75-7bd14a1e3e797fe3f2f2-0000202667";
			validator = "splash10-72a283bc75-7bd14a1e3e797fe3f2f2-0000202666";

//			Console.WriteLine("mine: " + res);
//			Console.WriteLine("gert: " + validator);
			Console.WriteLine(SHA256.Equals(res, validator)?"YAY!!! They match":"BOOO!! Try again...");
//			Console.WriteLine();

			res = splasher.splashIt(new Spectrum("12:12 13:7 14:28 15:999 16:57 17:302 18:975 19:8 25:5 26:26 27:142 28:398 29:239 30:49 31:100 32:20 33:27 35:4 36:19 38:31 39:100 40:53 41:176 42:99 43:122 44:117 45:155 46:9 47:7 49:6 50:28 51:59 52:72 53:63 54:43 55:66 56:55 57:70 59:404 60:21 61:12 62:21 63:47 64:27 65:45 66:20 67:46 68:15 69:43 70:29 71:24 72:19 73:21 74:25 75:79 76:40 77:47 78:26 79:14 80:18 81:20 82:64 84:144 85:43 86:29 87:31 88:36 89:70 90:31 91:31 92:16 93:12 94:14 96:100 98:18 99:52 100:52 101:100 102:71 103:51 104:25 105:20 106:9 107:12 108:18 110:135 111:58 112:38 113:114 114:87 115:80 116:40 117:38 118:15 119:15 120:21 121:20 122:11 123:73 124:52 125:68 126:92 127:115 128:96 129:37 130:16 131:13 132:21 133:13 134:15 135:18 136:166 137:111 138:135 139:218 140:166 141:116 142:59 143:36 144:11 145:15 146:9 147:14 148:29 149:83 150:47 151:73 152:89 153:62 154:42 155:28 156:32 157:45 158:14 159:8 160:14 161:21 162:46 163:46 164:165 165:136 166:159 167:80 168:45 169:18 170:10 171:21 172:12 173:86 174:40 175:43 176:32 177:41 178:37 179:74 180:56 181:44 182:17 183:8 184:3 185:6 186:13 187:20 188:28 189:27 190:45 191:333 192:578 193:212 194:67 195:40 196:9 197:5 198:12 199:15 200:96 201:59 202:55 203:62 204:35 205:98 206:104 207:86 208:50 209:14 210:5 211:3 212:8 213:21 214:49 215:52 216:53 217:43 218:49 219:87 220:81 221:53 222:21 223:9 224:6 225:27 226:83 227:322 228:244 229:202 230:98 231:39 232:17 233:14 234:18 235:10 236:9 237:12 238:7 239:20 240:31 241:79 242:59 243:94 244:40 245:34 246:10 247:14 248:12 249:9 250:9 251:12 252:23 253:34 254:129 255:207 256:113 257:123 258:39 259:27 260:23 261:15 262:11 263:10 264:10 265:6 266:9 267:13 268:11 269:22 270:18 271:43 272:35 273:23 274:13 275:7 276:5 277:4 278:6 279:15 280:12 281:11 282:6 283:27 284:31 285:177 286:138 287:190 288:77 289:50 290:12 291:4 292:2 293:1 294:1 295:6 296:8 297:6 298:6 299:238 300:52 301:89 302:23 303:15 304:5 305:4 306:0 308:0 309:2 310:15 311:13 312:14 313:132 314:31 315:72 316:28 317:19 318:14 319:6 320:7 321:2 322:3 323:0 324:0 325:1 326:0 327:3 328:3 329:26 330:10 331:285 332:55 333:96 334:17 335:3 336:0 339:3 340:8 341:6 342:4 343:27 345:963 346:202 347:354 348:71 349:9 350:1 354:1 355:0 356:0 357:7 358:2 359:3 360:0 361:1 369:1 370:0 371:5 372:9 373:37 374:12 375:16 376:3 377:1 389:3 390:0 391:1 402:2 404:447 405:108 406:178 407:37 408:4 409:0", SpectrumType.MS));
			validator = "splash10-7218e3efa9-2520d1d01cc8936959df-0002737328";

//			Console.WriteLine("mine  : " + res);
//			Console.WriteLine("sajjan: " + validator);
			Console.WriteLine(SHA256.Equals(res, validator)?"YAY!!! They match":"BOOO!! Try again...");
//			Console.WriteLine();


			Console.WriteLine("Time: " + (DateTime.Now - sTime).TotalSeconds + "s");

			if(!String.IsNullOrEmpty(args[0])) {
				this.hashFile(args[0]);
			}
		}

		public void hashFile(string filename) {
			StatisticBuilder stats = new StatisticBuilder();
			StringBuilder results = new StringBuilder();
			DateTime sTime, eTime;
			int count = 0;

			sTime = DateTime.Now;
			using (StreamReader sr = File.OpenText(filename)) {
				string s = String.Empty;
				while ((s = sr.ReadLine()) != null)	{
					string[] input = s.Split(',');
					if(count % 100 < 1) {
						Console.WriteLine("{0} ({1})", input[0], count);
					}
					string hash = splasher.splashIt(new Spectrum(input[1], SpectrumType.MS));
					eTime = DateTime.Now;
					stats.addTime((eTime - sTime).Milliseconds);

					results.AppendLine(String.Format("{0},{1},{2}", input[0], hash, input[1]));
					count++;
				}
			}

			FileInfo file = new FileInfo("complete-hash-csharp.csv");

			if(file.Exists) {
				file.Delete();
			}
			using(StreamWriter fout = new StreamWriter(File.OpenWrite("complete-hash-csharp.csv"))) {
				fout.AutoFlush = true;
				foreach(string line in results.ToString().Split('\n')) {
					fout.WriteLine(line);
				}
				fout.Flush();
			}

			Console.WriteLine(stats.getTimeData());
		}
	}
}
	