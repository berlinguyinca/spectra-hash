#where are our source files
set(SOURCE_FILES
        cpp/src/splash.cpp
        cpp/src/splash.hpp
        )

find_package( OpenSSL REQUIRED )

include_directories( ${OPENSSL_INCLUDE_DIR} )

include_directories( ${OPENSSL_CRYPTO_LIBRARY} )

#build this a library
add_library(SPLASH ${SOURCE_FILES})

#link our required libraries
target_link_libraries(SPLASH ${OPENCL_LIBRARIES} ${OPENSSL_CRYPTO_LIBRARY} ${OPENSSL_SSL_LIBRARY})
target_compile_features(SPLASH PRIVATE cxx_range_for)
