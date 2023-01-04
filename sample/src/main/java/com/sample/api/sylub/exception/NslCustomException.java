package com.excelacom.century.apolloneoutbound.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class NslCustomException extends WebApplicationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3156040750581929702L;

	public NslCustomException(Response response) {
		super(validate(response));
	}

	static Response validate(final Response response) {
		if (response.getStatus() > 200 && response.getStatus() < 300) {
			throw new IllegalArgumentException("Status code should not be in 200 series...");
		}
		return response;
	}

}