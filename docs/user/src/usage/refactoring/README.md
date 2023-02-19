# Refactoring

Hexatomic offers some so-called refactoring operations, which perform changes on
bulk. Refactoring operations can be applied to the whole project (via the menu
**Edit -> Refactor**) or on selected documents or sub-corpora, by clicking right
on selected item in the [“Corpus Structure” editor](../corpus-structure.md) and
opening the **Refactor** context menu item.

## Automatically generate node names

Node names are e.g. used in the [Graph Editor
console](../graph-editor/console.md). If these node names are not unique or too
complex to easily reference them, you can replace all node names with
automatically generated ones. Tokens will have the pattern `t1`, `t2`, `t3` and
so on. They will be ordered by the position of the token in the text. Other
nodes will have generic names like `n1`, `n2`, `n3` and so on.

**Warning:** Some corpus preparation pipelines use the node names to merge files
in different formats. If your corpus preparation pipeline requires specific node
names, you should not use this refactoring operation.