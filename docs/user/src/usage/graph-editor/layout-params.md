# Change layout parameters

Depending on the visible annotations and the specific graph, the default visualization of the graph might be cluttered.
The graph editor will try to arrange the nodes using a default layout algorithm.
You can adjust the layout parameters by expanding the "Graph Layout" section.

![Graph editor view with expanded layout parameters](./layout-params.png)

## Horizontal margin between tokens

![Horizontal margin parameter slider](./layout-param-horizontal-margin.png)


This parameter changes the horizontal space between the tokens at the bottom of the graph.
If the space is increased, this indirectly affects the space between other nodes, too.

This parameter is measured in "multiples of the average token width".
So for \"0\" there is no margin, for "1" the margin has the same width as the average token node, and for "2" the margin is twice as high as the average token node width.

![Effect of the settings 0, 1 and 2 for the horizontal margin parameter](horizontal-margin-examples.png)

## Vertical margin between nodes

![Vertical margin parameter slider](./layout-param-vertical-margin.png)

This parameter configures the vertical margin between *all* nodes.

This is measured in "multiples of the node height".
So for "0" there is no margin, for "1" the margin has the same height as the node.

![Effect of the settings 0, 1 and 2 for the vertical margin parameter](vertical-margin-examples.png)


## Vertical margin between tokens and non-tokens

![Vertical margin between token and non-token parameter slider](./layout-param-token-margin.png)


Tokens are grouped horizontally at the bottom of the graph visualization, i.e., in the bottom "row".
To allow space for pointing relations, you can add a margin between the token "row" and the annotation nodes in the "row" just above it.
This margin is in addition to the [vertical margin between nodes](#vertical-margin-between-nodes).

A margin of "1" means there is one empty level added between the token "row" and the annotation node "row" above it, an offset of "0"
means there is no additional space except for the regular vertical margin.

![Effect of the settings 0 and 3 for the vertical token margin parameter](./vertical-token-margin-examples.png)