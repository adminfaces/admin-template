#!/bin/sh

mvn clean deploy --settings settings.xml
mvn clean deploy -Pjavax --settings settings.xml
