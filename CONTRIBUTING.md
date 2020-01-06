# Contributing to Hexatomic

We welcome contributions from the community, and want to make contributing to this project as easy and transparent as possible, whether you contribute by

- reporting a bug,
- submitting code,
- proposing a new feature, or
- put yourself forward to become the maintainer,
- ...

Please read these guidelines before contributing.
If you have questions about them or want to suggest improvements, please [open a new issue](https://github.com/hexatomic/hexatomic/issues/new).

**TODO** Cf. https://gist.github.com/briandk/3d2e8b3ec8daf5a27a62 
https://help.github.com/en/articles/setting-guidelines-for-repository-contributors

## Development on GitHub

We use GitHub to host source code and documentation, to track issues such as bug reports and feature requests, and to provide Hexatomic downloads to users.

## Report a bug or other issues

We track bugs in and issues with Hexatomic, and record proposals of new features, in GitHub issues.
You can report a bug, request a feature, or simply ask a question, by [opening a new issue](https://github.com/hexatomic/hexatomic/issues/new).
It's easy.

## Contribute code or documentation through pull requests

[Pull requests](https://help.github.com/en/articles/github-glossary#pull-request) are the best way to propose changes to the codebase or the documentation sources. If you want to contribute code or documentation, please make sure that you read the relevant sections of the [developer documentation](https://hexatomic.github.io/hexatomic/v0.1.0/dev/) first. We actively welcome your pull requests:

1. Fork this repository.
1. Open a new issue describing what you are planning to do. The maintainer will decide and communicate whether your change is a *hotfix* or a *feature*.
1. Create a `feature` branch (for new functionality) with `mvn gitflow:feature-start`,  
or a `hotfix` branch (for bug fixes) with `mvn gitflow:hotfix-start`.
1. Do your work in this branch.
    - If you add code, add tests!
    - Update the relevant documentation in user and developer & maintainer documentation!
    - Test your changes locally!
3. Push your branch and create a pull request.  
    - If your contribution is new functionality, create it against `hexatomic:develop`.
    - If your contribution is a bug fix, create it against `hexatomic:master`.

### Code review schedules

We perform reviews of all changes to Hexatomic, be they in code, documentation, configuration, etc.
We have therefore set up regular time slots, in which maintainers perform (code) reviews.
Currently, we aim to review pull requests at the following times:
- Wednesday afternoons
- Thursday mornings
- Friday mornings
- Friday afternoons
  
All times are in Central European (Summer) Time and are meant for orientation. Note that we may sometimes not be able to perform reviews in these time slots due to illness, holidays, etc.

## License

By contributing code to Hexatomic, you agree that your contributions will be licensed under its [Apache License, Version 2.0](LICENSE).  
By contributing documentation to Hexatomic, you agree that your contributions will be licensed under its [CC0 1.0 Universal (CC0 1.0)](https://creativecommons.org/publicdomain/zero/1.0/legalcode) license.

## Governance

This section describes the decision-making processes for the Hexatomic project, and the roles involved in them.

### Roles

In the Hexatomic project, the following roles are defined.

- **Core contributors:** Active contributors to the Hexatomic software. The development of Hexatomic is either part of the job description of core contributors, or they are actively making substantial contributions to the project. Core contributors are listed in the [README file](README.md).

- **Maintainer:** Hexatomic has a single maintainer. This person is responsible for the development and release workflow of Hexatomic, including project and software documentation and facilitating maintenance transfers, as well as nominating core contributors. THe maintainer is named in the [README file](README.md).

- **Principal investigators:** As Hexatomic is research software, the principal investigators are responsible for strategic development of the project. The principal investigators are listed in the [README file](README.md).

### Decision making

Decisions are usually made by the maintainer, with potential recourse to the core contributors, the principal investigators, or both. **TODO Discuss and document**
