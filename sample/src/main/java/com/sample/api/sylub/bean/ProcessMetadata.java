package com.excelacom.century.apolloneoutbound.bean;

import java.io.Serializable;
import java.time.ZonedDateTime;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@ToString
@EqualsAndHashCode
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ProcessMetadata implements Serializable {
	
	private static final long serialVersionUID = 3245598052741951577L;
	

	private String transactionId;
	
	private String rootTransactionId;

	private String transactionType;
	

	private String processData;
	

	private String createdDate;


	private String createdBy;

	private String modifiedDate;
	

	private String modifiedBy;
	
}