# Project setup

> TODO Merge with repositories.md

**Repository structure of [`hexatomic/hexatomic`](https://github.com/hexatomic/hexatomic):**

- **Root directory**
	- main POM
	- `.travis.yml` ([Travis CI](https://travis-ci.org/) config)
	- Meta files (`.gitignore`, `README.md`, `LICENSE`, `CONTRIBUTING.md`, `CODE_OF_CONDUCT.md`, etc.)
- **`bundles`**
	- plug-in sub-projects
- **`features`**
	- feature sub-projects
- **`docs`**
	- source files for user and developer/maintainer documentation
- **`releng`**
	- configuration and release engineering files

## Setup details

The Hexatomic project in the `hexatomic/hexatomic` repository is set up generally
along the lines of [Eclipse Tycho for building plug-ins, OSGi bundles and Eclipse applications - Tutorial](http://web.archive.org/web/20190801113418/https://www.vogella.com/tutorials/EclipseTycho/article.html) by 
Lars Vogel and Simon Scholz, Version 13.03.2019, and [Eclipse RCP (Rich Client Platform) - Tutorial](http://web.archive.org/web/20190807184652/https://www.vogella.com/tutorials/EclipseRCP/article.html) by Lars Vogel, Version 10.12.2018.

This means that

1. It is **built using [Eclipse Tycho](http://web.archive.org/web/20190807185333/https://www.eclipse.org/tycho/)**, a plugin for the [Maven build system](http://web.archive.org/web/20190806000707/https://maven.apache.org/).
2. **Plug-ins**, i.e., units that encapsulate well-defined functionality, are located in the [`bundles`](https://github.com/hexatomic/hexatomic/tree/master/bundles/) directory.
3. **Features**, i.e., units that integrate one or more plug-ins, are located in the [`features`](https://github.com/hexatomic/hexatomic/tree/feature/0.1.0/features/) directory.
4. **Configuration and release engineering**-related files are located in the [`releng`](https://github.com/hexatomic/hexatomic/tree/feature/0.1.0/releng/) directory. This includes:
	- Project configuration (for packaging, target platform configuration, etc.)
	- *Product* definition; in the Eclipse world, products are units that in turn integrate one or more features to define a deliverable software.
	- *Target platform* definition; the target platform defines the features and plug-ins that Hexatomic is built on top of (*against*).
	-  *Update site* definition; Hexatomic's features and plug-ins are provided 
		1. through a repository in the [p2 repository format](http://web.archive.org/web/20190807191916/https://www.eclipse.org/equinox/p2/), from which Hexatomic can be updated automatically and manually from within the application; and
		2. As deployable units, i.e., the actual zip files that users will download in order to get Hexatomic.
	-  Miscellanea, e.g., shell scripts for deploying artifacts, etc. 
5. It uses **pomless builds**, i.e., not every single plug-in or feature needs its own build definition XML file (`pom.xml`), which saves a lot of manual labour.

### Project parent / root POM

Hexatomic has a [root POM](https://github.com/hexatomic/hexatomic/tree/master/pom.xml), i.e., to build the entire project, a single Maven command, e.g., `mvn clean verify`, in the root folder is enough.

### Target platform

tpd

## Documentation

All folders that are parent folders shall contain a `README.md` file that explains the contents of the folder.

`TODO FIXME BELOW`

Other than that, cf. section on documentation!

`TODO FIXME ABOVE`