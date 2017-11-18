package com.custom.sample.template;

import com.custom.sample.client.ResourceClient;
import com.custom.sample.client.ResourceSession;

public class ResourceTemplate {

	private ResourceClient client;

	private ResourceSession getSession() {
		return ResourceUtils.getSession(client);
	}

	public void addValue(String key, String value) {
		ResourceSession session = getSession();
		session.addValue(key, value);
	}

	public String getValue(String key) {
		ResourceSession session = getSession();
		return session.getValue(key);
	}
}
