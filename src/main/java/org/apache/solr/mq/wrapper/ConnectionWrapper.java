package org.apache.solr.mq.wrapper;

import java.io.IOException;

import com.rabbitmq.client.Connection;

public class ConnectionWrapper implements IConnectionWrapper {
	Connection connection;
	public ConnectionWrapper(Connection connection) {
		this.connection = connection;
	}
	public IChannelWrapper createChannel() throws IOException {
		// TODO Auto-generated method stub
		return new ChannelWrapper(connection.createChannel());
	}

}
