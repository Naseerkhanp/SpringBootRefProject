package com.excelacom.century.apolloneoutbound.bean;

import java.sql.Timestamp;

import org.springframework.stereotype.Component;

@Component
public class Promotion {
	
	private Integer promotionId;
	
	private Integer lId;
	
	private String eLineId;
	
	private String promoId;
	
	private String promoType;
	
	private String promoAllowance;
	
	private String startDate;
	
	private String endDate;
	
	private String status;
	
	private String expiredDate;
	
	private String createdDate;
	
	private String createdBy;

	private String modifiedBy;
	
	private Timestamp modifiedDate;
	
	public Integer getPromotionId() {
		return promotionId;
	}



	public void setPromotionId(Integer promotionId) {
		this.promotionId = promotionId;
	}



	public Integer getlId() {
		return lId;
	}



	public void setlId(Integer lId) {
		this.lId = lId;
	}



	public String geteLineId() {
		return eLineId;
	}



	public void seteLineId(String eLineId) {
		this.eLineId = eLineId;
	}



	public String getPromoId() {
		return promoId;
	}



	public void setPromoId(String promoId) {
		this.promoId = promoId;
	}



	public String getPromoType() {
		return promoType;
	}



	public void setPromoType(String promoType) {
		this.promoType = promoType;
	}



	public String getPromoAllowance() {
		return promoAllowance;
	}



	public void setPromoAllowance(String promoAllowance) {
		this.promoAllowance = promoAllowance;
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



	public String getStatus() {
		return status;
	}



	public void setStatus(String status) {
		this.status = status;
	}



	public String getExpiredDate() {
		return expiredDate;
	}



	public void setExpiredDate(String expiredDate) {
		this.expiredDate = expiredDate;
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



	public Timestamp getModifiedDate() {
		return modifiedDate;
	}



	public void setModifiedDate(Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
		
	
	@Override
	public String toString() {
		return "Account [promotionId="+promotionId+",lId="+lId+",eLineId="+eLineId+",promoId="+promoId+",promoType="+promoType+",promoAllowance="+promoAllowance+",startDate="+startDate+",endDate="+endDate+",status="+status+",expiredDate="+expiredDate+",createdDate="+createdDate+",createdBy="
				+ createdBy + ",, modifiedBy=" + modifiedBy + ",modifiedDate="+modifiedDate+"]";
	}

	
}
