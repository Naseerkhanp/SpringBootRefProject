/**
 * 
 */
package com.excelacom.century.apolloneoutbound.logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import datadog.trace.api.CorrelationIdentifier;

/**
 * @author Dhayanand.B
 *
 */
@Component
public class LoggerInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// log.info("Inside the Pre Handle method");
		ThreadContext.put("dd.trace_id", CorrelationIdentifier.getTraceId());
		ThreadContext.put("dd.span_id", CorrelationIdentifier.getSpanId());
		ThreadContext.put("trackId", "t-NA : r-NA");
		return true;
	}

	/*
	 * @Override public void postHandle(HttpServletRequest request,
	 * HttpServletResponse response, Object handler, ModelAndView modelAndView)
	 * throws Exception { System.out.println("Inside the Post Handle method"); }
	 */

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception exception) throws Exception {
		// log.info("After completion of request and response");
		ThreadContext.clearAll();
	}
}
