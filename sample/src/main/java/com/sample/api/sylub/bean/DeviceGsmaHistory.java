package com.excelacom.century.apolloneoutbound.bean;

import java.io.Serializable;






import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class DeviceGsmaHistory implements Serializable {
	
	private static final long serialVersionUID = 1L;
	


	private String gsmaDeviceId;
	

	private String transactionId;
	
	

	private String resourceId;

	private String imei;
	
	private int lineNumber;
	

	private String colouredList;
	

	private String blackListType;
	

	private String blackListAction;
	

	private String reasonCode;
	
	private String agentId;
	
	private String status;
	

	private String gsmaInputFileName;
	

	private String gsmaOutputFileName;
	

	private String gsmaResponseStatus;
	

	private String errorCode;
	

	private String errorMessage;
	

	private String errorOccuredAt;
	
	

	private String createdBy;
	

	private String createdDate;
	

	private String modifiedBy;
	
	
	
	private String modifiedDate;
	
	
	private String reqSubmitDate;

	
	private ResourceInfo resourceInfo;
	
	

}
