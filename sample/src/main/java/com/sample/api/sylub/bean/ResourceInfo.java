package com.excelacom.century.apolloneoutbound.bean;

import java.io.Serializable;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class ResourceInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	
	private Integer resourceId;

	private String resourceType;

	private String resourceValue;
	
	private String availability;

	private String status;

	private String allocatedDate;

	private String releasedDate;

	private String externalValue1;

	private String createdBy;

	private String createdDate;

	private String modifiedBy;

	private String modifiedDate;

	private String resourceSubtype;
	
	private String isBlacklist;
	
	private String blacklistType;
	

}