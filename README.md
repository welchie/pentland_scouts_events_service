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

### Building
 - mvn clean install spring-boot:repackage

### Running
 - mvn clean sping-boot:run

### Docker
 - ./docker_create.sh
 - docker run -p 80:8080 welchie99/scoutsevents:0.0.1-SNAPSHOT-PROD
