package com.excelacom.century.apolloneoutbound.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Feature  implements Serializable{
	private static final long serialVersionUID = 1L;


	@JsonProperty(value = "featureId")
	private Integer featureId;
	
	
	@JsonProperty(value = "lId")
	private Integer lId;
	
	
	@JsonProperty(value = "linePlanId")
	 private Integer linePlanId;
	
	
	@JsonProperty(value = "eLineId")
	 private  String  eLineId;
	 
	
	@JsonProperty(value = "featureCodes")
	 private String featureCodes;
	

	@JsonProperty(value = "startDate")
	 private Timestamp startDate;
	
	
	@JsonProperty(value = "endDate")
	private Timestamp  endDate;
	 
	 
	 
	 @JsonProperty(value = "createdBy")
	 private String createdBy;

		
		@JsonProperty(value = "createdDate")
		private Timestamp createdDate;
		
		
		@JsonProperty(value = "modifiedBy")
		private String modifiedBy;
	
		@JsonProperty(value = "modifiedDate")
		private Timestamp modifiedDate;
	
	    
		@JsonProperty(value = "isActive")
		private String isActive;
	 
		@JsonProperty(value = "includedWithPlan")
		private String includedWithPlan;
	
}
	
	