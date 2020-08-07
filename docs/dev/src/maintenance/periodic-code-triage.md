# Periodic code triage

Hotfixes can be merged with no code review if there is only one maintainer available.
To still be able to perform some kind of review, periodic triages are performed by one of the maintainers.

These triages must be done *once at the beginning of each quarter of the year*.
The time of the last triage and any findings are logged in the file `TRIAGE.md` in the main repository.

## Determining what changed since the last triage

Open the list of all pull requests in GitHub and filter by the "unreviewed" label and sort them to show the oldest ones first.
If there are no pull requests which such a label, the triage is finished and you can proceed to the "Documenting the results" section.

Next, open `TRIAGE.md` file and check which commit of the master branch was last triaged.
Create a new local branch from this commit. This branch should not be pushed to the remote repository.

For each of the pull requests with the label, merge the tip commit of the pull request into your branch.
It can happen that there is a conflict because for example reviewed PRs with larger features have been added in between the non-reviewed hotfix PRs.
If there is any conflict, abort the merge and review the current changes until this point and start a new triage with the commit before the merge commit of the problematic PR as a starting point.

For each problem you find, create a new issue in the GitHub issue tracker and add document it in the `TRIAGE.md` file.

## Documenting the results

Each triage should have its own section in the `TRIAGE.md` file.

The heading is the date and if a conflict occurred during merging the PRs, which part of the PR collection (e.g. "Part 1", "Part 2", etc.).
After the heading, the person who did the triage, the revision hash of the start commit and a list of PRs that where included in the triage.
Then add a list entry for each issue you found.

### Examples for a triage result

First part of a triage which was split due to conflicts.

```markdown
## 2020-08-07 (Part 1)

Triage done by [thomaskrause](github.com/thomaskrause). 
Revison start: 758884326b3e3a6b29f54418158e4cc0204be525
PRs: #199, #127, #138

### Issues

- Resolve settings for running from within Eclipse in docs #197
- TestGraphEditor.testShowSaltExample times out #202

```

Triage where no unreviewed pull requests have been found. You can omit the revision hash in this case.
```markdown
## 2020-04-01 

Triage done by [thomaskrause](github.com/thomaskrause).
PRs: none

```