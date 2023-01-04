package com.excelacom.century.apolloneoutbound.utils.properties;

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
public class ApolloNEProperties {

	private String server;
	
	private String outboundapolloNeUrl;
	
	private String dns;
	
	
	
}
