package org.apache.solr.handler.ext;

import java.io.IOException;

import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.RequestHandlerBase;
import org.apache.solr.handler.ext.worker.QueueListenerThread;
import org.apache.solr.mq.wrapper.ConnectionFactoryWrapper;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.solrcore.wrapper.ISolrCoreWrapper;
import org.apache.solr.util.plugin.SolrCoreAware;

import com.rabbitmq.client.ConnectionFactory;


public class SolrMessageQueue extends RequestHandlerBase implements SolrCoreAware {

	protected String mqHost;
	protected ConnectionFactory factory;
	protected String queue;
	protected String errorQueue;
	protected String plugin_handler;
	protected Boolean durable = Boolean.TRUE;
	protected ISolrCoreWrapper coreWrapper;
	protected NamedList<String> workerSettings;
	
	public SolrMessageQueue() {}
	
	
	
	@Override
	public void init(NamedList args) {
		super.init(args);
		mqHost = (String) this.initArgs.get("messageQueueHost");
		queue = (String) this.initArgs.get("queue");
		errorQueue = (String) this.initArgs.get("errorQueue");
		plugin_handler = (String) this.initArgs.get("updateHandlerName");
		workerSettings = (NamedList<String>) this.initArgs.get("workerSettings");
		if (workerSettings == null) workerSettings = new NamedList<String>();
		factory = new ConnectionFactory();
	    factory.setHost(mqHost);
	    
	    QueueListenerThread listener = new QueueListenerThread(coreWrapper, new ConnectionFactoryWrapper(factory), plugin_handler, queue);
	    listener.setDurable(durable);
	    listener.setWorkerSettings(workerSettings);
	    listener.start();
	    
	}

	@Override
	public String getDescription() {
		return "SOLR MessageQueue listener";
	}

	@Override
	public String getSource() {
		return "$Source$";
	}

	@Override
	public String getSourceId() {
		return "$Id$";
	}

	@Override
	public String getVersion() {
		return "$Revision$";
	}

	@Override
	public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse rsp) throws IOException  {
		rsp.add("description", "This is a simple message queueing plugin for solr.");
		rsp.add("host", mqHost);
		rsp.add("queue", queue);
		rsp.add("handler", plugin_handler);
		rsp.add("durable", durable.toString());
	}

	/**
	 * This gives us a handle to the SolrCore
	 *  @param core - the SolrCore
	 */
	public void inform(SolrCore core) {
		coreWrapper.setCore(core);
		//this.core = core;
	}



	public ISolrCoreWrapper getCoreWrapper() {
		return coreWrapper;
	}



	public void setCoreWrapper(ISolrCoreWrapper coreWrapper) {
		this.coreWrapper = coreWrapper;
	}

	

	
}
