The matlab-connector library enables function execution on a remote MATLAB instance.

JSON is used as a platform and language independent.

## Server setup

Add matlab_connector.m and compiled JAR to your MATLAB path (using `javaaddpath matlab-connector-0.4-jar-with-dependencies.jar;` or similar), then run.

`matlab_connector(44444, false, 0)`

## Sending requests

JSON. cells and structs are in this version.

## Using the Java client

### Getting started

The connector is available on the UncertWeb Maven repository, hosted at the [University of Münster](http://www.uni-muenster.de/). Adding the following snippet to your `pom.xml` file will include the repository in your project.

```xml
<repositories>
  <!-- Other repositories may be here too -->
  <repository>
    <id>UncertWebMavenRepository</id>
    <name>UncertWeb Maven Repository</name>
    <url>http://giv-uw.uni-muenster.de/m2/repo</url>
  </repository>
</repositories>
```

The dependency for the connector can then be added.

```xml
<dependencies>
  <!-- Other dependencies may be here too -->
  <dependency>
    <groupId>org.uncertweb</groupId>
    <artifactId>matlab-connector</artifactId>
    <version>0.4</version>
  </dependency>
</dependencies>
```

Alternatively, download the JAR file directly. ***********

### Evaluating a MATLAB function

To execute a function in Java. Host can be localhost, or remote.

```java
// Create handler instance
MLHandler handler = new MLHandler();

// Build request
MLRequest request = new MLRequest("do_a_sum");
request.addParameter(new MLScalar(2));
request.addParameter(new MLScalar(2));

// Send request
MLResult result = handler.sendRequest("localhost", 44444, request)

// Print result
System.out.println(result.toString());
```

## Build

If you wish to build the project from source, the [matlabcontrol 3.1.0](http://code.google.com/p/matlabcontrol/) library is required. As this is currently unavailable on most Maven repositories, you can instead [manually download the JAR file](http://code.google.com/p/matlabcontrol/downloads/detail?name=matlabcontrol-3.1.0.jar&can=1&q=) and install locally:

```console
$ mvn install:install-file -Dfile=matlabcontrol-3.1.0.jar -DgroupId=matlabcontrol -DartifactId=matlabcontrol -Dversion=3.1.0 -Dpackaging=jar
```

Then build:

```console
$ mvn clean package
```

<!--
Note that tests will fail unless a local MATLAB installation can be found.
-->

## Limitations

MATLAB is restricted to a single computational thread, preventing the server component from handling simultaneous requests. Security has also been overlooked in the current version, I'd recommended restricting access to the port the server runs on until access control is implemented.

## Acknowledgements

MATLAB client/server code based on [TCP/IP Socket Communications in MATLAB](http://www.mathworks.com/matlabcentral/fileexchange/21131) by Rodney Thomson.