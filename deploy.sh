#!/usr/bin/env bash
if [[ $# -eq 0 ]] ; then
    echo 'Need user@host as argument'
    exit 1
fi
DEPLOY_HOST=$1
TARGET=$DEPLOY_HOST:public_html/smtng
ZIP=$(PWD)/target/smtng.zip
DT=`date "+%y%m%d-%H%M"`
rm $ZIP
sbt fastOptJS fullOptJS
pushd target/scala-2.12
zip -rp $ZIP starmines-the-next-generation-opt.js starmines-the-next-generation-fastopt.*
pushd classes
zip -rp $ZIP index* styles* manifest* lib/* res/*
popd
popd
scp $ZIP ${DEPLOY_HOST}:public_html
ssh $DEPLOY_HOST "cd public_html && mkdir smtng-dev/$DT && cd smtng-dev/$DT && echo "$DT" > build.txt && unzip -o ../../smtng.zip && cd ../.. && rm smtng && ln -s smtng-dev/$DT smtng"