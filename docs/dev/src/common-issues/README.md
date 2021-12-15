# Common issues

This is a collection of common issues you may encounter during Hexatomic development.
If you encounter errors during the build or in the Eclipse IDE that aren't straightforward to solve, it may be worth having a look at this section.

## Target platform

Hexatomic uses a so-called *target platform* to define product and development dependencies.
You can read about it in [*Creating and activating the target platform*](../development/activating-target-platform.md).

---

### 🚧 `.target` may be out of sync with `.tpd`

Sometimes, contributors may update the `.tpd` source file, but forget to push a newly generated `.target` file to the repository.
This may lead to Eclipse being unable to resolve types, or other errors.

**💡 Generate the `target` file from the `.tpd` file as described in [*Creating and activating the target platform*](../development/activating-target-platform.md), and [contribute](https://github.com/hexatomic/hexatomic/tree/develop/CONTRIBUTING.md) the newly generated `.target` file back to the project.**

---

### 🚧 Target Platform may not be resolved

Whenever a Target Platform definition file has been changed, you have to activate it in Eclipse, so that dependencies can be resolved.
If you don't do this, Eclipse may show error messages which seemingly relate to something else, e.g.,
`The method method() of type SomeType must override or implement a supertype method`, or errors on imports.

**💡 Set the new definition as active target platform as described in [*Creating and activating the target platform*](../development/activating-target-platform.md).**

