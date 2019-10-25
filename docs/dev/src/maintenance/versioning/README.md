# Versioning 

Hexatomic adheres to [Semantic Versioning 2.0.0](https://semver.org/).

Versions take the form of `MAJOR.MINOR.PATCH`.

- `MAJOR` versions are incompatible with each other. `MAJOR` is incremented, when an incompatible API change is introduced.
- `MINOR` versions add "features", i.e., backwards-compatible functionality.
- `PATCH` versions add backwards-compatible bug fixes.

## Versioning of product, features, and modules

Hexatomic is complex software: It consists of modules which are bundled in features.
The actual software product deliverable is again made up of several features.
<!-- TODO See sections Architecture & terminology for details. -->

Despite this, we apply a unified versioning scheme for Hexatomic.
This means that changes on any level of modularity (module, feature, product) triggers
a change in the unified version number.

### Examples:

- A bug in a module is fixed. *All* version numbers in the Hexatomic product, *all* of its features
and *all* of its bundles increment the `PATCH` identifier.
- A new module - developed by Hexatomic contributors or a third-party module - is added to a feature.
The module adds new functionality to Hexatomic. *All* version numbers in the Hexatomic product, *all* of its features
and *all* of its bundles increment the `MINOR` identifier.

## Scope

The versioning scheme described above applies to Hexatomic as present in the [hexatomic/hexatomic repository on GitHub](https://github.com/hexatomic/hexatomic).
It does not automatically apply to contributions which live in other repositories.