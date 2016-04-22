FROM java:8-jdk
MAINTAINER Gert Leenders <leenders.gert@gmail.com>

WORKDIR /app

ADD build/distributions/ZoufZouf.tar ./

CMD ./ZoufZouf/bin/ZoufZouf
