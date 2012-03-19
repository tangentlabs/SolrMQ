package org.apache.solr.mq.wrapper;

import java.io.IOException;

import com.rabbitmq.client.ConnectionFactory;

public class ConnectionFactoryWrapper implements IConnectionFactoryWrapper {
	ConnectionFactory factory;
	
	public ConnectionFactoryWrapper(ConnectionFactory factory){
		this.factory = factory;
	}
	
	public void setHost(String host) {
		factory.setHost(host);
	}

	public IConnectionWrapper newConnection() throws IOException {
		return new ConnectionWrapper(factory.newConnection());
	}

}
