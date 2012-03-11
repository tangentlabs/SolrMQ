package org.apache.solr.handler.ext.worker;

import org.apache.solr.handler.utils.ISolrCoreWrapper;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.rabbitmq.client.QueueingConsumer.Delivery;

public class CallbackWorker extends QueueUpdateWorker{

	public CallbackWorker(ISolrCoreWrapper core, String updateHandler,
			Delivery delivery) {
		super(core, updateHandler, delivery);
	}

	@Override
	protected void handleResult(SolrQueryRequest request,
			SolrQueryResponse result) {
		// TODO Auto-generated method stub
		
	}

}
