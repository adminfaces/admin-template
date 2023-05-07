#!/bin/sh

mvn clean install -Prelease && mvn deploy -Prelease