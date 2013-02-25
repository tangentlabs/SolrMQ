package org.apache.solr.mq.wrapper;

import java.io.IOException;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultSaslConfig;
import com.rabbitmq.client.SaslConfig;
import com.rabbitmq.client.SaslMechanism;

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

	public void applyAuthentication(String username, String password) {
		factory.setUsername(username);
		factory.setPassword(password);
	}
	


}
