package org.apache.solr.handler.utils;

import org.apache.solr.core.SolrCore;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

public interface ISolrCoreWrapper {
	public void executeSolrUpdateRequest(String handler, SolrQueryRequest request,
			SolrQueryResponse response);
	public SolrCore getCore();
	public void setCore(SolrCore core) ;
}
