# Docker Container Operating System
FROM oraclelinux:8-slim
RUN microdnf install yum
RUN yum clean all \
   && yum update -y
RUN yum install -y librepo
RUN yum install -y tar
RUN yum install -y procps
RUN yum install -y sudo

# Docker Container Healthcheck
HEALTHCHECK NONE

# Docker Container Non-Root User
RUN useradd -ms /bin/bash appadmin 
RUN echo 'appadmin ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers

# Docker Container Label
LABEL "Product"="Sb ne Apollo"
LABEL Version="NSL"
LABEL Description="Sb ne Apollo Container."

# Docker Container Application Deployment
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} sb-ne-apollo.jar
COPY application.properties application.properties
COPY jdk1.8.0_151.tar /
RUN chown -R appadmin:appadmin /*.tar
RUN tar -xvf jdk1.8.0_151.tar 
RUN rm -rf jdk1.8.0_151.tar
RUN chown -R appadmin:appadmin /jdk1.8.0_151
RUN chown -R appadmin:appadmin /*.jar
RUN chmod -R 775 /jdk1.8.0_151

# Glowroot Configuration
#COPY glowroot /glowroot
#RUN chown -R appadmin:appadmin /glowroot

# DataDog Configuration
COPY /datadog/dd-java-agent-0.76.0.jar  /datadog/dd-java-agent-0.76.0.jar
RUN chown -R appadmin:appadmin /datadog

ENV LOG4J_FORMAT_MSG_NO_LOOKUPS=true

USER appadmin

#ENTRYPOINT ["/usr/bin/sudo","/jdk1.8.0_151/bin/java","-javaagent:/glowroot/glowroot.jar","-Dglowroot.agent.id=sb-ne-vzw","-jar","/sb-ne-apollo.jar","--spring.config.location=application.properties"]
ENTRYPOINT ["/usr/bin/sudo","/jdk1.8.0_151/bin/java","-Dlog4j.formatMsgNoLookups=true","-javaagent:/datadog/dd-java-agent-0.76.0.jar","-jar","/sb-ne-apollo.jar","--spring.config.location=application.properties"]