# Periodic unreviewed code triage

Hotfixes can be merged without code review if there is only one maintainer available.
To still be able to perform some kind of review, periodic triages of yet unreviewed code are performed by one of the maintainers.

These triages must be done *at the beginning of each quarter of the calendar year*.
The time of the last triage and any findings are logged in the file `UNREVIEWED_CODE_TRIAGE.md` in the main repository.

## Determining changes since the last triage

Open the list of all pull requests (PRs) in GitHub, filter them by the `unreviewed` label, and sort them to show the oldest ones first.
If there are no pull requests with such a label, the triage is finished and you can proceed to the "Documenting the results" section.

Next, open the file `UNREVIEWED_CODE_TRIAGE.md` and check which commit of the `main` branch was last triaged.
Create a new branch from this commit.

> **This branch must never be merged into another branch.**

For each of the pull requests with the `unreviewed` label, merge the tip commit of the pull request into your branch.
At this stage, conflicts may appear, which may, for example, be due to the addition of reviewed PRs with larger features in between non-reviewed hotfix PRs.
If there is any conflict, abort the merge, review the current changes up to this point, and start a new triage with the last commit before the merge commit of the problematic PR as a starting point.
For example, in the following history of pull requests, there would be two triages: one triage for PRs 1 and 2, starting from the last triaged `main` commit, and one triage from the PR X commit (which was reviewed) that includes PRs 4 and 5.

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

When you have merged all the pull requests included in this triage, compare the changes of the branch with the previously triaged commit and review the code changes.
To compare the changes, you can use the git command line (`git diff <last-triaged-commit> HEAD`), a graphical git diff tool, or the GitHub "compare branches" feature.
For each problem you find, create a new issue in the GitHub issue tracker and add it to the `UNREVIEWED_CODE_TRIAGE.md` file.

At the end of the triage and after all issues have been documented, remove the `unreviewed` label from the triaged pull requests.

## Documenting the results

Each triage should have its own section in the `UNREVIEWED_CODE_TRIAGE.md` file.
New triage results are added to the top of the file.

Use the date on which the triage has been completed as the header of the triage section.
If a conflict occurred during the merge of the PRs, and you have had to split the set of PRs that you need to triage as explained above, also add "Set" and the consecutive number of the set in the order of processing, e.g., `# 2020-08-27 (Set 1)`, `# 2020-08-27 (Set 2)`, etc.
Below the heading, record 

- a link to the GitHub user page of the person who did the triage, 
- the revision hash range **on the `main` branch** of the first and last commit you have triaged in the triaged set (or the whole triage if you didn't have to split it),
- a list of PRs that where included in the triage.
Then, add a list entry for each issue you found,  with the issue title and number URL, for example:


 ```markdown
- Resolve settings for running from within Eclipse in docs #197
- TestGraphEditor.testShowSaltExample times out #202
```

### Examples for triage results

First part of a triage which was split due to conflicts.

```markdown
## 2020-08-07 (Set 1)

Triage done by [thomaskrause](github.com/thomaskrause). 
Revison range: 758884326b3e3a6b29f54418158e4cc0204be525..874720de8fd6d5b415edad9a66412719abf49279
PRs: #199, #127, #138

### Issues

- Resolve settings for running from within Eclipse in docs #197
- TestGraphEditor.testShowSaltExample times out #202

```

Example file section for a triage where no unreviewed pull requests have been found. You can omit the start revision hash in this case.
```markdown
## 2020-04-01 

Triage done by [thomaskrause](github.com/thomaskrause).
PRs: none
Revision range: ..fbc424bc0918e16fc78d07307c4cca47bc1620f1
```
