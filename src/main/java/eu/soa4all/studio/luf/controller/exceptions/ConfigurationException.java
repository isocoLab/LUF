package eu.soa4all.studio.luf.controller.exceptions;

public class ConfigurationException extends Exception {

	private static final long serialVersionUID = -7942303202846004992L;

	public ConfigurationException() {
		super();
	}

	public ConfigurationException(String message) {
		super(message);
	}

	public ConfigurationException(Throwable cause) {
		super(cause);
	}

	public ConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

}
