package org.apache.solr.handler.ext.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.solr.common.params.MultiMapSolrParams;
import org.apache.solr.common.util.ContentStream;
import org.apache.solr.common.util.ContentStreamBase;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.handler.ext.exceptions.ResponseFailedException;
import org.apache.solr.handler.ext.exceptions.SolrMqException;
import org.apache.solr.handler.ext.exceptions.UpdateFailedException;
import org.apache.solr.mq.wrapper.IChannelWrapper;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrQueryRequestBase;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.solrcore.wrapper.ISolrCoreWrapper;

import com.rabbitmq.client.QueueingConsumer;

/**
 * Worker thread. This is spawned for each message consumed.
 * 
 * @author rnoble
 * 
 */
public abstract class QueueUpdateWorker extends Thread {
	QueueingConsumer.Delivery delivery;
	protected String updateHandler;
	protected ISolrCoreWrapper core;
	protected IChannelWrapper channel;
	protected NamedList<String> settings;
	protected IChannelWrapper errorChannel;
	protected String errorQueue;
	
	Logger logger = Logger.getLogger("org.apache.solr.handler.ext.worker.QueueUpdateWorker");
	

	/**
	 * Worker thread for the update
	 * 
	 * @param workerSettings
	 * 
	 * @param core
	 * @param updateHandler
	 * @param delivery
	 */
	public QueueUpdateWorker(NamedList<String> workerSettings,
			ISolrCoreWrapper core, IChannelWrapper channel,
			String updateHandler, QueueingConsumer.Delivery delivery) {
		super();
		this.settings = workerSettings;
		this.core = core;
		this.channel = channel;
		this.updateHandler = updateHandler;
		this.delivery = delivery;
	}

	/**
	 * Run the update worker. Read from a Delivery and handle the result.
	 */
	public void run() {
		logger.log(Level.ERROR, "handelling Result");
		/**
		 * DECODE:
		 */
		try {
			String message = StringUtils.newStringUtf8(delivery.getBody());
	
			SolrQueryRequest request = getRequest(getParams(), message);
			SolrQueryResponse response = getInitialResponse();
			if (updateHandler.equals("/null")){
				logger.log(Level.INFO, "SolrMQ NULL Handler ["+delivery.getBody()+"]");
			}else {
				performUpdateRequest(updateHandler, request, response);
				try {
					handleResult(request, response);
				} catch (UpdateFailedException e) {
					logger.log(Level.ERROR, e);
					handleError(e, request, response);
				} catch (ResponseFailedException e) {
					logger.log(Level.ERROR, e);
					handleError(e, request, response);
				}
			}
			if ("manual".equals(this.settings.get("acknowledge"))){
				logger.log(Level.INFO, "Sending Ack");
				try {
					channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
				} catch (IOException e) {
					logger.log(Level.ERROR, "SolrMQ Listener error: Sending Ack");
					logger.log(Level.ERROR, e);
				}
			}
		} catch (IllegalStateException ise){
			handleError(new UpdateFailedException("Non-UTF-8 Content", ise), null, null);
		}
	}

	protected abstract void handleError(SolrMqException e,
			SolrQueryRequest request, SolrQueryResponse response);

	protected abstract void handleResult(SolrQueryRequest request,
			SolrQueryResponse result) throws UpdateFailedException,
			ResponseFailedException;

	/**
	 * 
	 * @param request
	 * @param response
	 * @param handler
	 *            - name of the handler, like /update or /update/json. Should
	 *            probably be loaded.
	 * @return
	 */
	public SolrQueryResponse performUpdateRequest(String handler,
			SolrQueryRequest request, SolrQueryResponse response) {
		core.executeSolrUpdateRequest(handler, request, response);
		return response;
	}

	/**
	 * Gets the basic response for the request
	 * 
	 * @return SolrQueryResponse
	 */
	protected SolrQueryResponse getInitialResponse() {
		SolrQueryResponse response = new SolrQueryResponse();
		return response;
	}

	/**
	 * Generate the request that we will proxy through to one of the SOLR update
	 * handlers
	 * 
	 * @param params
	 *            - the parameters, these can be parsed as custom message
	 *            headers
	 * @param message
	 *            - the actual message, at present only strings are allowed.
	 * @return SolrQueryRequest
	 */
	protected SolrQueryRequest getRequest(Map<String, String[]> params,
			String message) {
		MultiMapSolrParams solrParams = new MultiMapSolrParams(params);
		SolrQueryRequestBase request = new SolrQueryRequestBase(core.getCore(),
				solrParams) {
		};

		ContentStream stream = new ContentStreamBase.StringStream(message);
		ArrayList<ContentStream> streams = new ArrayList<ContentStream>();
		streams.add(stream);
		request.setContentStreams(streams);
		return request;
	}

	/**
	 * Extract the parameters from the custom headers, if any have been added.
	 * 
	 * @return
	 */
	protected Map<String, String[]> getParams() {
		Map<String, Object> headers = delivery.getProperties().getHeaders();

		Map<String, String[]> params = new HashMap<String, String[]>();
		if (headers != null) {
			Set<String> keys = headers.keySet();
			for (String key : keys) {
				Object value = headers.get(key);
				params.put(key, new String[] { value.toString() });
			}
		}
		return params;
	}

	

	public NamedList<String> getSettings() {
		return settings;
	}

	public void setSettings(NamedList<String> settings) {
		this.settings = settings;
	}

	public IChannelWrapper getErrorChannel() {
		return errorChannel;
	}

	public void setErrorChannel(IChannelWrapper iChannelWrapper) {
		this.errorChannel = iChannelWrapper;
	}

	public void setErrorQueue(IChannelWrapper errorChannel, String errorQueue) {
		setErrorChannel(errorChannel);
		setErrorQueue(errorQueue);
		
	}

	public void setErrorQueue(String errorQueue) {
		this.errorQueue = errorQueue;
		
	}


}