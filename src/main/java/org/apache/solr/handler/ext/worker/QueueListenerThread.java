package org.apache.solr.handler.ext.worker;

import java.io.IOException;

import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.mq.wrapper.IChannelWrapper;
import org.apache.solr.mq.wrapper.IConnectionFactoryWrapper;
import org.apache.solr.mq.wrapper.IConnectionWrapper;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.solrcore.wrapper.ISolrCoreWrapper;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * Listener thread. This is the core listener.
 * Any message consumed spawns a new thread for handling. 
 *
 * @author rnoble
 *
 */
public class QueueListenerThread extends Thread{
	protected IConnectionFactoryWrapper factory;
	protected String handler;
	protected String queue;
	protected String errorQueue;
	protected NamedList<String> workerSettings;

	protected ISolrCoreWrapper core;
	protected Boolean durable;
	
	public QueueListenerThread(ISolrCoreWrapper coreWrapper, IConnectionFactoryWrapper factory, String handler, String queue){
		this.core = coreWrapper;
		this.factory = factory;
		this.handler = handler;
		this.queue = queue;
		this.durable = Boolean.FALSE;
		this.errorQueue = null;
		
	}
	
	public void run() {
		IConnectionWrapper connection;
		try {
			connection = factory.newConnection();
			IChannelWrapper channel = connection.createChannel();
		    channel.queueDeclare(queue, durable.booleanValue(), false, false, null);
		    channel.initialiseConsumer(queue);
		    
		    while (true) {
		      QueueingConsumer.Delivery delivery = channel.getNextDelivery();
		      QueueUpdateWorker worker = QueueUpdateWorker.getUpdateWorker(this, workerSettings, core, channel, handler, delivery);
		      String errorQueue = workerSettings.get("errorQueue");
		      if (errorQueue != null && !errorQueue.isEmpty()){
		    	  worker.setErrorChannel(buildErrorQueue(errorQueue));
		      }
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

	protected IChannelWrapper buildErrorQueue(String errorQueue) throws IOException {
		IConnectionWrapper errorConnection = factory.newConnection();
		IChannelWrapper channel = errorConnection.createChannel();
		channel.queueDeclare(errorQueue, true, false, false, null);
		return channel;
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

	public void setWorkerSettings(NamedList<String> workerSettings) {
		this.workerSettings = workerSettings;
	}

	public NamedList<String> getWorkerSettings() {
		return workerSettings;
	}
}