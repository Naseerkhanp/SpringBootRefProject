package com.excelacom.century.apolloneoutbound.bean;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinePlan {

	private String linePlanId;

	private String eLineId;

	private Integer lId;

	private String whsPlan;

	private String retailPlan;

	private String startDate;

	private String endDate;

	private Integer dataLimit;

	private String isGfPlan;

	private String createdDate;

	private String modifiedDate;

	private String createdBy;

	private String modifiedBy;
	
	private String planCategory;

	private String planType;
	
	private String planGroup;

}
