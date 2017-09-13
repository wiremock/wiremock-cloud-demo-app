# MockLab Demo App

Simple to do list app built on Spring Boot used to demonstrate [MockLab](http://get.mocklab.io).
 
## Running

Before starting the app you first need to set the base URL of your MockLab mock API. You can find the base URL in the Settings page
for your chosen Mock API.

Open `src/main/resources/application.properties` and set the `mocklab.baseurl` property to point to your API.

Provided you have Java installed and on the `PATH`, the app can be run from the commandline with a single command.

Windows:

```
gradlew bootRun
```

Linux or Mac OSX:

```
./gradlew bootRun
```

The app will start on port 9000 (which can be changed in `application.properties`). Once it is running, point your browser to [http://localhost:9000](http://localhost:9000).


## Executing the automated tests
 
Windows:

```
gradlew test
```

Linux or Mac OSX:

```
./gradlew test
```

