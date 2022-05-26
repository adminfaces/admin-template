#!/bin/sh

mvn clean install -Prelease && mvn install -Pjavax -Prelease && mvn deploy -Prelease