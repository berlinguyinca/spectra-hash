#!/bin/bash
set -o pipefail

NAME="splash/web"
IMAGE="eros.fiehnlab.ucdavis.edu/$NAME"

if [ -z "${2:-}" ]; then
  TAG="latest"
else
  TAG="$2"
fi


cd ../docker
pwd
docker build -t ${IMAGE} --iidfile build.log --rm=true . || exit 1
ID=$(tail -1 build.log | awk -F: '{print $2;}')
echo "tagging: $ID ${IMAGE}:$TAG"
docker tag $ID ${IMAGE}:$TAG
echo "tagging: $ID ${NAME}:$TAG"
docker tag $ID ${NAME}:$TAG

echo "push: $1"
if [ "$1" == "push" ]; then
  docker push $IMAGE:$TAG
else
  echo "pushing disabled!"
fi
