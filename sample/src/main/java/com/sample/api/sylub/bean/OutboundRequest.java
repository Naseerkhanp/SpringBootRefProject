package com.excelacom.century.apolloneoutbound.bean;

import java.io.Serializable;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class OutboundRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String requestJson;
	
	private String entityId;
	
	private String operationName;
	
	private String transUid;
	
	private String groupId;
	
	private String processPlanId;
	
	private String applicationName;
	
	private Integer tenantId;
	
	private String responseId;
	
	private String sourceSystem;
	
	private String targetSystem;
	
	private Long transId;
	
	private String isRetryMDNCodePresent;
	
	private Integer retryZipCodeCount = 0;
	
	private String processPlanName;
	
	private String workflowName;
	
	private String errorCode;
	
}
