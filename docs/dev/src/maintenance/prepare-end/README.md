# Prepare for the end of maintenance period

Due to the prevalence of fixed-term contracts, research software projects are under constant threat to lose funding and thus be brought to a premature end-of-life.
To react to this eventuality, it is necessary to prepare Hexatomic for its end-of-maintenance.
This is part of a setup of the software project, that makes it possible to revive the project even after longer periods of dormancy or abandonment.

If you know that your maintenance term is coming to an end, please take the following precautions to prepare Hexatomic for a later revival.
For each step, an approximate <i class="fa fa-hourglass-start"></i> start time is suggested.

## 1. Freeze development

<i class="fa fa-hourglass-start"></i> *Start of the last quarter of your maintenance term*

Do not start to draft, plan, or develop new functionality features.
Instead, use available resources to prepare for end-of-maintenance.

Communicate the upcoming end-of-maintenance to current contributors during as soon as possible.
If you are lucky, one of them may want to integrate a feature so badly that they take on maintenance themself, or are able to round up more funding.

## 2. Prioritize and clear open issues and pull requests

<i class="fa fa-hourglass-start"></i> *Start of the last quarter of your maintenance term*

Prioritize open issues and pull requests by necessity to be merged before end-of-maintenance.
If you use a code platform such as GitHub or GitLab, use its project management features (boards, milestones, etc.) to track progress, and react to delays by communicating with the stakeholders in issues and pull requests.
De-prioritize as necessary.

For any new incoming issue or pull request, communicate to their authors that development is frozen due to the upcoming end of your maintenance term.
Perhaps there is a new maintainer among them, or a new funding idea to be had.

Focus on clearing those issues and PRs with the highest priority.
Make sure that you maintain the high standards for accepting contributions to Hexatomic.

You can work on clearing issues and PRs up until one month before the end of your maintenance term.

## 3. Issue an end-of-maintenance announcement to the whole community

<i class="fa fa-hourglass-start"></i> *Start of the last quarter of your maintenance term*

Publicize the end-of-maintenance date loud and clear wherever it makes sense, e.g., the project homepage and the `README.md` file.
Make sure to use any communication channels to the community you have: issues, mailing lists, conferences, etc.
Don't forget to provide a way to contact you, as someone from the community may want to volunteer as maintainer, or may have funding available.

## 4. Batten down the hatches

<i class="fa fa-hourglass-start"></i> *1 month before the end of your maintenance term*

Take a round trip across the project.
Read the documentation carefully again to make sure that it contains everything that may be needed for a revival later on.
Fix last *small* things that catch your attention.

## 5. Release and archive

<i class="fa fa-hourglass-start"></i> *1 week before the end of your maintenance term*

Make sure that everything that is release-ready has been released.
Make one last release if necessary, and publish it.
Archive the whole repository on Software Heritage;
read the [Software Heritage documentation](https://www.softwareheritage.org/save-and-reference-research-software/) to learn how.

## 6. Say goodbye

<i class="fa fa-hourglass-start"></i> *1 day before the end of your maintenance term*

Add a note at the top of the `README.md`, notifying future repository visitors that Hexatomic has reached a temporary end-of-maintenance.
Let them know that it can, however, be revived, and link to the relevant developer and maintainer documentation on Software Heritage (e.g., [`https://archive.softwareheritage.org/browse/origin/content/?origin_url=https://github.com/hexatomic/hexatomic&path=docs/dev/src/SUMMARY.md`](https://archive.softwareheritage.org/browse/origin/content/?origin_url=https://github.com/hexatomic/hexatomic&path=docs/dev/src/SUMMARY.md)). 
Take your leave, and don't forget to switch off the lights.
Thank you for maintaining Hexatomic! <i class="fa fa-heart"></i>
