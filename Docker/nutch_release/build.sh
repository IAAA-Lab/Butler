#!/bin/sh

if test "$#" -ne 2; then
    echo 'usage' $0 'nombreImagen seed'
else
    docker build -t $1  --build-arg seed=$2  .
fi
