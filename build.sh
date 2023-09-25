#!/bin/bash
mvn clean package spring-boot:repackage
docker build --build-arg proxy=$1 -t ghcr.io/w-fsi/cadip_mock .
docker push ghcr.io/w-fsi/cadip_mock:latest
