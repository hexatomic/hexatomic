# Adding a new editor

Following the principle of "[separation of concerns](https://en.wikipedia.org/wiki/Separation_of_concerns)", editors should generally be developed in separate, dedicated bundles in the `bundles` directory of the project.
You can either create a new bundle or add an editor to an existing bundle project.
In Eclipse 4, additions to the user interface are organized in so-called [Parts](http://web.archive.org/web/20190807184652/https://www.vogella.com/tutorials/EclipseRCP/article.html#parts).
A part consists of two components:
- a `PartDescriptor` entry in the `fragment.e4xmi` file, and 
- the actual Java class implementing the behavior

## Adding a minimal Java class

Add a Java class to your bundle-project.
This class does not need to inherit any interface, but should have a method with the `@PostConstruct` annotation.
```java
public class TextViewer {
	@PostConstruct
	public void postConstruct(Composite parent, MPart part, ProjectManager projectManager) { 
		// TODO: add actual implementation
	}
}
```
This example injects the SWT `parent` composite which can be used to construct user interface elements and the `part` parameter, which describes the application model for this part.
The `projectManager` is an Hexatomic specific services that gives access to the global Salt project which is currently loaded.
SWT stands for "Standard Widget Toolkit" and is used as user interface widget toolkit in this example.
You can directly add the SWT instructions to define your user interface or the the [Eclipse Window Builder](https://www.eclipse.org/windowbuilder/) for a graphical editor.

## Add part to application model

To add the newly created class to the Eclipse RCP 4 application model, open the `fragment.e4xmi` file of the existing bundle or create a new one with `File -> New -> Other` menu and choosing "New Model Fragment".

![Adding a new fragment model file](./new-model-fragment-file.png)

In the fragment editor, add a new model fragment by selecting "Model fragments" and clicking on the "Add" button.

![Adding a new model fragment in the editor](./new-model-fragment.png)

Edit the model fragment properties by selecting the newly created entry.
Make sure to the the extended element ID to `xpath:/`and the feature name to `descriptors` (1).
This means that the model fragment extends the part descriptors of the application.
Then, add a new part descriptor with the `Add` button (2). 

![Edit model fragment properties](./add-model-fragment-descriptor.png)

Select the new part descriptor in the model fragment editor and you can edit several properties, like the caption of the part or an icon.
Make sure to set the "Class URI" field to the newly created editor class.
You can use the "Find" button to navigate the workspace and insert the proper value.
Also, the "Category" should be set to `org.corpus_tools.hexatomic.tag.editor` to mark that this part is an Hexatomic editor. 
The `Label`field will be used as a name for the editor, e.g. when the user has a selection of editors to choose from for opening a document.

![Part descriptor general properties](./part-descriptor-class.png)


## Get the associated Salt document to display

When an editor part is created, it is initialized with a state. 
This state can hold internal configurations, but also contains the ID of the Salt document this editor should edit.
It is stored in the setting with the name `org.corpus_tools.hexatomic.document-id`.
The injected `ProjectManager` can then be used to get the actual document.

```java
String documentID = part.getPersistedState()
	.get("org.corpus_tools.hexatomic.document-id");
Optional<SDocument> doc = projectManager.getDocument(documentID);
```
