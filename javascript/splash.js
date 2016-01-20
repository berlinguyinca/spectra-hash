/**
 * Service to get and validate Splash key.
 */
'use strict';

// rest endpoint
var SPLASH_REST = 'http://splash.fiehnlab.ucdavis.edu/splash/it';
var SPLASH_VALIDATE = 'http://splash.fiehnlab.ucdavis.edu/splash/validate';


function handleError(xhr, status, errorThrown) {
    alert('Xhr Error ' + xhr.responseText);
    alert('Http Status ' + status);
    alert('Error ' + errorThrown);
}

function formatData(spectra) {
    if (typeof spectra === 'object') {
        return JSON.stringify(spectra);
    }
    else {
        console.log('Spectra must be an Object');
        return 'undefined';
    }
}

/**
 * loads spectra to REST service and retrieve the splash key.
 * @param spectra - Object {}
 * @param callback - function on success, if none is provided, data will be console logged
 */
function generateSplash(spectra, callback) {

    callback = (typeof callback !== 'undefined') ? callback : function(data) {
        console.log(data);
    };

    // verify spectra object
    var serializeSpectra = formatData(spectra);

    // make ajax call
    if (serializeSpectra !== 'undefined') {
        $.ajax({
            type: 'POST',
            contentType: 'application/json; charset=utf-8',
            url: SPLASH_REST,
            data: serializeSpectra
        }).done(callback)
          .fail(handleError);
    }
}


/**
 * loads a spectra object against our REST service to validate the splash key.
 * @param spectra Object {}
 * @param callback - function on success, if none is provided, data will be console logged
 */
function validateSplash(spectra, callback) {

    callback = (typeof callback !== 'undefined') ? callback : function(data) {
        console.log(data)
    };

    // validate spectra object
    if (typeof spectra !== 'object') {
        return console.log('Spectra must be a valid JSON object');
    }
    else if (typeof spectra.splash === 'undefined') {
        return console.log('request must have a valid splash key');

    }

    // make ajax call
    else {
        var serializeSpectra = formatData(spectra);

        if (serializeSpectra !== 'undefined') {
            $.ajax({
                type: 'POST',
                contentType: 'application/json; charset=utf-8',
                url: SPLASH_VALIDATE,
                data: serializeSpectra
            }).done(callback)
              .fail(handleError);
        }
    }
}