package com.excelacom.century.apolloneoutbound.bean;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OfferingDetails {

	private String serviceId;

	private String planCategory;
	
	private String planSubcategory;

	private String planGroup;

	private List<IncludedFeature> includedFeature;

	@Component
	@Getter
	@Setter
	public class IncludedFeature {

		private String includedWithPlan;

		private String featureCodes;

		private String subscribe;

	}
}
