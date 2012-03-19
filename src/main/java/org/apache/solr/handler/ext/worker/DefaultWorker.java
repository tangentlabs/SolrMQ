package org.apache.solr.handler.ext.worker;

import java.io.IOException;

import org.apache.solr.common.util.NamedList;
import org.apache.solr.handler.ext.exceptions.ResponseFailedException;
import org.apache.solr.handler.ext.exceptions.SolrMqException;
import org.apache.solr.handler.ext.exceptions.UpdateFailedException;
import org.apache.solr.mq.wrapper.IChannelWrapper;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.solrcore.wrapper.ISolrCoreWrapper;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer.Delivery;

public class DefaultWorker extends QueueUpdateWorker{

	public DefaultWorker(NamedList<String> workerSettings, ISolrCoreWrapper core, IChannelWrapper channel2, String updateHandler,
			Delivery delivery) {
		super(workerSettings, core, channel2, updateHandler, delivery);
	}

	@Override
	protected void handleResult(SolrQueryRequest request,
			SolrQueryResponse result) throws UpdateFailedException, ResponseFailedException {
		if (result.getException() != null){
			throw new UpdateFailedException(result.getException());
		}
		if ((delivery.getProperties().getReplyTo() != null) && (delivery.getProperties().getCorrelationId() != null)){
			String values = result.getValues().toString();
			try {
				sendResponse(channel, delivery.getProperties().getReplyTo(), values);
			} catch (IOException e) {
				throw new ResponseFailedException(e);
			}
		}
		
		try {
			channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	protected void handleError(SolrMqException e,
			SolrQueryRequest request, SolrQueryResponse response) {
		String errorQueue = settings.get("errorQueue");
		if (errorChannel != null && errorQueue != null && !errorQueue.isEmpty()){
			try {
				sendResponse(errorChannel, errorQueue, e.getMessage());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}
	
	protected void sendResponse(IChannelWrapper errorChannel, String queue, String message) throws IOException{
		BasicProperties props = new BasicProperties.Builder()
            .correlationId(delivery.getProperties().getCorrelationId())
            .build();
		errorChannel.basicPublish( "", queue, props, message.getBytes());
	}


}
