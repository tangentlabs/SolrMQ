package org.apache.solr.mq.wrapper;

import java.io.IOException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;
import com.rabbitmq.client.ShutdownSignalException;

public interface IChannelWrapper {

	void queueDeclare(String queue, boolean booleanValue, boolean b, boolean c,
			Object object);

	void basicConsume(String queue, boolean b, QueueingConsumer consumer);

	void basicPublish(String string, String queue, BasicProperties props,
			byte[] bytes);

	void initialiseConsumer(String queue) throws IOException;

	Delivery getNextDelivery() throws ShutdownSignalException, ConsumerCancelledException, InterruptedException;

	void basicAck(long deliveryTag, boolean b) throws IOException;

}
