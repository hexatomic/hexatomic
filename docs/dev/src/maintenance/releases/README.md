# Releases

To release a new version of Hexatomic, run the following commands in the repository root directory.

1. `git checkout develop` - Checks out the *develop* branch.
2. `git pull origin develop` - Updates the local *develop* branch.
6. `mvn process-sources` - Updates the file headers in Java files.
6. `git add .` - Adds the updated files to the Git index.
7. `git commit -m "Update file headers"` - Commits the updates to version control.
8. `git push` - Updates the remote develop branch.
3. `mvn gitflow:release-start` - Starts the release process.
4. The Maven GitFlow plugin will then prompt you to enter a version number for the release. Make sure you enter a version identifier according to semantic versioning, in the format `MAJOR.MINOR.PATCH`. Do *not* use a `-SNAPSHOT` suffix!
5. `mvn keepachangelog:release -N` - Updates the changelog in the release branch.
6. `git add .` - Adds the updated changelog to the Git index.
7. `git commit -m "Update changelog"` - Commits the updated changelog to version control.
8. `git push` - Updates the remote release branch.
9. `mvn gitflow:release-finish` - Finalizes the release process.

You can try to copy, paste and run the following snippet directly in your (Linux) terminal.

```bash
git checkout develop && \
git pull origin develop && \
mvn process-sources && \
git add . && \
git commit -m "Update file headers" && \
git push && \
mvn gitflow:release-start && \
mvn keepachangelog:release -N && \
git add . && \
git commit -m "Update changelog" && \
git push && \
mvn gitflow:release-finish
```

## What to do when releases go wrong?

When a release doesn't work as expected, simply

1. Checkout the `develop` branch (`git checkout develop`).
2. Delete the release branch for the version you wanted to release (`git branch -d release/{version}`).
3. Delete the same branch on GitHub (via the web interface).
4. Restart the release process from the top.