# Continuous integration

When you work on the Hexatomic source code, or the documentation, you naturally diverge
from the state of the default branch of the repository, `develop`.

In order to ensure that your changes don't break anything, and to safeguard the
quality of your changes

- you are required to write tests for your changes, and additionally
- your changes are automatically tested and analysed whenever you push to the
  Git branch you do your work in.

These automated test and analysis runs are done with the help of a *continuous integration (CI)
service*. They are also run during the release process. Hexatomic uses [*GitHub
Actions*](https://docs.github.com/en/actions) as CI service.

## CI workflows

CI workflows define what the CI service should run, and what triggers these runs.

GitHub Actions are configured in the files in `.github/workflows`.
There are different definitions for the [test workflow](#test-workflow) and for
the [release workflow](#release-workflow).

Workflows run in a specified environment and consist of one or more *jobs*, which each consist of one or more
*steps*. Steps can run shell commands, or predefined *actions* that are
available from GitHub. For Hexatomic, the environment that is used to run workflows is a
virtual machine on GitHub's servers running a recent Linux version. 

### Test workflow

The [test workflow](https://github.com/hexatomic/hexatomic/blob/develop/.github/workflows/test.yml) is run whenever Git commits are pushed to the repository.
It consists of two separate jobs:

1. A job that builds and tests Hexatomic with the defined Java version and runs
   static code analysis
2. A job that builds and tests the documentation

#### OpenJDK and static code analysis job

Steps:

1. Install a window manager for running UI integration tests in the background
2. Check out the source code to the virtual machine that runs the workflow
3. Cache the local Maven repository to reduce workflow run times
4. Set environment variables
5. Run tests using Maven with enabled code coverage reports, and upload the
   results to SonarCloud

#### Documentation test job

Steps:

1. Check out the source code to the virtual machine that runs the workflow
2. Download and install mdBook
3. Test the user documentation with `mdbook test`
4. Test the developer and maintainer documentation with `mdbook test`

### Release workflow

The [release
workflow](https://github.com/hexatomic/hexatomic/blob/develop/.github/workflows/release.yml)
is run whenever Git tags are pushed to the repository that start with a `v`
(e.g., `v0.6.0`). Following the [development workflow for Hexatomic](../../development/workflow/), this should only be
the case during a [release](../releases/).
The release workflow consists of two separate jobs:

1. A job that installs and tests Hexatomic with the defined Java version,
   updates the citation metadata, and deploys the release binaries to GitHub
2. A job that builds the documentation and deploys the built website to the
   `github-pages` branch of the repository, from where GitHub Pages renders it

#### Deploy release binaries job

1. Install a window manager for running UI integration tests in the background
2. Check out the source code to the virtual machine that runs the workflow
3. Set environment variables
4. Install the product with Maven, which includes running all tests
5. Update the citation metadata in the `CITATION.cff` file
6. Run the build again including the updated CFF file
7. Create a draft release on GitHub and attach the release binaries

#### Deploy documentation job

1. Check out the source code to the virtual machine that runs the workflow
2. Download and install mdBook
3. Get the version identifier for the current release and save it in a variable
4. Build the user documentation into a directory qualified with the version identifier
5. Build the developer and maintainer documentation into a directory qualified with the version identifier
6. Checkout the branch `github-pages` and run a script that prepares the branch
7. Push the build artifacts to the `github-pages` branch

## Static code analysis

<!-- TODO -->
