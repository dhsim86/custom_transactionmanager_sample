package com.custom.sample.transaction;

import com.custom.sample.client.ResourceClient;
import com.custom.sample.client.ResourceSession;
import com.custom.sample.exception.ResourceAccessException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.*;
import org.springframework.transaction.support.*;

public class CustomTransactionManager extends AbstractPlatformTransactionManager
	implements ResourceTransactionManager, InitializingBean {

	private static class ResourceTransactionObject implements SmartTransactionObject {

		private CustomResourceHolder resourceHolder;

		public CustomResourceHolder getResourceHolder() {
			return resourceHolder;
		}

		public void setResourceHolder(CustomResourceHolder resourceHolder) {
			this.resourceHolder = resourceHolder;
		}

		@Override
		public boolean isRollbackOnly() {
			return this.resourceHolder.isRollbackOnly();
		}

		@Override
		public void flush() {
			// no operation.
		}
	}

	private ResourceClient client;

	public CustomTransactionManager() {
		setTransactionSynchronization(SYNCHRONIZATION_ALWAYS);
	}

	public CustomTransactionManager(ResourceClient client) {
		this();
		setResourceClient(client);
		afterPropertiesSet();
	}

	public ResourceClient getResourceClient() {
		return client;
	}

	public void setResourceClient(ResourceClient client) {
		this.client = client;
	}

	@Override
	public Object getResourceFactory() {
		return getResourceClient();
	}

	@Override
	public void afterPropertiesSet() {
		if (getResourceClient() == null) {
			throw new IllegalArgumentException("Property 'ResourceClient' is required");
		}
	}

	@Override
	protected Object doGetTransaction() throws TransactionException {
		ResourceTransactionObject txObject = new ResourceTransactionObject();
		txObject.setResourceHolder(
			(CustomResourceHolder)TransactionSynchronizationManager.getResource(getResourceClient()));
		return txObject;
	}

	@Override
	protected boolean isExistingTransaction(Object transaction) throws TransactionException {
		ResourceTransactionObject txObject = (ResourceTransactionObject) transaction;
		return (txObject.getResourceHolder() != null);
	}

	@Override
	protected void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {
		if (definition.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT) {
			throw new InvalidIsolationLevelException("Resource transaction does not support an isolation level concept");
		}

		ResourceTransactionObject txObject = (ResourceTransactionObject) transaction;
		ResourceSession session = null;

		try {
			session = getResourceClient().getSession();
			txObject.setResourceHolder(new CustomResourceHolder(getResourceClient(), session));
			txObject.getResourceHolder().setSynchronizedWithTransaction(true);

			int timeout = determineTimeout(definition);
			if (timeout != TransactionDefinition.TIMEOUT_DEFAULT) {
				txObject.getResourceHolder().setTimeoutInSeconds(timeout);
			}

			TransactionSynchronizationManager.bindResource(getResourceClient(), txObject.getResourceHolder());

		} catch (Throwable e) {
			throw new CannotCreateTransactionException("Could not create resource transaction", e);
		}
	}

	@Override
	protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
		ResourceTransactionObject txObject = (ResourceTransactionObject) status.getTransaction();
		ResourceSession session = txObject.getResourceHolder().getSession();

		try {
			session.commit();
		} catch (ResourceAccessException e) {
			throw new TransactionSystemException("Could not commit resource transaction.", e);
		}
	}

	@Override
	protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
		ResourceTransactionObject txObject = (ResourceTransactionObject) status.getTransaction();
		ResourceSession session = txObject.getResourceHolder().getSession();

		try {
			session.rollback();
		} catch (ResourceAccessException e) {
			throw new TransactionSystemException("Could not rollback resource transaction.", e);
		}
	}

	@Override
	protected void doSetRollbackOnly(DefaultTransactionStatus status) throws TransactionException {
		ResourceTransactionObject txObject = (ResourceTransactionObject) status.getTransaction();
		txObject.getResourceHolder().setRollbackOnly();
	}

	@Override
	protected void doCleanupAfterCompletion(Object transaction) {
		ResourceTransactionObject txObject = (ResourceTransactionObject) transaction;
		TransactionSynchronizationManager.unbindResource(getResourceClient());
		txObject.getResourceHolder().clear();
	}
}
