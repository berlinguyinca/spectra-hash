library(splashR)
context("Test suite")

test_that("caffeine", {
  caffeine <- cbind(mz=c(138.0641, 195.0815),
                      intensity=c(71.59, 261.7))
  hash <- getSplash(caffeine)
  expect_equal(hash, "splash10-0002-0900000000-b112e4e059e1ecf98c5f")
})

test_that("hashes", {
  filedata <- read.csv(
    system.file("extdata", "test-set-v1-splashed-with-validation-1.6.csv", package = "splashR"),
    header=FALSE, stringsAsFactors=FALSE
  )

  spectra <- filedata[,3]
  names(spectra) <- filedata[,1]
  
  peaks <- lapply(spectra, function(s) nist2matrix(s))
  results <- sapply(peaks, function(p) getSplash(p))

  ##
  ## "test" hashes
  ##

  truefullhash <- sapply(strsplit((filedata[,1]), "-"), function(x) x[4])
  names(truefullhash) <- filedata[,1] 
  ourfullhash <- sapply(strsplit((results), "-"), function(x) x[4])

  if (any(truefullhash!=ourfullhash)) {
    cat("Mismatches for:\n")
    mismatches <- truefullhash!=ourfullhash
    output <- paste(truefullhash[mismatches], ourfullhash[mismatches], "\n", sep=" != ")
    names(output) <- names(spectra)[mismatches]
    cat(output)
  }
  expect_equal(ourfullhash, truefullhash);
})

test_that("histograms", {
  filedata <- read.csv(
    system.file("extdata", "test-set-v1-splashed-with-validation-1.6.csv", package = "splashR"),
    header=FALSE, stringsAsFactors=FALSE
  )

  spectra <- filedata[,3]
  names(spectra) <- filedata[,1]

  peaks <- lapply(spectra, function(s) nist2matrix(s))
  results <- sapply(peaks, function(p) getSplash(p))

  ##
  ## "test" prefilter histograms
  ##

  truefullhist <- sapply(strsplit((filedata[,1]), "-"), function(x) x[2])
  names(truefullhist) <- filedata[,1] 
  ourfullhist <- sapply(strsplit((results), "-"), function(x) x[2])

  if (any(truefullhist!=ourfullhist)) {
    cat("Mismatches for:\n")
    mismatches <- truefullhist!=ourfullhist
    output <- paste(names(truefullhist[mismatches]), ":",
                    truefullhist[mismatches], "!=",
                    ourfullhist[mismatches], "\n", sep=" ")
    names(output) <- names(spectra)[mismatches]
    cat(output)
  }
  expect_equal(ourfullhist, truefullhist);

  ##
  ## "test" histograms
  ##

  truefullhist <- sapply(strsplit((filedata[,1]), "-"), function(x) x[3])
  names(truefullhist) <- filedata[,1] 
  ourfullhist <- sapply(strsplit((results), "-"), function(x) x[3])

  if (any(truefullhist!=ourfullhist)) {
    cat("Mismatches for:\n")
    mismatches <- truefullhist!=ourfullhist
    output <- paste(names(truefullhist[mismatches]), ":",
                    truefullhist[mismatches], "!=",
                    ourfullhist[mismatches], "\n", sep=" ")
    names(output) <- names(spectra)[mismatches]
    cat(output)
  }
  expect_equal(ourfullhist, truefullhist);


})
