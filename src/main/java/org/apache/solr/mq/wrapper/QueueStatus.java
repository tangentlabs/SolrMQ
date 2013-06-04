package org.apache.solr.mq.wrapper;

import com.rabbitmq.client.AMQP.Queue.DeclareOk;

public class QueueStatus {
	
	
	int messageCount = 0;
	int consumerCount = 0;
	
	public QueueStatus(DeclareOk queueDetails){
		consumerCount = queueDetails.getConsumerCount();
		messageCount = queueDetails.getMessageCount();
	}
	
	public int getMessageCount() {
		return messageCount;
	}
	public void setMessageCount(int messageCount) {
		this.messageCount = messageCount;
	}
	public int getConsumerCount() {
		return consumerCount;
	}
	public void setConsumerCount(int consumerCount) {
		this.consumerCount = consumerCount;
	}
}
