package com.excelacom.century.apolloneoutbound.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Configuration
@ConfigurationProperties("sbneapollo.service")
public class ApolloNEQueueProperties {

	private String server;
	
	private String outboundapolloNeUrl;
	
	private String dns;
	
	private String rabbitListenerContainer;
	
	private String apolloNeOutboundServiceQueue;

	private String apolloNeOutboundServiceExchange;
	
	
	private String apolloNeSendClientQueue;

	private String apolloNeRetryExchange;

	private String apolloNeRetryRouter;

	private String apolloNeRetryQueue;

	private String apolloNeSendClientExchange;

	private String apolloNeSendClientRouter;
	
	private String apolloNeErrorQueue;
	
	private String apolloNeErrorExchange;
	
	private String apolloNeErrorRouter;

	private int connectionTimeOut;

	private long receiveTimeOut;
	
	private int maxretrycount;

	private Boolean enableQueueCheck;
	
	private String searchEnvironmentWaitQueue;
	
	private String searchEnvironmentWaitExchange;

	private String searchEnvWaitTopicExchange;
}
