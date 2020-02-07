# Graph Editor

The graph editor is for visualizing and annotating annotation graphs.
It provides a general visualization that displays all possible types of annotation in a graph of annotatable elements.

![Screenshot of the graph editor](graph-viewer.png)

On the right-hand side of the interface, you can select which segment of the current document to show.
You can select more than one segment to display, by holding the <kbd>Ctrl</kbd> key while clicking on additional segments.
You can also show a whole range of segments by holding the <kbd>Shift</kbd> key and clicking on the last segment of the range you want to select.

You can also choose to display spans and their annotations in the graph, by checking the checkbox **Include spans**. 
Spans are special nodes to collect a number of tokens and to annotate them all at once.
If you want to learn more about spans, please read the [Salt documentation](http://corpus-tools.org/salt/#documentation).

Similarly, you can show or hide pointing relations between nodes in the graph by using the checkbox **Include pointing relations**.

And you can filter the segments that include annotations of a specific *name* by using the filter text field above the list of segments.