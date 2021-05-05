# Licensing and citation

This section describes how licensing and citation information is handled in Hexatomic.

## What licenses is Hexatomic licensed under?

The Hexatomic software is licensed under the Apache License, Version 2.0.
Read [the whole license text](http://web.archive.org/web/20191017082353/http://www.apache.org/licenses/LICENSE-2.0) to learn more about the license.

The Hexatomic documentation is licensed under a [CC0 1.0 Universal (CC0 1.0)](https://creativecommons.org/publicdomain/zero/1.0/legalcode) license. 
See the [License page](../../LICENSE.html) for more information.

## Automating the fulfillment of license requirements and citation

In order to make it easier to maintain the licenses for Hexatomic and its dependencies, 
we automate the fulfillment of license requirements.

We also automatically generate the citation metadata for Hexatomic in the Citation File Format (CFF) file [`CITATION.cff`](https://github.com/hexatomic/hexatomic/blob/develop/CITATION.cff).
You can learn more about CFF one the [Citation File Format website](https://citation-file-format.github.io/).

### Source files

Hexatomic includes the Maven License Plugin from MojoHaus in the build.
This plug-in handles the license information of the source files included in this repository.
You can read about the plug-in on the [Maven License Plugin website](http://www.mojohaus.org/license-maven-plugin/index.html).

### Dependencies

We use the Citation File Format Maven plugin to
document Hexatomic's dependencies, their licenses, and their citation information.
The plugin is run during the [continuous integration release workflow](../continuous-integration/#release-workflow).
This plug-in provides two goals, which can be executed to update the third-party dependency information.
Since our external dependencies are currently collected by the `org.corpus_tools.hexatomic` feature,
you have to execute the following Maven commands in the `features/org.corpus_tools.hexatomic` folder.
The created files and folders will be located in the root folder of this project.

`mvn cff:create` creates a new `CITATION.cff` file in the [Citation File Format (CFF)](https://citation-file-format.github.io/).
This file does not only include basic information about the authorship of the Hexatomic project but also lists dependencies, for citation purposes.

`mvn cff:third-party-folder` re-creates the `THIRD-PARTY` folder.
It includes the license itself and additional license files like `NOTICE` (used by projects under the Apache License Version 2.0) or `about.html` (used by Eclipse P2 repositories). 

You can configure the behavior of the CFF plug-in by editing the `features/org.corpus_tools.hexatomic/pom.xml`.
Several templates for curated license information are located in the `releng/templates` folder.
This includes the `releng/templates/CITATION.cff` file, which is a template for citation information - e.g., the curated list of authors - for the software itself (here: Hexatomic),
but it can also be used to provide citation metadata for references, which shouldn't be overwritten during auto-generation.

To learn more about configuring the CFF plugin and working with templates, refer to the [Citation File Format Maven plugin project](https://github.com/hexatomic/cff-maven-plugin).
