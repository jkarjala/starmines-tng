#!/usr/bin/env bash
if [[ $# -eq 0 ]] ; then
    echo 'Need user@host and build DT as argument'
    exit 1
fi
DEPLOY_HOST=$1
DT=$2
ssh $DEPLOY_HOST "cd public_html && rm smtng && ln -s smtng-dev/$DT smtng"