# Matlab Connector [![Build Status](https://travis-ci.org/52North/matlab-connector.png?branch=master)](https://travis-ci.org/52North/matlab-connector)

**This project is not part of the 52°North managed code base.**

The matlab-connector library enables function execution on a remote MATLAB instance.

JSON is used as a platform and language independent.

## Server setup

```
Usage: java -jar matlab-connector-2.0-SNAPSHOT-with-dependencies.jar [options]
  Options:
    -b, --base-dir
       The base directory.
       Default: /home/auti/Source/matlab-connector
        --cert-file
       Path to SSL server certificate (incl. chain) in PEM format.
        --clientauth
       Path to a PEM file containing all trusted (client) certificates.
        --debug
       Show debug output.
       Default: false
    -h, --help
       Display this help message.
        --key-file
       Path to SSL server key in PEM format.
        --keystore-config
       Path to the SSL config file.
    -p, --port
       The port to listen on.
       Default: 7000
    -t, --threads
       The amount of server threads.
       Default: 5
        --trust-file
       Path to a PEM file containing all trusted (client) certificates.
```

## Using the Java client

### Evaluating a MATLAB function

To execute a function in Java. Host can be localhost, or remote.

```java
// Create client instance
MLClient client = new MLClient();

// Build request
MLRequest request = new MLRequest("do_a_sum");
request.addParameter(new MLScalar(2));
request.addParameter(new MLScalar(2));

// Send request
MLResult result = client.sendRequest("localhost", 44444, request);

// Print result
System.out.println(result.toString());
```

## Build

```bash
mvn clean install
```

