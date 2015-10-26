library(digest) # for digest

## Some constants, taken from SplashVersion1.java
BINS <- 10;
BIN_SIZE <- 100;
EPS_CORRECTION = 1.0e-7

## FINAL_SCALE_FACTOR <- 9; ## Base 10
FINAL_SCALE_FACTOR <- 35; ## Base 36 

decimalavoidance <- function(x) {
    format(floor (x * 1000000), scientific=FALSE)
}

getBlockHash <- function(peaks,
                          RELATIVE_INTENSITY_SCALE=100,
                          MAX_HASH_CHARATERS_ENCODED_SPECTRUM=20) {
    max_intensity = max(peaks[,2])

    ## Scale to maximum intensity
    peaks[,2] <-    as.integer(peaks[,2] / max(peaks[,2]) * RELATIVE_INTENSITY_SCALE)

    ## Sorted by ascending m/z and ties broken by descending intensity
    o <- order(peaks[,1], -1*peaks[,2], decreasing=FALSE)
    
    peakString <- paste(apply(peaks[o,,drop=FALSE], MARGIN=1,
                              FUN=function(x) {paste(c(decimalavoidance(x[1]+EPS_CORRECTION), x[2]),
                                  collapse=":")}), collapse=" ")

    ## cat("Prehash: ", peakString, sep="", file="/tmp/prehash-R") ## Debugging of spectrum-string
    block2 <- substr(digest(peakString, algo="sha256", serialize=FALSE),
                     1, MAX_HASH_CHARATERS_ENCODED_SPECTRUM)    
}


## Global variable saves frequent re-allocation of the conversion array
integer2base36code <<- sapply(c(48:57, 97:122), function(i) rawToChar(as.raw(i)))
integer2base36 <- function(i) {
    paste(integer2base36code[i+1], collapse="")
}

getBlockHist <- function(peaks) {
    ## Initialise output
    wrappedhist <- integer(BINS)

    binindex <- as.integer(peaks[,1] / BIN_SIZE) 

    summedintensities <- tapply(peaks[,2], binindex, sum)
    wrappedbinindex <- unique(binindex) %% BINS 
    wrappedintensities <- tapply(summedintensities, wrappedbinindex, sum)
    normalisedintensities <- as.integer(wrappedintensities/max(wrappedintensities)*FINAL_SCALE_FACTOR)
    
    wrappedhist[sort(unique(wrappedbinindex))+1] <- normalisedintensities
    paste(integer2base36(wrappedhist), collapse="")
  
}

getSplash <- function(peaks) {

    block2 <- getBlockHist(peaks)
    block3 <- getBlockHash(peaks)

    splash <- paste("splash10",
                    block2,
                    block3,
                    sep="-")

    return(splash)
}


if (FALSE) {
    ## The caffeine example from the paper
    caffeine <- cbind(mz=c(138.0641, 195.0815),
                      intensity=c(71.59, 261.7))
    getSplash(caffeine)
}

if (FALSE) {
    ##
    ## Read test data from splash distribution.
    ## Requires 
    ##

    filedata <- read.csv("../base-dataset/spectra/splashed/csharp/test-set-v1-csharp.csv",
                         header=FALSE, stringsAsFactors=FALSE)

    filedata <- read.csv("../base-dataset/spectra/test-set-with-splash-v1.csv",
                         header=FALSE, stringsAsFactors=FALSE)

    filedata <- read.csv("../base-dataset/spectra/validated/csharp/test-set-v1-csharp-validated.csv",
                         header=FALSE, stringsAsFactors=FALSE)

    filedata <- read.csv("/tmp/output.csv",
                         header=FALSE, stringsAsFactors=FALSE)


    spectra <- filedata[,3]
    names(spectra) <- filedata[,2]

    peaks <- lapply(spectra, function(s) {
        t(sapply(unlist(strsplit(s, " "), use.names=F),
                 function(y) as.numeric(unlist(strsplit(y, ":"))), USE.NAMES=FALSE))
    })

    results <- sapply(peaks, function(p) getSplash(p))


    ##
    ## "test" hashes
    ##

    truefullhash <- sapply(strsplit((filedata[,1]), "-"), function(x) x[3])
    ourfullhash <- sapply(strsplit((results), "-"), function(x) x[3])

    if (any(truefullhash!=ourfullhash)) {
        cat("Mismatches for:\n")
        mismatches <- truefullhash!=ourfullhash
        output <- paste(truefullhash[mismatches], ourfullhash[mismatches], sep=" != ")
        names(output) <- names(spectra)[mismatches]
        output
        cat("In total ", length(output), "mismatches\n")
        cat("In line: ", which(mismatches), "\n")
    } else {
        cat("Hashes passed test\n")
    }

    ##
    ## "Test" histogram 
    ##

    truefullhist <- sapply(strsplit((filedata[,2]), "-"), function(x) x[2])
    ourfullhist <- sapply(strsplit((results), "-"), function(x) x[2])

    if (any(truefullhist!=ourfullhist)) {
        cat("Mismatches for:\n")
        mismatches <- truefullhist!=ourfullhist
        output <- paste(truefullhist[mismatches], ourfullhist[mismatches], sep=" != ")
        names(output) <- names(spectra)[mismatches]
        output
    } else {
        cat("Histograms passed test")
    }
}





