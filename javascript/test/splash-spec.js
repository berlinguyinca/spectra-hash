'use strict';

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

describe('Splash Key: ', function () {

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

	it('calls REST and returns a splash key', function(done) {
		expect(splashKey).toBe('splash10-0z00000000-f5bf6f6a4a1520a35d4f');
		done();
	});
});
