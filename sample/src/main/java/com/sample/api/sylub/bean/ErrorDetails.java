package com.excelacom.century.apolloneoutbound.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorDetails {
	
	private String httpStatusCode;
	
	private String message;
	
	private String status;

}
