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

# For debugging JVM
EXPOSE 8849
# Size of GC set for ns-standard-4
ENV CATALINA_OPTS="-Xms14600M -Xmx14800M -XX:+UseG1GC -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=8849 -Dcom.sun.management.jmxremote.rmi.port=8849 -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=localhost"