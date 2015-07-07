package org.apache.solr.mq.wrapper;

import java.io.IOException;
import java.util.Map;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;
import com.rabbitmq.client.ShutdownSignalException;

public interface IChannelWrapper {

	QueueStatus queueDeclare(String queue, boolean booleanValue, boolean b, boolean c,
			Map<String,Object> object) throws IOException;

	void publish(String string, String queue, BasicProperties props,
			byte[] bytes);

	void initialiseConsumer(String queue) throws IOException;

	Delivery getNextDelivery() throws ShutdownSignalException, ConsumerCancelledException, InterruptedException;

	void basicAck(long deliveryTag, boolean b) throws IOException;

	public void cancelConsumer();
	
	public void purgeQueue() throws IOException;
	
	public void deleteQueue() throws IOException;

	QueueStatus queueDeclarePassive(String queue) throws IOException;

}
