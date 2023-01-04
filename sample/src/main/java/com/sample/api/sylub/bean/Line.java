package com.excelacom.century.apolloneoutbound.bean;

import java.sql.Timestamp;

import org.springframework.stereotype.Component;

@Component
public class Line {

	private String lineId;
	
	private String acctId;
	
	private String accountNumber;
	
	private String eLineId;
	
	private String mdn;
	
	private String lineStatus;
	
	private String lteStatus;
	
	private String lineName;
	
	private String activationDate;
	
	private String hostMdn;
	
	private String deactivationDate;
	
	private String min;
	
	private String bcd;
	
	private String hotlineType;
	
	private String isPorted;
	
	private String lineType;
	
	private String serviceType;
	
	private String referenceNumber;
	
	private String inflightTransStatus;
	
	private String speedRedudecFlag;
	
	private String isGfPlan;
	
	private String createdDate;
	
	private String createdBy;
		
	private String modifiedBy;
	
	private String modifiedDate;
	
	private String isMigrated;
	
	private String rcsEligibileStatus;
	
	private String syncTransaction;
	
	private String migStatus;
	
	public String getMigStatus() {
		return migStatus;
	}

	public void setMigStatus(String migStatus) {
		this.migStatus = migStatus;
	}

	public String getRcsEligibileStatus() {
		return rcsEligibileStatus;
	}

	public void setRcsEligibileStatus(String rcsEligibileStatus) {
		this.rcsEligibileStatus = rcsEligibileStatus;
	}
	
	public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public String getAcctId() {
		return acctId;
	}

	public void setAcctId(String acctId) {
		this.acctId = acctId;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String geteLineId() {
		return eLineId;
	}

	public void seteLineId(String eLineId) {
		this.eLineId = eLineId;
	}

	public String getMdn() {
		return mdn;
	}

	public void setMdn(String string) {
		this.mdn = string;
	}

	public String getLineStatus() {
		return lineStatus;
	}

	public void setLineStatus(String lineStatus) {
		this.lineStatus = lineStatus;
	}

	public String getLteStatus() {
		return lteStatus;
	}

	public void setLteStatus(String lteStatus) {
		this.lteStatus = lteStatus;
	}

	public String getLineName() {
		return lineName;
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}

	public String getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(String activationDate) {
		this.activationDate = activationDate;
	}

	public String getHostMdn() {
		return hostMdn;
	}

	public void setHostMdn(String string) {
		this.hostMdn = string;
	}

	public String getDeactivationDate() {
		return deactivationDate;
	}

	public void setDeactivationDate(String deactivationDate) {
		this.deactivationDate = deactivationDate;
	}

	public String getMin() {
		return min;
	}

	public void setMin(String string) {
		this.min = string;
	}

	public String getBcd() {
		return bcd;
	}

	public void setBcd(String string) {
		this.bcd = string;
	}

	public String getHotlineType() {
		return hotlineType;
	}

	public void setHotlineType(String hotlineType) {
		this.hotlineType = hotlineType;
	}

	public String getIsPorted() {
		return isPorted;
	}

	public void setIsPorted(String isPorted) {
		this.isPorted = isPorted;
	}

	public String getLineType() {
		return lineType;
	}

	public void setLineType(String lineType) {
		this.lineType = lineType;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getInflightTransStatus() {
		return inflightTransStatus;
	}

	public void setInflightTransStatus(String inflightTransStatus) {
		this.inflightTransStatus = inflightTransStatus;
	}

	public String getSpeedRedudecFlag() {
		return speedRedudecFlag;
	}

	public void setSpeedRedudecFlag(String speedRedudecFlag) {
		this.speedRedudecFlag = speedRedudecFlag;
	}

	public String getIsGfPlan() {
		return isGfPlan;
	}

	public void setIsGfPlan(String isGfPlan) {
		this.isGfPlan = isGfPlan;
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

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getIsMigrated() {
		return isMigrated;
	}

	public void setIsMigrated(String isMigrated) {
		this.isMigrated = isMigrated;
	}

	public String getSyncTransaction() {
		return syncTransaction;
	}

	public void setSyncTransaction(String syncTransaction) {
		this.syncTransaction = syncTransaction;
	}

	@Override
	public String toString() {
		return "Line [lineId=" + lineId + ", acctId=" + acctId + ", accountNumber=" + accountNumber + ", eLineId=" + eLineId
				+ ", mdn=" + mdn + ", lineStatus=" + lineStatus + ", lteStatus=" + lteStatus + ", lineName=" + lineName
				+ ", activationDate=" + activationDate + ", hostMdn=" + hostMdn + ", deactivationDate="
				+ deactivationDate + ", min=" + min + ", bcd=" + bcd + ", hotlineType=" + hotlineType + ", isPorted="
				+ isPorted + ", lineType=" + lineType + ", serviceType=" + serviceType + ", referenceNumber="
				+ referenceNumber + ", inflightTransStatus=" + inflightTransStatus + ", speedRedudecFlag="
				+ speedRedudecFlag + ", isGfPlan=" + isGfPlan + ", createdDate=" + createdDate + ", createdBy="
				+ createdBy + ", modifiedBy=" + modifiedBy + ", modifiedDate=" + modifiedDate + ", isMigrated="
				+ isMigrated + "]";
	}

	
	
}
