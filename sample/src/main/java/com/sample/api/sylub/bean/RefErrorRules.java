package com.excelacom.century.apolloneoutbound.bean;

import java.io.Serializable;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@ToString
@EqualsAndHashCode
@Getter
@Setter
@JsonSerialize
public class RefErrorRules implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@JsonProperty(value = "errorRuleId")
	private Integer errorRuleId;
	
	
	@JsonProperty(value = "errorCode")
	private String errorCode;

	
	@JsonProperty(value = "errorMessage")
	private String errorMessage;

	
	@JsonProperty(value = "httpStatusCode")
	private String httpStatusCode;

	
	@JsonProperty(value = "actionToExecute")
	private String actionToExecute;

	
	@JsonProperty(value = "retryLimit")
	private String retryLimit;

	
	@JsonProperty(value = "ruleDetails")
	private String ruleDetails;

	

}
