library(digest) # for digest


## Some constants, taken from SplashVersion1.java
BINS <- 10;
BIN_SIZE <- 100;
FINAL_SCALE_FACTOR <- 9;

decimalavoidance <- function(x) {
    format(floor (x * 1000000), scientific=FALSE)
}

getBlock2Full <- function(peaks,
                          RELATIVE_INTENSITY_SCALE=1000.0,
                          MAX_HASH_CHARATERS_ENCODED_SPECTRUM=20) {
    max_intensity = max(peaks[,2])

    ## Scale to maximum intensity
    peaks[,2] <-    peaks[,2] / max(peaks[,2]) * RELATIVE_INTENSITY_SCALE

    ## Sorted by ascending m/z and ties broken by descending intensity
    o <- order(peaks[,1], -1*peaks[,2], decreasing=FALSE)
    
    peakString <- paste(apply(peaks[o,,drop=FALSE], MARGIN=1,
                              FUN=function(x) {paste(c(decimalavoidance(x[1]),
                                                       decimalavoidance(x[2])),
                                  collapse=":")}), collapse=" ")
    ## cat("Prehash: ", peakString, sep="", file="/tmp/prehash-R") ## Debugging of spectrum-string
    block2 <- substr(digest(peakString, algo="sha256", serialize=FALSE),
                     1, MAX_HASH_CHARATERS_ENCODED_SPECTRUM)    
}

getBlock3Hist <- function(peaks) {

    ## Initialise output
    wrappedhist <- integer(BINS)

    binindex <- as.integer(peaks[,1] / BIN_SIZE) 

    summedintensities <- tapply(peaks[,2], binindex, sum)
    wrappedbinindex <- unique(binindex) %% BINS 
    wrappedintensities <- tapply(summedintensities, wrappedbinindex, sum)
    normalisedintensities <- as.integer(wrappedintensities/max(wrappedintensities)*FINAL_SCALE_FACTOR)
    
    wrappedhist[sort(unique(wrappedbinindex))+1] <- normalisedintensities

    paste(wrappedhist, collapse="")


    ## nominalmz-5-1  
    ## "0009041010 != 0007092030" 
  
}

getSplash <- function(peaks) {

    block2 <- getBlock2Full(peaks)
    block3 <- getBlock3Hist(peaks)

    splash <- paste("splash10",
                    block2,
                    block3,
                    sep="-")

    return(splash)
}

##
## Read test data from splash distribution.
## Requires 
##

filedata <- read.csv("../base-dataset/spectra/test-set-not-splashed-v1.csv",
                     header=FALSE, stringsAsFactors=FALSE)

spectra <- filedata[,2]
names(spectra) <- filedata[,1]

peaks <- lapply(spectra, function(s) {
                    t(sapply(unlist(strsplit(s, " "), use.names=F),
                             function(y) as.numeric(unlist(strsplit(y, ":"))), USE.NAMES=FALSE))
                })

results <- sapply(peaks, function(p) getSplash(p))


## Validated full hash block:
refdata <- read.csv("../base-dataset/spectra/test-set-with-splash-v1.csv",
                     header=FALSE, stringsAsFactors=FALSE)

##
## "test" hashes
##

truefullhash <- sapply(strsplit((refdata[,"V1"]), "-"), function(x) x[2])
ourfullhash <- sapply(strsplit((results), "-"), function(x) x[2])

if (any(truefullhash!=ourfullhash)) {
    names(spectra)[truefullhash!=ourfullhash]
} else {
    cat("Hashes passed test\n")
}


##
## "Test" histogram 
##

truefullhist <- sapply(strsplit((refdata[,"V1"]), "-"), function(x) x[3])
ourfullhist <- sapply(strsplit((results), "-"), function(x) x[3])

if (any(truefullhist!=ourfullhist)) {
    cat("Mismatches for:\n")
    mismatches <- truefullhist!=ourfullhist
    output <- paste(truefullhist[mismatches], ourfullhist[mismatches], sep=" != ")
    names(output) <- names(spectra)[mismatches]
    output
} else {
    cat("Histograms passed test")
}


## Only snippets below this point

if (FALSE) {

    peaks <- peaks[["nominalmz-5-1"]]
    

}





