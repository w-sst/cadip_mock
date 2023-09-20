FROM ubuntu:22.04

ARG proxy
ENV http_proxy $proxy
ENV https_proxy $proxy

RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get -y install default-jre-headless && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*
    
WORKDIR /app
COPY target/cadip_mock*.jar .
COPY start.sh .

ENTRYPOINT ["/app/start.sh"]