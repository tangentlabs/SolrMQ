package org.apache.solr.mq.wrapper;

import java.io.IOException;

import org.apache.solr.common.util.NamedList;

public interface IConnectionFactoryWrapper {
	void setHost(String host);
	IConnectionWrapper newConnection(NamedList workerSettings) throws IOException;
	void applyAuthentication(String username, String password);
	IChannelWrapper getChannel(IConnectionWrapper connection, NamedList workerSettings) throws IOException;
}
