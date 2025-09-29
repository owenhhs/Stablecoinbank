#!/bin/bash

mvn clean install -DskipTests --settings ./settings.xml -P${ENV}
#mvn clean install -DskipTests -P${ENV}

exit_code=$?
if [ $exit_code -ne 0 ];then
    exit $exit_code
fi

cp ${PKG}/target/${PKG}.jar ./app.jar
ls -l
