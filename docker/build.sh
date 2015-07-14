#!/bin/bash
set -o pipefail

NAME="splash/web"
IMAGE="gose.fiehnlab.ucdavis.edu:5000/$NAME"

cd ../docker
pwd
docker build -t ${IMAGE} --rm=true . | tee build.log || exit 1
ID=$(tail -1 build.log | awk '{print $3;}')
docker tag -f $ID ${IMAGE}:latest
docker tag -f $ID ${NAME}:latest

echo "push: $1"
if [ "$1" == "push" ]; then
  docker push $IMAGE
else
  echo "pushing disabled!"
fi
