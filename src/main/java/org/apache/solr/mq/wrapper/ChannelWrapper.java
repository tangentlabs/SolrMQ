package org.apache.solr.mq.wrapper;

import java.io.IOException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;
import com.rabbitmq.client.ShutdownSignalException;

public class ChannelWrapper implements IChannelWrapper {
	Channel channel;
	QueueingConsumer consumer;
	
	
	public ChannelWrapper(Channel channel) {
		this.channel = channel;
	}

	public void queueDeclare(String queue, boolean booleanValue, boolean b,
			boolean c, Object object) {
		// TODO Auto-generated method stub

	}

	public void basicConsume(String queue, boolean b, QueueingConsumer consumer) {
		// TODO Auto-generated method stub

	}

	public void basicPublish(String string, String queue,
			BasicProperties props, byte[] bytes) {
		// TODO Auto-generated method stub
		
	}

	public void initialiseConsumer(String queue) throws IOException {
		consumer = new QueueingConsumer(channel);
		channel.basicConsume(queue, false, consumer);
	}

	public Delivery getNextDelivery() throws ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		
		return consumer.nextDelivery();
	}

	public void basicAck(long deliveryTag, boolean b) throws IOException {
		channel.basicAck(deliveryTag, b);
		
	}

}
