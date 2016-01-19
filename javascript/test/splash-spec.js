'use strict';


/**
 * @test SPLASH KEY REST API
 */

// Splash Key data mock
var splashKey = '';
var spectra = {
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

// test our AJAX Call to REST API
describe('Splash Key Test: ', function () {
	beforeEach(function (done) {
		$.ajax({
			type: 'POST',
			contentType: 'application/json; charset=utf-8',
			url: SPLASH_REST,
			data: JSON.stringify(spectra),
			success: function (data) {
				splashKey = data;
				done();
			},
			error: function (xhr, status, errorThrown) {
				console.log('XHR ' + JSON.stringify(xhr));
				console.log('STATUS ' + status);
				console.log('ERROR THROWN ' + errorThrown);
				done();
			}
		});
	});

	it('calls REST and returns a splash key', function (done) {
		expect(splashKey).toBe('splash10-0z00000000-f5bf6f6a4a1520a35d4f');
		done();
	});

	it('returns undefined when spectra is not a JSON object', function () {
		var result = formatData('');
		expect(result).toBe('undefined');
	});

	it('returns a splashkey', function(done) {
		var result = generateSplash(spectra);
		expect(result).toBe('splash10-0z00000000-f5bf6f6a4a1520a35d4f');
		done();
	});

});


/**
 * @test SPLASH KEY VALIDATION REST API
 */

// Splash Validate data mock
var result = {};
var spectraToValidate = {
	"spectrum": {
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
	},
	"splash": "splash10-0z00000000-f5bf6f6a4a1520a35d4f"
};


describe('Validate Splash Key Test: ', function () {
	beforeEach(function (done) {
		$.ajax({
			type: 'POST',
			contentType: 'application/json; charset=utf-8',
			url: SPLASH_VALIDATE,
			data: JSON.stringify(spectraToValidate),
			success: function (data) {
				result = data;
				done();
			},
			error: function (xhr, status, errorThrown) {
				console.log('XHR ' + JSON.stringify(xhr));
				console.log('STATUS ' + status);
				console.log('ERROR THROWN ' + errorThrown);
				done();
			}
		});
	});

	it('calls REST API and validates the splash key', function (done) {
		expect(result.validationSuccessful).toBe(true);
		done();
	});

	it('console logs error when spectra is not an object', function () {
		spyOn(console, 'log').and.callThrough();
		validateSplash('');
		expect(console.log).toHaveBeenCalledWith('Spectra must be a valid JSON object');
	});

	it('console logs error when a spectra object does not contain a splash key', function () {
		var noSplashKey = {
			"spectrum": {}
		};

		spyOn(console, 'log').and.callThrough();
		validateSplash(noSplashKey);
		expect(console.log).toHaveBeenCalledWith('request must have a valid splash key');
	});
});