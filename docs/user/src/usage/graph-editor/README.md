# Graph Editor

The graph editor is for visualizing and annotating annotation graphs.
It provides a general visualization that displays all possible types of annotation in a graph of annotatable elements.

![Screenshot of the graph editor](graph-viewer.png)

On the bottom of the graph editor is the console that you can use to edit the graph.
How to do this is explained in detail in the [Editing the graph](./console.md) section.

## Select visible segment

On the right-hand side of the interface, you can select which segment of the current document to show in the [graph view](#graph-view).
For large graphs, it can take some time until its layout is calculated.
The checkbox next to the segment indicates if this calculation is finished.
You can select more than one segment to display, by holding the <kbd>Ctrl</kbd> key while clicking on additional segments.
You can also show a whole range of segments by holding the <kbd>Shift</kbd> key and clicking on the last segment of the range you want to select.

## Graph view

The left-hand side of the graph editor is taken up by the graph view, which displays the nodes and relations in the data model of the current document.

You can navigate the graph view as follows:

- **Zoom in and out** by using the **mouse wheel**.
  - You will zoom in to where your mouse cursor is.
- **Zoom in and out** by using **using the keyboard**.
  - If you press and hold the <kbd>Ctrl</kbd> key, you can zoom in with the <kbd>+</kbd> key and zoom out with the <kbd>-</kbd> key.

- **Move** the area of the graph that is displayed by **using the keyboard**:
  - The <kbd>Arrow keys</kbd> move the area in the respective direction, and <kbd>PgUp</kbd> and <kbd>PgDown</kbd> move it up and down.
  - If you press and hold the <kbd>Shift</kbd> key and then use the arrow or paging keys, you move more quickly.

- **Move** the area of the graph that is displayed by **using the mouse wheel and function keys**:
  - If you hold down <kbd>Shift</kbd> key while moving the mouse wheel, you can scroll *up and down*.
  - If you hold down <kbd>Ctrl</kbd> key while moving the mouse wheel, you can scroll *left and right*.

- You can **center the view** around a specific point in the graph by double-clicking that point.

- Double-clicking over a node will additionally **append the node name reference to the console prompt**. This allows to insert node references easily without having to type them in [console commands](console.md).

If you don't like the layout of the graph, you can change it by dragging nodes with your mouse or [adjusting the layout parameters](./layout-params.md).

