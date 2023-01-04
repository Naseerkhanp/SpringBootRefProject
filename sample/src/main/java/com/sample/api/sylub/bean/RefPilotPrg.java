package com.excelacom.century.apolloneoutbound.bean;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class RefPilotPrg{
	
	private String imei;
	
	private String pilotProgram;

	private String description;

	private String createdDate;

	private String createdBy;
	
	private String modifiedDate;

	private String modifiedBy;
	
	

}
