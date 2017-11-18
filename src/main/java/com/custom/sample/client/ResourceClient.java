package com.custom.sample.client;

import com.custom.sample.exception.ResourceAccessException;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.aop.framework.ProxyFactory;

public abstract class ResourceClient {

	public static class ResourceAccessExceptionAdvice implements ThrowsAdvice {

		public void afterThrowing(Exception e) {
			throw new ResourceAccessException(e.getMessage(), e);
		}
	}

	private static final ThrowsAdvice exceptionAdvice;

	static {
		exceptionAdvice = new ResourceAccessExceptionAdvice();
	}

	public ResourceSession getSession() {

		ResourceSession session = doGetSession();

		ProxyFactory proxyFactory = new ProxyFactory(session);
		proxyFactory.addAdvice(exceptionAdvice);
		return (ResourceSession) proxyFactory.getProxy();
	}

	abstract protected ResourceSession doGetSession();
}
