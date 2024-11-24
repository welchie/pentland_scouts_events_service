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
- 1.0.0 Production release
  - Updated to include EventAttendeeHistory feature
- Previous releases see Github history 

### Building
```bash
    mvn -Dspring-boot.run.profiles=dev clean install spring-boot:repackage 
```

### Running
#### From Maven 
```bash
    mvn -Dspring-boot.run.profiles=dev clean spring-boot:run
  ```

- Requires application-PROFILE.properties in /src/main/resources e.g. application-dev.properties
 - OR from built JAR
``` bash
    cd target
    java -Dspring.profiles.active=dev -jar events-service-1.0.0.jar
 ```

### Docker

#### Creating 
````bash
    ./docker_create_prod.sh
    docker images | grep scoutsevents
 ````
    
    
#### Running
``` bash
    docker rm scouts-events
    docker run -d -p 9090:8080 --name scouts-events welchie99/scoutsevents:1.0.0-PROD
    docker ps
    docker logs scouts-events -f 
````

#### Stopping 
``` bash
    docker stop scouts-events
```

### TODO
 - ~~Add Activities / Teams / Scoreboard API~~  Separate project
 - Parameterise builds - current version hardcoded in files
 - Version/Get return the env prod/dev
