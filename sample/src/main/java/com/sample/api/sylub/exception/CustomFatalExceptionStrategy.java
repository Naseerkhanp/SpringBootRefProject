package com.excelacom.century.apolloneoutbound.exception;

import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;

public class CustomFatalExceptionStrategy extends ConditionalRejectingErrorHandler.DefaultExceptionStrategy {

	public CustomFatalExceptionStrategy() {
		super();
	}

	@Override
	public boolean isFatal(Throwable t) {
		return !(t.getCause() instanceof NslCustomException);
	}
}