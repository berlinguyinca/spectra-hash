#include <ctime>
#include <iomanip>
#include <iostream>
#include <string>

#include "splash.hpp"


using namespace std;


// Debug mode
const bool DEBUG = false;


int main(int argc, char** argv) {
    int i = 0;
    string input;
    
    // Log times
    clock_t start = clock();
    

    while(getline(cin, input)) {
        ++i;
        
        if(DEBUG) {
            cerr << "Spectrum #" << i << endl;
        }

        // Handle input of the form [id],[spectrum string]
        int delim_pos = input.find(',');

        if(delim_pos != string::npos) {
            string id = input.substr(0, delim_pos);
            string spectrum_string = input.substr(delim_pos + 1);

            // Print the spectrum id with the calculated splash id
            string s = splashIt(spectrum_string, '1');
            cout << id << "\t" << s << endl;
        } else {
            // Print the spectrum id with the calculated splash id
            cout << splashIt(input, '1') << endl;
        }
        
        // Provide output for large files
        if(i % 10000 == 0) {
            cerr << "processed " << i << " spectra, " << setprecision(2) << fixed
                 << (std::clock() - start) / (double)(CLOCKS_PER_SEC / 1000) / i
                 << " ms average time to splash a spectrum." << endl;
        }
    }

    cerr << "finished processing, processing took: " << setprecision(2) << fixed
         << (std::clock() - start) / (double)CLOCKS_PER_SEC << " s" << endl
         << "processed " << i << " spectra" << endl
         << "average time including io to splash a spectrum is: " << setprecision(2) << fixed
         << (std::clock() - start) / (double)(CLOCKS_PER_SEC / 1000) / i  << " ms" << endl;
}