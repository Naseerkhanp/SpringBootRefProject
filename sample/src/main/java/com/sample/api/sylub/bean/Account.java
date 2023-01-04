package com.excelacom.century.apolloneoutbound.bean;

import java.sql.Timestamp;

import org.springframework.stereotype.Component;

@Component
public class Account {

	private Integer accountId;

	private String accountNumber;

	private String contextId;

	private String accountType;

	private String accountStatus;

	private String pin;

	private String extAccountId;

	private String extAccountStatus;

	private String subgroupcd;
	public String getSubgroupcd() {
		return subgroupcd;
	}
	public void setSubgroupcd(String subgroupcd) {
		this.subgroupcd = subgroupcd;
	}

	private String createdDate;

	private String createdBy;

	private String modifiedDate;

	private String modifiedBy;

	private String isMigrated;

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getExtAccountId() {
		return extAccountId;
	}

	public void setExtAccountId(String extAccountId) {
		this.extAccountId = extAccountId;
	}

	public String getExtAccountStatus() {
		return extAccountStatus;
	}

	public void setExtAccountStatus(String extAccountStatus) {
		this.extAccountStatus = extAccountStatus;
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

	public String getIsMigrated() {
		return isMigrated;
	}

	public void setIsMigrated(String isMigrated) {
		this.isMigrated = isMigrated;
	}

	@Override
	public String toString() {
		return "Account [accountId=" + accountId + ", accountNumber=" + accountNumber + ", contextId=" + contextId
				+ ", accountType=" + accountType + ", accountStatus=" + accountStatus + ", pin=" + pin
				+ ", extAccountId=" + extAccountId + ", extAccountStatus=" + extAccountStatus + ", subgroupcd="
				+ subgroupcd + ", createdDate=" + createdDate + ", createdBy=" + createdBy + ", modifiedDate="
				+ modifiedDate + ", modifiedBy=" + modifiedBy + ", isMigrated=" + isMigrated + "]";
	}

}
