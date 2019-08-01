# Hexatomic

This repository holds the source code for [Hexatomic](https://hexatomic.github.io/hexatomic), 
an extensible framework for linguistic multi-layer corpus annotation.

*Hexatomic* is being developed in the [Hexatomic research project](https://hexatomic.github.io)
at Humboldt-Universität zu Berlin and Friedrich Schiller University Jena.

## Project structure

*Hexatomic* is an Eclipse e4 Application. It is built with Maven and Tycho, and set
up following [Eclipse Tycho for building plug-ins, OSGi bundles and Eclipse applications - Tutorial](http://web.archive.org/web/20190801113418/https://www.vogella.com/tutorials/EclipseTycho/article.html) by 
Lars Vogel and Simon Scholz, Version 13.03.2019.

### Root

The root directory contains the main POM.

## releng

The `releng` folder contains the project configuration and
relesae engineering content.

## Build

Verify that the project builds by running `mvn clean verify` in the root of the repository.

## Maintain

### Working with pull requests (PRs)

To include **pull requests** in the repository, do `Rebase and merge` rather than the other merging options on GitHub.