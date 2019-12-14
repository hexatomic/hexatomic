<!-- Please refer to our contributing documentation for any questions on submitting a pull request, or let us know here if you need any help. -->

## Pull request checklist

Please check if your PR fulfills the following requirements:
- [ ] Tests for the changes have been added (for bug fixes / features)
- [ ] Docs have been reviewed and added / updated if needed (for bug fixes / features)
- [ ] The *Unreleased* section in CHANGELOG.md has been amended to reflect the changes in this PR
- [ ] Build (`mvn verify`) was run locally and any changes were pushed
- [ ] The pull request is against the correct branch (`master` for bug fixes, `develop` for new functionality)


## Pull request type

<!-- Please try to limit your pull request to one type, submit multiple pull requests if needed. --> 

Please check the type of change your PR introduces:
- [ ] Bugfix
- [ ] Feature
- [ ] Code style update (formatting, renaming)
- [ ] Refactoring (no functional changes, no API changes)
- [ ] Build related changes
- [ ] Documentation content changes
- [ ] Other (please describe): 


## What is the current behavior?
<!-- Please describe the current behavior that you are modifying, or link to a relevant issue. -->

Issue Number: N/A


## What is the new behavior?
<!-- Please describe the behavior or changes that are being added by this PR. -->

-
-
-

## Does this introduce a breaking change?

- [ ] Yes
- [ ] No

<!-- If this introduces a breaking change, please describe the impact and migration path for existing applications below. -->

## Does this introduce new dependencies?

- [ ] Yes
- [ ] No

If this introduces new dependencies:

- [ ] [Dependencies and citation templates](https://github.com/hexatomic/hexatomic/tree/develop/releng/templates) have been updated where necessary

## Other information

<!-- Any other information that is important to this PR such as screenshots of how the component looks before and after the change. -->

---

# Checklist for maintainers

## Releases

- [ ] Do **NOT** push the green merge button on GitHub.  
Follow the latest version of the [developer/maintainer docs](https://hexatomic.github.io/hexatomic/dev/) for making releases.
- [ ] License and citation information for dependencies are complete (each dependency has a folder in `/THIRD-PARTY/`).