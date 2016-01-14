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
 * @param spectra Object {}
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
			contentType: 'application/json; charset=utf-8',
			url: SPLASH_REST,
			data: serializeSpectra,
			success: function (data) {
				splashKey = data;
			},
			error: function (xhr, status, errorThrown) {
				console.log('XHR ' + JSON.stringify(xhr));
				console.log('STATUS ' + status);
				console.log('ERROR THROWN ' + errorThrown);
			}
		});
	}

	return splashKey;
}


/**
 * loads a spectra object against our REST service to validate the splash key.
 * @param spectra Object {}
 * @return returns JSON object
 */
function validateSplash(spectra) {

	var result = {};

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
				data: serializeSpectra,
				success: function (data) {
					result = data;
				},
				error: function (xhr, status, errorThrown) {
					console.log('XHR ' + JSON.stringify(xhr));
					console.log('STATUS ' + status);
					console.log('ERROR THROWN ' + errorThrown);
				}
			});
		}
	}
	return result;
}