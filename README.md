# CrisisWatcher
**CrisisWatcher** is a chat application with custom alerts, developed for a university course. It uses TCP for connection and communication and UDP for communication.

## Compiling from source
> **Note:** All the following commands must be executed from the root folder of this repository

### Requirements
 - *Maven version 3.9.9+*
 - *Java version 21.0.6+*

### Compilation
    mvn clean compile assembly:single -f "./crisiswatcher/pom.xml"

### Run
#### Server
    java -cp "./crisiswatcher/target/crisiswatcher-1.0-jar-with-dependencies.jar" dev.crisiswatcher.Server

#### Client
    java -cp "./crisiswatcher/target/crisiswatcher-1.0-jar-with-dependencies.jar" dev.crisiswatcher.Client
