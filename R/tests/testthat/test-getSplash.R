library(splash)
context("Test suite")

test_that("caffeine", {
  caffeine <- cbind(mz=c(138.0641, 195.0815),
                      intensity=c(71.59, 261.7))
  hash <- getSplash(caffeine)
  expect_equal(hash, "splash10-0z00000000-41aaacd27e19486feddd")
})

test_that("hashes", {
  filedata <- read.csv(
    system.file("extdata", "test-set-v1-csharp.csv", package = "splash"),
    header=FALSE, stringsAsFactors=FALSE
  )

  spectra <- filedata[,3]
  names(spectra) <- filedata[,1]

  peaks <- lapply(spectra, function(s) {
    t(sapply(
        unlist(strsplit(s, " "), use.names=F),
        function(y) as.numeric(unlist(strsplit(y, ":"))), USE.NAMES=FALSE
    ))
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
    output <- paste(truefullhash[mismatches], ourfullhash[mismatches], "\n", sep=" != ")
    names(output) <- names(spectra)[mismatches]
    cat(output)
  }
  expect_equal(ourfullhash, truefullhash);
})

test_that("histograms", {
  filedata <- read.csv(
    system.file("extdata", "test-set-v1-csharp.csv", package = "splash"),
    header=FALSE, stringsAsFactors=FALSE
  )

  spectra <- filedata[,3]
  names(spectra) <- filedata[,1]

  peaks <- lapply(spectra, function(s) {
    t(sapply(
        unlist(strsplit(s, " "), use.names=F),
        function(y) as.numeric(unlist(strsplit(y, ":"))), USE.NAMES=FALSE
    ))
  })

  results <- sapply(peaks, function(p) getSplash(p))

  ##
  ## "test" histograms
  ##

  truefullhash <- sapply(strsplit((filedata[,1]), "-"), function(x) x[2])
  ourfullhash <- sapply(strsplit((results), "-"), function(x) x[2])

  if (any(truefullhash!=ourfullhash)) {
    cat("Mismatches for:\n")
    mismatches <- truefullhash!=ourfullhash
    output <- paste(truefullhash[mismatches], ourfullhash[mismatches], "\n", sep=" != ")
    names(output) <- names(spectra)[mismatches]
    cat(output)
  }
  expect_equal(ourfullhash, truefullhash);
})
