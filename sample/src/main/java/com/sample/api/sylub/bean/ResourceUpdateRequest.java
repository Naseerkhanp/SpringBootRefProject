package com.excelacom.century.apolloneoutbound.bean;

import java.util.Map;


import org.springframework.stereotype.Component;


import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
@Component
public class ResourceUpdateRequest {
	
	

	private Line lineDetails;
	
	private TransactionHistory transactionHistory;
	
	private PromotionDetails promotionDetails;
	
	private Device deviceDetails;
	
	private String oldICCID;
	
	private String oldIMEI;
	
	private Account account;
	
	private Sim simDetails;

	private LinePlan linePlan;
	
	private ReferenceValue referenceValue;

	public PromotionDetails getPromotionDetails() {
		return promotionDetails;
	}

	public void setPromotionDetails(PromotionDetails promotionDetails) {
		this.promotionDetails = promotionDetails;
	}

	public TransactionHistory getTransactionHistory() {
		return transactionHistory;
	}

	public void setTransactionHistory(TransactionHistory transactionHistory) {
		this.transactionHistory = transactionHistory;
	}

	public Line getLineDetails() {
		return lineDetails;
	}

	public void setLineDetails(Line lineDetails) {
		this.lineDetails = lineDetails;
	}

	private String transationId;

	private String transactionName;

	private LineHistory lineHistory;
	
	public LineHistory getLineHistory() {
		return lineHistory;
	}

	public void setLineHistory(LineHistory lineHistory) {
		this.lineHistory = lineHistory;
	}

	public String getTransationId() {
		return transationId;
	}
	
	public Map<String, String> getFeatureCodes() {
		return featureCodes;
	}

	private Map<String, String> featureCodes;
	
	private Map<String, String> featureMap;
	
	public String getTransactionName() {
		return transactionName;
	}
	
	public void setFeatureCodes(Map<String, String> featMap) {
		this.featureCodes = featMap;
	}

	public void setTransationId(String transationId) {
		this.transationId = transationId;
	}

	public void setTransactionName(String transactionName) {
		this.transactionName = transactionName;
	}

	@Override
	public String toString() {
		return "ResourceUpdateRequest [lineDetails=" + lineDetails + ", promotionDetails=" + promotionDetails + "]";
	}
	
	private String oldLineId;
	
	public String getOldLineId() {
		return transationId;
	}

	public void setOldLineId(String oldLineId) {
		this.oldLineId = oldLineId;
	}
	
	private String newLineId;
	
	public String getNewLineId() {
		return newLineId;
	}
	public void setNewLineId(String newLineId) {
		this.newLineId = newLineId;
	}	
	
	private String oldMdn;
	
	public String getOldMdn() {
		return oldMdn;
	}

	public void setOldMdn(String oldMdn) {
		this.oldMdn = oldMdn;
	}
	
	private String newMdn;
	
	public String getNewMdn() {
		return newMdn;
	}
	public void setNewMdn(String newMdn) {
		this.newMdn = newMdn;
	}
}
