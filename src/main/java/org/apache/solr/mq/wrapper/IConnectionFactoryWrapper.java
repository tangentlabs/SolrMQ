package org.apache.solr.mq.wrapper;

import java.io.IOException;

public interface IConnectionFactoryWrapper {
	void setHost(String host);
	IConnectionWrapper newConnection() throws IOException;
	void applyAuthentication(String username, String password);
}
