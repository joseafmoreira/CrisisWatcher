# CrisisWatcher
## Description
**CrisisWatcher** is a chat application with custom alerts made for an university course using TCP and UDP for connection and communication respectively.
## Requirements
 - *Maven version 3.9.9+*
 - *Java version 21.0.6+*

## Compilation

    mvn clean compile assembly:single -f "./crisiswatcher/pom.xml

## Run
### Server
    java -cp "./crisiswatcher/target/crisiswatcher-1.0-jar-with-dependencies.jar" dev.crisiswatcher.Server
### Client
    java -cp "./crisiswatcher/target/crisiswatcher-1.0-jar-with-dependencies.jar" dev.crisiswatcher.Client
