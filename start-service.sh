#!/bin/sh

export IAM_MODE=TEST

A=SSOLoginWebApp
V=LATEST
JARFILE=$A-$V.jar

pkill -f $A

wget -O $JARFILE "http://mvnrepo.cantara.no/service/local/artifact/maven/content?r=snapshots&g=net.whydah.sso&a=$A&v=$V&p=jar"
nohup java -jar -DIAM_CONFIG=ssologinwebapp.TEST.properties $JARFILE &

tail -f nohup.out
