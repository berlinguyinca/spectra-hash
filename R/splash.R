library(digest) # for digest

decimalavoidance <- function(x) {
    format(floor (x * 1000000), scientific=FALSE)
}

getBlock2topN <- function(peaks, n=10) {
    o <- order(peaks[,2], decreasing=TRUE)
    peakString <- paste(decimalavoidance(peaks[o[1:max(1,length(o))],1]), collapse=" ")

    block2 <- substr(digest(peakString, algo="sha256", serialize=FALSE), 1, 10)        
}

getBlock3full <- function(peaks,
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
    # cat("Prehash: ", peakString, sep="", file="/tmp/prehash-R") ## Debugging of spectrum-string
    block3 <- substr(digest(peakString, algo="sha256", serialize=FALSE),
                     1, MAX_HASH_CHARATERS_ENCODED_SPECTRUM)    
}
    
getSplash <- function(peaks) {

#    block2 <- getBlock2topN(peaks, 10)
    block3 <- getBlock3full(peaks)

    splash <- paste("splash10",
                    #block2,
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

truefullhash <- sapply(strsplit((refdata[,"V1"]), "-"), function(x) x[3])
ourfullhash <- sapply(strsplit((results), "-"), function(x) x[2])

## "test"
any(truefullhash!=ourfullhash)






