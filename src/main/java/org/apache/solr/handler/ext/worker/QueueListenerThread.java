package org.apache.solr.handler.ext.worker;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.mq.wrapper.ConnectionFactoryWrapper;
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
	protected String errorQueue = null;
	protected NamedList workerSettings;

	protected ISolrCoreWrapper core;
	protected Boolean durable;
	private IConnectionWrapper connection;
	private IChannelWrapper channel;
	private Exception error;
	private UpdateWorkerFactory updateWorkerFactory;
	
	Logger logger = Logger.getLogger("org.apache.solr.handler.ext.worker.QueueListenerThread");
	private IChannelWrapper errorChannel;
	
	
	public QueueListenerThread(ISolrCoreWrapper coreWrapper, IConnectionFactoryWrapper factory, UpdateWorkerFactory updateWorkerFactory, String handler, NamedList workerSettings, NamedList errorSettings, String queue){
		this.core = coreWrapper;
		this.updateWorkerFactory = updateWorkerFactory;
		this.factory = factory;
		this.handler = handler;
		this.queue = queue;
		this.durable = Boolean.FALSE;
		this.workerSettings = workerSettings;
		try {
			//TODO: causes nullpointer Exception 
			this.errorChannel = buildErrorQueue((NamedList)errorSettings);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.mode = RUNNING;
	}
	
	public void run() {

		for (;;){
			try {
				logger.log(Level.INFO, "SolrMQ connection starting");
				connection = factory.newConnection(workerSettings);
				logger.log(Level.INFO, "SolrMQ connection started");
				//channel = connection.createChannel();
				channel = factory.getChannel(connection, workerSettings);
				
				logger.log(Level.INFO, "SolrMQ channel created");
			    channel.queueDeclare(queue, durable.booleanValue(), false, false, null);
			    logger.log(Level.INFO, "SolrMQ queue declared ["+queue+"]");
			    channel.initialiseConsumer(queue);
			    logger.log(Level.INFO, "SolrMQ consumer started");
			    while (true) {
			      logger.log(Level.INFO, "SolrMQ consumer listening...");
			      QueueingConsumer.Delivery delivery = channel.getNextDelivery();
			      logger.log(Level.INFO, "SolrMQ message recieved ["+delivery.getBody()+"]");
			      
			      /**
			       * TODO: Change this to use an executorService
			       */
			      
			      QueueUpdateWorker worker = updateWorkerFactory.getUpdateWorker(this, workerSettings, core, channel, handler, delivery);
			      logger.log(Level.INFO, "SolrMQ worker started: "+worker.getClass().getName());
			      
			      worker.setErrorQueue(errorChannel, errorQueue);
			      
			      logger.log(Level.INFO, "SolrMQ worker running update...");
			      worker.run();
			      logger.log(Level.INFO, "SolrMQ worker update complete.");
			    }
			} catch (Exception e) {
				logger.log(Level.ERROR, "SolrMQ Exception on Listener: "+e.getClass().getName());		
				logger.log(Level.ERROR, e);				
				error = e;
			}
			if (mode == STOPPED){
				logger.log(Level.INFO, "SolrMQ worker stopped.");
				break;
			}
			try {
				logger.log(Level.WARN, "Connection failed, reconnecting in 1 second.");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.log(Level.ERROR, e);	
			}
		}
		
	}

	protected IChannelWrapper buildErrorQueue(NamedList errorSettings) throws IOException {
		if (errorSettings == null) return null;
		IConnectionFactoryWrapper factory = new ConnectionFactoryWrapper();
		String queue = (String)errorSettings.get("queue");
		this.errorQueue = queue;
		IConnectionWrapper errorConnection = factory.newConnection(errorSettings);
		IChannelWrapper channel = errorConnection.createChannel();
		channel.queueDeclare(queue, true, false, false, null);
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

	public NamedList getWorkerSettings() {
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