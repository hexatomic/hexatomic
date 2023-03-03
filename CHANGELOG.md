# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Fixed

- There seems to be an issue with Maven-based target platform entries and the
  automatic update function. Removed Maven-based target platform entries and
  replace them with the original P2 repos for Pepper and Salt. 

## [1.1.4] - 2023-03-03

### Fixed

- Explicitly set the Java version in the workflow to publish the P2 update
  site.


## [1.1.3] - 2023-03-03

- ~~Excplicitly set the Java version in the workflow to publish the P2 update
  site.~~ 

## [1.1.2] - 2023-03-03

### Fixed

- Workaround for a bug in Maven 3.9.0 that let our release workflows fail. To
  avoid this, we excplicitly use a specific Maven version instead of using the
  default one from GitHub Actions.

## [1.1.1] - 2023-03-03

### Fixed

- Deleting a document could not be undone (#422)
- "Tokenize after import" checkbox label was located wrong in text importer (#420)
- Running the tests on macOS would fail (#417). An GitHub action is now
  configured to execute the tests also on macOS for each pull request.
- About dialog did mention the Apache License for Hexatomic itself, but not the
  dependencies (#416).
- Update to Eclipse platform version 2022-12, which has various fixes. This does
  not change any functionality for the user, but we also now bundle Java version


## [1.1.0] - 2023-02-19

### Fixed

- Stabilize graph editor test to wait until background jobs are finished before
  selecting the text fragment.
- Clarify Mac OS installation instructions in user documentation.
- Show more explanations when update check fails and only show status message
  when update check was started in background (#410).

### Added

- Add user interface for graph editor graph layout parameters  (#133)
- Add refactoring operation to automatically generate node names like `n1`, `t1`, etc. (#370)

## [1.0.1] - 2022-08-30

### Fixed

- Create and use v1 update site

## [1.0.0] - 2022-08-23

### Changed

- Improve developer docs on GitHub releases to wait for binaries attachments and mention Zenodo publications

## [0.14.0] - 2022-08-23

### Added

- Add collection DOI to citation file template (#387)

### Fixed

- Disable automatic workflow that to update CITATION.CFF file since it always
  fails to push without a PR and code review.
- Fix documentation for releases on how to update the CITATION.CFF file.
- Fix license metadata for referencing for bundle org.tukaani.xz


## [0.13.0] - 2022-07-25

### Added

- Add CI workflow to automatically update dependency and citation metadata on
  pushes to `develop` (#394)
- Document to close IDEs during release process (#395)
- Hexatomic is automatically published to Zenodo for each release (#387)
- Use the product definition to create the CITATION.CFF file and THIRD-PARTY
  folder. Excuting `mvn -Pcff package` in the root folder will re-create these
  files.

## [0.12.0] - 2022-06-21

### Fixed

- Manually update THIRD-PARTY folder and CITATION.cff during release process
- Fix changelog sections that weren't updated for tag v0.11.0

## [0.10.0] - 2022-06-21

### Added

- Add functionality for splitting spans into single spans to Grid Editor (#101)
- Add functionality for merging spans into a single span to Grid Editor (#102)
- Deploy p2 repo for updates automatically for releases (#288) 
- Add auto-archival on Software Heritage whenever git tags are pushed to the repository (#21)

### Changed

- Changed name of the production branch from `master` to `main` (#333)
- The About page doesn't show any information about the current snapshot build anymore, as only released versions are supported (#383)

### Fixed

- Fixed changelog rendering issue where links aren't rendered on GitHub (#388)
- Fixed creation of the dependency list in CFF file (#392)

## [0.9.0] - 2022-05-24

### Added

- Add keyboard shortcut to create spans in Grid Editor (#243)
- Add "update functionality" in the "Help Menu"
- Add "Preferences" dialog to allow auto-updates at startup in the "Help Menu"
- Add overview tables for grid editor keyboard shortcuts to user documentation (#361)
- Add support for adding new annotation columns via context menu and keyboard shortcuts to Grid Editor (#358)
- Add command to change the covered text of a token in the Graph Editor (#373)

### Changed

- Use consistent and accessible colour palette across all editors (#173)
- Allow to select multiple annotations in graph editor as filter (#4)
- Updated Eclipse platform to release 2021-12
- Updated Tycho to 2.7.0

### Fixed

- Optimize column lifecycle in the Grid Editor, so that emptied columns don't disappear (#258, #346)
- Let annotation rename dialog display the actual annotation name, not the display name of the column (#259)
- Graph Editor did not update view when an annotation value was changed (#362)
- Checkstyle Eclipse plugin update site was wrong (#323)


## [0.8.0] - 2021-11-16

### Added

- Add import of plain text files with optional tokenization (#110)
- Add the GraphAnno format to the supported import formats (#112)
- Add tutorial for creating a corpus from scratch

### Fixed

- Fixed some minor typos in the GUI

## [0.7.1] - 2021-10-26

### Fixed

- Remove and ignore .settings folders from repository to enable smoother import into Eclipse (#332)
- Fix partially failing integration test by determining file menu programmatically rather than relying on defaults

## [0.7.0] - 2021-09-28

### Added

- Add a command to create spans to the graph editor (#337)

## [0.6.1] - 2021-08-11

### Fixed

- Fix the update of the segmentation list when a token is deleted from the Graph Editor (#326)

## [0.6.0] - 2021-05-26

### Added

- Add section about building Hexatomic locally in developer/maintainer documentation (#194)
- Add section about continuous integration in developer/maintainer documentation (#84)
- Add information on how to make releases on GitHub to developer documentation (#211)
- Add section about documentation in developer/maintainer documentation (#58)
- Document basic architecture and modularization (#41)
- Java is now included in the released product, it is not necessary to have Java installed (#20)
- Document common development issues (#278)
- Document working with contributions via pull request (#85)
- Document use of issue & PR templates (#86)
- Document preparation for end-of-maintenance (#276)
- Document tasks for new maintainers when taking over the project (#275)
- Document when to make releases (#61)
- Document maintenance tasks (#277)
- Document communication with users via mailing list (#318)

### Changed

- Use GitHub Actions instead of Travis CI for deploying the release binaries and the documentation.
- Updated Eclipse platform to release 2021-03
- Updated Tycho to 2.1.0
- Java 11 is required to build Hexatomic

### Fixed

- Fix erroneous launch configuration that broke launching from Eclipse
- Fix incomplete contribution guidelines (#292)
- Fix startup error message in macOS by removing broken code signature on release (#271)

## [0.5.1] - 2021-03-11

### Fixed

- Allow all Unicode letter characters as identifier in the graph console 
  and the "ideographic full stop" as punctuation. (#261)

## [0.5.0] - 2021-03-04

### Added

- Implement undoing and redoing changes (#38)
- Implement changing annotation names for whole annotation columns (#97)
- Implement import and export using Pepper (#111) for PAULA XML (#114) and EXMARaLDA files (#115)
- Implement changing annotation names for selected cells from the same or different columns (#228)
- Implement creating and annotating new spans over empty cells in existing span columns (#98)
- Add link to mailing list to user documentation
- Add "About" dialog and link to online documentation in the "Help" menu

### Fixed

- Fixed incorrect description of unreviewed hotfixes workflow in developer/maintainer documentation
- When adding tokens in the graph editor to a previously empty document, you don't need to zoom in anymore (#224)
- UI Integration tests can be run in Eclipse without depending on the JUnit runtime that's packaged with the IDE

### Changed

- Using GitHub Actions instead of Travis CI for testing the pull requests
- Update Tycho (build-system related) to version 1.7.0
- Ratio between corpus structure editor and editor window is now 30/70 at start

## [0.4.4] - 2020-09-16

### Fixed

- Fix exception when adding tokens in the graph editor after saving the opened document (#220)
- Fix exception when adding tokens across >1 instances of the graph editor after saving the opened document (#214)

## [0.4.3] - 2020-09-04

### Fixed

- Several potential bugs (e.g. null pointer exceptions) reported by static code analysis

## [0.4.2] - 2020-08-11

### Fixed

- Release artifacts where not created because Travis configuration contained bugs (#209)

## [0.4.1] - 2020-08-11

### Fixed

- Release artifacts where not created because tagged release commits where not included in Travis configuration

## [0.4.0] - 2020-08-11

### Added

- Editor for the Salt annotation graph based on Zest and using a command line interface similar to GraphAnno
- Grid Editor for token and span annotations based on NatTable and using a similar interface to Excel
- Salt Projects can be saved now
- Annotation graph updates are propagated using the Eclipse RCP event bus
- Added a process to merge non-reviewed code and triage these unreviewed PRs regulary

### Changed

- Don't unload the Salt document graph when text viewer is closed, notify the project manager instead.
  This will only unload the document graph when no other editor has opened the document.
- Updated the Eclipse platform to the 2020-03 release.
- Updated PR template so that every checkbox is an actual task and should be checked to pass quality control.

### Removed

- The Salt graphs are not using the insufficient Salt notification extension anymore
- `ProjectManager` is not managing the Salt update events anymore, its `addListener` and `removeListener` functions have been removed.

### Fixed

- Update views via notification even if the object that changed was created before the view.
- Allow to run Hexatomic on Java 11 platforms
- Set locale in UI tests to avoid issues with auto-detected keyboard layouts


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


[Unreleased]: https://github.com/hexatomic/hexatomic/compare/v1.1.3...HEAD
[1.1.3]: https://github.com/hexatomic/hexatomic/compare/v1.1.2...v1.1.3
[1.1.2]: https://github.com/hexatomic/hexatomic/compare/v1.1.1...v1.1.2
[1.1.1]: https://github.com/hexatomic/hexatomic/compare/v1.1.0...v1.1.1
[1.1.0]: https://github.com/hexatomic/hexatomic/compare/v1.0.1...v1.1.0
[1.0.1]: https://github.com/hexatomic/hexatomic/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/hexatomic/hexatomic/compare/v0.14.0...v1.0.0
[0.14.0]: https://github.com/hexatomic/hexatomic/compare/v0.13.0...v0.14.0
[0.13.0]: https://github.com/hexatomic/hexatomic/compare/v0.12.0...v0.13.0
[0.12.0]: https://github.com/hexatomic/hexatomic/compare/v0.10.0...v0.12.0
[0.10.0]: https://github.com/hexatomic/hexatomic/compare/v0.9.0...v0.10.0
[0.9.0]: https://github.com/hexatomic/hexatomic/compare/v0.8.0...v0.9.0
[0.8.0]: https://github.com/hexatomic/hexatomic/compare/v0.7.1...v0.8.0
[0.7.1]: https://github.com/hexatomic/hexatomic/compare/v0.7.0...v0.7.1
[0.7.0]: https://github.com/hexatomic/hexatomic/compare/v0.6.1...v0.7.0
[0.6.1]: https://github.com/hexatomic/hexatomic/compare/v0.6.0...v0.6.1
[0.6.0]: https://github.com/hexatomic/hexatomic/compare/v0.5.1...v0.6.0
[0.5.1]: https://github.com/hexatomic/hexatomic/compare/v0.5.0...v0.5.1
[0.5.0]: https://github.com/hexatomic/hexatomic/compare/v0.4.4...v0.5.0
[0.4.4]: https://github.com/hexatomic/hexatomic/compare/v0.4.3...v0.4.4
[0.4.3]: https://github.com/hexatomic/hexatomic/compare/v0.4.2...v0.4.3
[0.4.2]: https://github.com/hexatomic/hexatomic/compare/v0.4.1...v0.4.2
[0.4.1]: https://github.com/hexatomic/hexatomic/compare/v0.4.0...v0.4.1
[0.4.0]: https://github.com/hexatomic/hexatomic/compare/v0.3.1...v0.4.0
[0.3.1]: https://github.com/hexatomic/hexatomic/compare/v0.3.0...v0.3.1
[0.3.0]: https://github.com/hexatomic/hexatomic/compare/v0.2.0...v0.3.0
[0.2.0]: https://github.com/hexatomic/hexatomic/compare/v0.1.3...v0.2.0
[0.1.3]: https://github.com/hexatomic/hexatomic/compare/v0.1.2...v0.1.3
[0.1.2]: https://github.com/hexatomic/hexatomic/compare/v0.1.1...v0.1.2
[0.1.1]: https://github.com/hexatomic/hexatomic/compare/v0.1.0...v0.1.1
