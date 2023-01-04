package com.excelacom.century.apolloneoutbound.bean;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ErrorMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String responseCode;
	
	private String description;

	
}
