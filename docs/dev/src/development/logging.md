# Logging

We use the slf4j logging API in our code.
To enable a logger for a specific class, create a static final variable which holds the class-specific logging object.

```java
private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MyClass.class);
```

You can use this `log` object everwhere in the code to output debug, info, warning or error messages to the console or to the log files. 

```java
int counter = 101;
log.debug("Selecting created document {}.", 101);
```

See the [SLF4J manual](https://www.slf4j.org/manual.html) for more information on the API.

To implement the API, we use the [Logback library](https://logback.qos.ch/) in the `org.corpus_tools.hexatomic.core` bundle.
For testing and debugging purposes, there is a `logback-test.xml` file in the root of the logging bundle. 
This configuration file is loaded whenever Hexatomic is started from Eclipse using the Debug/Run configuration and will output messages on the `debug` level.
When compiling the Hexatomic product, the file `logback.xml` is copied to the product root directory and loaded from there.
This allows a user/developer to customize the logging on its own, even when using a published binary artifact instead of debugging directly from the Eclipse IDE.
Per default, only messages from the `info` level and above are printed to the command line.
See the [Logback documentation](https://logback.qos.ch/manual/configuration.html) how to write or adapt this file.
