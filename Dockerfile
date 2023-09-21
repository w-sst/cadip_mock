FROM ubuntu:focal-20211006

#ARG proxy
#ENV http_proxy $proxy
#ENV https_proxy $proxy

RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get -y install default-jre-headless && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*
    
WORKDIR /app
COPY target/cadip_mock*.jar .
COPY start.sh .

RUN mkdir /data

ENTRYPOINT ["/app/start.sh"]