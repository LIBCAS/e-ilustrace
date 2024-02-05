#!/bin/bash

EIL_TAG=`git log -1 --format=%h`
echo "EIL_TAG="$EIL_TAG

INDEX_TAG=`git submodule status index | cut -c2-8`
echo "INDEX_TAG="$INDEX_TAG