package org.apache.solr.handler.ext.exceptions;

public class ResponseFailedException extends SolrMqException {
	private static final long serialVersionUID = 1L;

	public ResponseFailedException() {}

	public ResponseFailedException(String arg0) {
		super(arg0);
	}

	public ResponseFailedException(Throwable arg0) {
		super(arg0);
	}

	public ResponseFailedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
