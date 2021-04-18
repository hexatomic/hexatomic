# Development setup

## Operating system for development

You can develop and build Hexatomic on Linux, Mac OS and Windows.
We suggest that you use **Linux** for development, to avoid non-straightforward install procedures for required technologies as well as potential issues with file encodings, paths, and file permissions.

## Required software

Hexatomic is implemented in **Java**, built with **Apache Maven**, and versioned with **Git**.
You need all three on your computer to contribute code to Hexatomic.

The documentation is written in Markdown and generated with **mdbook**.

### Java 11

You need to have a copy of the **Java Development Kit (JDK), Version 11** installed on your computer.
It shouldn't make any difference whether you use the open *OpenJDK* implementation of Java, or an *Oracle JDK*.
We suggest that you use **OpenJDK** since more recent versions of the *Oracle JDK* have a more restricted license.
OpenJDK is included in the package management repositories (dpkg (via apt), RPM, etc.) of most Linux distributions.
For stand-alone packages, you can use the installers distributed by the [AdoptOpenJDK project](https://adoptopenjdk.net).
AdoptOpenJDK packages are are available for Windows, MacOS, and Linux. If you are asked to choose a JVM, both HotSpot and OpenJ9 work with Hexatomic.
When installing the AdoptOpenJDK on Windows, make sure to specify that the JAVA_HOME environment variable is set.

You can check which version of Java you have installed by typing the following command into the terminal of your computer:

```bash
java -version
```

On Linux, this should produce the following output, or something similar. The second line may look different, depending on the Linux distribution you use.

```bash
openjdk version "11.0.10" 2021-01-19
OpenJDK Runtime Environment (build 11.0.10+9-Ubuntu-0ubuntu1.20.04)
OpenJDK 64-Bit Server VM (build 11.0.10+9-Ubuntu-0ubuntu1.20.04, mixed mode, sharing)
```

### Apache Maven

You need to have a copy of **Apache Maven** in **version 3.6.3 or newer** installed on your computer.
To find out how to install Maven on your system, please see the [Apache Maven website](https://maven.apache.org/).

You can check which version of Maven you have installed by typing the following command into the terminal of your computer:

```bash
mvn -version
```

On Linux, this should produce the following output, or something similar. The first line is the important one, the other ones may look different, depending on the Linux distribution you use.

```bash
Apache Maven 3.6.3
Maven home: /usr/share/maven
Java version: 11.0.10, vendor: Ubuntu, runtime: /usr/lib/jvm/java-11-openjdk-amd64
Default locale: de_DE, platform encoding: UTF-8
OS name: "linux", version: "5.10.0-1016-oem", arch: "amd64", family: "unix"
```

> <i class="fa fa-bug"></i> **Known bug in Maven version 3.6.2**
>
> If `mvn -version` returns Maven version *3.6.2*, you will have to install version 3.6.3 or newer instead,
> as version 3.6.2 contains a bug that will break the Hexatomic build. 
> You can learn more about this in the respective [bug report](https://issues.apache.org/jira/browse/MNG-6765).

### Git

You need to have a copy of **Git** in **version 2.17.1 or newer** installed on your computer.
To find out how to install Git on your system, please see the [Git website](https://git-scm.com/).

You can check which version of Git you have installed by typing the following command into the terminal of your computer:

```bash
git --version
```

On Linux, this should produce the following output.

```bash
git version 2.17.1
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

### Eclipse IDE installation

The download contains an installer which lets you configure the setup of your IDE.
To develop Hexatomic, you will need to install [Eclipse IDE for RCP and RAP Developers](https://www.eclipse.org/downloads/packages/release/2019-06/r/eclipse-ide-rcp-and-rap-developers):

- Extract the downloaded file.
- Start the Eclipse installer (`eclipse-inst`).
- From the list of available Eclipse packages, choose **Eclipse IDE for RCP and RAP Developers**.

During the installation, accept licenses, set directories, etc., as you see fit.

After you have installed the IDE itself, install the required tool plugins.

### IDE Plugin installation

If not noted otherwise, Eclipse plugins are installed as follows:

- Open the *Install Wizard* via the menu **Help > Install New Software...**
- In the **Work with:** field, enter the URL of the Update Site for the plugin, given in brackets below.
- Select the plugins you want to install, and install it with the help of the wizard.
- (Installing IDE plugins might trigger security warnings on Windows)


### List of Eclipse IDE plugins required for Hexatomic development

- [**Target Platform Definition DSL and Generator**](https://github.com/eclipse-cbi/targetplatform-dsl), **version 3.0.0-SNAPSHOT or newer**  
(Update Site: <http://download.eclipse.org/cbi/tpd/3.0.0-SNAPSHOT/>)
- [**Eclipse Checkstyle Plugin**](https://checkstyle.org/eclipse-cs/) (Update Site: <https://checkstyle.org/eclipse-cs/update>)
- 