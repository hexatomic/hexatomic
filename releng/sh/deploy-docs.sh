#!/bin/bash

# Used by CI to deploy the freshly built and existing docs/<version>/<user/dev> directory to Github Pages,
# And add the respective index pages.

if [ -n "$GITHUB_TOKEN" ]; then
    cd "$GITHUB_WORKSPACE"

    echo "Clone gh-pages"
    # Clones the gh-pages branch into a directory `gh-pages` in the build directory
    git clone -q  -b gh-pages https://hexatomic:$GITHUB_TOKEN@github.com/hexatomic/hexatomic gh-pages &>/dev/null
    cd gh-pages
    # Copies the freshly built documentation directories to the root of the gh-pages repository, i.e.,
    # to gh-pages/dev/<short-version/ and gh-pages/dev/<short-version>, thereby overwriting existing
    # documentation for this minor version.
    cp -R ${GITHUB_WORKSPACE}/docs/user/book/* .
    cp -R ${GITHUB_WORKSPACE}/docs/dev/book/* .
    # Declare a string array for the directory prefixes "user" and "dev" to use in the following loop
    declare -a arr=("dev" "user")
    # Loop through both prefixes
    for DOCS_TYPE in "${arr[@]}" 
    do
        # Grab a formatted version of the current timestamp
        DATE=$(date +"%Y-%m-%d %H:%M")
        # If an index entry in the HTML table for the current <short-version> exists (either highlighted with CSS class "table-success" as 
        # the latest version, or not), just change the "last updated" date in the respective table cell to the value of the $DATE variable.
        if grep -P "<tr><td( class=\"table-success\")?><a href=\"\.\/$SHORT_VERSION\/index\.html\">$SHORT_VERSION<\/a> \(last updated: \d{4}-\d{2}-\d{2} \d{2}:\d{2}\)<\/td><\/tr>" "$DOCS_TYPE"/index.html; then 
            echo "Version already exists, changing update date."; 
            sed -i -e "s/$SHORT_VERSION<\/a> (last updated: [0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9] [0-9][0-9]:[0-9][0-9]/$SHORT_VERSION<\/a> \(last updated: $DATE/g" "$DOCS_TYPE"/index.html
        # Else, if *no* index entry in the HTML table for the current <short-version> exists, remove the existing
        # CSS class highlighting the latest version, and replace the replacement HTML comment with a new table row on top of the table,
        # after reduplicating the replacement HTML comment for future replacements.
        # The CSS highlighting will stay on the latest minor release, even if a hotfix for an earlier minor release is pushed, as hotfixes do
        # not change the <short-version>.
        else
            sed -i -e "s/ class=\"table-success\"//g" "$DOCS_TYPE"/index.html
            sed -i -e "s/<\!--REPLACEME-->/<\!--REPLACEME-->\\n            <tr><td class=\"table-success\"><a href=\"\.\/$SHORT_VERSION\/index.html\">$SHORT_VERSION<\/a> \(last updated: $DATE\)<\/td><\/tr>/g" "$DOCS_TYPE"/index.html
        fi
    done
    # Add all changes to the git index and let a virtual GH user push it to gh-pages.
    git add .
    git -c user.name='GitHub Action CI' -c user.email='gh-actions@corpus-tools.org' commit -m "Update documentation"
    echo "Push to gh-pages"
    git push -q https://hexatomic:$GITHUB_TOKEN@github.com/hexatomic/hexatomic gh-pages &>/dev/null
    cd "$GITHUB_WORKSPACE"
fi