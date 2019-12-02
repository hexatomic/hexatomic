# Mocking injected dependencies

In Eclipse RCP 4 applications such as Hexatomic, dependency injection is used extensively.
To restrict the scope of unit tests to the actual code that is being tested, rather than extending it to include any classes or interfaces from injected dependencies that the code under test may use, you can mock these classes or interfaces.
To mock dependencies, we use the [Mockito framework](https://site.mockito.org/).
In the following example, the `IEventBroker` is not implemented but mocked with Mockitos `mock()` method.
It can then be used normally by the class under test, in this case, the `ProjectManager` which has a field `events` of the 
type `IEventBroker`.

```java
package org.corpus_tools.hexatomic.core;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.emf.common.util.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestProjectManager {

  // this test case tests the ProjectManager
  private ProjectManager projectManager;    
  private IEventBroker events;

  @BeforeEach
  public void setUp() throws Exception {
    // create a mocked version of the interface
    events = mock(IEventBroker.class);
    projectManager = new ProjectManager();
    // use the mocked object in the tested class
    projectManager.events = events;
    projectManager.postConstruct();
  }

  @Test
  public void testEventOnOpen() {
    projectManager.open(URI.createFileURI("/tmp/exampleSaltProject"));
    // check that a certain method of the mocked object was called with the specified arguments
    verify(events).send(eq(ProjectManager.TOPIC_CORPUS_STRUCTURE_CHANGED), anyString());
  }
}

```
