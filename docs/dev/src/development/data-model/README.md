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

You can register for any changes on the Salt project (e.g., added annotations to a document), by adding a *Salt notification listener* to the project manager using its `addListener(Listener listener)` function.
For more information on Salt notification listener's, see the [Salt notification documentation](https://korpling.github.io/salt/doc/notification.html).
After registering your listener, you will receive *all* updates for *all* documents and *all* changes to the project structure.
You have to decide if you need to handle an update on you own, e.g. because it is related to document you are editing and where the event should trigger a redraw.
