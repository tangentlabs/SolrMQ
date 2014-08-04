package org.apache.solr.mq.wrapper;

import java.io.IOException;
import java.util.Map;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;
import com.rabbitmq.client.ShutdownSignalException;

public class ChannelWrapper implements IChannelWrapper {
	Channel channel;
	QueueingConsumer consumer;
	String queue;
	
	public ChannelWrapper(Channel channel) {
		this.channel = channel;
	}

	public void queueDeclare(String queue, boolean booleanValue, boolean b,
			boolean c, Map<String,Object> object) throws IOException {
		this.queue = queue;
		this.channel.queueDeclare(queue, booleanValue, b, c, object);
		
	}

	public void basicConsume(String queue, boolean b, QueueingConsumer consumer) {

	}

	public void basicPublish(String string, String queue,
			BasicProperties props, byte[] bytes) {
		// TODO Auto-generated method stub
		
	}
	
	public void cancelConsumer(){
		try {
			consumer.handleCancel(consumer.getConsumerTag());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void initialiseConsumer(String queue) throws IOException {
		consumer = new QueueingConsumer(channel);
		channel.basicConsume(queue, true, consumer);
	}

	public Delivery getNextDelivery() throws ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		try {
			return consumer.nextDelivery();
		} catch (ShutdownSignalException sse){
			sse.getReference();
			throw sse;
		}
	}

	public void basicAck(long deliveryTag, boolean b) throws IOException {
		channel.basicAck(deliveryTag, b);
		
	}

	public void purgeQueue() throws IOException {
		channel.queuePurge(queue);
		
	}

	public void deleteQueue() throws IOException {
		channel.queueDelete(queue);
		
	}

	public void declareExchange(String name, ExchangeType type) throws IOException {
		// TODO Auto-generated method stub
		channel.exchangeDeclare(name, type.toString());
	}

}
