# Filter visible annotations

To access additional filter options, click and expand a relevant category in the **Filter View** section.

![Screenshot of the graph editor with expanded filter view](filter-view.png)

You can choose to display spans and their annotations in the graph, by first expanding **Annotation Types** and then checking the checkbox **Spans**. 
Spans are special nodes that "collect" a number of tokens.
Span annotations can then be made for that *specific set* of tokens, e.g., a phrase, clause or sentence.
If you want to learn more about spans, please read the [Salt documentation](http://corpus-tools.org/salt/#documentation).

Similarly, you can show or hide pointing relations between nodes in the graph by using the checkbox **Pointing Relations**.

**Node Annotations** allows you to filter the segments that include annotations of a specific *name*.
To add a filter criterion (also called a "facet"), search for an annotation name in the text field and select the matching annotation.
This creates a new filter badge with the annotation name of the applied filter.
You can add more than one filter. All nodes that contain any of the selected annotation names will be shown.
Click on the close button on a filter badge to delete the respective annotation name filter.
If you select no annotation names, all annotations will be visible.
