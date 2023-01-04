package com.excelacom.century.apolloneoutbound.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties("sbneapollo.service")
@Configuration
@Getter
@Setter
public class ApolloNEOutboundServiceProperties {

	private String getlinedetailsbymdn;

	private String acctDetails;
	
	private String getDeviceDetailsByLineId;

}
