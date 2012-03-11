package org.apache.solr.handler.utils;

import org.apache.solr.core.SolrCore;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrRequestHandler;
import org.apache.solr.response.SolrQueryResponse;

public class SolrCoreWrapper implements ISolrCoreWrapper{
	SolrCore core;
	
	public SolrCoreWrapper(){
		
	}
	
	public SolrCoreWrapper(SolrCore core){
		this.core = core;
	}
	
	public void executeSolrUpdateRequest(String handler,
			SolrQueryRequest request, SolrQueryResponse response) {
		SolrRequestHandler requestHandler = core.getRequestHandler(handler);
		core.execute(requestHandler, request, response);
	}

	public SolrCore getCore() {
		return core;
	}

	public void setCore(SolrCore core) {
		this.core = core;
	}

}
