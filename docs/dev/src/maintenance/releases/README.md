# Releases

## Feature releases

To release a new *feature* (i.e., minor) version of Hexatomic, run the following commands in the repository root directory.

1. `git checkout develop` - Checks out the *develop* branch (feature releases are always based on `develop`).
2. `git pull origin develop` - Updates the local *develop* branch.
6. `mvn clean verify` - Builds the project, runs tests, and may update the file headers in Java files.
6. `git add .` - Adds the updated files - if any - to the Git index.
7. `git commit -m "Update file headers"` - Commits the updates to version control.
8. `git push` - Updates the remote *develop* branch.
3. `mvn gitflow:release-start` - Starts the release process.
4. The Maven GitFlow plugin will then prompt you to enter a version number for the release. Make sure you enter a version identifier according to semantic versioning, in the format `MAJOR.MINOR.PATCH`. Do *not* use a `-SNAPSHOT` suffix!
5. Check if the `[Unreleased]` changelog section in `CHANGELOG.md` is complete and up-to-date. Make changes, commit and push if necessary.
5. `mvn keepachangelog:release -N` - Updates the changelog in the release branch.
6. `git add .` - Adds the updated changelog to the Git index.
7. `git commit -m "Update changelog"` - Commits the updated changelog to version control.
8. `git push` - Updates the remote release branch.
9. `mvn gitflow:release-finish` - Finalizes the release process.

## Hotfix releases

Hotfixes come into the project via pull requests of a *hotfix branch* against `master`.
**Important**: Do **not** merge pull requests from hotfix branches into master!

Instead, checkout the hotfix branch locally, and start the merge and release process with the Maven GitFlow plugin:

1. `git checkout hotfix/{hotfix version}` - Checks out the respective hotfix branch.
2. `git pull origin hotfix/{hotfix version}` - Checks for any remote changes to the branch.
6. `mvn clean verify` - Builds the project, runs tests, and may update the file headers in Java files.
6. `git add .` - Adds the updated files - if any - to the Git index.
7. `git commit -m "Update file headers"` - Commits the updates to version control.
8. `git push` - Updates the remote *develop* branch.
5. Check if the `[Unreleased]` changelog section in `CHANGELOG.md` is complete and up-to-date. Make changes, commit and push if necessary.
5. `mvn keepachangelog:release -N` - Updates the changelog in the release branch.
6. `git add .` - Adds the updated changelog to the Git index.
7. `git commit -m "Update changelog"` - Commits the updated changelog to version control.
8. `git push` - Updates the remote release branch.
9. `mvn gitflow:hotfix-finish` - Finalizes the hotfix and finishes the merge and release procedure.

## What to do when releases go wrong?

When a feature release doesn't work as expected, simply

1. Checkout the `develop` branch (`git checkout develop`).
2. Delete the release branch for the version you wanted to release (`git branch -d release/{version}`).
3. Delete the same branch on GitHub (via the web interface).
4. Restart the release process from the top.

When a hotfix release doesn't work as expected, simply

1. Make the necessary changes in the hotfix branch, then continue with the merge and release process.