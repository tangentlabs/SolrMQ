package org.apache.solr.handler.ext.exceptions;

public class UpdateFailedException extends SolrMqException {

	private static final long serialVersionUID = 1L;

	public UpdateFailedException() {}

	public UpdateFailedException(String arg0) {
		super(arg0);
	}

	public UpdateFailedException(Throwable arg0) {
		super(arg0);
	}

	public UpdateFailedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
