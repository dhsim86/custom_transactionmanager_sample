package com.custom.sample.client;

import org.springframework.aop.Advisor;

public interface ResourceSession {

	Advisor getAutocommitAdvisor();

	void commit();
	void rollback();

	void addValue(String key, String value);
	String getValue(String key);
}
