# Development workflow

Hexatomic is developed on [GitHub](https://github.com) and follows a common development model.

## Versioning

Hexatomic adheres to [**Semantic Versioning 2.0.0**](https://semver.org).

Versions take the form of `MAJOR.MINOR.PATCH`.

- `MAJOR` versions are *incompatible* with each other. `MAJOR` is incremented, when an incompatible API change is introduced.
- `MINOR` versions add "features", i.e., backwards-compatible functionality.
- `PATCH` versions add backwards-compatible bug fixes.

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

## When to hotfix, when to feature

- All bug fixes *on a released version* are hotfixes, as the bugs appear in master
- develop is always right
- develop collects features
- JGitFlow: ALWAYS start hotfixes from `master`!!!