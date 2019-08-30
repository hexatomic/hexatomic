package org.corpus_tools.hexatomic.core;

import java.util.Stack;

import org.corpus_tools.salt.extensions.notification.Listener;
import org.corpus_tools.salt.graph.GRAPH_ATTRIBUTES;

public class ProjectChangeListener implements Listener {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProjectChangeListener.class);

	private final Stack<Listener.Event> events = new Stack<Listener.Event>();

	@Override
	public void notify(NOTIFICATION_TYPE type, GRAPH_ATTRIBUTES attribute, Object oldValue, Object newValue,
			Object container) {

		events.push(new Event(type, attribute, oldValue, newValue, container));

		log.debug("Project update: type={} attribute={} oldValue={} newValue={} container={}", type, attribute,
				oldValue, newValue, container);

	}

}
