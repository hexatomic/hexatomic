# Data model

Hexatomic works on instances of a graph-based meta model for linguistic data called **Salt**.

In Salt, linguistic data is organized in projects: 

- A **Salt project** contains at least one - and often only one - **corpus graph**.
- A corpus graph contains **corpora** as nodes. 
- The child nodes of a corpus can again be corpora (so-called *sub-corpora*) and **documents**.
- Each document has a **document graph**, which is the model element containing the actual linguistic data: 
    - primary data sources (text, audio, or video material), and
    - annotations.

Salt is very powerful in that it is theory-neutral and tagset-independent, and can model a vast variety of linguistic data and annotations.

To find out more about the Salt meta model for linguistic data and its Java API, please refer to the [Salt homepage](https://corpus-tools.org/salt), and the [Salt documentation](https://korpling.github.io/salt/doc/).

## Accessing the data model

During runtime, Hexatomic operates on a single Salt project at any one time.
The currently opened Salt project is part of the global state of the application, and can be accessed by injecting an instance of the `ProjectManager` singleton into your code.
For an overview of how dependency injection works in Eclipse-based applications such as Hexatomic, see Lars Vogel's tutorial ["Dependency injection and Eclipse"](http://web.archive.org/web/20190807184652/https://www.vogella.com/tutorials/EclipseRCP/article.html#dependency-injection-and-eclipse).

```java
import javax.inject.Inject;
import org.corpus_tools.hexatomic.core.ProjectManager;
import org.corpus_tools.salt.common.SaltProject;

public class MyClass {
	
	@Inject
	private ProjectManager projectManager;
	
	public void execute() {
		SaltProject theProject = projectManager.getProject();
	}
}
```

You can register for any changes on the Salt project (e.g., added annotations to a document), by subscribing to the `Topics.ANNOTATION_CHANGED` topic, which will be sent by the [Eclipse RCP `IEventBroker` service](http://web.archive.org/web/20200427021644/https://www.vogella.com/tutorials/Eclipse4EventSystem/article.html).
After registering your listener, you will receive *all* updates for *all* documents and *all* changes to the project structure.
You have to decide in your own code if you need to handle an update, e.g., because it is related to a document you are editing for which the event should trigger redrawing the editor you are implementing.
The argument for the `Topics.ANNOTATION_CHANGED` event is of the type `org.corpus_tools.hexatomic.core.undo.ChangeSet` and contains a list of all changes.
It also has a helper function to test if a given document is affected by its changes.
```java
@Inject
@org.eclipse.e4.core.di.annotations.Optional
private void onDataChanged(@UIEventTopic(Topics.ANNOTATION_CHANGED) Object element) {
  if (element instanceof ChangeSet) {
    ChangeSet changeSet = (ChangeSet) element;
    if (changeSet.containsDocument(
        part.getPersistedState().get(OpenSaltDocumentHandler.DOCUMENT_ID))) {
      // TODO: check graph update is relevant for this editor and update UI
    }
  }
}
```
