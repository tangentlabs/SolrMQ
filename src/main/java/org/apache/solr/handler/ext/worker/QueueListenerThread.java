package org.apache.solr.handler.ext.worker;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.mq.wrapper.IChannelWrapper;
import org.apache.solr.mq.wrapper.IConnectionFactoryWrapper;
import org.apache.solr.mq.wrapper.IConnectionWrapper;
import org.apache.solr.solrcore.wrapper.ISolrCoreWrapper;

import com.rabbitmq.client.QueueingConsumer;

/**
 * Listener thread. This is the core listener.
 * Any message consumed spawns a new thread for handling. 
 *
 * @author rnoble
 *
 */
public class QueueListenerThread extends Thread{
	public final int STOPPED = 0;
	public final int RUNNING = 1;
	protected int mode = RUNNING;
	protected IConnectionFactoryWrapper factory;
	protected String handler;
	protected String queue;
	protected String errorQueue;
	protected NamedList<String> workerSettings;

	protected ISolrCoreWrapper core;
	protected Boolean durable;
	private IConnectionWrapper connection;
	private IChannelWrapper channel;
	private Exception error;
	private UpdateWorkerFactory updateWorkerFactory;
	
	public QueueListenerThread(ISolrCoreWrapper coreWrapper, IConnectionFactoryWrapper factory, UpdateWorkerFactory updateWorkerFactory, String handler, String queue){
		this.core = coreWrapper;
		this.updateWorkerFactory = updateWorkerFactory;
		this.factory = factory;
		this.handler = handler;
		this.queue = queue;
		this.durable = Boolean.FALSE;
		this.errorQueue = null;
		this.mode = RUNNING;
	}
	
	public void run() {
		for (;;){
			try {
				if ("true".equals(workerSettings.get("useAuthentication")) && !StringUtils.isEmpty(workerSettings.get("username")) && !StringUtils.isEmpty(workerSettings.get("password"))){
					factory.applyAuthentication(workerSettings.get("username"), workerSettings.get("password"));
				}
				connection = factory.newConnection();
				channel = connection.createChannel();
			    channel.queueDeclare(queue, durable.booleanValue(), false, false, null);
			    channel.initialiseConsumer(queue);
			    
			    while (true) {
			      
			      QueueingConsumer.Delivery delivery = channel.getNextDelivery();
			      QueueUpdateWorker worker = updateWorkerFactory.getUpdateWorker(this, workerSettings, core, channel, handler, delivery);
			      String errorQueue = workerSettings.get("errorQueue");
			      if (errorQueue != null && !errorQueue.isEmpty()){
			    	  worker.setErrorChannel(buildErrorQueue(errorQueue));
			      }
			      worker.run();
	//		      worker.start();
			    }
			} catch (Exception e) {
				error = e;
			}
			if (mode == STOPPED){
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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

	public IConnectionWrapper getConnection() {
		return connection;
	}

	public void setConnection(IConnectionWrapper connection) {
		this.connection = connection;
	}

	public void requestStop() {
		
		channel.cancelConsumer();
		connection.stopConnection();
	}
	
	public void purgeQueue() throws IOException {
		channel.purgeQueue();
	}
	
	public void deleteQueue() throws IOException {
		channel.deleteQueue();
	}

	public Exception getError() {
		return error;
	}

	public void setError(Exception error) {
		this.error = error;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}
}