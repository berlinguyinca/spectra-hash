# pySPLASH

A Python implementation of the [SPLASH](http://splash.fiehnlab.ucdavis.edu) (SPectraL hASH), an unambiguous, database-independent spectral identifier.  

## Installation

pySPLASH is compatable with 

Install from source by downloading the [source code (ZIP)](https://github.com/berlinguyinca/spectra-hash/zipball/master) or cloning this git repository

    $ git clone git://github.com/berlinguyinca/spectra-hash.git

and running:

    $ cd spectra-hash/python
    $ python setup.py install

Python 2.7, 3.3, 3.4 & 3.5 are supported.

## Usage

### API

To generate a SPLASH for the following [mass spectrum of caffeine](http://www.massbank.jp/jsp/FwdRecord.jsp?id=PR100026)

    138.0641 71.59
    195.0815 261.7

begin by importing:

    from splash import Spectrum, SpectrumType, Splash

Create a `Spectrum` object from a string representation

    spectrum = Spectrum('138.0641:71.59 195.0815:261.7', SpectrumType.MS)

or from a list of ion-intnsity pairs

    spectrum = Spectrum([(138.0641, 71.59), (195.0815, 261.7)], SpectrumType.MS)

Finally, call the SPLASHer

    Splash().splash(spectrum)

### Command-line tool

The pySPLASH repository also contains a command-line tool `pySplash.py` to quickly process pre-formatted data.  It expects plain text data to be passed over standard input with at least two columns, one corresponding to an identifier and another with the mass spectrum in the string representation described above.

A file containing the mass spectrum with of the format `identifier,spectrum`:

    PR100026,138.0641:71.59 195.0815:261.7

can be processed with

    $ python bin/pySplash.py -o1 -s2 -T, -t MS /path/to/spectrum_file

Please to the usage documentation for more information about the command-line options:

    python bin/pySplash.py -h