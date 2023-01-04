package com.excelacom.century.apolloneoutbound.bean;

import java.sql.Timestamp;
import java.util.List;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class NpaNxx {

	private int refNpaNxxPrgId;   
	
	private int  ngaNxxNum;
	
	private String ngpId;
	
	private String rateCenter;
	
	private String state;
	
	private  String  zipCode;
	
	private Timestamp createdDate;
	
	private String createdBy;
	
	private Timestamp modifiedDate;
	
	private String modifiedBy;

	@JsonProperty(value = "queryString")
	private List<String> queryString;
	
	@Override
	public String toString() {
		return "NpaNxx [refNpaNxxPrgId=" + refNpaNxxPrgId + ", ngaNxxNum=" + ngaNxxNum + ", ngpId=" + ngpId
				+ ", rateCenter=" + rateCenter + ", state=" + state + ", zipCode=" + zipCode + ", createdDate="
				+ createdDate + ", createdBy=" + createdBy + ", modifiedDate=" + modifiedDate + ", modifiedBy="
				+ modifiedBy + "]";
	}
}
