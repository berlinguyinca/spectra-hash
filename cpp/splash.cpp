#include <algorithm>
#include <cmath>
#include <cstdlib>
#include <iomanip>
#include <iostream>
#include <sstream>
#include <string>
#include <utility>
#include <vector>

#include <openssl/sha.h>

using namespace std;


const bool DEBUG = false;

// Precision of floating point operations and representations
const double PRECISION = 6;
const double EPS = 1.0e-6;

// Value to scale relative spectra
const double RELATIVE_INTENSITY_SCALE = 1000.0;

// Separator for building spectrum strings
const char ION_SEPARATOR = ' ';

// Full spectrum hash properties
const char ION_PAIR_SEPARATOR = ':';
const int MAX_HASH_CHARATERS_ENCODED_SPECTRUM = 20;

// Top ions block properties
const int MAX_TOP_IONS = 10;
const int MAX_HASH_CHARACTERS_TOP_IONS = 10;

// Spectrum sum properties
const int SPECTRUM_SUM_PADDING = 10;
const int SPECTRUM_SUM_MAX_IONS = 100;



string sha256(const string s) {
    unsigned char digest[SHA256_DIGEST_LENGTH];

    SHA256_CTX sha256;
    SHA256_Init(&sha256);
    SHA256_Update(&sha256, s.c_str(), s.size());
    SHA256_Final(digest, &sha256);

    stringstream ss;

    for(int i = 0; i < SHA256_DIGEST_LENGTH; ++i) {
        ss << hex << setw(2) << setfill('0') << (int)digest[i];
	}

    return ss.str();
}


// http://stackoverflow.com/a/236803
vector<string> split(const string &s, char delimeter) {
	vector<string> elements;
	stringstream ss(s);
	string element;

	while(getline(ss, element, delimeter)) {
		elements.push_back(element);
	}

	return elements;
}


bool ionPairMzComparator(const pair<double, double> a, const pair<double, double> b) {
	if(abs(a.first - b.first) < EPS) {
		return a.second > b.second;
	} else {
		return a.first < b.first;
	}
}

bool ionPairIntensityComparator(const pair<double, double> a, const pair<double, double> b) {
	if(abs(a.second - b.second) < EPS) {
		return a.first < b.first;
	} else {
		return a.second > b.second;
	}
}


string buildFirstBlock(vector<pair<double, double> > &spectrum, char spectrum_type) {
	stringstream ss;
	ss << "splash" << spectrum_type << '0';
	return ss.str();
}

string encodeTopIons(vector<pair<double, double> > &spectrum, char spectrum_type) {
	sort(spectrum.begin(), spectrum.end(), ionPairIntensityComparator);

	int i = 0;
	stringstream ss;

	for(vector<pair<double, double> >::iterator it = spectrum.begin(); it != spectrum.end(); ++it) {
		ss << setprecision(6) << fixed << (*it).first;

		if(++i == MAX_TOP_IONS || i == spectrum.size()) {
			break;
		} else {
			ss << ION_SEPARATOR;
		}
	}

	if(DEBUG) {
		cerr << "Top Ions: '" << ss.str() << "'" << endl;
	}

	return sha256(ss.str()).substr(0, MAX_HASH_CHARACTERS_TOP_IONS);
}

string encodeSpectrum(vector<pair<double, double> > &spectrum, char spectrum_type) {
	sort(spectrum.begin(), spectrum.end(), ionPairMzComparator);

	int i = 0;
	stringstream ss;

	for(vector<pair<double, double> >::iterator it = spectrum.begin(); it != spectrum.end(); ++it) {
		ss << setprecision(6) << fixed << (*it).first << ION_PAIR_SEPARATOR << (*it).second;

		if(++i < spectrum.size()) {
			ss << ION_SEPARATOR;
		}
	}

	if(DEBUG) {
		cerr << "Encoded Spectrum: '" << ss.str() << "'" << endl;
	}

	return sha256(ss.str()).substr(0, MAX_HASH_CHARATERS_ENCODED_SPECTRUM);
}

string calculateSum(vector<pair<double, double> > &spectrum, char spectrum_type) {
	sort(spectrum.begin(), spectrum.end(), ionPairIntensityComparator);

	int i = 0;
	double spectrumSum = 0.0;
	stringstream ss;
	
	for(vector<pair<double, double> >::iterator it = spectrum.begin(); it != spectrum.end() && ++i <= SPECTRUM_SUM_MAX_IONS; ++it) {
		spectrumSum += (*it).first * (*it).second;
	}
	
	ss << setfill('0') << setw(SPECTRUM_SUM_PADDING) << static_cast<long>(spectrumSum);

	if(DEBUG) {
		cerr << "Spectrum Sum: " << setprecision(PRECISION) << fixed << spectrumSum << " -> " << ss.str() << endl;
	}
	
	return ss.str();
}


string splashIt(vector<pair<double, double> > &spectrum, char spectrum_type) {
	stringstream ss;
	
	ss << buildFirstBlock(spectrum, spectrum_type) << '-';
	ss << encodeTopIons(spectrum, spectrum_type) << '-';
	ss << encodeSpectrum(spectrum, spectrum_type) << '-';
	ss << calculateSum(spectrum, spectrum_type);

	return ss.str();
}


int main(int argc, char** argv) {
	int i = 0;
	string input;

	while(getline(cin, input)) {
		if(DEBUG) {
			cout << "Spectrum #" << ++i << endl;
		}

		// Handle input of the form [id],[spectrum string]
		int delim_pos = input.find(',');
		
		string id = input.substr(0, delim_pos);
		string spectrum_string = input.substr(delim_pos + 1);

		// Convert spectrum to a vector of ion pairs and find the max intensity
		vector<string> ion_strings = split(spectrum_string, ' ');
		vector<pair<double, double> > spectrum;
		double maxIntensity = 0;

		for(vector<string>::iterator it = ion_strings.begin(); it != ion_strings.end(); ++it) {
			delim_pos = (*it).find(':');
			double mz = atof((*it).substr(0, delim_pos).c_str());
			double intensity = atof((*it).substr(delim_pos + 1).c_str());


			if(intensity > maxIntensity)
				maxIntensity = intensity;

			// Store ion as a pair object, with 'first' corresponding to m/z
			// and 'second' to intensity
			spectrum.push_back(make_pair(mz, intensity));
		}

		// Normalize spectrum
		for(vector<pair<double, double> >::iterator it = spectrum.begin(); it != spectrum.end(); ++it) {
			(*it).second = (*it).second / maxIntensity * RELATIVE_INTENSITY_SCALE;
		}

		// Print the spectrum id with the calculated splash id
		cout << id << "," << splashIt(spectrum, '1') << endl;
	}
}
