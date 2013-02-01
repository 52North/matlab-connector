package org.uncertweb.matlab;

public class MLConnectorException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new <code>MLConnectorException</code> instance with the given message.
	 * 
	 * @param message the exception message
	 */
	public MLConnectorException(String message) {
		super(message);
	}
	
	/**
	 * Creates a new <code>MLConnectorException</code> instance with the given message and cause.
	 * 
	 * @param message the exception message
	 * @param cause the cause of the exception
	 */
	public MLConnectorException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
