package com.excelacom.century.apolloneoutbound.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromotionDetails {

	private static final long serialVersionUID = 1L;

	private Integer promotionId;

	private String iId;

	private String eLineId;

	private String promoID;

	private String promoType;

	private String promoAllowance;

	private String startDate;

	private String endDate;

	private String status;

	private String expiryDate;

	private String createdDate;

	private String createdBy;

	private String modifiedDate;

	private String modifiedBy;

}
