package com.custom.sample.transaction;

import com.custom.sample.client.ResourceClient;
import com.custom.sample.client.ResourceSession;
import org.springframework.transaction.support.ResourceHolderSupport;
import org.springframework.util.Assert;

public class CustomResourceHolder extends ResourceHolderSupport {

	private ResourceClient client;
	private ResourceSession session;

	public CustomResourceHolder(ResourceClient client, ResourceSession session) {
		this.client = client;
		setSession(session);
	}

	public void setSession(ResourceSession session) {
		Assert.notNull(session, "resource session must not be null.");

		if (this.session == null) {
			this.session = session;
		}
	}

	public ResourceSession getSession() {
		return session;
	}

	@Override
	public void clear() {
		super.clear();
		session = null;
	}
}
