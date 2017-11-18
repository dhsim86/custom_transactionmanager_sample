package com.custom.sample.template;

import com.custom.sample.client.ResourceClient;
import com.custom.sample.client.ResourceSession;
import com.custom.sample.config.ResourceTestConfig;
import com.custom.sample.transaction.CustomResourceHolder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.DefaultMockingDetails;
import org.mockito.internal.util.MockUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.ResourceTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = ResourceTestConfig.class)
public class ResourceUtilsTest {

	@Autowired
	private ResourceSession resourceSession;

	@Autowired
	private ResourceClient resourceClient;

	@Autowired
	private ResourceTransactionManager resourceTransactionManager;

	@Test
	public void configurationTest() {

		DefaultMockingDetails sessionMockingDetails;
		DefaultMockingDetails clientMockingDetails;

		given: {
			sessionMockingDetails =  new DefaultMockingDetails(resourceSession, new MockUtil());
			clientMockingDetails = new DefaultMockingDetails(resourceClient, new MockUtil());
		}

		then: {
			assertThat(sessionMockingDetails.isMock()).isTrue();
			assertThat(clientMockingDetails.isMock()).isTrue();
		}
	}

	@Test
	public void autoCommitTest() {

		ResourceSession session;
		CustomResourceHolder resourceHolder;

		when: {
			session = ResourceUtils.getSession(resourceClient);
			resourceHolder = (CustomResourceHolder)TransactionSynchronizationManager.getResource(resourceClient);
		}

		then: {
			assertThat(Proxy.isProxyClass(session.getClass())).isTrue();
			assertThat(resourceHolder).isNull();
		}
	}

	@Test
	@Transactional(value = "resourceTransactionManager")
	public void nonAutoCommitTest() {

		ResourceSession session;
		CustomResourceHolder resourceHolder;

		when: {
			session = ResourceUtils.getSession(resourceClient);
			resourceHolder = (CustomResourceHolder)TransactionSynchronizationManager.getResource(resourceClient);
		}

		then: {
			assertThat(Proxy.isProxyClass(session.getClass())).isFalse();
			assertThat(resourceHolder).isNotNull();
		}
	}
}
