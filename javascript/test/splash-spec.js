'use strict';

var should = require('chai').should(),
    splash = require('../splash'),
    loadSplash = splash.loadSplash,
    validateSplash = splash.validateSplash;

describe('Load Splash', function() {
   it('takes a spectra as a arguement', function() {
      var spectra = '10:15 20.1:12.5';
       console.log('TEST '+ loadSplash);
       //loadSplash(spectra).should.equal('');
   });
});