
FROM amazoncorretto:21-al2023-jdk
MAINTAINER chris@weewelchie.org
ARG BUILD_ARTIFACT=events-service-0.0.11-SNAPSHOT
ARG SPRING-PROFILE=dev

CMD RUN ECHO ${BUILD_ARTIFACT}
CMD RUN ECHO ${SPRING-PROFILE}

COPY target/${BUILD_ARTIFACT}.jar ${BUILD_ARTIFACT}.jar
ENTRYPOINT ["java","-jar","events-service-0.0.11-SNAPSHOT.jar","--spring.profiles.active=prod"]
