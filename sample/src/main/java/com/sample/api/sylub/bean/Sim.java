package com.excelacom.century.apolloneoutbound.bean;

import java.sql.Timestamp;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
@Component
public class Sim {

	private Integer simId;

	private String eLineId;

	private String simType;

	private String iccid;

	private String imsi;

	private String simStatus;

	private String matchingID;

	private String activationDate;

	private String deactivationDate;

	private String firstActivatedNetwork;

	private String startDate;

	private String endDate;

	private String createdDate;

	private String createdBy;

	private String modifiedBy;

	private String modifiedDate;

	private String lastUpdated;
	
	private String smdpStatus;
	
	private String lineId;

	public Integer getSimId() {
		return simId;
	}

	public void setSimId(Integer simId) {
		this.simId = simId;
	}

	public String geteLineId() {
		return eLineId;
	}

	public void seteLineId(String eLineId) {
		this.eLineId = eLineId;
	}

	public String getSimType() {
		return simType;
	}

	public void setSimType(String simType) {
		this.simType = simType;
	}

	public String getIccid() {
		return iccid;
	}

	public void setIccid(String iccid) {
		this.iccid = iccid;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getSimStatus() {
		return simStatus;
	}

	public void setSimStatus(String simStatus) {
		this.simStatus = simStatus;
	}

	public String getMatchingID() {
		return matchingID;
	}

	public void setMatchingID(String matchingID) {
		this.matchingID = matchingID;
	}

	public String getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(String activationDate) {
		this.activationDate = activationDate;
	}

	public String getDeactivationDate() {
		return deactivationDate;
	}

	public void setDeactivationDate(String deactivationDate) {
		this.deactivationDate = deactivationDate;
	}

	public String getFirstActivatedNetwork() {
		return firstActivatedNetwork;
	}

	public void setFirstActivatedNetwork(String firstActivatedNetwork) {
		this.firstActivatedNetwork = firstActivatedNetwork;
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

	public String getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	

}
