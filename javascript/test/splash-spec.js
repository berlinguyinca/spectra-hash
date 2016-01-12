'use strict';

var should = require('chai').should(),
    splash = require('../splash'),
    loadSplash = splash.loadSplash,
    validateSplash = splash.validateSplash,
    spectra = {
        "ions": [
            {
                "mass": 100,
                "intensity": 1
            },
            {
                "mass": 101,
                "intensity": 2
            },
            {
                "mass": 102,
                "intensity": 3
            }
        ],
        "type": "MS"
    };

describe('Load Splash Test:', function () {
    //it('returns an empty string when no parameter is passed', function () {
    //    loadSplash().should.equal('');
    //});

    it('returns a splash key with valid spectra', function() {
        //console.log(typeof spectra === 'object');
        var res = loadSplash(spectra);
        //console.log(res);
    });

});