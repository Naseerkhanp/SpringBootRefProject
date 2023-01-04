package com.excelacom.century.apolloneoutbound.logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Component
public class ServiceRegistrationConfig extends WebMvcConfigurationSupport {

	@Autowired
	LoggerInterceptor serviceInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(serviceInterceptor);
	}
}
