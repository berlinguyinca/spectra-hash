/**
 * Service to get and validate Splash key.
 */
'use strict';

// rest endpoint
const SPLASH_REST = 'http://splash.fiehnlab.ucdavis.edu/splash/it';
const SPLASH_VALIDATE = 'http://splash.fiehnlab.ucdavis.edu/splash/validate';


function formatData(spectra) {
    if (typeof spectra === 'object') {
        return spectra;
    }
    else if (typeof spectra === 'string') {
        return JSON.parse(spectra);
    }
    else if (Array.isArray(spectra)) {
        return spectra.serializeArray();
    }
    else {
        alert('Spectra must be an Object, String, or Array');
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
            },
            error: function (error, msg, httpStatus) {
                alert('ERROR ' + msg + ' STATUS ' + httpStatus);
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
                alert('ERROR ' + msg + ' STATUS ' + httpStatus);
            }
        });
    }
    return result;
}