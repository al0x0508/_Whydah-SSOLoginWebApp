#!/bin/sh
nohup /usr/bin/java -DIAM_MODE=PROD -DIAM_CONFIG=/home/SSOLoginWebApp/ssologinservice.PROD.properties -jar /home/SSOLoginWebApp/SSOLoginWebApp.jar
