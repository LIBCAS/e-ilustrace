#!/bin/sh
set -e

CONF_PATH="/root/.vise/project/Illustrations/data/conf.txt"

if [ -f "$CONF_PATH" ]; then
  echo "conf.txt found — creating visual group..."
  cd /root/vise/code/vise/cmake_build
  ./vise/vise-cli --cmd=create-visual-group \
    --vgroup-id=illustration-group-40 \
    --vgroup-name='Illustration Group' \
    --vgroup-description='Visual groupings of all illustrations' \
    --query-type=file \
    --max-matches=50 --min-match-score=25 --vgroup-min-score=40 \
    --filename-like=% --match-iou-threshold=0.65 \
    Illustrations:"$CONF_PATH" &&
    echo "visual-group-id-list=illustration-group-40" >> $CONF_PATH
else
  echo "conf.txt not found — skipping visual group creation"
fi

if [[ -z "${WEB_NAMESPACE}" ]]; then
  /root/vise/code/vise/cmake_build/vise/vise-cli --cmd=web-ui --http-address=0.0.0.0
else
  /root/vise/code/vise/cmake_build/vise/vise-cli --cmd=web-ui --http-address=0.0.0.0 --http-namespace=${WEB_NAMESPACE}
fi
