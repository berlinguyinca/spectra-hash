library(digest) # for digest

getSplash <- function(peaks) {

    peakString <- paste(apply(peaks, MARGIN=1,
                              FUN=function(x) {paste(c(x[1], x[2]),
                                  collapse=":")}), collapse=" ")

    block3 <- substr(digest(peakString, algo="sha256"), 1, 20)

    splash <- paste("splash10", block3, sep="-")

    return(splash)
}

peaks <- matrix(data=c(124.1, 31739,
                    125.1,  2905,
                    129.1,  2850,
                    131.1, 49572,
                    132.1, 4865,
                    133.1, 81554,
                    144.1, 1940,
                    145.1, 41441),
                ncol=2, byrow=TRUE)

getSplash(peaks)

