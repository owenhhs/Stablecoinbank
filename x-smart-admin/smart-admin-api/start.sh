#!/bin/bash

export TZ=Asia/Shanghai

java -Djava.security.egd=file:/dev/./urandom -jar /opt/apps/app.jar
