# splashR

An R implementation of the [SPLASH](http://splash.fiehnlab.ucdavis.edu) (SPectraL hASH), an unambiguous, database-independent spectral identifier.  

## Installation

Installing the R package can be done with the devtools package directly from within
R:

```R
> library(devtools)
> install_github("berlinguyinca/spectra-hash", subdir="splashR")           
```

## Usage

To generate a splash you need to source the R functions, and call `getSplash()`
on a dataframe or matrix with two numeric columns containing m/z and intensity:

```R
## The caffeine example from the paper
library(splashR)

## The caffeine example from the paper
caffeine <- cbind(mz=c(138.0641, 195.0815),
                  intensity=c(71.59, 261.7))
getSplash(caffeine)
```

## Credits

This library was written by Stefen Neuman is licensed under the [BSD 3 license](LICENSE).