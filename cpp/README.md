# SPLASH++

A C++ implementation of the [SPLASH](http://splash.fiehnlab.ucdavis.edu) (SPectraL hASH), an unambiguous, database-independent spectral identifier.  

## Compilation

SPLASH++ builds on recent Linux and Mac OS distributions with compilers that support C++11 features.  

### Linux

The packages required to build SPLASH++ on a linux-based operating system are:

* gcc-4.6.3 or newer
* make
* g++
* libssl-dev / openssl-devel

Running `make` will generate a binary named `splash`.  Tests can be run using `make test`.

### Mac OS

On OS X Yosemite (10.10) or older, Xcode 5.0+ required.  Compiling will generate warnings regarding the deprecation of OpenSSL in Mac OS since Lion (10.7) due to the migration to Common Crypto.  

On OSX El Capitan (10.11), the OpenSSL headers were removed, and so we recommend installing OpenSSL with [Homebrew](brew.sh).

    brew update
    brew install openssh

Then, either force Homebrew create symlinks to `/usr/local`

    brew link openssl --force
    make

or add the OpenSSL link/library paths as environmental variables

    EXTRA_CPPFLAGS=-I/usr/local/opt/openssl/include EXTRA_LDFLAGS=-L/usr/local/opt/openssl/lib make

This will generate a binary named `splash`


## Usage

### Command-line tool

The compiled version of SPLASH++ is a command-line tool for efficient processing of pre-formatted data.  It expects plain text passed via standard input with two columns of data, the first corresponding to an identifier and the second with a mass spectrum in a single-line string representation.

To generate the SPLASH of the following [mass spectrum of caffeine](http://www.massbank.jp/jsp/FwdRecord.jsp?id=PR100026) with MassBank identifier PR100026,

    m/z        intensity
    138.0641   71.59
    195.0815   261.7

can be processed with

    $ echo "PR100026,138.0641:71.59 195.0815:261.7" | ./splash
    splash10-0002-0900000000-b112e4e059e1ecf98c5f,PR100026,138.0641:71.59 195.0815:261.7

The SPLASH is added as a new column with the given identifier and spectrum following.  SPLASH++ can process multiple lines of standard input, with each line corresponding to a mass spectral entry matching the format  `identifier,spectrum`.
    
### API

After adding the SPLASH++ to your project, include the header in your code:

    #include "splash/splash.hpp"

The `splashIt` function supports two mass spectral representations as parameters: a single-line string representation:

    std::string splash = splashIt("138.0641:71.59 195.0815:261.7", '1');

or a vector of pairs of m/z and intensity values as double-precision floats:

    std::vector<std::pair<double, double> > spectrum;
    spectrum.push_back(std::make_pair(138.0641, 71.59));
    spectrum.push_back(std::make_pair(195.0815, 261.7));

    std::string splash = splashIt(spectrum, '1');

The `'1'` given as the second parameter indicates the MS SPLASH type.  This will be improved in later iterations of SPLASH++.

## Credits

This library was written by Sajjan S. Mehta is licensed under the [BSD 3 license](https://github.com/berlinguyinca/spectra-hash/blob/master/license).