# Architecture

Considerations regarding the architecture of Hexatomic are driven by
the requirement that Hexatomic is **extensible**, and can be used
for different kinds of corpus linguistic research.

The most essential architectural feature of Hexatomic is therefore a high degree of **modularization**. 
This means that whatever feature should be added to Hexatomic, it should be added as a new module,
while at the same time the existing code base needs as few changes as possible.

For different stakeholders in Hexatomic, the architecture should cater for their respective needs:

- *Linguistic researchers* expect a seamless experience in using the software,
and should be able to achieve their research goals without having to use other tools
during their research workflow.
- *Maintainers* expect a clean overview of the architecture, and to
be able to locate different parts easily.
- *Developers* expect to be able to add new features with minimum effort,
in cleanly encapsulated modules, against well-defined and documented interfaces.