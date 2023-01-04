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
public class LineHistory {

	private int lineHistoryId;   
	
	private int  lId;
	
	private String eLineId;
	
	private String accountNumber;
	
	private String acctStatus;
	
	private  String  mdn;
	
	private String lineStatus;
	
	private String oldValue;
	
	private String newValue;
	
	private String fieldType;
	
	private String transactionType;
	
	private String  ordType;
	
	private String 	startDate;
	
	private String endDate;
	
	private int   version;
	
	private String  transactionId;
	
	private String  agentId;
	
	private Timestamp createdDate;
	
	private String createdBy;	
	
	private String modifiedBy;
	
	private String modifiedDate;
	
	@JsonProperty(value = "createdDateBeforeDays")
	private Integer createdDateBeforeDays;

	@JsonProperty(value = "queryString")
	private List<String> queryString;
	
	public int getLineHistoryId() {
		return lineHistoryId;
	}
	public void setLineHistoryId(int lineHistoryId) {
		this.lineHistoryId = lineHistoryId;
	}
	public int getlId() {
		return lId;
	}
	public void setlId(int lId) {
		this.lId = lId;
	}
	public String geteLineId() {
		return eLineId;
	}
	public void seteLineId(String eLineId) {
		this.eLineId = eLineId;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getAcctStatus() {
		return acctStatus;
	}
	public void setAcctStatus(String acctStatus) {
		this.acctStatus = acctStatus;
	}
	public  String getMdn() {
		return mdn;
	}
	public void setMdn(String mdn) {
		this.mdn = mdn;
	}
	public String getLineStatus() {
		return lineStatus;
	}
	public void setLineStatus(String lineStatus) {
		this.lineStatus = lineStatus;
	}
	public String getOldValue() {
		return oldValue;
	}
	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}
	public String getNewValue() {
		return newValue;
	}
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public String getOrdType() {
		return ordType;
	}
	public void setOrdType(String ordType) {
		this.ordType = ordType;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getAgentId() {
		return agentId;
	}
	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	
	// Added EventType for MWTGNSL-957 by Geetha Perumal
	
		private String eventType;
		
		public String geEventType() {
			return eventType;
		}
		public void setEventType(String eventType) {
			this.eventType = eventType;
		}
		
		@Override
		public String toString() {
			return "LineHistory [lineHistoryId=" + lineHistoryId + ", lId=" + lId + ", eLineId=" + eLineId
					+ ", accountNumber=" + accountNumber + ", acctStatus=" + acctStatus + ", mdn=" + mdn
					+ ", lineStatus=" + lineStatus + ", oldValue=" + oldValue + ", newValue=" + newValue
					+ ", fieldType=" + fieldType + ", transactionType=" + transactionType + ", ordType=" + ordType
					+ ", startDate=" + startDate + ", endDate=" + endDate + ", version=" + version + ", transactionId="
					+ transactionId + ", agentId=" + agentId + ", createdDate=" + createdDate + ", createdBy="
					+ createdBy + ", modifiedBy=" + modifiedBy + ", modifiedDate=" + modifiedDate
					+ ", createdDateBeforeDays=" + createdDateBeforeDays + ", queryString=" + queryString
					+ ", eventType=" + eventType + "]";
		}
	
	
	
	

	
	
	
	
	

		
}
