package org.apache.solr.mq.wrapper;

import java.io.IOException;

public interface IConnectionWrapper {

	IChannelWrapper createChannel() throws IOException;

	public String getStatus();
	public String stopConnection();
	
	

}
