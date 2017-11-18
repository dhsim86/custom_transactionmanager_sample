package com.custom.sample.config;

import com.custom.sample.client.ResourceClient;
import com.custom.sample.client.ResourceSession;
import com.custom.sample.transaction.CustomTransactionManager;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.PlatformTransactionManager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
public class ResourceTestConfig {

	@Bean
	public ResourceSession resourceSession() {
		ResourceSession session = mock(ResourceSession.class);
		when(session.getAutocommitAdvisor()).thenReturn(mock(DefaultPointcutAdvisor.class));

		return session;
	}

	@Bean
	@DependsOn(value = "resourceSession")
	public ResourceClient resourceClient(ResourceSession resourceSession) {
		ResourceClient resourceClient = mock(ResourceClient.class);
		when(resourceClient.getSession()).thenReturn(resourceSession);

		return resourceClient;
	}

	@Bean
	@DependsOn(value = "resourceClient")
	public PlatformTransactionManager resourceTransactionManager(ResourceClient client) {
		return new CustomTransactionManager(client);
	}
}
