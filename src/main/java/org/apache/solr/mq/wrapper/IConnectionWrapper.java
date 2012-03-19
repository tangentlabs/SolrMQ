package org.apache.solr.mq.wrapper;

import java.io.IOException;

import com.rabbitmq.client.Channel;

public interface IConnectionWrapper {

	IChannelWrapper createChannel() throws IOException;

}
