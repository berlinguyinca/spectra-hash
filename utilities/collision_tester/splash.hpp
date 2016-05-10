#ifndef SPLASH_H    // To make sure you don't declare the function more than once by including the header multiple times.
#define SPLASH_H

#include <string>
#include <vector>

using namespace std;

string splashIt_double(vector<pair<double, double> > &spectrum, char spectrum_type);
string splashIt(string spectrum_string, char spectrum_type);

#endif
