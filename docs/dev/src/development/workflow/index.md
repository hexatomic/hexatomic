# Development workflow

Hexatomic is developed on [GitHub](https://github.com) and follows a common development model.

## Versioning

Hexatomic adheres to [**Semantic Versioning 2.0.0**](https://semver.org).

Versions take the form of `MAJOR.MINOR.PATCH`.

- `MAJOR` versions are *incompatible* with each other. `MAJOR` is incremented, when an incompatible API change is introduced.
- `MINOR` versions add "features", i.e., backwards-compatible functionality.
- `PATCH` versions add backwards-compatible bug fixes.

`TODO Include internal versioning information, e.g., for different plugins/features.`

## GitFlow, Pull Requests, Releases

`TODO FIXME BELOW`

### Working with pull requests (PRs)

- To include **pull requests** in the repository, do `Rebase and merge` rather than the other merging options on GitHub.

### Internal workflow

Project-internal contributors fork the main repository (hexatomic/hexatomic), do their work in JGitFlow
branches (feature/hotfix), then write a pull request against develop.

Maintainers work with JGitFlow to work on release branches.

`TODO FIXME ABOVE`

## Continuous integration

## Pull Requests

## Naming conventions

`TODO FIXME BELOW`

- Plugin projects are called `org.corpus_tools.hexatomic.<plugin-name>` and are stored in `/bundles/org.corpus_tools.hexatomic.<plugin-name>`.
- Feature projects are called `org.corpus_tools.hexatomic.<feature-name>.feature` with an identifier of `org.corpus_tools.hexatomic.<feature-name>`, and 
are stored in `features/org.corpus_tools.hexatomic.<feature-name>`.

`TODO FIXME ABOVE`

## When to release a hotfix, and when to release a feature

`TODO FIXME BELOW`

- All bug fixes *on a released version* are hotfixes, as the bugs appear in master
- develop is always right
- develop collects features
- JGitFlow: ALWAYS start hotfixes from `master`!!!

`TODO FIXME ABOVE`

Within the Hexatomic project, *hotfixes* are changes that are made to a deployed major or minor release. 
These obviously include functional changes (i.e., bug fixes), but also non-functional changes to Hexatomic, such as updating the documentation to reflect the latest released version, updating the release workflow, etc.

*Features* are changes that introduce new functionality to the software, while the existing functionality keeps working.

The following table gives some examples.

|              Feature               |                                Hotfix                               |
|------------------------------------|---------------------------------------------------------------------|
| Add a new editor                   | Fix a bug in an existing editor                                     |
| Add new functionality to an editor | Fix unwanted behaviour in the release workflow                      |
| Add other functionality            | Update the documentation to reflect the last release                |
|                                    | Update documentation to reflect changes in the development workflow |

It is important to note that **all bug fixes that are made *on a released version* are hotfixes**, 
and that **the `develop` branch always contains the truth**, i.e., always holds the stage of development against which all features must be developed.
Implementation of new functionality must **always** start with a branch from `develop` via `mvn gitflow:feature-start`!
Do not base your work on any other `feature/...` or other branches.
Finished features are collected in `develop` before release.