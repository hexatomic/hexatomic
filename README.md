<!-- PROJECT SHIELDS -->
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-2-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->
[![Build Status][travis-master-shield]][travis-master-url]  
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![Apache License, Version 2.0][license-shield]][license-url]
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-1-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->



<!-- PROJECT LOGO -->
<br />
<p align="center">
  <!-- <a href="https://github.com/hexatomic/hexatomic">
    <img src="images/logo.png" alt="Logo" width="80" height="80">
  </a>
  -->
  <h3 align="center">Hexatomic</h3>

  <p align="center">
    A platform for deep multi-layer linguistic corpus annotation.
    <br />
    <a href="https://hexatomic.github.io/hexatomic/user/"><strong>Read the user docs »</a> |
    <a href="https://hexatomic.github.io/hexatomic/dev/">Read the developer/maintainer docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/hexatomic/hexatomic/issues/new">Report Bug</a>
    ·
    <a href="https://github.com/hexatomic/hexatomic/issues/new">Request Feature</a>
  </p>
</p>



<!-- TABLE OF CONTENTS -->
## Table of Contents

- [Table of Contents](#table-of-contents)
- [About The Project](#about-the-project)
  - [Technologies](#technologies)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Build](#build)
- [Documentation](#documentation)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [Key documents](#key-documents)
- [Changelog](#changelog)
- [License](#license)
- [Contact](#contact)
- [Team](#team)
  - [Maintainer](#maintainer)
  - [Core contributors](#core-contributors)
  - [Principal investigators](#principal-investigators)
  - [Contributors](#contributors)
<!--- [Acknowledgements](#acknowledgements)-->



<!-- ABOUT THE PROJECT -->
## About The Project

<!-- [![Product Name Screen Shot][product-screenshot]](https://example.com) -->

Hexatomic is an extensible, OS-independent platform for deep multi-layer linguistic annotation of corpora.

It is being developed for sustainability, in order to support research software re-use rather than new development of software with each new research project. Using Hexatomic, linguistic research projects can implement what they need on top of an existing platform with high compatibility to other tools and pipelines.

Hexatomic is funded by [Deutsche Forschungsgemeinschaft (DFG)](https://www.dfg.de/en/) under grant number [391160252](https://gepris.dfg.de/gepris/projekt/391160252?language=en).

Development is based at the [Department of English Studies (Friedrich Schiller University Jena)](https://www.iaa.uni-jena.de/en/) and the [Department for German Studies and Linguistics (Humboldt-Universität zu Berlin)](https://www.linguistik.hu-berlin.de/de).

### Technologies

- Hexatomic is a Java application based on the [Eclipse][eclipse] 4 Platform.
- The Hexatomic documentation is built with [mdbook][mdbook], a utility to create modern online books from Markdown files.



<!-- GETTING STARTED -->
## Getting Started

Clone this repository to your computer:

```sh
git clone https://github.com/hexatomic/hexatomic.git
```

### Prerequisites

You need at least the following software installed on your computer to develop Hexatomic:

- Java Development Kit (JDK) version `>= 1.8`, either [OpenJDK][openjdk] (Linux, Mac OS), or [Oracle JDK][oracle-jdk] (Windows)
- [Apache Maven][maven] version `>= 3.6.0`

If you want to build the documentation locally, you also need

- [mdbook][mdbook] version `>= 0.3.1`

For development, we recommend that you use the tooling provided by the

- [Eclipse IDE][eclipse-download] version `>= 2019-06`

### Build

Go to the repository root on your computer, and run the following command to verify that the project builds.

```bash
mvn clean install
```


<!-- USAGE EXAMPLES -->
## Documentation

Please refer to the documentation for details on how to develop Hexatomic: [Developer & Maintainer Documentation][dev-docs].

If you are looking for documentation of how to *use* Hexatomic, please refer to the [user documentation][user-docs].


<!-- ROADMAP -->
## Roadmap

See the [open issues](https://github.com/hexatomic/hexatomic/issues) for a list of proposed features (and known issues).




<!-- CONTRIBUTING -->
## Contributing

We welcome contributions from the community!

Please refer to the [`CONTRIBUTING.md`][contributing] file for information on how to contribute to Hexatomic.

If you contribute to Hexatomic in any way, you are expected to adhere to our project's [Code of Conduct][coc].

## Key documents

- [Contributing to Hexatomic][contributing] - How to contribute to the development of this module.
- [Code of Conduct][coc] - The code of conduct that we expect contributors to adhere to.
- [Developer documentation][dev-docs] - Important documentation on development and tools.


<!-- CHANGELOG -->
## Changelog

See [CHANGELOG.md][changelog].



<!-- LICENSE -->
## License

The Hexatomic software is licensed under the Apache License, Version 2.0. See [`LICENSE`](LICENSE) for more information.  
The Hexatomic documentation is licensed under a [CC0 1.0 Universal (CC0 1.0)][cc0] license.



<!-- CONTACT -->
## Contact

The project website of the Hexatomic research project is at [hexatomic.github.io][project].

You can contact us per email: `hexatomic [at] corpus-tools.org`.



<!-- TEAM -->
## Team

### Maintainer

- Stephan Druskat ([@sdruskat](https://github.com/sdruskat))

### Core contributors

- Stephan Druskat ([@sdruskat](https://github.com/sdruskat))
- Thomas Krause ([@thomaskrause](https://github.com/thomaskrause))

### Principal investigators

- Volker Gast ([@VolkerGast](https://github.com/VolkerGast))
- Anke Lüdeling ([@AnkeLuedeling](https://github.com/AnkeLuedeling))



<!-- ACKNOWLEDGEMENTS -->
<!--## Acknowledgements
- The [mdbook][mdbook] project
-->





<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/hexatomic/hexatomic.svg?style=flat-square
[contributors-url]: https://github.com/hexatomic/hexatomic/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/hexatomic/hexatomic.svg?style=flat-square
[forks-url]: https://github.com/hexatomic/hexatomic/network/members
[stars-shield]: https://img.shields.io/github/stars/hexatomic/hexatomic.svg?style=flat-square
[stars-url]: https://github.com/hexatomic/hexatomic/stargazers
[issues-shield]: https://img.shields.io/github/issues/hexatomic/hexatomic.svg?style=flat-square
[issues-url]: https://github.com/hexatomic/hexatomic/issues
[license-shield]: https://img.shields.io/github/license/hexatomic/hexatomic.svg?style=flat-square
[license-url]: https://github.com/hexatomic/hexatomic/blob/master/LICENSE
[product-screenshot]: images/screenshot.png
[travis-master-shield]: https://img.shields.io/travis/hexatomic/hexatomic/develop.svg?style=flat-square
[travis-master-url]: https://travis-ci.org/hexatomic/hexatomic

[eclipse-download]: https://www.eclipse.org/downloads/
[eclipse]: https://eclipse.org
[mdbook]: https://github.com/rust-lang-nursery/mdBook
[maven]: https://maven.apache.org/
[openjdk]: https://openjdk.java.net/
[oracle-jdk]: https://www.oracle.com/technetwork/java/javase/downloads/index.html

[user-docs]: https://hexatomic.github.io/hexatomic/user/
[dev-docs]: https://hexatomic.github.io/hexatomic/dev/
[contributing]: ./CONTRIBUTING.md
[coc]: ./CODE_OF_CONDUCT.md
[cc0]: https://creativecommons.org/publicdomain/zero/1.0/legalcode
[project]: https://hexatomic.github.io
[changelog]: ./CHANGELOG.md

### Contributors

We would like to thank the following people for contributing to Hexatomic!  
([Overview of roles as represented by emojis](https://allcontributors.org/docs/en/emoji-key))

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="https://sdruskat.net"><img src="https://avatars0.githubusercontent.com/u/3007126?v=4" width="100px;" alt=""/><br /><sub><b>Stephan Druskat</b></sub></a><br /><a href="#maintenance-sdruskat" title="Maintenance">🚧</a> <a href="https://github.com/hexatomic/hexatomic/commits?author=sdruskat" title="Code">💻</a> <a href="#content-sdruskat" title="Content">🖋</a> <a href="https://github.com/hexatomic/hexatomic/commits?author=sdruskat" title="Documentation">📖</a> <a href="#fundingFinding-sdruskat" title="Funding Finding">🔍</a> <a href="#ideas-sdruskat" title="Ideas, Planning, & Feedback">🤔</a> <a href="#infra-sdruskat" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a> <a href="#question-sdruskat" title="Answering Questions">💬</a> <a href="https://github.com/hexatomic/hexatomic/pulls?q=is%3Apr+reviewed-by%3Asdruskat" title="Reviewed Pull Requests">👀</a> <a href="https://github.com/hexatomic/hexatomic/commits?author=sdruskat" title="Tests">⚠️</a> <a href="#talk-sdruskat" title="Talks">📢</a> <a href="https://github.com/hexatomic/hexatomic/issues?q=author%3Asdruskat" title="Bug reports">🐛</a></td>
    <td align="center"><a href="http://u.hu-berlin.de/korpling-thomaskrause"><img src="https://avatars3.githubusercontent.com/u/2168104?v=4" width="100px;" alt=""/><br /><sub><b>Thomas Krause</b></sub></a><br /><a href="https://github.com/hexatomic/hexatomic/commits?author=thomaskrause" title="Code">💻</a> <a href="#content-thomaskrause" title="Content">🖋</a> <a href="https://github.com/hexatomic/hexatomic/commits?author=thomaskrause" title="Documentation">📖</a> <a href="#fundingFinding-thomaskrause" title="Funding Finding">🔍</a> <a href="#ideas-thomaskrause" title="Ideas, Planning, & Feedback">🤔</a> <a href="#infra-thomaskrause" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a> <a href="#question-thomaskrause" title="Answering Questions">💬</a> <a href="https://github.com/hexatomic/hexatomic/pulls?q=is%3Apr+reviewed-by%3Athomaskrause" title="Reviewed Pull Requests">👀</a> <a href="https://github.com/hexatomic/hexatomic/commits?author=thomaskrause" title="Tests">⚠️</a> <a href="#talk-thomaskrause" title="Talks">📢</a></td>
  </tr>
</table>

<!-- markdownlint-enable -->
<!-- prettier-ignore-end -->
<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!
