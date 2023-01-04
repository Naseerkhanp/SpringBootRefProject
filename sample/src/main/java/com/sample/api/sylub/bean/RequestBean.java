package com.excelacom.century.apolloneoutbound.bean;

import java.io.Serializable;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Component
public class RequestBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String request;
	
	private String source;
	
	private String destination;
	
	/** The Constant customerID. */
	private String CustomerID;

	private String CustomerRequestID;

	private String mdn;

	private String oldMDN;

	private String newMDN;

	private String hostMDN;

	private String imei;

	private String imsi;

	private String iccid;

	private String eid;
	
	private String zipCode;

	private String transId;

	private String eiccid;

	private String matchingId;

	private String referenceNumber;

	private String returnURL;

	private String asyncErrorURL;

	private String accountNumber;

	private String subscriberGroupCd;

	private String id;

	private String iccId;
	
	private String deviceId;
	
	private String planCode;
	
	private String smdp_status;
	
	private String iccid1;
	
	private String mno_activation_date;
	
	private String mno_last_updated_date;
	
	private String iccid2;
	
	private String imsi2;
	
	private String smdp_esimProfileStatus;
	
	private String smdp_last_updated_date;
	
	private String cbrs_Status;
	
	private String cbrs_last_updated_date;
	
	private String matching_Id;
	
	private String deviceid;
	
	private String userId;
	
	private String origData;
	
	private String newData;
	
	private String orderType;

	private String contextId;
	
    private String originalRefNumber;
    
    private String errorDescription;
    
    private String billCycleResetDay;
    
    private String lineId;
    
    private String accountId;
    
    private String isValid;
    
    private String make;
    
    private String model;
    
    private String mode;
    
    private String prodType;
    
    private String deviceCategory;
    
    private String cdmaLess;
    
    private String syncTransaction;
    
    private String newRatePlan;
	
	private String oldRatePlan;
	
	private String action;
	
	private String pin;
	
	private String type;
	
	private String min;
	
	private String oldDeviceId;
	
	private String newDeviceId;
	
	private String oldIccid;
	
	private String hotlineType;
	
	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public String getBillCycleResetDay() {
		return billCycleResetDay;
	}

	public void setBillCycleResetDay(String billCycleResetDay) {
		this.billCycleResetDay = billCycleResetDay;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	public String getOriginalRefNumber() {
		return originalRefNumber;
	}

	public void setOriginalRefNumber(String originalRefNumber) {
		this.originalRefNumber = originalRefNumber;
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}	
	
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOrigData() {
		return origData;
	}

	public void setOrigData(String origData) {
		this.origData = origData;
	}

	public String getNewData() {
		return newData;
	}

	public void setNewData(String newData) {
		this.newData = newData;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getDeviceid() {
		return deviceid;
	}

	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}

	public String getMatching_Id() {
		return matching_Id;
	}

	public void setMatching_Id(String matching_Id) {
		this.matching_Id = matching_Id;
	}

	public String getSmdp_status() {
		return smdp_status;
	}

	public void setSmdp_status(String smdp_status) {
		this.smdp_status = smdp_status;
	}

	public String getIccid1() {
		return iccid1;
	}

	public void setIccid1(String iccid1) {
		this.iccid1 = iccid1;
	}

	public String getMno_activation_date() {
		return mno_activation_date;
	}

	public void setMno_activation_date(String mno_activation_date) {
		this.mno_activation_date = mno_activation_date;
	}

	public String getMno_last_updated_date() {
		return mno_last_updated_date;
	}

	public void setMno_last_updated_date(String mno_last_updated_date) {
		this.mno_last_updated_date = mno_last_updated_date;
	}

	public String getIccid2() {
		return iccid2;
	}

	public void setIccid2(String iccid2) {
		this.iccid2 = iccid2;
	}

	public String getImsi2() {
		return imsi2;
	}

	public void setImsi2(String imsi2) {
		this.imsi2 = imsi2;
	}

	public String getSmdp_esimProfileStatus() {
		return smdp_esimProfileStatus;
	}

	public void setSmdp_esimProfileStatus(String smdp_esimProfileStatus) {
		this.smdp_esimProfileStatus = smdp_esimProfileStatus;
	}

	public String getSmdp_last_updated_date() {
		return smdp_last_updated_date;
	}

	public void setSmdp_last_updated_date(String smdp_last_updated_date) {
		this.smdp_last_updated_date = smdp_last_updated_date;
	}

	public String getCbrs_Status() {
		return cbrs_Status;
	}

	public void setCbrs_Status(String cbrs_Status) {
		this.cbrs_Status = cbrs_Status;
	}

	public String getCbrs_last_updated_date() {
		return cbrs_last_updated_date;
	}

	public void setCbrs_last_updated_date(String cbrs_last_updated_date) {
		this.cbrs_last_updated_date = cbrs_last_updated_date;
	}

	public String getPlanCode() {
		return planCode;
	}

	public void setPlanCode(String planCode) {
		this.planCode = planCode;
	}
	
	public String getOldMDN() {
		return oldMDN;
	}

	public void setOldMDN(String oldMDN) {
		this.oldMDN = oldMDN;
	}

	public String getNewMDN() {
		return newMDN;
	}

	public void setNewMDN(String newMDN) {
		this.newMDN = newMDN;
	}

	public String getHostMDN() {
		return hostMDN;
	}

	public void setHostMDN(String hostMDN) {
		this.hostMDN = hostMDN;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getMatchingId() {
		return matchingId;
	}

	public void setMatchingId(String matchingId) {
		this.matchingId = matchingId;
	}

	public String getTransId() {
		return transId;
	}

	public void setTransId(String transId) {
		this.transId = transId;
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

	public String getEid() {
		return eid;
	}

	public void setEid(String eid) {
		this.eid = eid;
	}

	public String getCustomerID() {
		return CustomerID;
	}

	public void setCustomerID(String customerID) {
		CustomerID = customerID;
	}

	public String getCustomerRequestID() {
		return CustomerRequestID;
	}

	public void setCustomerRequestID(String customerRequestID) {
		this.CustomerRequestID = customerRequestID;
	}

	public String getEiccid() {
		return eiccid;
	}

	public void setEiccid(String eiccid) {
		this.eiccid = eiccid;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getReturnURL() {
		return returnURL;
	}

	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}

	public String getAsyncErrorURL() {
		return asyncErrorURL;
	}

	public void setAsyncErrorURL(String asyncErrorURL) {
		this.asyncErrorURL = asyncErrorURL;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getSubscriberGroupCd() {
		return subscriberGroupCd;
	}

	public void setSubscriberGroupCd(String subscriberGroupCd) {
		this.subscriberGroupCd = subscriberGroupCd;
	}
		
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIccId() {
		return iccId;
	}

	public void setIccId(String iccId) {
		this.iccId = iccId;
	}
	
	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	@Override
	public String toString() {
		return "RequestBean [request=" + request + ", source=" + source + ", destination=" + destination
				+ ", CustomerID=" + CustomerID + ", CustomerRequestID=" + CustomerRequestID + ", mdn=" + mdn
				+ ", oldMDN=" + oldMDN + ", newMDN=" + newMDN + ", hostMDN=" + hostMDN + ", imei=" + imei + ", imsi="
				+ imsi + ", iccid=" + iccid + ", eid=" + eid + ", transId=" + transId + ", eiccid=" + eiccid
				+ ", matchingId=" + matchingId + ", referenceNumber=" + referenceNumber + ", returnURL=" + returnURL
				+ ", asyncErrorURL=" + asyncErrorURL + ", accountNumber=" + accountNumber + ", subscriberGroupCd="
				+ subscriberGroupCd + ", id=" + id + ", iccId=" + iccId + ", deviceId=" + deviceId + ", planCode="
				+ planCode + ", smdp_status=" + smdp_status + ", iccid1=" + iccid1 + ", mno_activation_date="
				+ mno_activation_date + ", mno_last_updated_date=" + mno_last_updated_date + ", iccid2=" + iccid2
				+ ", imsi2=" + imsi2 + ", smdp_esimProfileStatus=" + smdp_esimProfileStatus
				+ ", smdp_last_updated_date=" + smdp_last_updated_date + ", cbrs_Status=" + cbrs_Status
				+ ", cbrs_last_updated_date=" + cbrs_last_updated_date + ", matching_Id=" + matching_Id + ", deviceid="
				+ deviceid + ", userId=" + userId + ", origData=" + origData + ", newData=" + newData + ", orderType="
				+ orderType + ", contextId=" + contextId + ", originalRefNumber=" + originalRefNumber
				+ ", errorDescription=" + errorDescription + ", billCycleResetDay=" + billCycleResetDay + ", lineId="
				+ lineId + ", accountId=" + accountId + ", isValid=" + isValid + ", make=" + make + ", model=" + model
				+ ", mode=" + mode + ", prodType=" + prodType + ", deviceCategory=" + deviceCategory + ", cdmaLess="
				+ cdmaLess + ", syncTransaction=" + syncTransaction + ", newRatePlan=" + newRatePlan + ", oldRatePlan="
				+ oldRatePlan + ", action=" + action + ", pin=" + pin + ", type=" + type + ", min=" + min
				+ ", oldDeviceId=" + oldDeviceId + ", newDeviceId=" + newDeviceId + ", zipCode=" + zipCode + ", oldIccid=" + oldIccid
				+ ", hotlineType=" + hotlineType + "]";
	}

}
