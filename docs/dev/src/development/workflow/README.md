# Development workflow

> **Summary**
>
> - We use [Gitflow](http://web.archive.org/web/20190821195236/https://nvie.com/posts/a-successful-git-branching-model/) for developing Hexatomic,
> with the [Maven plugin for Gitflow](https://github.com/aleksandr-m/gitflow-maven-plugin).
> - To contribute **new functionality**, create a feature branch by running `mvn gitflow:feature-start` in the repository root, and create a [*draft pull request*](http://web.archive.org/web/20190822091222/https://github.blog/2019-02-14-introducing-draft-pull-requests/) against **`develop`** on <https://github.com/hexatomic/hexatomic>.
> - To contribute a **critical bug fix, or urgent fixes to documentation, or release engineering**, create a hotfix branch by running `mvn gitflow:hotfix-start` in the repository root, and create a [*draft pull request*](http://web.archive.org/web/20190822091222/https://github.blog/2019-02-14-introducing-draft-pull-requests/) against **`master`** on <https://github.com/hexatomic/hexatomic>.
> - Describe your changes in the pull request title and complete the pull request form.
> - When you think that your contribution is good to be merged into the main repository, change the pull request status to **Ready for review**.
> - Collaborate with the maintainer to fulfill any missing requirements and to merge your changes into the main repository.

## Work in a fork

Unless you are part of the [core contributors team](https://github.com/hexatomic/hexatomic#team) for Hexatomic, 
you cannot add changes in the main Hexatomic GitHub repository itself.

Instead, use a "fork" of this repository to do your work. 
A fork is a copy of the original repository which you own, i.e., which you
can make *any* changes to. To learn how to create a fork, read the section 
[*Downloading the source code to your computer*](../getting-the-source-code.html#downloading-the-source-code-to-your-computer).

## The Gitflow branching model

Hexatomic follows a common development workflow: Gitflow. It is a development workflow that works with a specific Git branch structure, and is actually quite simple to understand and follow, and based on just a few rules.
Also, Hexatomic actually uses a [Maven plugin for Gitflow](#maven-plugin-for-gitflow), which makes it really easy to work with Gitflow.

If you are interested in learning more about Gitflow, read the [original blog post describing it](http://web.archive.org/web/20190821195236/https://nvie.com/posts/a-successful-git-branching-model/). The figure below gives an overview of the complete Gitflow workflow.

---

[![](gitflow.png)](http://web.archive.org/web/20190821195236/https://nvie.com/files/Git-branching-model.pdf)  
***Figure:*** **Git branching model overview.** Graphic by Vincent Driessen from the original blog post ["A successful Git branching model"](http://nvie.com/archives/323). Licensed under a [CC BY-SA license](https://creativecommons.org/licenses/by-sa/4.0/). ![](https://img.shields.io/badge/CC-BY%20SA-yellowgreen?logo=creative-commons)

---

Gitflow assumes that there is a single, central "repository of truth".
In our case, this is the main repository at <https://github.com/hexatomic/hexatomic>.

### Main branches

In the repository, there are two **main branches** that *always* exist and have an *infinite lifetime*:

- **`master`**
- **`develop`**

The `master` branch always reflects the **production-ready** state.
In other words, `master` will always contain the *releases* of Hexatomic,
and any changes that are merged into `master` are releases.

The `develop` branch always contains the latest **finished development changes**.
That means, whenever a feature is ready to be released, it is merged into `develop`
to wait for the next release (which will then contain the new feature).

### Branches you do your work in

In Gitflow, there are two types of branches where the actual work is happening:

- **`feature` branches** are used for developing new functionality, and to fix issues that change functionality.
- **`hotfix` branches** are used to fix *critical* bugs in releases (which break functionality), and to fix urgent issues in documentation and release engineering for releases. Hotfixes don't change functionality, they repair functionality.

**These distinctions are important**, because depending on what type of contribution you want to make, it means that you have to start your work by running different commands.

#### Contribute new or changed functionality (*feature*)

To contribute new functionality to Hexatomic, or change functionality (including fixes for functional issues), documentation, or release engineering processes, create a feature branch which is based on the `develop` branch.

1. Download the `develop` branch from your fork repository:  
```bash
# Download the contents of the remote `develop` branch (and others) to your local repository
git fetch
# Switch to the local branch `develop`
git checkout develop 
```
2. Install the project as-is to prepare your development work:  
```bash
mvn clean install
```
3. Create the new feature branch:  
```bash
mvn gitflow:feature-start
```
4. Give the feature branch a name as prompted by the Maven Gitflow plugin.
5. [Create a draft pull request](#create-a-pull-request-for-your-contribution-before-you-start-working) against the `develop` branch of <https://github.com/hexatomic/hexatomic> and start working.
6. Make, commit, and push your changes to this branch.
7. Once you've finished your work, run `mvn clean install` to make sure that the project builds correctly.
8. If any files were changed during `mvn clean install` - e.g., license headers have been added automatically - make sure to commit and push these changes.
9. Make sure that you have documented your changes in the changelog (`CHANGELOG.md`) in the `[Unreleased]` section, and that the updated changelog is pushed to your feature branch.
10. Once you are ready to have your changes merged into the project, request a review of your pull request from the maintainer (via the *Reviewers* settings for the pull request) and clicking `Ready to review` in the pull request page on GitHub.


#### Contribute critical bug fixes, or urgent documentation, or release engineering fixes for a released version (*hotfix*)

To contribute a new critical bug fix, documentation, or release engineering fix to Hexatomic, create a hotfix branch which is based on the `master` branch.

1. Create the new hotfix branch:  
```bash
mvn gitflow:hotfix-start
```
2. Give the hotfix branch a name as prompted by the Maven Gitflow plugin.
3. [Create a draft pull request](#create-a-pull-request-for-your-contribution-before-you-start-working) against the `master` branch of <https://github.com/hexatomic/hexatomic> and start working.
4. Make, commit, and push your changes to this branch.
5. Once you've finished your work, run `mvn clean install` to make sure that the project builds correctly.
6. If any files were changed during `mvn clean install` - e.g., license headers have been added automatically - make sure to commit and push these changes.
7. Make sure that you have documented your changes in the changelog (`CHANGELOG.md`) in the `[Unreleased]` section, and that the updated changelog is pushed to your hotfix branch.
8. Once you are ready to have your changes merged into the project, request a review of your pull request from the maintainer (via the *Reviewers* settings for the pull request) and clicking `Ready to review` in the pull request page on GitHub.

## Create a pull request for your contribution before you start working

Once you are ready to start working on your contribution, it's time to let the Hexatomic maintainer know about it, so that they can discuss the new changes and provide comments and support while you work.
The way to do this is via a [pull request on GitHub](https://help.github.com/en/articles/about-pull-requests).
Pull requests (*PR*s) aren't part of Git as such, but a feature of the GitHub platform.

This is how you start a [draft pull request](http://web.archive.org/web/20190822091222/https://github.blog/2019-02-14-introducing-draft-pull-requests/):
1. Go to <https://github.com> and log in.
2. In your fork of the Hexatomic main repository (`github.com/<your-user-name>/hexatomic`), select the branch you have been working on via the **Branches** tab or the **Branch** dropdown menu.
3. Click the button **New pull request**. It's next to the *Branch* dropdown menu.
4. **Set the correct target of the pull request**:
    - For *features*, the base repository is `hexatomic/hexatomic` and the **base branch is `develop`**, the *compare* branch is your feature branch.
    - For *hotfixes*, the base repository is `hexatomic/hexatomic` and the **base branch is `master`**, the *compare* branch is your hotfix branch.
5. Click the **Create pull request** button.
6. Give your pull request a **meaningful title** describing your changes.
7. **Complete the pull request form** in the editor window.
8. From the green dropdown menu below the editor, select **Draft pull request**.
9. Make sure that **Allow edits from maintainers** is activated.
10. Click the **Draft pull request** button to create your pull request.

Now, anytime you push a new commit to your feature branch, it will also show up in the pull request located in the Hexatomic main repository.
This way, the Hexatomic maintainer can track progress, review changes as soon as they come in, and discuss changes with you.

## What is a feature contribution, what is a hotfix?

Within the Hexatomic project, *hotfixes* are changes that are made to a deployed major or minor release.
Hotfixes *repair broken functionality*, i.e., functionality that does not work at all.
In contrast, changes to working functionality are made in *features*.
Hotfixes can also include *urgent* non-functional changes to Hexatomic, such as correcting documentation which does not reflect actual functionality, urgent changes to the release workflow, etc.

*Features* are changes that introduce new functionality to the software, or changes to functionality, while the existing functionality keeps working.
This also includes changes to functionality which are regarded as *issue fixes*. These fixes address issues with functionality that actually works, but is intended to work differently.

The following table gives some examples.

|              Feature               |                                Hotfix                               |
|------------------------------------|---------------------------------------------------------------------|
| Add a new editor                   | Fix a bug in an existing editor, which breaks functionality         |
| Add new functionality to an editor | Fix behaviour in the release workflow which breaks the build        |
| Add other functionality            | Correct documentation which wrongly describes functionality         |
| Change functionality               |                                                                     |
| Update documentation               |                                                                     |
| Update release engineering         |                                                                     |

It is important to note that **the `develop` branch always contains the truth**, i.e., always holds the stage of development against which all features must be developed.
Implementation of new functionality must **always** start with a branch from `develop` via `mvn gitflow:feature-start`!
Do not base your work on any other `feature/...` or other branches.
Finished features are collected in `develop` before release.
