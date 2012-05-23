package org.apache.solr.mq.wrapper;

import java.io.IOException;

import org.apache.solr.response.SolrQueryResponse;

import com.rabbitmq.client.Channel;

public interface IConnectionWrapper {

	IChannelWrapper createChannel() throws IOException;

	public String getStatus();
	public String stopConnection();
	
	

}
