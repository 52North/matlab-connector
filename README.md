# Matlab Connector [![Build Status](https://travis-ci.org/autermann/matlab-connector.png?branch=master)](https://travis-ci.org/autermann/matlab-connector)

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

### Use SSL client authentication

To enable SSL encryption of the connection you can use the following example:
```java
// should the connection only be SSL secured
// or does the server also neeeds authentication
boolean requireClientAuth = true;

// create a SSL Configuration from key, certificate and trusted certificates
SSLConfiguration config = new PemFileSSLConfiguration(
    "/path/to/the/key/file.pem",
    "/path/to/the/certificate/file.pem",
    "/path/to/the/trust/file.pem",
    requireClientAuth);

// Create client instance
MLClient client = new MLClient(new SSLSocketFactory(config));

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

If you wish to build the project from source, the [matlabcontrol 4.1.0](http://code.google.com/p/matlabcontrol/) library is required. As this is currently unavailable on most Maven repositories, you can instead [manually download the JAR file](http://code.google.com/p/matlabcontrol/downloads/detail?name=matlabcontrol-4.1.0.jar&can=1&q=) and install locally:

```bash
chmod +x deps.sh && ./deps.sh
```

Then build:

```bash
mvn clean install
```

