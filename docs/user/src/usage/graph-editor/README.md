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

The command `n` will create a new node and optionally add annotations and dominance relations to existing nodes.
Each of its new annotation arguments has the form `name:value` or `namespace:name:value`.
Arguments starting with `#` refer to the node names to which dominance edges are added (e.g. `#someNodeName`).
Optionally, a layer can be assigned to the node by adding a layer name as an argument.

The name of the newly created node is returned if creating the node was successful.

#### Examples

```
n pos:NN lemma:house #t1 #t2
```

Adds a new node dominating the two given tokens.
The new node carries two annotations: one is named "NN" and has the value "NN", the other one has the name"lemma" and has the value "house".

```
n tiger:pos:NN #otherNode1
```

Adds a new node, spanning over the node with name "otherNode1", and with an annotation named "pos" and the annotation value "NN".
The namespace of the annotation is "tiger".

### Annotate: `a`

Adds or updates annotations to existing nodes.
Give the referenced nodes (with the `#nodeName` syntax), and the attributes are arguments.
You can delete existing annotations by leaving the value in the attribute empty.

#### Examples

```
a pos:NN #t1 #t2
```
Sets the annotation "pos" to the value "NN" for both nodes "t1" and "t2".

```
a pos: #t1
```
Deletes the "pos" annotation for the "t1" node.


### Delete node: `d`

Deletes any node or edge of the graph.
Give the entities to delete as argument.

### Tokenize: `t`

Tokenize the given argument string and add the tokens to the annotation graph.
String values can be enclosed in quotes, e.g., for punctuation and for tokens that include whitespace.

#### Example

```
t This is an example "."
```