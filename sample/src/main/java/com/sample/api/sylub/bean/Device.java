package com.excelacom.century.apolloneoutbound.bean;
import java.sql.Timestamp;

import org.springframework.stereotype.Component;

@Component
public class Device {
	
	private int deviceId;
	
	private int lId;
	
	private String eLineId;
	
	private String imei;
	
	private String make;
	
	private String model;
	
	private String modeValue;
	
	private String cdmaless;
	
	private String euiccId;
	
	private String eid;
	
	private String deviceType;
	
	private String equipmentType;
	
	private String os;
	
	private String deviceName;
	
	private String macAddress;
	
	private String isByod;
	
	private String euiccCapable;
	
	private String startDate;
	
	private String endDate;
	
	private String createdDate;
	
	private String createdBy;
		
	private String modifiedBy;
	
	private String modifiedDate;
	
	private String eligibilityCode;

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
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

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getModeValue() {
		return modeValue;
	}

	public void setModeValue(String modeValue) {
		this.modeValue = modeValue;
	}

	public String getCdmaLess() {
		return cdmaless;
	}

	public void setCdmaLess(String cdmaless) {
		this.cdmaless = cdmaless;
	}

	public String getEuiccId() {
		return euiccId;
	}

	public void setEuiccId(String euiccId) {
		this.euiccId = euiccId;
	}

	public String getEid() {
		return eid;
	}

	public void setEid(String eid) {
		this.eid = eid;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getEquipmentType() {
		return equipmentType;
	}

	public void setEquipmentType(String equipmentType) {
		this.equipmentType = equipmentType;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getIsByod() {
		return isByod;
	}

	public void setIsByod(String isByod) {
		this.isByod = isByod;
	}

	public String getEuiccCapable() {
		return euiccCapable;
	}

	public void setEuiccCapable(String euiccCapable) {
		this.euiccCapable = euiccCapable;
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

	public String getEligibilityCode() {
		return startDate;
	}

	public void setEligibilityCode(String eligibilityCode) {
		this.eligibilityCode = eligibilityCode;
	}
	
	@Override
	public String toString() {
		return "Device [deviceId=" + deviceId + ", lId=" + lId + ", eLineId=" + eLineId + ", imei=" + imei + ", make="
				+ make + ", model=" + model + ", modeValue=" + modeValue + ", cdmaLess=" + cdmaless + ", euiccId="
				+ euiccId + ", eid=" + eid + ", deviceType=" + deviceType + ", equipmentType=" + equipmentType + ", os="
				+ os + ", deviceName=" + deviceName + ", macAddress=" + macAddress + ", isByod=" + isByod
				+ ", euiccCapable=" + euiccCapable + ", startDate=" + startDate + ", endDate=" + endDate
				+ ", createdDate=" + createdDate + ", createdBy=" + createdBy + ", modifiedBy=" + modifiedBy
				+ ", modifiedDate=" + modifiedDate + "]";
	}	

}
