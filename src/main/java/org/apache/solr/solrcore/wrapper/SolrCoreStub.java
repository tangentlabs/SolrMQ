package org.apache.solr.solrcore.wrapper;

import org.apache.solr.core.SolrCore;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

public class SolrCoreStub implements ISolrCoreWrapper {

	public void executeSolrUpdateRequest(String handler,
			SolrQueryRequest request, SolrQueryResponse response) {
		
	}

	public SolrCore getCore() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setCore(SolrCore core) {
	}

}
