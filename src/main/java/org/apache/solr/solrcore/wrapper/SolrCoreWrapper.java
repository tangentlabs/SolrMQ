package org.apache.solr.solrcore.wrapper;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.solr.core.SolrCore;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrRequestHandler;
import org.apache.solr.response.SolrQueryResponse;

public class SolrCoreWrapper implements ISolrCoreWrapper{
	SolrCore core;
	Logger logger = Logger.getLogger("org.apache.solr.solrcore.wrapper.SolrCoreWrapper");
	
	public SolrCoreWrapper(){}
	
	public SolrCoreWrapper(SolrCore core){
		this.core = core;
	}
	
	public void executeSolrUpdateRequest(String handler,
			SolrQueryRequest request, SolrQueryResponse response) {
		logger.log(Level.INFO, "Update "+handler);
		SolrRequestHandler requestHandler = core.getRequestHandler(handler);
		logger.log(Level.INFO, "Got Update Handler "+requestHandler.getName());
		core.execute(requestHandler, request, response);
		logger.log(Level.INFO, "Update Complete");
	}

	public SolrCore getCore() {
		return core;
	}

	public void setCore(SolrCore core) {
		this.core = core;
	}

}
