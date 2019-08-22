# Development workflow

> **SUMMARY**
>
> - We use [Gitflow](http://web.archive.org/web/20190821195236/https://nvie.com/posts/a-successful-git-branching-model/) for developing Hexatomic,
> with the [Maven plugin for Gitflow](https://github.com/aleksandr-m/gitflow-maven-plugin).
> - To contribute **new functionality**, create a feature branch by running `mvn gitflow:feature-start` in the repository root, then create a pull request against `develop` on <https://github.com/hexatomic/hexatomic>.
> - To contribute a **bug fix, add to documentation**, create a feature branch by running `mvn gitflow:feature-start` in the repository root, then create a pull request against `develop` on <https://github.com/hexatomic/hexatomic>.


Hexatomic follows a common development workflow: Gitflow. It is a development workflow that works with a specific Git branch structure.
It is actually quite simple to understand and follow, and based on just a few rules.
Also, Hexatomic actually uses a [Maven plugin for Gitflow](#maven-plugin-for-gitflow), which makes it really easy to work with Gitflow.


If you are interested in learning more about Gitflow, you can read the [original blog post describing it](http://web.archive.org/web/20190821195236/https://nvie.com/posts/a-successful-git-branching-model/).

## The Gitflow branching model

Gitflow assumes that there is a single, central "repository of truth".
In our case, this is the one located at <https://github.com/hexatomic/hexatomic>.

### Main branches

In the repository, there are two **main branches** that *always* exist and have an *infinite lifetime*:

- **`master`**
- **`develop`**

The `master` branch always reflects the **production-ready** state.
Said differently, `master` will always contain the *releases* of Hexatomic.
Any changes that are merged into `master` are releases.

The `develop` branch always contains the latest **finished development changes**.
That means, whenever a feature is ready to be released, it is merged into `develop`
to wait for the next release (which will then contain the new feature).

### Supporting branches

In Gitflow, there are three types of branches which help organizing the development and maintenance work:

- **`feature`** branches
- **`hotfix`** branches
- **`release`** branches

![](gitflow.png)  
***Figure:*** **Git branching model overview.** Graphic by Vincent Driessen from the original blog post ["A successful Git branching model"](http://nvie.com/archives/323). Licensed under a [CC BY-SA license](https://creativecommons.org/licenses/by-sa/4.0/). ![](https://img.shields.io/badge/CC-BY%20SA-yellowgreen?logo=creative-commons)

### Maven plugin for Gitflow



---

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