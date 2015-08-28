package org.apache.solr.mq.wrapper;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.solr.common.util.NamedList;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultSaslConfig;
import com.rabbitmq.client.SaslConfig;
import com.rabbitmq.client.SaslMechanism;

public class ConnectionFactoryWrapper implements IConnectionFactoryWrapper {
	
	Logger logger = Logger.getLogger("org.apache.solr.mq.wrapper.ConnectionFactoryWrapper");
	
	ConnectionFactory factory;
	
	public ConnectionFactoryWrapper(){
		this.factory = new ConnectionFactory();
	}
	
	public ConnectionFactoryWrapper(ConnectionFactory factory){
		this.factory = factory;
	}
	
	public void setHost(String host) {
		factory.setHost(host);
	}

	public IConnectionWrapper newConnection(NamedList workerSettings) throws IOException {
		applySettings(workerSettings);
		return newConnection();
	}
	
	

	public IConnectionWrapper newConnection() throws IOException {
		return new ConnectionWrapper(factory.newConnection());
	}

	public void applyAuthentication(String username, String password) {
		logger.info("username = ["+username+"] password = ["+password+"]");
		factory.setUsername(username);
		factory.setPassword(password);
	}
	
	protected void applySettings(NamedList workerSettings) {
		String host = (String)workerSettings.get("messageQueueHost");
		if (host != null){
			setHost(host);
		}
		NamedList authentication = (NamedList)workerSettings.get("authentication");
		if (authentication != null){
			String username = (String)authentication.get("username");
			String password = (String)authentication.get("password");
			applyAuthentication(username, password);
		}
		//String vHost = (String)workerSettings.get("virtualHost");
		String vHost = "rabbit_data_vagrant_vhost";
		if (vHost != null){
			factory.setVirtualHost(vHost);
		}
	}

	public IChannelWrapper getChannel(IConnectionWrapper connection,
			NamedList workerSettings) throws IOException {
		IChannelWrapper channel = connection.createChannel();
		NamedList exchange = (NamedList)workerSettings.get("exchange");
		if (exchange != null){
			channel.declareExchange((String)exchange.get("name"), 
					ExchangeType.fromValue((String)exchange.get("type")));
		}
		
		return channel;
	}

}
