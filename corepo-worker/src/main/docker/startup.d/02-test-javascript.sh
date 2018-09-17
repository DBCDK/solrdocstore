#!/bin/bash -x

if [ -d /javascript ]; then
    echo "IMPORTING JAVASCRIPT"
    mkdir -p /tmp/WEB-INF/classes/
    cp -a /javascript /tmp/WEB-INF/classes/
    find /tmp/WEB-INF/classes/ -type f | xargs touch # ensure all modified now (newer than those in the archive)
    (cd /tmp && zip -ur0 --exclude=*/.svn/* --exclude=*/.git/* --exclude=*.test.js $PAYARA_CFG_DIR/app.war WEB-INF)
fi