package eu.soa4all.studio.luf.controller.exceptions;

public class RDFRepositoryException extends Exception {

	private static final long serialVersionUID = -1177381882027513500L;

	public RDFRepositoryException() {
		super();
	}

	public RDFRepositoryException(String message) {
		super(message);
	}

	public RDFRepositoryException(Throwable cause) {
		super(cause);
	}

	public RDFRepositoryException(String message, Throwable cause) {
		super(message, cause);
	}

}
