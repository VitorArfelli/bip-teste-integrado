FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY beneficio-contract ./beneficio-contract
COPY beneficio-ejb ./beneficio-ejb
COPY beneficio-api ./beneficio-api
RUN mvn -pl beneficio-ejb -am -DskipTests package

FROM quay.io/wildfly/wildfly:31.0.1.Final-jdk17

ENV WILDFLY_USER=beneficios
ENV WILDFLY_PASSWORD=beneficios123

USER root

COPY --from=build /workspace/beneficio-ejb/target/beneficio-ejb-0.0.1-SNAPSHOT.jar /opt/jboss/wildfly/standalone/deployments/beneficio-ejb.jar
COPY docker/wildfly/configure.cli /opt/jboss/wildfly/bin/configure.cli
COPY docker/wildfly/postgresql/module.xml /opt/jboss/wildfly/modules/org/postgresql/main/module.xml
ADD https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.3/postgresql-42.7.3.jar /opt/jboss/wildfly/modules/org/postgresql/main/postgresql-42.7.3.jar

RUN chown -R jboss:root /opt/jboss/wildfly/standalone/deployments /opt/jboss/wildfly/bin/configure.cli /opt/jboss/wildfly/modules/org/postgresql \
    && chmod 0644 /opt/jboss/wildfly/modules/org/postgresql/main/postgresql-42.7.3.jar

RUN /opt/jboss/wildfly/bin/add-user.sh -a -u ${WILDFLY_USER} -p ${WILDFLY_PASSWORD} \
    && /opt/jboss/wildfly/bin/jboss-cli.sh --file=/opt/jboss/wildfly/bin/configure.cli \
    && rm -rf /opt/jboss/wildfly/standalone/configuration/standalone_xml_history/current \
    && chown -R jboss:root /opt/jboss/wildfly/standalone

USER jboss

EXPOSE 8080
