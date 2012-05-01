matlab-connector
================

Enables function execution on a remote MATLAB instance.


Build
---------------------

Requires [http://code.google.com/p/matlabcontrol/](matlabcontrol 3.1.0). Install locally using Maven, then build.

    mvn install:install-file -Dfile=<path-to-matlabcontrol-3.1.0.jar> -DgroupId=matlabcontrol -DartifactId=matlabcontrol -Dversion=3.1.0 -Dpackaging=jar
    mvn clean install


Usage
---------------------

Add matlab_connector.m and compiled JAR to your MATLAB path, then run.

`matlab_connector(44444, false, 0)`


Limitations
---------------------

* Single threaded.


Specification
---------------------

* TCP sockets.
* JSON.