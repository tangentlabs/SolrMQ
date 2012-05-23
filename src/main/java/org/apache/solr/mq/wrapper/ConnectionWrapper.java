package org.apache.solr.mq.wrapper;

import java.io.IOException;

import org.apache.solr.response.SolrQueryResponse;

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
	public String getStatus() {
		// TODO Auto-generated method stub
		if (connection.getCloseReason() == null){
			return null;
		}
		return connection.getCloseReason().getMessage();
	}
	
	
	public String stopConnection(){
		try {
			connection.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			connection.abort();
		}
		return "Connection Closed";
	}
	

}
