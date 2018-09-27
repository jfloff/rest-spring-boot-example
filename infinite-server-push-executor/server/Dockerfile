FROM jfloff/thrike:8.5

LABEL maintainer="jfloff@inesc-id.pt"

###################
# Build Server
#
ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom"
WORKDIR /home/server
# cache gradle build dependencies - is less often changed than code
ADD build.gradle /home/server
RUN gradle dependencies
# add all code and build
ADD . /home/server
RUN gradle build && \
    cp build/libs/server.war /usr/local/tomcat/webapps/