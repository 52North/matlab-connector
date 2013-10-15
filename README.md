# Matlab Connector [![Build Status](https://travis-ci.org/autermann/matlab-connector.png?branch=master)](https://travis-ci.org/autermann/matlab-connector)

The matlab-connector library enables function execution on a remote MATLAB instance.

JSON is used as a platform and language independent.

## Server setup

```
Usage: java -jar matlab-connector-2.0-SNAPSHOT-with-dependencies.jar [options]
  Options:
    -b, --base-dir
       The base directory.
       Default: current directory
    -h, --help
       Display this help message.
    -p, --port
       The port to listen on.
       Default: 7000
    -t, --threads
       The amount of server threads.
       Default: 5

```

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
    <version>2.0-SNAPSHOT</version>
  </dependency>
</dependencies>
```

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

If you wish to build the project from source, the [matlabcontrol 4.1.0](http://code.google.com/p/matlabcontrol/) library is required. As this is currently unavailable on most Maven repositories, you can instead [manually download the JAR file](http://code.google.com/p/matlabcontrol/downloads/detail?name=matlabcontrol-4.1.0.jar&can=1&q=) and install locally:

```bash
chmod +x deps.sh && ./deps.sh
```

Then build:

```bash
mvn clean package
```

Tests will fail unless a local MATLAB installation can be found.

## Limitations

Security has also been overlooked in the current version, I'd recommended restricting access to the port the server runs on until access control is implemented.
