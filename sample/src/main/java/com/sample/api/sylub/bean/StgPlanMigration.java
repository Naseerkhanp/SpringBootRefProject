package com.excelacom.century.apolloneoutbound.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@ToString
@Getter
@Setter
public class StgPlanMigration {
	
	private String Id;

	private String eLineId;
	
	private Long batchId;
	
	private String bcd;

	private String status;

	private String transactionId;

	private String processedStartDate;
	
	private String createdDate;

	private String createdBy;

	private String modifiedBy;

	private String modifiedDate;
	
	private String errorCode;
	
	private String errorMessage;
	
	private String mediationSyncStatus;
	
	private String processedEndDate;

	private String rollBackStatus;

}
