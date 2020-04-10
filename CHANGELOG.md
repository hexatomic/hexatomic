# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Editor for the Salt annotation graph based on Zest and using a command line interface similar to GraphAnno
- Viewer for token and span annotations based on NatTable and using a similar interface to Excel
- Salt Projects can be saved now

### Changed

- Don't unload the Salt document graph when text viewer is closed, notify the project manager instead.
  This will only unload the document graph when no other editor has opened the document.

### Fixed

- Update views via notification even if the object that changed was created before the view.

## [0.3.1] - 2019-12-16

### Fixed

- Include CITATION.cff in product builds (#89)
- Automatically update CITATION.cff on tags in Travis pre-deploy

## [0.3.0] - 2019-12-14

### Changed

- Improve formatting of developer/maintainer documentation
- Add dependencies and templates checks to PR template

### Fixed

- Resolve TODOs in developer/maintainer documentation
- Improve developer/maintainer documentation towards current state

## [0.2.0] - 2019-12-12

### Added

- Add maintainer section to pull request template outlining release procedure to follow.
- Add a unit test bundle for "core" and integration test bundle that uses SWTBot.
- Add maintainer section to pull request template outlining release procedure to follow
- Add target platform section to developer/maintainer documentation
- Add third party license information and a CITATION.cff file 

## [0.1.3] - 2019-11-07

### Fixed

- Fix #75: Subdirectory compilation did not work unless the product has been installed once with `mvn install`

## [0.1.2] - 2019-11-06

### Fixed

- Renaming documents and other items in the corpus structure editor did not work

## [0.1.1] - 2019-11-01

- Fix indentation in developer/maintainer documentation (use spaces, not tabs)
- Update developer workflow documentation
- Update maintainer release documentation

## [0.1.0] - 2019-11-01

- This is the first public preview release of the Hexatomic developer platform.
[Unreleased]: https://github.com/hexatomic/hexatomic/compare/v0.3.1...HEAD
[0.3.1]: https://github.com/hexatomic/hexatomic/compare/v0.3.0...v0.3.1
[0.3.0]: https://github.com/hexatomic/hexatomic/compare/v0.2.0...v0.3.0
[0.2.0]: https://github.com/hexatomic/hexatomic/compare/v0.1.3...v0.2.0
[0.1.3]: https://github.com/hexatomic/hexatomic/compare/v0.1.2...v0.1.3
[0.1.2]: https://github.com/hexatomic/hexatomic/compare/v0.1.1...v0.1.2
[0.1.1]: https://github.com/hexatomic/hexatomic/compare/v0.1.0...v0.1.1
