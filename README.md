matlab-connector
============

Enables function execution on a remote MATLAB instance.


Build
---------------------

Requires [matlabcontrol 3.1.0](http://code.google.com/p/matlabcontrol/). Install locally using Maven, then build.

    mvn install:install-file -Dfile=matlabcontrol-3.1.0.jar -DgroupId=matlabcontrol -DartifactId=matlabcontrol -Dversion=3.1.0 -Dpackaging=jar
    mvn clean install


Usage
---------------------

Add matlab_connector.m and compiled JAR to your MATLAB path (using `javaaddpath matlab-connector-0.3-SNAPSHOT-jar-with-dependencies.jar;` or similar), then run.

`matlab_connector(44444, false, 0)`

To remotely execute a function in Java.

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


Limitations
---------------------

* Single threaded.


Specification
---------------------

* TCP sockets.
* JSON.


Acknowledgements
---------------------
MATLAB client/server code based on [TCP/IP Socket Communications in MATLAB](http://www.mathworks.com/matlabcentral/fileexchange/21131) by Rodney Thomson.