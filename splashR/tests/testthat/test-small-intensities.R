library(splashR)
context("Test suite")

test_that("small-intensities", {
  # tests for the SPLASH histogram issue reported in:
  # https://github.com/MassBank/MassBank-data/issues/248
  s <- cbind(mz=c(44.998, 80.0261, 93.0321, 108.0227),
             intensity=c(0.2, 0.1, 0.4, 0.3))
  expect_equal(getSplash(s), "splash10-052f-9300000000-5cd70311703e2423a1c5")

  # using MassBank scaled intensities
  # should yield the same histogram blocks but different hash block
  s <- cbind(mz=c(44.998, 80.0261, 93.0321, 108.0227),
             intensity=c(499, 249, 999, 749))
  expect_equal(getSplash(s), "splash10-052f-9300000000-a485843aed0475c0a1b9")
})
