package org.apache.solr.handler.ext.worker;

import java.io.IOException;

import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.utils.ISolrCoreWrapper;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * Listener thread. This is the core listener.
 * Any message consumed spawns a new thread for handeling. 
 *
 * @author rnoble
 *
 */
public class QueueListenerThread extends Thread{
	protected ConnectionFactory factory;
	protected String handler;
	protected String queue;
	protected String errorQueue;
	

	protected ISolrCoreWrapper core;
	protected Boolean durable;
	
	public QueueListenerThread(ISolrCoreWrapper coreWrapper, ConnectionFactory factory, String handler, String queue){
		this.core = coreWrapper;
		this.factory = factory;
		this.handler = handler;
		this.queue = queue;
		this.durable = Boolean.FALSE;
		this.errorQueue = null;
		
	}
	
	public void run() {
		Connection connection;
		try {
			connection = factory.newConnection();
			Channel channel = connection.createChannel();
		    channel.queueDeclare(queue, durable.booleanValue(), false, false, null);
		    QueueingConsumer consumer = new QueueingConsumer(channel);
		    channel.basicConsume(queue, true, consumer);
		    
		    while (true) {
		      QueueingConsumer.Delivery delivery = consumer.nextDelivery();
		      QueueUpdateWorker worker = QueueUpdateWorker.getUpdateWorker(this, core, channel, handler, delivery);

		      worker.start();
		    }
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ShutdownSignalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConsumerCancelledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public Boolean getDurable() {
		return durable;
	}

	public void setDurable(Boolean durable) {
		this.durable = durable;
	}
	public String getErrorQueue() {
		return errorQueue;
	}

	public void setErrorQueue(String errorQueue) {
		this.errorQueue = errorQueue;
	}
}