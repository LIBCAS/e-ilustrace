#!/bin/sh
set -e

if [[ -z "${WEB_NAMESPACE}" ]]; then
  /root/vise/code/vise/cmake_build/vise/vise-cli --cmd=web-ui --http-address=0.0.0.0
else
  /root/vise/code/vise/cmake_build/vise/vise-cli --cmd=web-ui --http-address=0.0.0.0 --http-namespace=${WEB_NAMESPACE}
fi
