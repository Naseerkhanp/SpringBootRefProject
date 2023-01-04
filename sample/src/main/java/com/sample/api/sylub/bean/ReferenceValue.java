package com.excelacom.century.apolloneoutbound.bean;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ReferenceValue {

	private Integer valueId;

	private Integer typeId;

	private String value;

	private String valueDesc;

	private String createdDate;

	private String createdBy;

	private String modifiedDate;

	private String modifiedBy;

}