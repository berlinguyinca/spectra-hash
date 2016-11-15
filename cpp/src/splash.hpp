#ifndef SPLASH_H // To make sure you don't declare the function more than once
                 // by including the header multiple times.
#define SPLASH_H

#include <string>
#include <vector>

std::string splashIt(const std::vector<std::pair<double, double> > &spectrum,
                     char spectrum_type);
std::string splashIt(const std::string &spectrum_string, char spectrum_type);

#endif
