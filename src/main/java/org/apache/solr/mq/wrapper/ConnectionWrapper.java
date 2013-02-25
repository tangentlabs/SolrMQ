package org.apache.solr.mq.wrapper;

import java.io.IOException;

import com.rabbitmq.client.Connection;

public class ConnectionWrapper implements IConnectionWrapper {
	Connection connection;
	public ConnectionWrapper(Connection connection) {
		this.connection = connection;
	}
	public IChannelWrapper createChannel() throws IOException {
		return new ChannelWrapper(connection.createChannel());
	}
	public String getStatus() {
		if (connection.getCloseReason() == null){
			return null;
		}
		return connection.getCloseReason().getMessage();
	}
	
	
	public String stopConnection(){
		try {
			connection.close();
		} catch (IOException e) {
			connection.abort();
		}
		return "Connection Closed";
	}
	

}
