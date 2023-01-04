package com.excelacom.century.apolloneoutbound.bean;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Builder
public class SubOrder {
	private String refcode;
	private String responsestatus;
	private String deviceid;
	private String partnerid;
	private String branchid;
	private String recordidentifier;
	private String blackliststatus;
	private String greyliststatus;
	private ArrayList<Imeihistory> imeihistory;
	private String manufacturer;
	private String brandname;
	private String marketingname;
	private String modelname;
	private String band;
	private String operatingsys;
	private String nfc;
	private String bluetooth;
	@JsonProperty("WLAN")
	private String WLAN;
	private String devicetype;
}
