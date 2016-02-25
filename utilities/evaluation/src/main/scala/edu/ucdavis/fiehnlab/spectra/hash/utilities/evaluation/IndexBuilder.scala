package edu.ucdavis.fiehnlab.spectra.hash.utilities.evaluation

import edu.ucdavis.fiehnlab.index._
import edu.ucdavis.fiehnlab.index.cache.SpectrumCache
import edu.ucdavis.fiehnlab.index.histogram.{SimilarHistogramIndex, HistogramIndex}
import edu.ucdavis.fiehnlab.math.histogram._
import edu.ucdavis.fiehnlab.math.histogram.SplashHistogram._

import edu.ucdavis.fiehnlab.math.spectrum.{BinByRoundingMethod, BinningMethod}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
  * Created by wohlgemuth on 2/19/16.
  */
@Component
class IndexBuilder {

  val binniningMethod: BinningMethod = new BinByRoundingMethod

  val spectraCache: SpectrumCache = SpectrumCache.create()

  /**
    * a list of predifined indexes to utilize
    *
    * @return
    */
  def build(): List[Index] = {

    val histogramList: List[Histogram] =
      new Top10IonsSeparationHistogram ::
        new Top10IonsModulo36Histogram ::
        Seq(8,10, 16, 36).collect {
          case base =>

            Seq(10, 15, 20, 25).collect {
              case length =>

                Seq(5, 10, 25, 50, 75,100).collect {

                  case bin =>
                    new SplashHistogram(base, length, bin)
                }
            }.flatten
        }.flatten.toList

    val histogramBasedIndex: List[Index] = histogramList.collect {
      case histogram => new HistogramIndex(binniningMethod, spectraCache, histogram)
    }.toList

    val indexList = new LinearIndex(binniningMethod, spectraCache) :: histogramBasedIndex

    indexList
  }

}
