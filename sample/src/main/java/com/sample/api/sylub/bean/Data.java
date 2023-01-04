package com.excelacom.century.apolloneoutbound.bean;

import java.util.ArrayList;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Data {
	 	private String transactionId;
	    private String code;
	    private String reason;
	    private ArrayList<Message> message;
	    private ArrayList<SubOrder> subOrder;
	 //   private ArrayList<AdditionalData> additionalData;
}
