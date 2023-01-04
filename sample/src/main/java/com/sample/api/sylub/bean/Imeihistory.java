package com.excelacom.century.apolloneoutbound.bean;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@Builder
@ToString
public class Imeihistory {
		private String action;
	    private String date;
	    private String by;
	   // @JsonProperty("Country") 
	    private String Country;
}
