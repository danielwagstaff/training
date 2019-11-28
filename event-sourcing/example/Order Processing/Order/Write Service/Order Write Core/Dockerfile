####
# This Dockerfile is used in order to build a container that runs the Quarkus application in JVM mode
#
# Before building the docker image run:
#
# mvn package
#
# Then, build the image with:
#
# docker build -t triad.distribution/order-write .
#
# Then run the container using:
#
# docker run -i --rm -p 8080:8080 triad.distribution/order-write
#
###
FROM fabric8/java-alpine-openjdk8-jre
ENV JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0 \
                  -Djava.util.logging.manager=org.jboss.logmanager.LogManager \
                  -Dcassandra.connection.node=172.17.0.2 \
                  -Dcassandra.connection.port=9042 \
                  -Dcassandra.connection.dataCentre=datacenter1 \
                  -Dcassandra.connection.tableName=event_store.events"
ENV AB_ENABLED=jmx_exporter
COPY ["./target/*-runner.jar", "/deployments/app.jar"]
ENTRYPOINT [ "/deployments/run-java.sh" ]
