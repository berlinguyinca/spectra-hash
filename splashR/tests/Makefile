TOP=..
PKG=${shell cd ${TOP};pwd}
SUITE=./testthat.R
R=R

BASEDATA=../../base-dataset/spectra/notsplashed/test-set-v1.csv
VALIDATORPATH=../../validation/target
VALIDATOR=validation-1.3-SNAPSHOT

all: inst dataprep test

dataprep:
	java -jar ${VALIDATORPATH}/${VALIDATOR}.jar -c -s 2 -t ms ${BASEDATA} ../inst/extdata/test-set-v1-splashed-with-${VALIDATOR}.csv



inst: # Install package
	cd ${TOP}/..;\
	${R} CMD INSTALL ${PKG}

test: # Run unit tests
	export RCMDCHECK=FALSE;\
	cd ${TOP}/tests;\
	${R} --vanilla --slave < ${SUITE}

# clean:
#	rm -f report.html  reportSummary.txt  report.txt *~
