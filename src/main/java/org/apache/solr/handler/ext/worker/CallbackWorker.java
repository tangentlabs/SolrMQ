package org.apache.solr.handler.ext.worker;

import org.apache.solr.handler.utils.ISolrCoreWrapper;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer.Delivery;

public class CallbackWorker extends QueueUpdateWorker{

	public CallbackWorker(ISolrCoreWrapper core, Channel channel, String updateHandler,
			Delivery delivery) {
		super(core, channel, updateHandler, delivery);
	}

	@Override
	protected void handleResult(SolrQueryRequest request,
			SolrQueryResponse result) {
		//delivery.
		// TODO Auto-generated method stub
		
	}

}
