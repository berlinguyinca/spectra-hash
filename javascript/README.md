# SPLASH JavaScript API

A JavaScript API wrapper for [SPLASH](http://splash.fiehnlab.ucdavis.edu) (SPectraL hASH), an unambiguous, database-independent spectral identifier.  

## Usage

Include `splash.js` in index.html

* `generateSplash(spectra)` - generates Splash Key
* `validateSplash(spectra)` - validates Splash Key

Spectra must be in valid JSON object. Please refer to the [SPLASH REST documentation](http://splash.fiehnlab.ucdavis.edu/) for examples.

To run tests, install Jasmine and Karma then run "karma start" from the command line.


## Credits

This wrapper API was written by Ben Nguyen is licensed under the [BSD 3 license](https://github.com/berlinguyinca/spectra-hash/blob/master/license).