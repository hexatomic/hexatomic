# Working with contributions

As documented in the [*Workflow*](../../development/workflow/) section, all code and documentation contributions to Hexatomic are made via [pull requests](#maintaining-pull-requests) from branches or forks, while [other types of contributions](#maintaining-contributions-that-are-not-code-contributions) may not.

Pull requests can come from within the Hexatomic core development team that works on branches in the Hexatomic repository (*internal pull requests*), or from others that have forked the Hexatomic repository and work on branches in their repository copy (*external pull requests*).

## Maintaining pull requests

The maintainer is in charge of supervising pull requests from their creation to the point when they are merged into the target branched, or dismissed and closed.

### Communication with the creator of the pull request  

Communication with the creator of the pull request is by far the most important task in pull request maintenance.
Communication should be timely, and in line with the [Hexatomic Code of Conduct](https://github.com/hexatomic/hexatomic/blob/develop/CODE_OF_CONDUCT.md).

The primary aim of communication in pull requests is to ensure that the contribution 
is **suitable for merging** into Hexatomic, and of **sufficient quality**.

The secondary aim of communication is to provide a **welcoming, helpful, appreciative experience** for the contributor,
that helps to build a community around the software.

### Evaluating contributions

The contribution must be evaluated in terms of 

- **suitability, e.g.:**
  - Is the contribution going into the right place?
  - Does it have the right scope?
  - Is it of the right size, or should it be split into different pull requests?
  - Does it do what it is meant to do?
  - Will it improve Hexatomic?
- **quality, e.g.:**
  - Is the contribution free of security issues, bugs, code smells and style issues?
  - Is the contribution well-tested?
  - Will the contribution introduce regressions?
  - Is the contribution documented?

Evalution is done through different tools:
- [communication](#communication-with-the-creator-of-the-pull-request) to clarify suitability and help fulfill non-functional requirements
- [manual testing](#manual-testing-of-pull-requests) and code review to evaluate functional requirements <!-- TODO Add link to section on code review -->
- static code analysis to evaluate quality <!-- TODO Add link to section on static code analysis -->

#### Manual testing of pull requests

Check out the branch of the pull request, and run it locally to manually test the changes.
For external pull requests, only do so *after* checking for potential security issues.

### Moving pull requests forward

Pull requests should have a maximally short life-span.
The maintainer should help moving pull requests forward by completing their tasks in a timely fashion.
If a pull request stalls, they should inquire what they can do to help, to move it forward towards merging.
If a pull request is orphaned, the maintainer can decide to adopt and complete it themself, or find other contributors to complete it.
Alternatively, it should be closed with a comment that explains the reasons for closing.

### Merging pull requests

The way that pull requests are merged into their target branch depends on their type.

1. Pull requests that provide **hotfixes**, i.e., repair functionality that is broken rather than provide 
new or change existing functionality, **must not be merged** from the GitHub UI directly. This is
because they target the `master` branch, which only contains released code. Therefore, code that
goes into the `master` branch must be formally released, which cannot be done from the GitHub UI.
Instead, refer to the section about [releasing hotfixes](../releases/README.md#hotfix-releases) to learn how to do this.
2. Pull requests that target the `develop` branch must not necessarily be released on merge. Instead,
code changes from different pull requests can be bundled into a release. Therefore, it is safe to
merge the pull request from the GitHub UI by clicking the green **Merge pull request** button (see figure below).

#### Requirements for merging

You must only merge pull requests

1. that have undergone code review, have been approved by the reviewer, and have no unresolved questions or change suggestions;
2. that have passed static code analysis;
3. that have passed all CI workflows;
4. for which all tasks on the pull request checklist have been completed;
5. for which copyright, license and citation information are complete.

![When all requirements are fulfilled, pull requests to `develop` can be merged by clicking the green button "Merge pull request".](merge-pull-request.png)

## Maintaining contributions that are not code contributions

People can contribute to Hexatomic without creating a pull request.
Such contributions can be feature requests, bug reports, code reviews, user testing, organizing events, writing or talking about Hexatomic, asking questions, etc.
These contributions should be treated on par with [pull requests](#maintaining-pull-requests) and maintained just as timely and conscientiously.
Also, non-code contributions should be attributed and credited. <!-- TODO Add link to crediting section -->
