package org.corpus_tools.hexatomic.core;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.EventHandler;

public class MockEventBroker implements IEventBroker {

	public String lastTopic = null;

	@Override
	public boolean send(String topic, Object data) {
		lastTopic = topic;
		return true;
	}

	@Override
	public boolean post(String topic, Object data) {
		lastTopic = topic;
		return true;
	}

	public boolean unsubscribe(org.osgi.service.event.EventHandler eventHandler) {
		return true;
	}

	@Override
	public boolean subscribe(String topic, EventHandler eventHandler) {
		return true;
	}

	@Override
	public boolean subscribe(String topic, String filter, EventHandler eventHandler, boolean headless) {
		return true;
	};

}