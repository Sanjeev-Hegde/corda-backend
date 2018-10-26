FROM openjdk:8u171-jdk-alpine

RUN echo http://nl.alpinelinux.org/alpine/v3.8/main/ > /etc/apk/repositories; \
    echo http://mirror.yandex.ru/mirrors/alpine/v3.8/community >> /etc/apk/repositories


RUN apk add bash iputils && \
    rm -rf /var/cache/apk/* && \
    # Add user to run the app && \
    addgroup app && \
    adduser -G app -D -s /bin/bash app && \
    # Create /opt/corda directory && \
    mkdir -p /opt/app/src

#RUN apk add screen

# Copy corda jar
ADD  https://dl.bintray.com/r3/corda/net/corda/corda/${CORDA_VERSION}/corda-${CORDA_VERSION}.jar                       /opt/corda/corda.jar
RUN  chown -R corda:corda /opt/corda
#ADD --chown=corda:corda https://dl.bintray.com/r3/corda/net/corda/corda-webserver/${CORDA_VERSION}/corda-webserver-${CORDA_VERSION}.jar   /opt/corda/corda-webserver.jar


COPY ./api/run-backend.sh /run-backend.sh
COPY ./ /opt/app/src
RUN cd /opt/app/src && \
    sync && \
    chmod +x gradlew && \
   ./gradlew -Dhttp.proxyHost=webproxy.prd.lab-nxtit.priv -Dhttp.proxyPort=3128 -Dhttps.proxyHost=webproxy.prd.lab-nxtit.priv -Dhttps.proxyPort=3128 -x test api:build && \
 #  ./gradlew  deployNodesUAT && \
   sync

    ## run using:  java -Dserver.port=8080 -Dspring.profiles.active=gbis -jar api/build/libs/corda-api-0.0.1-SNAPSHOT.jar

RUN chmod +x /run-backend.sh && \
    sync
#COPY /opt/corda/src/build /opt/corda/app
#RUN rm -rf /opt/corda/src


RUN chmod -R u+x /opt/app && \
    chgrp -R 0 /opt/app && \
    chmod -R g=u /opt/app /etc/passwd

# Working directory for Corda
WORKDIR /opt/app
ENV HOME=/opt/app

USER 10001
EXPOSE 8080

# Start it
CMD ["/run-backend.sh"]
