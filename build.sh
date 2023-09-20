#!/bin/bash
mvn clean package spring-boot:repackage
docker build --build-arg proxy=$1 -t cadip_mock .
