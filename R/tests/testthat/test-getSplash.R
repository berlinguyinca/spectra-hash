library(splash)
context("Test suite")

test_that("caffeine", {
  caffeine <- cbind(mz=c(138.0641, 195.0815),
                      intensity=c(71.59, 261.7))
  hash <- getSplash(caffeine)
  cat(hash)
  expect_equal(hash, "splash10-0z00000000-41aaacd27e19486feddd")
})

