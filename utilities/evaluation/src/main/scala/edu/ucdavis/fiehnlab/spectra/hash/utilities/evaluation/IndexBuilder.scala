package edu.ucdavis.fiehnlab.spectra.hash.utilities.evaluation

import edu.ucdavis.fiehnlab.index._
import edu.ucdavis.fiehnlab.index.cache.SpectrumCache
import edu.ucdavis.fiehnlab.index.histogram.{SimilarHistogramIndex, HistogramIndex}
import edu.ucdavis.fiehnlab.math.histogram._
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

  val histogram: List[Histogram] = new Base36Length10BinSize10Histogram :: new Base36Length10BinSize25Histogram :: new Base36Length10BinSize50Histogram :: new Base36Length10BinSize100Histogram :: Nil

  /**
    * a list of predifined indexes to utilize
    *
    * @return
    */
  def build(): List[Index] = {

    val histogramList = histogram.collect {
      case x => new HistogramIndex(binniningMethod, spectraCache, x)
    }

    val indexList =  new SimilarHistogramIndex(binniningMethod,spectraCache,new Base36Length10BinSize100Histogram,0.9)  :: new LinearIndex(binniningMethod, spectraCache) :: histogramList

    indexList
  }

}
