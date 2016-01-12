/**
 * Service to get and validate Splash key.
 *
 */

'use strict';

var request = require('request');
request.debug = true;


// rest endpoint
const SPLASH_REST = 'http://splash.fiehnlab.ucdavis.edu/splash/it';
const SPLASH_VALIDATE = 'http://splash.fiehnlab.ucdavis.edu/splash/validate';

// export our NPM module
module.exports = {
    loadSplash: loadSplash,
    validateSplash: validateSplash,
    formatData: formatData,
    SPLASH_REST: SPLASH_REST,
    SPLASH_VALIDATE: SPLASH_VALIDATE
};

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
        console.log('Spectra must be an Object, String, or Array');
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

    // make http rest call
    if (serializeSpectra !== 'undefined') {

        var options = {
            uri: SPLASH_REST,
            method: 'POST',
            json: spectra
        };

        var callback = function (error, res, body) {
            console.log('test');
        };

        //console.log(request(options,function(){}));
        request(options,callback);

        //var data = queryString.stringify({
        //    'compilation_level': 'ADVANCED_OPTIMIZATIONS',
        //    'output_format': 'json',
        //    'output_info': 'compiled_code',
        //    'warning_level': 'QUIET',
        //    'js_code': spectra
        //});
        //
        //var options = {
        //    host: SPLASH_REST,
        //    method: 'POST',
        //    headers: {
        //        'Content-Type': 'application/json',
        //        'Content-Length': Buffer.byteLength(data)
        //    }
        //};
        //
        //var request = http.request(options, function(res) {
        //   res.setEncoding('utf8');
        //   res.on('error', function(error) {
        //      console.log(error);
        //   });
        //   res.on('data', function(chunk) {
        //       console.log('RESPONSE ' + chunk);
        //   });
        //});
        //
        //request.write(data);
        //request.end();
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
        console.log('Spectra must be a valid JSON object');
    }
    else if (typeof spectra.splash === 'undefined') {
        console.log('request must have a valid splash key');
    }

    // make http rest call
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

