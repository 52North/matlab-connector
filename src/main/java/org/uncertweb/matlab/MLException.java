package org.uncertweb.matlab;

/**
 * Represents an exception returned by MATLAB during function execution.
 * 
 * @author Richard Jones
 *
 */
public class MLException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private String message;
	
	/**
	 * Creates a new <code>MLException</code> instance with the given message.
	 * 
	 * @param message the exception message
	 */
	public MLException(String message) {
		this.message = message;
	}
	
	/**
	 * Returns the exception message.
	 * 
	 * @return the exception message
	 */
	public String getMessage() {
		return message;
	}
	
}
