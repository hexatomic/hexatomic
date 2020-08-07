# Periodic unreviewed code triage

Hotfixes can be merged with no code review if there is only one maintainer available.
To still be able to perform some kind of review, periodic triages of yet unreviewed code are performed by one of the maintainers.

These triages must be done *once at the beginning of each quarter of the year*.
The time of the last triage and any findings are logged in the file `TRIAGE.md` in the main repository.

## Determining what changed since the last triage

Open the list of all pull requests in GitHub and filter by the "unreviewed" label and sort them to show the oldest ones first.
If there are no pull requests which such a label, the triage is finished and you can proceed to the "Documenting the results" section.

Next, open `TRIAGE.md` file and check which commit of the master branch was last triaged.
Create a new branch from this commit.
**This branch should never be merged into another branch.**

For each of the pull requests with the label, merge the tip commit of the pull request into your branch.
It can happen that there is a conflict because for example reviewed PRs with larger features have been added in between the non-reviewed hotfix PRs.
If there is any conflict, abort the merge and review the current changes until this point and start a new triage with the commit before the merge commit of the problematic PR as a starting point.
For example in the following history of pull requests, there would be two triages: one for PR 1 and 2 starting from the last triaged master commit, and one triage from the PR X commit (which was reviewed) that includes PR 4 and 5.

```plain
last triaged commit
        |
        v
        + <-- PR 1 (not reviewed)
        |
        v
        + <-- PR 2 (not reviewed)
        |
        v
        + <-- PR X (reviewed)
        |
        v
    conflict! <-- PR 4 (not reviewed)
        |
        v     <-- PR 5 (not reviewed)

```

When you have merged all the pull requests for included in this triage, compare the changes of the branch with the previously triaged commit and review the code changes.
For each problem you find, create a new issue in the GitHub issue tracker and add document it in the `TRIAGE.md` file.

## Documenting the results

Each triage should have its own section in the `TRIAGE.md` file.

The heading is the date and if a conflict occurred during merging the PRs, which part of the PR collection (e.g. "Part 1", "Part 2", etc.).
After the heading, the person who did the triage, the revision hash of the start/end commit and a list of PRs that where included in the triage.
Then add a list entry for each issue you found.

### Examples for a triage result

First part of a triage which was split due to conflicts.

```markdown
## 2020-08-07 (Part 1)

Triage done by [thomaskrause](github.com/thomaskrause). 
Revison range: 758884326b3e3a6b29f54418158e4cc0204be525..874720de8fd6d5b415edad9a66412719abf49279
PRs: #199, #127, #138

### Issues

- Resolve settings for running from within Eclipse in docs #197
- TestGraphEditor.testShowSaltExample times out #202

```

Triage where no unreviewed pull requests have been found. You can omit the start revision hash in this case.
```markdown
## 2020-04-01 

Triage done by [thomaskrause](github.com/thomaskrause).
PRs: none
Revision range: ..fbc424bc0918e16fc78d07307c4cca47bc1620f1
```