package com.custom.sample.transaction;

import com.custom.sample.client.ResourceClient;
import com.custom.sample.client.ResourceSession;
import com.custom.sample.config.ResourceTestConfig;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.internal.util.DefaultMockingDetails;
import org.mockito.internal.util.MockUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = ResourceTestConfig.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ResourceTransactionManagerTest {

	@Autowired
	private ResourceSession resourceSession;

	@Autowired
	private ResourceClient resourceClient;

	private static boolean isRollBackCalled;
	private static boolean isCommitCalled;

	@Test
	public void configurationTest() {

		DefaultMockingDetails sessionMockingDetails;
		DefaultMockingDetails clientMockingDetails;

		given: {
			sessionMockingDetails = new DefaultMockingDetails(resourceSession, new MockUtil());
			clientMockingDetails = new DefaultMockingDetails(resourceClient, new MockUtil());
		}

		then: {
			assertThat(sessionMockingDetails.isMock()).isTrue();
			assertThat(clientMockingDetails.isMock()).isTrue();
		}
	}


	@Test
	@Transactional(value = "resourceTransactionManager")
	public void $01_calledRollbackTest() {

		// Since AbstractPlatformTransactionManager.doRollBack method is called automatically on junit test.

		given: {
			isRollBackCalled = false;
		}

		when: {
			doAnswer((invocation) -> {
				isRollBackCalled = true;
				return null;
			}).when(resourceSession).rollback();
		}
	}

	@Test
	@Rollback(false)
	@Transactional(value = "resourceTransactionManager")
	public void $02_calledCommitTest() {

		// Since AbstractPlatformTransactionManager.doCommit method is called automatically on junit test with @RollBack(false).

		given: {
			isCommitCalled = false;
		}

		when: {
			doAnswer((invocation) -> {
				isCommitCalled = true;
				return null;
			}).when(resourceSession).commit();
		}
	}

	@Test
	public void $03_verifyTransactional() {

		then: {
			assertThat(isRollBackCalled).isTrue();
			assertThat(isCommitCalled).isTrue();
		}
	}
}
