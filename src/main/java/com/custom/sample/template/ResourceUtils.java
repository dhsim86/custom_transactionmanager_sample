package com.custom.sample.template;

import com.custom.sample.client.ResourceClient;
import com.custom.sample.client.ResourceSession;
import com.custom.sample.transaction.CustomResourceHolder;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class ResourceUtils {

	public static ResourceSession getSession(ResourceClient client) {

		CustomResourceHolder resourceHolder =
			(CustomResourceHolder)TransactionSynchronizationManager.getResource(client);

		if (resourceHolder != null) {
			return resourceHolder.getSession();
		}

		return getAutoCommitSession(client);
	}

	private static ResourceSession getAutoCommitSession(ResourceClient client) {

		ResourceSession session = client.getSession();

		ProxyFactory proxyFactory = new ProxyFactory(session);
		proxyFactory.addAdvisor(session.getAutocommitAdvisor());
		return (ResourceSession) proxyFactory.getProxy();
	}
}
