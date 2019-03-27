# pySPLASH

A Python implementation of the [SPLASH](http://splash.fiehnlab.ucdavis.edu) (SPectraL hASH), an unambiguous, database-independent spectral identifier.  

## Installation

pySPLASH is compatable with 

Install from source by downloading the [source code (ZIP)](https://github.com/berlinguyinca/spectra-hash/zipball/master) or cloning this git repository

```
$ git clone git://github.com/berlinguyinca/spectra-hash.git
```

and running:

```bash
$ cd spectra-hash/python
$ python setup.py install
```

Python 2.7, 3.3, 3.4 & 3.5 are supported.  Tests can be run using

    $ python setup.py test

## Using is as python package dependency in PIP

please create a file called requirements.txt and add the following line

```
git+git://github.com/berlinguyinca/spectra-hash.git@#egg=splash&subdirectory=python
```

afterwards execute a pip install -r requirements.txt

and it should install splash into your local env.

## Usage

### API

To generate a SPLASH for the following [mass spectrum of caffeine](http://www.massbank.jp/jsp/FwdRecord.jsp?id=PR100026) with MassBank identifier PR100026,

```
m/z        intensity
138.0641   71.59
195.0815   261.7
```

begin by importing:

```python
from splash import Spectrum, SpectrumType, Splash
```

Create a `Spectrum` object from a string representation

```python
spectrum = Spectrum('138.0641:71.59 195.0815:261.7', SpectrumType.MS)
```

or from a list of ion-intnsity pairs

```python
spectrum = Spectrum([(138.0641, 71.59), (195.0815, 261.7)], SpectrumType.MS)
```

Finally, call the SPLASHer

```python
Splash().splash(spectrum)
```

### Command-line tool

The pySPLASH repository also contains a command-line tool `pySplash.py` to quickly process pre-formatted data.  It expects a plain text file containing least two columns, one corresponding to an identifier and another with the mass spectrum in the string representation described above.

A file containing mass spectral entries on each line matching the format `identifier,spectrum`:

```
PR100026,138.0641:71.59 195.0815:261.7
```

can be processed with
```
$ python bin/pySplash.py -o1 -s2 -T, -t MS /path/to/spectrum_file
splash10-0002-0900000000-b112e4e059e1ecf98c5f,PR100026,138.0641:71.59 195.0815:261.7
```

The SPLASH is added as a new first column with the rest of the data exported in the same order as in the input file.  Note that Please to the usage documentation for more information about the command-line options:

```
python bin/pySplash.py -h
```

## Credits

This library was written by Sajjan S. Mehta is licensed under the [BSD 3 license](https://github.com/berlinguyinca/spectra-hash/blob/master/license).
