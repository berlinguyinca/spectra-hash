#!/bin/bash
set -o pipefail

NAME="splash/web"
IMAGE="eros.fiehnlab.ucdavis.edu/$NAME"

cd ../docker
pwd
docker build -t ${IMAGE} --iidfile build.log --rm=true . || exit 1
ID=$(tail -1 build.log | awk -F: '{print $2;}')
echo "tagging: $ID ${IMAGE}:wcag"
docker tag $ID ${IMAGE}:wcag
echo "tagging: $ID ${NAME}:wcag"
docker tag $ID ${NAME}:wcag

echo "push: $1"
if [ "$1" == "push" ]; then
  docker push $IMAGE
else
  echo "pushing disabled!"
fi
