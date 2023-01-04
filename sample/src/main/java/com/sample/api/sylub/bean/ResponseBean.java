package com.excelacom.century.apolloneoutbound.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseBean {

	private Data data;

	@Builder
	@Getter
	@Setter
	@ToString
	public static class Data {

		private String imsi;

		private List<Mdn> mdn;

		private List<Iccid> simId;
	}

	@Builder
	@Getter
	@Setter
	@ToString
	public static class Mdn {

		private String type;

		private String value;

	}

	@Builder
	@Getter
	@Setter
	@ToString
	public static class Iccid {

		private String type;

		private String value;

	}
}
