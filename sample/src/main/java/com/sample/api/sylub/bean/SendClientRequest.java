/**
 * 
 */
package com.excelacom.century.apolloneoutbound.bean;

import java.io.Serializable;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Dhayanand.B
 *
 */
@Getter
@Setter
@ToString
public class SendClientRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String serviceName;

	private String request;

	private String response;

	private Map<String, String> dataMap;

	private String operationName;

	private String httpMethod;

	private String entityId;

	private String transId;

	private String groupId;

	private String processPlanId;

	private String responseId;

	private Boolean isRetryTransaction;

	private Integer retryCount;

	private String transactionStatus;
	
	private RcsIntegrationServiceBean rcsServiceBean;
	
	private OutboundRequest outReqBean;
	
	private String statusCode;
	
	private String target;

	private Integer retryZipCodeCount = 0;

	private String stackTrace;
	
	private String QueueName;
	
	private String endpointURL;

	private String endUrl;
	
	private boolean invokeitmbo;
}
