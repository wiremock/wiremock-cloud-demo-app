# WireMock Cloud Demo App

Simple to do list app built on Spring Boot used to demonstrate [WireMock Cloud](http://wiremock.io).
 
## Running

Provided you have Java installed and on the `PATH`, the app can be run from the commandline with a single command.

Windows:

```
gradlew bootRun
```

Linux or Mac OSX:

```
./gradlew bootRun
```

The app will start on port 7100 (which can be changed in `application.properties`). Once it is running, point your browser to [http://localhost:7100](http://localhost:7100).


## Executing the automated tests
 
Windows:

```
gradlew test
```

Linux or Mac OSX:

```
./gradlew test
```
