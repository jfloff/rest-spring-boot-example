FROM jfloff/thrike:8.5

LABEL maintainer="jfloff@inesc-id.pt"

###################
# Build Client
#
ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom"
WORKDIR /home/client
# cache gradle build dependencies - is less often changed than code
ADD build.gradle /home/client
RUN gradle dependencies
# add all code and build
ADD . /home/client
RUN gradle build && \
    cp build/libs/client.war /usr/local/tomcat/webapps/

# For debugging JVM
EXPOSE 8849
ENV CATALINA_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=8849 -Dcom.sun.management.jmxremote.rmi.port=8849 -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=localhost"