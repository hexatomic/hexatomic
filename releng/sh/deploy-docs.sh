#!/bin/bash

# Used by CI to deploy the freshly built and existing docs/<version>/<user/dev> directory to Github Pages

if [ -n "$GITHUB_TOKEN" ]; then
    cd "$TRAVIS_BUILD_DIR"

    echo "Clone gh-pages"
    git clone -q  -b gh-pages https://sdruskat:$GITHUB_TOKEN@github.com/hexatomic/hexatomic gh-pages &>/dev/null
    cd gh-pages
    cp -R ${TRAVIS_BUILD_DIR}/docs/user/book/* .
    cp -R ${TRAVIS_BUILD_DIR}/docs/dev/book/* .
    git add .
    git -c user.name='Travis CI' -c user.email='travis@corpus-tools.org' commit -m "Update documentation"
    echo "Push to gh-pages"
    git push -q https://sdruskat:$GITHUB_TOKEN@github.com/hexatomic/hexatomic gh-pages &>/dev/null
    cd "$TRAVIS_BUILD_DIR"
fi