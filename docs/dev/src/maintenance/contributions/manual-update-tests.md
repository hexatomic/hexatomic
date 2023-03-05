# Manual tests of updates

For updates, we rely on the Eclipse platform and our update site that is [hosted
on GitHub pages](https://github.com/hexatomic/updates) and there are several
things that can't be tested automatically, but can break for various reasons.
We have to test two scenarios: 
- Updates from the last released version works with the changes from this pull request
- It is possible to update to a newer version with the changes from this pull request.

**Do not commit any changes during the manual tests.**

## Prepare a local update site

1. Adjust the file `releng/org.corpus_tools.hexatomic.product/p2.inf` and the local update site as repository by adding the following lines. 
   Replace `<TMP>` with a temporary folder where you want to store the update site content, e.g. `/tmp/hexatomic-update-site`.
```plain
  addRepository(type:0,location:file${#58}<TMP>/repository,name:Local test,enabled:true);\
  addRepository(type:1,location:file${#58}<TMP>/repository,name:Local test,enabled:true);\
``` 
2. Change the version number of Hexatomic to a large version number
```bash
mvn tycho-versions:set-version -DnewVersion=1999.0.0 
```
3. Build the current version with `mvn package`
4. Copy the folder `releng/org.corpus_tools.hexatomic.product/target/repository` to the temporary folder `<TMP>`
5. Reset the version number to the next release

## Updating from PR state to a newer version

1. Execute `mvn package` to create the product
2. Run Hexatomic from the command line and **check that there is a message in the toolbar about an available update**.
```
releng/org.corpus_tools.hexatomic.product/target/products/org.corpus_tools.hexatomic.product/linux/gtk/x86_64/hexatomic
``` 
3. **Apply the update**. Restart if necessary.
4. Manually check for an update via the *Help* > *Update* menu, and check that the status bar again reports that "Hexatomic is up to date".

## Updating from the latest release

1. Check out the source with the release tag `git checkout tags/v<version>`. The `p2.inf` file should stay locally altered.
2. Execute `mvn package` to create the product
3. Run Hexatomic from the command line and **check that there is a message in the toolbar about an available update**.
```
releng/org.corpus_tools.hexatomic.product/target/products/org.corpus_tools.hexatomic.product/linux/gtk/x86_64/hexatomic
``` 
4. **Apply the update**. Restart if necessary.
5. Manually check for an update via the *Help* > *Update* menu, and check that the status bar again reports that "Hexatomic is up to date".
6.  Revert all local changes in the source code repository and checkout the original branch for the PR using Git.
