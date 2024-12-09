@echo off
"./apache-maven-3.9.9/bin/mvn" clean compile assembly:single -f "./crisiswatcher/pom.xml"