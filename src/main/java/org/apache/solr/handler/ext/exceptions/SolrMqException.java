package org.apache.solr.handler.ext.exceptions;

public abstract class SolrMqException extends Exception {
	private static final long serialVersionUID = 1L;

	public SolrMqException() {
	}

	public SolrMqException(String arg0) {
		super(arg0);
	}

	public SolrMqException(Throwable arg0) {
		super(arg0);
	}

	public SolrMqException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
