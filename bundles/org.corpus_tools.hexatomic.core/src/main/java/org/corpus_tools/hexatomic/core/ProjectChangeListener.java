package org.corpus_tools.hexatomic.core;

import java.util.Stack;

import javax.inject.Inject;

import org.corpus_tools.salt.extensions.notification.Listener;
import org.corpus_tools.salt.graph.GRAPH_ATTRIBUTES;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;

@Creatable
public class ProjectChangeListener implements Listener {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProjectChangeListener.class);

	private final Stack<Listener.Event> undoEvents = new Stack<Listener.Event>();


	@Inject
	private IEventBroker events;
	
	public ProjectChangeListener() {
	}
	
	@Override
	public void notify(NOTIFICATION_TYPE type, GRAPH_ATTRIBUTES attribute, Object oldValue, Object newValue,
			Object container) {

		undoEvents.push(new Event(type, attribute, oldValue, newValue, container));

		log.debug("Project update: type={} attribute={} oldValue={} newValue={} container={}", type, attribute,
				oldValue, newValue, container);
		
		// Notify all views
		events.send(ProjectManager.TOPIC_PROJECT_CHANGED, null);

	}

}
