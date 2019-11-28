####
# This Dockerfile is used in order to build a container that runs the Quarkus application in JVM mode
#
# Before building the docker image run:
#
# mvn package
#
# Then, build the image with:
#
# docker build -t triad.distribution/process .
#
# Then run the container using:
#
# docker run -i --rm -p 8080:8080 triad.distribution/process
#
###
FROM fabric8/java-alpine-openjdk8-jre
ENV JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0 \
                  -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV AB_ENABLED=jmx_exporter
COPY ["./target/*-runner.jar", "/deployments/app.jar"]
ENTRYPOINT [ "/deployments/run-java.sh" ]
