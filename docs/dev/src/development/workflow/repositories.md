# Repositories

Currently, Hexatomic is developed within a single repository at <https://github.com/hexatomic/hexatomic>.

Push access to this repository is granted to the [Hexatomic core contributors](https://github.com/hexatomic/hexatomic#core-contributors).
Other contributors must work in their own fork. 

Section [*Downloading the source code to your computer*](../getting-the-source-code.html#downloading-the-source-code-to-your-computer) describes how to create a fork. If you have not worked in your own fork for a longer period of time, then you can use the GitHub's "Fetch Upstream" function (directly below the greed **Code** button in the fork overview) to synchronize your fork with the current main repository.

## Project setup

**Repository structure of [`hexatomic/hexatomic`](https://github.com/hexatomic/hexatomic):**

- **Root directory**
    - main [POM](http://web.archive.org/web/20191212124853/https://maven.apache.org/guides/introduction/introduction-to-the-pom.html) (`pom.xml`)
    - Meta files (`.gitignore`, `README.md`, `LICENSE`, `CONTRIBUTING.md`, `CODE_OF_CONDUCT.md`, etc.)
- **`bundles`**
    - bundle sub-projects
- **`features`**
    - feature sub-projects
- **`docs`**
    - source files for user and developer/maintainer documentation
- **`releng`**
    - configuration and release engineering files
- **`.github`** 
    - GitHub specific configuration, such as issue templates and workflows of the GitHub Actions CI

### Setup details

The Hexatomic project in the `hexatomic/hexatomic` repository is set up generally
along the lines of [Eclipse Tycho for building plug-ins, OSGi bundles and Eclipse applications - Tutorial](http://web.archive.org/web/20190801113418/https://www.vogella.com/tutorials/EclipseTycho/article.html) by 
Lars Vogel and Simon Scholz, Version 13.03.2019, and [Eclipse RCP (Rich Client Platform) - Tutorial](http://web.archive.org/web/20190807184652/https://www.vogella.com/tutorials/EclipseRCP/article.html) by Lars Vogel, Version 10.12.2018.

This means that

1. It is **built using [Eclipse Tycho](http://web.archive.org/web/20190807185333/https://www.eclipse.org/tycho/)**, a plugin for the [Maven build system](http://web.archive.org/web/20190806000707/https://maven.apache.org/).
2. **Bundles** (a.k.a. "plug-ins"), i.e., units that encapsulate well-defined functionality, are located in the [`bundles`](https://github.com/hexatomic/hexatomic/tree/main/bundles/) directory.
3. **Features**, i.e., units that integrate one or more bundles, are located in the [`features`](https://github.com/hexatomic/hexatomic/tree/feature/0.1.0/features/) directory.
4. **Configuration and release engineering**-related files are located in the [`releng`](https://github.com/hexatomic/hexatomic/tree/feature/0.1.0/releng/) directory. This includes:
    - Project configuration (for packaging, target platform configuration, etc.)
    - *Product* definition; in the Eclipse world, products are units that in turn integrate one or more features to define a deliverable software.
    - *Target platform* definition; the target platform defines the features and bundles that Hexatomic is built on top of (*against*).
    - *Update site* definition; Hexatomic's features and bundles are provided 
        1. through a repository in the [p2 repository format](http://web.archive.org/web/20190807191916/https://www.eclipse.org/equinox/p2/), from which Hexatomic can be updated automatically and manually from within the application in the future; and
        2. As deployable units, i.e., the actual files that users will download in order to get Hexatomic.
    -  Miscellanea, e.g., shell scripts for deploying artifacts, templates for third-party licensing and citation, etc. 
5. It uses **pomless builds**, i.e., not every single bundle or feature needs its own build definition XML file (`pom.xml`), which saves a lot of manual labour.

#### Project parent / root POM

Hexatomic has a [root POM](https://github.com/hexatomic/hexatomic/tree/main/pom.xml) to build the entire project with a single Maven command, e.g., `mvn clean install`, issued in the root folder.

#### Target platform

The target platform for Hexatomic is defined using the [target platform definition file](https://github.com/hexatomic/hexatomic/blob/develop/releng/org.corpus_tools.hexatomic.target/org.corpus_tools.hexatomic.target.target).
It is used by both the Eclipse IDE and the Tycho Maven Plugin (which is used to build Hexatomic).
More details about target platforms can be found in the section [*Creating and activating the target platform*](../activating-target-platform.html).

#### Documentation

Both the user documentation and the documentation for developers and maintainers are kept in the [`/docs/` folder](https://github.com/hexatomic/hexatomic/tree/develop/docs), in a subfolder for each documentation type.
Documentation is written in Markdown from which websites are generated for the end user with *mdbook* (see section [*Development setup*](../setup.html#mdbook)).
The actual sources for the documentation can be found in the respective `/src/` subfolders.

#### README files

All folders that are parent folders in the repository contain a `README.md` file that explains the contents of the folder.
