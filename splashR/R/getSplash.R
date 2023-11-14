
## Some constants

## Prefilter properties
PREFILTER_BASE = 3
PREFILTER_LENGTH = 10
PREFILTER_BIN_SIZE = 5

## Similarity histogram properties
SIMILARITY_BASE = 10
SIMILARITY_LENGTH = 10
SIMILARITY_BIN_SIZE = 100

EPS_CORRECTION = 1.0e-7

decimalavoidance <- function(x) {
    format(floor (x * 1000000), scientific=FALSE)
}

nist2matrix <- function(s) {
    t(sapply(
        unlist(strsplit(s, " "), use.names=F),
        function(y) as.numeric(unlist(strsplit(y, ":"))), USE.NAMES=FALSE
    ))
}

getBlockHash <- function(peaks,
                          RELATIVE_INTENSITY_SCALE=100,
                          MAX_HASH_CHARATERS_ENCODED_SPECTRUM=20) {
    max_intensity = max(peaks[,2])

    ## Scale to maximum intensity
    peaks[,2] <-    as.integer(peaks[,2] / max(peaks[,2]) * RELATIVE_INTENSITY_SCALE + EPS_CORRECTION)

    ## Sorted by ascending m/z and ties broken by descending intensity
    o <- order(peaks[,1], -1*peaks[,2], decreasing=FALSE)
    
    peakString <- paste(apply(peaks[o,,drop=FALSE], MARGIN=1,
                              FUN=function(x) {paste(c(decimalavoidance(x[1]+EPS_CORRECTION), x[2]),
                                  collapse=":")}), collapse=" ")

    ## cat("Prehash: ", peakString, sep="", file="/tmp/prehash-R") ## Debugging of spectrum-string
    block2 <- substr(digest(peakString, algo="sha256", serialize=FALSE),
                     1, MAX_HASH_CHARATERS_ENCODED_SPECTRUM)    
}

integer2base36 <- function(i) {
    integer2base36code <- sapply(c(48:57, 97:122), function(i) rawToChar(as.raw(i)))
    paste(integer2base36code[i+1], collapse="")
}


filter_spectrum <- function(peaks, top_ions = NULL, base_peak_percentage = NULL) {

    ## Filter first by base peak percentage if specified
    if (!missing(base_peak_percentage)) {
        base_peak_intensity = max(peaks[,2])
        peaks <- peaks[peaks[,2] >= base_peak_percentage * base_peak_intensity, , drop=FALSE]
    }

    ## Filter by top ions if specified
    if (!missing(top_ions)) {
        o <- order(-1*peaks[,2], peaks[,1], decreasing=FALSE)[seq(1:min(top_ions, nrow(peaks)))]
        peaks <- peaks[o, , drop=FALSE]
    }
    peaks
}

translate_base <- function(s, initial_base, final_base, fill_length) {
    n <- strtoi(s, initial_base)

    ns <- integer(fill_length)
    i <- 1
    
    while (n > 0) {
        ns[i] <- n %% final_base
        n <- n %/% final_base
        i <- i+1
    } 
    
    s <- integer2base36(rev(ns))
    
    return (s)
}

getBlockHist <- function(peaks, histBase, histLength, binSize) {
    ## Initialise output
    wrappedhist <- integer(histLength)

    ## Sorted by ascending m/z and ties broken by descending intensity
    o <- order(peaks[,1], -1*peaks[,2], decreasing=FALSE)
    peaks <- peaks[o,,drop=FALSE]
    
    binindex <- as.integer(peaks[,1] / binSize) 

    summedintensities <- tapply(peaks[,2], binindex, sum)
    wrappedbinindex <- unique(binindex) %% histLength
    wrappedintensities <- tapply(summedintensities, wrappedbinindex, sum)
    normalisedintensities <- as.integer(EPS_CORRECTION + (histBase-1) * wrappedintensities / max(wrappedintensities))
    
    wrappedhist[sort(unique(wrappedbinindex))+1] <- normalisedintensities
    paste(integer2base36(wrappedhist), collapse="")
}

getSplash <- function(peaks) {

    filteredPeaks <- filter_spectrum(peaks, 10, 0.1)
    
    block2 <-     translate_base(getBlockHist(filteredPeaks,
                           histBase=PREFILTER_BASE,
                           histLength=PREFILTER_LENGTH,
                           binSize=PREFILTER_BIN_SIZE),PREFILTER_BASE,36,4)

    block3 <- getBlockHist(peaks,
                           histBase=SIMILARITY_BASE,
                           histLength=SIMILARITY_LENGTH,
                           binSize=SIMILARITY_BIN_SIZE)

    block4 <- getBlockHash(peaks)

    splash <- paste("splash10",
                    block2,
                    block3,
                    block4,
                    sep="-")

    return(splash)
}
