# Taking over as new maintainer

This section describes what you will have to do to assume the maintainer role for the Hexatomic software project.

The [Hexatomic research project](https://hexatomic.github.io) aims to create a *sustainable* software project around Hexatomic.
This means that new maintainers should be able to come to the project, even when it has been dormant or abandoned for some time.
This section therefore assumes that there is currently *no active maintainer* for the project who will hand it over proactively in an ordered fashion.

If you want to take over as maintainer at this stage, you should go through the following steps.

## Announce that you want to take over as new maintainer

The old maintainers may still be around, and may be able to help you gain access to the repository.
Therefore it is a matter of due diligence to first announce your willingness to act as the new maintainer of the project.

The best way to do this is to [create a new issue](https://github.com/hexatomic/hexatomic/issues/new) in the original repository.
Also make sure that you [*@mention*](https://web.archive.org/web/20210505163556/https://docs.github.com/en/github/writing-on-github/basic-writing-and-formatting-syntax#mentioning-people-and-teams) the last known maintainer of the project.
This information should be available in the `README.md` file at the root of the repository.
They may be notified of the mention, and react by commenting on your issue.
This way you can discuss the best way for you to take over as new maintainer.

Alternatively, look for any other contact information for the project, such as an email address or a mailing list address, and announce your plan there.

If you get no reaction to your announcement, you can assume that the project has been abandoned, and proceed with the next step.

## Get administrative rights to the source code repository, or fork it

The source code and documentation sources for Hexatomic are provided in a repository on GitHub at <https://github.com/hexatomic/hexatomic>.
In order to gain the necessary control over the repository to maintain it, you have two options.

### 1. Get administrative rights for the original repository.

If you can find a person with administrative rights to the project (see the [previous section](#announce-that-you-want-to-take-over-as-new-maintainer)),
ask them to give you administrative rights (e.g. make you the owner) for the original GitHub repository.

This is the preferred option, as all repository configurations as well as the history of issues and pull requests remain intact.

### 2. Fork the repository

If you cannot find someone to give you administrative rights to the repository, you can fork it, i.e., make a copy of the original repository and host it on a coding platform under your own or your organization's account.
A fork will give you complete control over the repository, but you will lose the issue and pull request history.

We encourage you to rename your fork repository to something other than - but perhaps similar to - *hexatomic*, and also change any documentation and in-application branding accordingly.
This will help users separate your fork project from the original project.
Make sure that you act within the bounds of the respective [licenses](../licensing/), e.g., with regard to copyright notices.
Also note that while you may add yourself to the list of authors in the [`CITATION.cff](../licensing/) file once you have fulfilled authorship requirements,<!-- TODO Link once they're there -->
you must not remove any authors from this list, until you can establish that their contributions are not part of the work anymore.
This may be the case when you have deleted all lines of code or documentation that a single author has contributed.

## Update maintainer information

Update the maintainer information in the relevant places:

- The *Maintainer* section in `README.md` (part of the *Team* section). You may also choose to update the *Core contributors* section.
- The list of contributors in `README.md`. List yourself for maintenance.
- When you have fulfilled the authorship requirements for Hexatomic, also add yourself as an author to the citation metadata in `CITATION.cff`. This is a software citation metadata file. You can read about its format in the [Citation File Format documentation](https://citation-file-format.github.io/).

## Update public information about code reviews

The contribution guidelines in `CONTRIBUTING.md` contain information on when code reviews are likely to be performed. Change the information there as appropriate, e.g., update the time zone.

## Get ready for maintenance

- Read through the maintenance documentation.
- Check, update or newly set up the necessary services in the repository that you will need for maintenance: a [continuous integration service](../continuous-integration/), [static code analysis](../continuous-integration/#static-code-analysis), [issue and pull request templates](../repository/templates.md), etc.

## Set up your local system to prepare for maintenance

Follow the [development setup documentation](../../development/setup.md) to set up your local system and get ready for maintenance work on the codebase.

## Test the state of Hexatomic

Check if the [build](../../development/getting-the-source-code.md#build-hexatomic-locally) runs, and fix any issues that you encounter.

## Maintain open contributions

Check if there are any open pull requests for Hexatomic in the original repository.
If you are working in a fork, consider getting in touch with the authors of the open pull requests and ask them to put them up again against your fork.
[Maintain the open pull requests](../contributions/) so that they can be closed or merged.

## Maintain open issues

If you work in the original repository, check for any open issues, and maintain them until they can be closed.
If you work in a fork, go through open issues in the original repository and discuss with the authors whether to copy them to your fork.
If the issue authors are not available, decide which of the open issues should be copied to the fork.
If you create copies of issues in your fork, make sure to link to the issues in the original repository.

## Make a new release

[Make a release](../releases/) if any changes to Hexatomic were made during any of the previous steps.
This is also a good way to celebrate that you are now the maintainer of Hexatomic!

## Announce the new release to the community

Announce the new release as described in the [Releases](../releases/#promoting-releases-to-the-community) section.
If you can find a person with administrative rights to the project (see the [previous section](#announce-that-you-want-to-take-over-as-new-maintainer)),
ask them to give you administrative rights for the mailing list `hexatomic-users@lists.hu-berlin.de` as well.
If they are not available, you can always just subscribe to the mailing list and send a message as a subscriber.
Consider to move the mailing list to a list provider you have administrative access to, and if you do this, also announce it via the `hexatomic-users@lists.hu-berlin.de` list.
