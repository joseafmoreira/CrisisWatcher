# CrisisWatcher
**CrisisWatcher** is a chat application with custom alerts made for an university course using TCP and UDP for connection and communication respectively.

Application made using these software
Software version list
 - *Maven version 3.9.9*
 - *Java version 21.0.6*

You can compile the project using the following commands in the repository root folder.
### Linux / MacOS
`mvn clean compile assembly:single -f "./crisiswatcher/pom.xml`
### Windows
`mvn clean compile assembly:single -f "./crisiswatcher/pom.xml"`

After the compilation you run the server using the following command

`java -cp "./crisiswatcher/target/crisiswatcher-1.0-jar-with-dependencies.jar" dev.crisiswatcher.Server`

And to run a client use the following command

`java -cp "./crisiswatcher/target/crisiswatcher-1.0-jar-with-dependencies.jar" dev.crisiswatcher.Client`
