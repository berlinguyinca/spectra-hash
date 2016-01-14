/**
 * Service to get and validate Splash key.
 */
'use strict';

// rest endpoint
var SPLASH_REST = 'http://splash.fiehnlab.ucdavis.edu/splash/it';
var SPLASH_VALIDATE = 'http://splash.fiehnlab.ucdavis.edu/splash/validate';


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
 * @param spectra
 * @return splashKey
 */
function loadSplash(spectra) {

    var splashKey = '';

    // verify spectra object
    var serializeSpectra = formatData(spectra);

    // make ajax call
    if (serializeSpectra !== 'undefined') {
        $.ajax({
            type: 'POST',
            contentType: 'application/json',
            dataType: 'json',
            url: SPLASH_REST,
            data: serializeSpectra,
            success: function (data) {
                splashKey = data;
                console.log(splashKey);
            },
            error: function (error, msg, httpStatus) {
                console.log('ERROR ' + msg + ' STATUS ' + httpStatus);
            }
        });
    }

    return splashKey;
}


/**
 * loads a spectra object against our REST service to validate the splash key.
 * @param spectra
 * @return returns JSON object
 */
function validateSplash(spectra) {

    var result = {};

    // validate spectra object
    if (typeof spectra !== 'object') {
        alert('Spectra must be a valid JSON object');
    }
    else if (typeof spectra.splash === 'undefined') {
        alert('request must have a valid splash key');
    }

    // make ajax call
    else {
        $.ajax({
            type: 'POST',
            contentType: 'application/json',
            dataType: 'json',
            url: SPLASH_VALIDATE,
            data: spectra,
            success: function (data) {
                result = data;
            },
            error: function (error, msg, httpStatus) {
                console.log('ERROR ' + msg + ' STATUS ' + httpStatus);
            }
        });
    }
    return result;
}