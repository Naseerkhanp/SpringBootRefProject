package com.excelacom.century.apolloneoutbound.bean;

import java.io.Serializable;
import java.util.Map;

import org.json.JSONObject;

import com.excelacom.century.apolloneoutbound.utils.constants.ApolloNEConstants;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class RcsIntegrationServiceBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ncmOutboundServiceName;

	private String operationName;

	private String userName;

	private String passWord;

	private boolean auth;

	private String authorization;
	
	private String request;

	private String transcationId;

	private String iccid;

	private EHttpMethods httpMethod;

	private String ncmSouthBoundUrl;

	private JSONObject requestInfo;

	private JSONObject pathInfo;
	
	private String searchEnvFlag;
	
	private String endUrl;

	public String getRequestJson() {
		if (this.httpMethod != null) {
			switch (this.httpMethod) {
			/*
			 * case GET: return this.requestInfo.toString();
			 */
			case POST:
				if (requestInfo != null) {
					return this.requestInfo.toString();
				} else {
					return ApolloNEConstants.EMPTY;
				}
			case PUT:
				if (requestInfo != null) {
					return this.requestInfo.toString().concat(ApolloNEConstants.COMMA).concat(this.pathInfo.toString());
				} else {
					return ApolloNEConstants.EMPTY;
				}

			case DELETE:
				if (requestInfo != null) {
					return this.pathInfo.toString();
				} else {
					return ApolloNEConstants.EMPTY;
				}

			default:
				return ApolloNEConstants.EMPTY;
			}
		} else {
			return this.requestInfo.toString();
		}
	}

	private Map<String, String> headerMap;

}
