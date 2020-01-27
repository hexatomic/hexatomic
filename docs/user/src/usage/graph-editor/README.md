# Graph Editor

The graph editor allows visualizing and annotation graphs.
It is a general visualization that aims to display all possible types of annotation as a graph.

![Screenshot of the graph editor](graph-viewer.png)

On the right side of the interface, you can select which span to show.
Select more than one span by holding the <kbd>Ctrl</kbd> key while clicking on the segment.

## Console

The graph editor contains a console, which you can use to manipulate the annotation graph.
You first enter a command by entering it as text behind the so-called prompt `> ` and pressing <kbd>Enter</kbd>.

![Screenshot of the console prompt, showing the text > name arg1 arg2](prompt.png)

Commands typically start with its name and a list of arguments. The arguments are specific to each command but can share similar syntax.
Hexatomics command line syntax is similar to the one of [GraphAnno](https://github.com/LBierkandt/graph-anno/blob/master/doc/GraphAnno-Documentation_en.pdf).

Currently, the following commands are supported.

### New node: `n`

The command `n` will create a new node.
It is followed by the attributes the new node should have.
Each attribute has the form `name:value` or `namespace:name:value`.
A node should cover existing tokens or other nodes.
The covered nodes are given as argument, with nodes referenced as `n nodeName` and tokens as `t` followed by the index of the token.
Optionally, a layer can be assigned to the node by adding a layer name as an argument.

The ID of the newly created node is returned if creating the node was successful.

#### Examples

```
n pos:NN lemma:house t 1 t 2
```

Adds a new node with two annotations spanning over the first two tokens of the text.
One annotation is named "NN" and has the value "NN", the other one has the name"lemma" and has the value "house".

```
n tiger:pos:NN n otherNode1
```

Adds a new node, spanning over the node with name "otherNode1", and with an annotation named "pos" and the annotation value "NN".
The namespace of the annotation is "tiger".

### Delete node: `d`

Deletes any node or edge of the graph.
Give the entities to delete as argument.

### Tokenize: `t`

Tokenize the given argument string and add the tokens to the annotation graph.

#### Examples

```
t This is an example
```