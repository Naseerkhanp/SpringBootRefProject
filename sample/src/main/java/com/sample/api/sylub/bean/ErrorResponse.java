package com.excelacom.century.apolloneoutbound.bean;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ErrorResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private int code;

	private String reason;

	private List<ErrorMessage> message;

}
