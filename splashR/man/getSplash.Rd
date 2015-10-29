\name{getSplash}
\alias{getSplash}
\title{
Calculates the SPLASH of a spectrum
}
\description{
Starting from an input matrix with peaks, it calculates the SPLASH of the
spectrum.
}
\usage{
getSplash(peaks)
}
\arguments{
 \item{peaks}{a matrix of two columns, "mz" and "intensity".}
}
\value{
the SPLASH of the spectrum.
}
\examples{
caffeine <- cbind(
  mz=c(138.0641, 195.0815),
  intensity=c(71.59, 261.7)
)
splash <- getSplash(caffeine)
}
\author{
Steffen Neumann
}
