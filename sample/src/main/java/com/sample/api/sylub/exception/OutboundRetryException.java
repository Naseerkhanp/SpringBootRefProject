/**
 * 
 */
package com.excelacom.century.apolloneoutbound.exception;

/**
 * @author Dhayanand.B
 *
 */
public class OutboundRetryException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OutboundRetryException() {
		super();
	}

	public OutboundRetryException(String message) {
		super(message);
	}

	public OutboundRetryException(String message, Throwable cause) {
		super(message, cause);
	}

	public OutboundRetryException(Throwable cause) {
		super(cause);
	}

}
