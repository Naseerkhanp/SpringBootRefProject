package com.excelacom.century.apolloneoutbound.bean;


import org.springframework.stereotype.Component;


@Component
public class TransactionHistory {
	
	private String transactionId;
	
	private String eLineId;
	
	private String accountNumber;
	
	private String mdn;
	
	private String imei;
	
	private String iccid;
	
	private String min;
	
	private String imsi;
	
	private String agentId;
	
	private String agentFirstName;
	
	private String agentLastName;
	
	private String agentEmailId;
	
	private String agentAddressLine1;
	
	private String agentAddressLine2;
	
	private String agentCity;
	
	private String agentState;
	
	private String agentZipCode;
	
	private String channel;
	
	private String agentPhoneNumber;
	
	private String referenceNumber;
	
	private String transactionStartDate;
	
	private String transactionEndDate;
	
	private String transactionStatus;
	
	private String createdDate;
	
	private String createdBy;
	
	private String modifiedDate;
	
	private String modifiedBy;
	
	private String transactionType;
	
	private String orderType;
	
	private String notificationStatus;
	
	private String altZipCodeInd;
	
	public String getAltZipCodeInd() {
		return altZipCodeInd;
	}

	public void setAltZipCodeInd(String altZipCodeInd) {
		this.altZipCodeInd = altZipCodeInd;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
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

	public String getMdn() {
		return mdn;
	}

	public void setMdn(String mdn) {
		this.mdn = mdn;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getIccid() {
		return iccid;
	}

	public void setIccid(String iccid) {
		this.iccid = iccid;
	}

	public String getMin() {
		return min;
	}

	public void setMin(String min) {
		this.min = min;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public String getAgentFirstName() {
		return agentFirstName;
	}

	public void setAgentFirstName(String agentFirstName) {
		this.agentFirstName = agentFirstName;
	}

	public String getAgentLastName() {
		return agentLastName;
	}

	public void setAgentLastName(String agentLastName) {
		this.agentLastName = agentLastName;
	}

	public String getAgentEmailId() {
		return agentEmailId;
	}

	public void setAgentEmailId(String agentEmailId) {
		this.agentEmailId = agentEmailId;
	}

	public String getAgentAddressLine1() {
		return agentAddressLine1;
	}

	public void setAgentAddressLine1(String agentAddressLine1) {
		this.agentAddressLine1 = agentAddressLine1;
	}

	public String getAgentAddressLine2() {
		return agentAddressLine2;
	}

	public void setAgentAddressLine2(String agentAddressLine2) {
		this.agentAddressLine2 = agentAddressLine2;
	}

	public String getAgentCity() {
		return agentCity;
	}

	public void setAgentCity(String agentCity) {
		this.agentCity = agentCity;
	}

	public String getAgentState() {
		return agentState;
	}

	public void setAgentState(String agentState) {
		this.agentState = agentState;
	}
	
	public String getAgentZipCode() {
		return agentZipCode;
	}

	public void setAgentZipCode(String agentZipCode) {
		this.agentZipCode = agentZipCode;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getAgentPhoneNumber() {
		return agentPhoneNumber;
	}

	public void setAgentPhoneNumber(String agentPhoneNumber) {
		this.agentPhoneNumber = agentPhoneNumber;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getTransactionStartDate() {
		return transactionStartDate;
	}

	public void setTransactionStartDate(String transactionStartDate) {
		this.transactionStartDate = transactionStartDate;
	}

	public String getTransactionEndDate() {
		return transactionEndDate;
	}

	public void setTransactionEndDate(String transactionEndDate) {
		this.transactionEndDate = transactionEndDate;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}
	 

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}



	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getNotificationStatus() {
		return notificationStatus;
	}

	public void setNotificationStatus(String notificationStatus) {
		this.notificationStatus = notificationStatus;
	}

	@Override
	public String toString() {
		return "TransactionHistory [transactionId=" + transactionId + ", eLineId=" + eLineId + ", accountNumber="
				+ accountNumber + ", mdn=" + mdn + ", imei=" + imei + ", iccid=" + iccid + ", min=" + min + ", imsi="
				+ imsi + ", agentId=" + agentId + ", agentFirstName=" + agentFirstName + ", agentLastName="
				+ agentLastName + ", agentEmailId=" + agentEmailId + ", agentAddressLine1=" + agentAddressLine1
				+ ", agentAddressLine2=" + agentAddressLine2 + ", agentCity=" + agentCity + ", agentState=" + agentState
				+ ", agentZipCode=" + agentZipCode + ", channel=" + channel + ", agentPhoneNumber=" + agentPhoneNumber
				+ ", referenceNumber=" + referenceNumber + ", transactionStartDate=" + transactionStartDate
				+ ", transactionEndDate=" + transactionEndDate + ", transactionStatus=" + transactionStatus
				+ ", createdDate=" + createdDate + ", createdBy=" + createdBy + ", modifiedDate=" + modifiedDate
				+ ", modifiedBy=" + modifiedBy + ", transactionType=" + transactionType + ", orderType=" + orderType
				+ ", notificationStatus=" + notificationStatus + ", altZipCodeInd=" + altZipCodeInd + "]";
	}
}
