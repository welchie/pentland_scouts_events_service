# Pentland Scouts - Events Backend Service

A project to provide the backend service capabilities using Spring-Boot and DynamoDB..

## Architecture
Spring Boot application

- config - DynamoDBConfig
- controller - RestController classes
- model - DynamoDB Beans
- repositories - Spring CRUD Repositories
- Application.java - main Spring Boot class.

### Latest Updates
- 0.0.1-SNAPSHOT
  - Initial input
- 0.0.2-SNAPSHOT
  - Adding additional Personal Details

### Building
 - mvn clean install spring-boot:repackage

### Running
  - From Maven
    - mvn -Dspring-boot.run.profiles=<PROFILE HERE> clean spring-boot:run
    - Requires application-PROFILE.properties in /src/main/resources e.g. application-dev.properties
 - OR from built JAR
   -  java -Dspring.profiles.active=prod -jar events-service-0.0.2-SNAPSHOT.jar



### Docker
 - ./docker_create.sh
 - docker run -p 80:8080 welchie99/scoutsevents:0.0.1-SNAPSHOT-PROD
