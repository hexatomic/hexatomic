# Development setup

## Operating system for development

You can develop and build Hexatomic on Linux, Mac OS and Windows.
We suggest that you use **Linux** for development, to avoid non-straightforward install procedures for required technologies as well as potential issues with file encodings, paths, and file ALLOWANCES.

## Required software

Hexatomic is implemented in **Java** and built with **Apache Maven**.
You need both on your computer to contribute code to Hexatomic.

The documentation is written in Markdown and generated with **mdbook**.

### Java 1.8

You need to have a copy of the **Java Development Kit (JDK), Version 1.8** (sometimes referred to as *Java 8*) installed on your computer.
It shouldn't make any difference whether you use the open *OpenJDK* implementation of Java, or an *Oracle JDK*.
We suggest that you use **OpenJDK** in its latest build, `1.8u222`. To find out how to install OpenJDK on your system, please see the [OpenJDK website](https://openjdk.java.net/).

You can check which version of Java you have installed by typing the following command into the terminal of your computer:

```bash
java -version
```

On Linux, this should produce the following output. The second line may look different, depending on the Linux distribution you use.

```bash
openjdk version "1.8.0_222"
OpenJDK Runtime Environment (build 1.8.0_222-8u222-b10-1ubuntu1~18.04.1-b10)
OpenJDK 64-Bit Server VM (build 25.222-b10, mixed mode)
```

### Apache Maven

You need to have a copy of **Apache Maven** in **version 3.6.0 or newer** installed on your computer.
To find out how to install Maven on your system, please see the [Apache Maven website](https://maven.apache.org/).

You can check which version of Maven you have installed by typing the following command into the terminal of your computer:

```bash
mvn -version
```

On Linux, this should produce the following output. the first line is the important one, the other ones may look different, depending on the Linux distribution you use.

```bash
Apache Maven 3.6.0
Maven home: /usr/share/maven
Java version: 1.8.0_222, vendor: Private Build, runtime: /usr/lib/jvm/java-8-openjdk-amd64/jre
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "5.0.0-23-generic", arch: "amd64", family: "unix"
```

### mdbook

**If you want to build the documentation online book locally** to, e.g., see your changes as they will look like online, 
you need to have a copy of **mdbook, version 0.3.1 or newer** installed on your computer.
To find out how to install mdbook on your system, please see the [mdbook website](https://github.com/rust-lang-nursery/mdBook).

You can check which version of mdbook you have installed by typing the following command into the terminal of your computer:

```bash
mdbook --version
```

On Linux, this should produce the following output.

```bash
mdbook v0.3.1
```


## Suggested editor: Eclipse Integrated Development Environment (IDE)

Hexatomic is built on the Eclipse 4 Platform, and takes the form of an Eclipse product composed of Eclipse plugins and Eclipse features.
While you can certainly write your Java, XML, Markdown, etc., in any editor of your choice, we suggest that you use the free and open source **Eclipse IDE, version 2019-06 or newer**.

You can download it from the [Eclipse download website](https://www.eclipse.org/downloads/).
The download contains an installer which lets you configure the setup of your IDE.
To develop Hexatomic, you will need to install Eclipse with the following options:

- **TODO**

After you have installed the IDE itself, install the following tool plugins:

- **TODO**

You can install these plugins by clicking **TODO**