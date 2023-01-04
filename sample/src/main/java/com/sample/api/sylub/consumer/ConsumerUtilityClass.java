package com.excelacom.century.apolloneoutbound.consumer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.excelacom.century.apolloneoutbound.bean.EHttpMethods;
import com.excelacom.century.apolloneoutbound.bean.ErrorCodes;
import com.excelacom.century.apolloneoutbound.bean.OutboundRequest;
import com.excelacom.century.apolloneoutbound.bean.RcsIntegrationServiceBean;
import com.excelacom.century.apolloneoutbound.bean.SendClientRequest;
import com.excelacom.century.apolloneoutbound.dao.RcsDao;
import com.excelacom.century.apolloneoutbound.entity.ServiceInfo;
import com.excelacom.century.apolloneoutbound.exception.OutboundRetryException;
import com.excelacom.century.apolloneoutbound.properties.ApolloNEQueueProperties;
import com.excelacom.century.apolloneoutbound.utils.constants.ApolloNEConstants;
import com.excelacom.century.apolloneoutbound.utils.constants.CommonConstants;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class ConsumerUtilityClass {
	@Autowired
	private ApolloNEQueueProperties properties;

	@Autowired
	private ApolloNEQueueProperties apolloNEQueueProperties;

	@Autowired
	private RcsDao rcsDao;

	@Autowired
	private RestTemplate restTemplate;

	public SendClientRequest sendMessageToClient(SendClientRequest sendClientRequest) throws JSONException,
			URISyntaxException, InterruptedException, ExecutionException, NoSuchAlgorithmException,
			KeyManagementException, IOException, UnknownHostException, ResourceAccessException, OutboundRetryException {
		log.debug("Inside operationName::" + sendClientRequest.getOperationName());
		String outputString = CommonConstants.EMPTYSTRING;
		String statusCode = "500";
		ResponseEntity<String> response = null;
		String endUrl = CommonConstants.EMPTYSTRING;
		String server = properties.getServer();
		URL url;
		BufferedReader in = null;
		InputStreamReader isr = null;
		String soapAction = CommonConstants.EMPTYSTRING;
		String contextId = "";
		String tokenCode = "";
		if (sendClientRequest.isInvokeitmbo()) {
			List<Map<String, Object>> mboCredentialDetails = null;
			String mboOauthClientUrl = CommonConstants.EMPTYSTRING;
			String mboOauthClientSecret = CommonConstants.EMPTYSTRING;
			String mboOauthClientId = CommonConstants.EMPTYSTRING;
			endUrl = sendClientRequest.getEndUrl();
			String request = sendClientRequest.getRcsServiceBean().getRequest();
			try {
				contextId = getContextId(sendClientRequest.getDataMap().get("initRequest"));
				log.info("contextId:: " + contextId);
				if (contextId == null || "".equalsIgnoreCase(contextId)) {
					contextId = rcsDao.getContextFromReturnUrl(endUrl);
				}

				mboCredentialDetails = rcsDao.mboCredentialDetails(server, contextId);
				log.info("Inside mboOauthClientUrl:: " + mboCredentialDetails.toString());

				if (mboCredentialDetails != null && mboCredentialDetails.size() > 0) {
					for (int i = 0; i < mboCredentialDetails.size(); i++) {
						mboOauthClientUrl = (String) mboCredentialDetails.get(i).get("SERVICE_URL");
						mboOauthClientId = (String) mboCredentialDetails.get(i).get("service_request");
						mboOauthClientSecret = (String) mboCredentialDetails.get(i).get("service_description");
					}
				}

			} catch (Exception e) {
				log.error("ErrorCode:: " + ErrorCodes.CECC0008 + " : Error getting contextId details::", e);
				throw new OutboundRetryException("ContextId Configuration missing");
			}
			log.info("Inside mboOauthClientUrl:: " + mboOauthClientUrl + "- mboOauthClientId ::" + mboOauthClientId
					+ " - mboOauthClientSecret :: " + mboOauthClientSecret);
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} };

			// Install the all-trusting trust manager
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

			url = new URL(mboOauthClientUrl);
			List<NameValuePair> tokenRequest = new ArrayList<NameValuePair>();
			tokenRequest.add(new BasicNameValuePair("client_id", mboOauthClientId));
			tokenRequest.add(new BasicNameValuePair("client_secret", mboOauthClientSecret));
			tokenRequest.add(new BasicNameValuePair("grant_type", "client_credentials"));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("charset", "utf-8");
			conn.setConnectTimeout(10000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			OutputStream os = conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
			String token = getQuery(tokenRequest);
			writer.write(token);
			writer.flush();
			writer.close();
			os.close();
			InputStream is;
			if (conn.getResponseCode() >= 400) {
				is = conn.getErrorStream();
			} else {
				is = conn.getInputStream();
			}
			isr = new InputStreamReader(is);
			in = new BufferedReader(isr);
			String responseString = null;
			String tokenResponse = CommonConstants.EMPTYSTRING;
			while ((responseString = in.readLine()) != null) {
				tokenResponse = tokenResponse + responseString;
			}
			if (!"".equalsIgnoreCase(tokenResponse)) {
				JSONObject obj = new JSONObject(tokenResponse);
				tokenCode = obj.getString("access_token");
			}
			if (!StringUtils.hasText(tokenCode)) {
				log.error("ErrorCode:: " + ErrorCodes.CECC0008 + " : Unable to GET MBO Token");
				throw new OutboundRetryException("Unable to GET MBO Token");
			}
			if (!"".equalsIgnoreCase(tokenCode)) {
				sendClientRequest.setEndpointURL(endUrl);
				url = new URL(endUrl);
				HttpURLConnection serviceConn = (HttpURLConnection) url.openConnection();
				serviceConn.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON_VALUE);
				serviceConn.setRequestProperty("Accept", MediaType.APPLICATION_JSON_VALUE);
				serviceConn.setRequestProperty("SOAPAction", soapAction);
				serviceConn.setRequestProperty("Authorization", "Bearer " + tokenCode);
				serviceConn.setConnectTimeout(10000);
				serviceConn.setDoOutput(true);
				serviceConn.setDoInput(true);
				OutputStream out = serviceConn.getOutputStream();
				BufferedWriter writer1 = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
				out.write(request.getBytes());
				out.close();
				writer1.flush();
				writer1.close();
				BufferedReader reader = null;
				log.info("serviceConn.getResponseCode()::" + serviceConn.getResponseCode());
				if (serviceConn.getResponseCode() == 200) {
					reader = new BufferedReader(new InputStreamReader(serviceConn.getInputStream()));
				} else {
					reader = new BufferedReader(new InputStreamReader(serviceConn.getErrorStream()));
				}
				String res = IOUtils.toString(reader);
				log.info("response from ITMBO ::" + res);
				statusCode = String.valueOf(serviceConn.getResponseCode());
				outputString = res + "~" + serviceConn.getResponseCode() + "~" + endUrl;
				log.info("outputString for itmbo call sendMessageToClient::" + outputString + "::statusCode0::"
						+ statusCode);
			}

		} else {
			String endPointUrl = rcsDao.getEndpointUrl(sendClientRequest.getDataMap().get("EndpointURL"),
					apolloNEQueueProperties.getServer());
			log.debug("endPointUrl :: " + endPointUrl);
			if (StringUtils.hasText(endPointUrl)) {

				if (endPointUrl.contains("${")) {

					endPointUrl = getURL(sendClientRequest.getRcsServiceBean().getRequest(), endPointUrl);
					if (endPointUrl.contains("${")) {
						endPointUrl = getResponseFromGetClient(endPointUrl,
								sendClientRequest.getRcsServiceBean().getRequest(), sendClientRequest.getDataMap(),
								sendClientRequest.getOperationName(), sendClientRequest.getResponseId());
					}
					// endPointUrl = apolloNEService.formatUrlForPathParam(endPointUrl, pathMap);
					sendClientRequest.getRcsServiceBean().setNcmSouthBoundUrl(endPointUrl);
					sendClientRequest.getRcsServiceBean().setHttpMethod(EHttpMethods.valueOf(CommonConstants.GET));
					log.debug("endPointUrl ::" + endPointUrl);
					log.debug("getTransId ::" + sendClientRequest.getOutReqBean().getTransId());
					rcsDao.updateRequestDetails(endPointUrl, sendClientRequest.getOutReqBean().getTransId());
				} else {
					sendClientRequest.getRcsServiceBean().setNcmSouthBoundUrl(endPointUrl);
					sendClientRequest.getRcsServiceBean().setHttpMethod(EHttpMethods.valueOf(CommonConstants.POST));
				}
				Map<String, String> headerMap = getHeaderInfo(sendClientRequest.getRcsServiceBean().getRequest(),
						sendClientRequest.getRcsServiceBean().getTranscationId());
				if (headerMap != null) {
					sendClientRequest.getRcsServiceBean().setHeaderMap(headerMap);
				}
				log.debug("rcsServiceBean.getHttpMethod() :: " + sendClientRequest.getRcsServiceBean().getHttpMethod());
				sendClientRequest.setEndpointURL(endPointUrl);
				response = callExtSysForQueue(sendClientRequest.getRcsServiceBean(), sendClientRequest.getOutReqBean());
				log.debug("response::" + response.toString());
				if (response != null) {
					statusCode = String.valueOf(response.getStatusCode().value());
				}
				if (response != null && response.getBody() != null) {
					outputString = response.getBody();

				}
				outputString = outputString + "~" + statusCode + "~" + endPointUrl;

				log.info("outputString for sendMessageToClient::" + outputString + "::statusCode0::" + statusCode);

			}
		}
		if (StringUtils.hasText(statusCode) && statusCode.startsWith("2")) {
			sendClientRequest.setTransactionStatus(CommonConstants.SUCCESS);
		} else {
			sendClientRequest.setTransactionStatus(CommonConstants.FAILURE);
		}
		sendClientRequest.setResponse(outputString);
		sendClientRequest.setIsRetryTransaction(Boolean.FALSE);
		sendClientRequest.setStatusCode(statusCode);

		log.debug("sendClientRequest::" + sendClientRequest);

		return sendClientRequest;

	}

	public String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for (NameValuePair pair : params) {
			if (first)
				first = false;
			else
				result.append("&");
			result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
		}
		return result.toString();
	}

	public String getContextId(String requestJson) {
		log.debug("Inside getContextId::" + requestJson);
		String contextId = "";
		JSONObject ipObject = new JSONObject();
		try {
			if (requestJson != null && !"".equalsIgnoreCase(requestJson)) {
				if (requestJson.startsWith("[")) {
					JSONArray ipArr = new JSONArray(requestJson);
					ipObject = ipArr.getJSONObject(0);
					log.debug("ipObject45::" + ipObject.toString());
				} else if (requestJson.startsWith("{")) {
					ipObject = new JSONObject(requestJson);
				}
				ipObject = ipObject.getJSONObject("data");
				log.debug("ipObject::" + ipObject.toString());

				if (ipObject.has("account")) {
					ipObject = ipObject.getJSONObject("account");
					log.debug("ipObject42::" + ipObject.toString());
					if (ipObject.has("contextId")) {
						contextId = ipObject.getString("contextId");
						log.debug("ipObjects2::" + contextId);
					}
				}
			}
			return contextId;
		} catch (Exception e) {
			log.error("ErrorCode : " + ErrorCodes.CECC0007 + " : Exception {}", e);
		}
		return contextId;
	}

	public String getURL(String request, String endUrl) {
		try {
			JSONArray jsonArray = new JSONArray(jsonFormatter2(request));
			JSONObject jObject = jsonArray.getJSONObject(0);
			Map<String, String> pathMap = new HashMap<String, String>();
			Iterator<?> keys = jObject.keys();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				String value = jObject.getString(key);
				pathMap.put(key, value);
			}
			return StringSubstitutor.replace(endUrl, pathMap, "${", "}");
		} catch (Exception e) {
			log.error("ErrorCode : " + ErrorCodes.CECC0007 + " : getURL Exception{}", e);
		}
		return null;
	}

	public String getResponseFromGetClient(String endPointUrl, String request, Map<String, String> dataMap,
			String operationName, String responseId) throws IOException {
		log.info("Inside getResponseFromGetClient:: " + request + " ::: " + operationName);
		log.info("getResponseFromGetClient endPointUrl:: " + endPointUrl);
		Map<String, String> pathParamMap = new HashMap<>();
		String imsi = "";
		String deviceId = "";
		String mdn = "";
		String iccid = "";
		try {
			if (request.startsWith("{")) {
				request = "[" + request + "]";
			}
			JSONArray jsonarr = new JSONArray(request);
			JSONObject object = jsonarr.getJSONObject(0);
			JSONObject dataObject = jsonarr.getJSONObject(0);
			if (object.has(CommonConstants.DATA)) {
				dataObject = object.getJSONObject(CommonConstants.DATA);
				if (dataObject.has(CommonConstants.IMSI)) {
					imsi = dataObject.getString(CommonConstants.IMSI);
				}
				if (dataObject.has(CommonConstants.MDN)) {
					mdn = dataObject.getString(CommonConstants.MDN);
				}
				if (dataObject.has(CommonConstants.ICCID)) {
					iccid = dataObject.getString(CommonConstants.ICCID);
				}
			}
			if (object.has(CommonConstants.VALIDATE_DEVICE_INQUIRY)) {
				object = object.getJSONObject(CommonConstants.VALIDATE_DEVICE_INQUIRY);
				if (object.has("data")) {
					object = object.getJSONObject("data");
					if (object.has("deviceId")) {
						deviceId = object.getString("deviceId");
					}
				}
			}

			if (operationName.equalsIgnoreCase(CommonConstants.IMSI_INQUIRY)) {
				pathParamMap.put("imsi", imsi);
				endPointUrl = StringSubstitutor.replace(endPointUrl, pathParamMap, "${", "}");
			}
			if (operationName.equalsIgnoreCase(CommonConstants.PROMOTION_INQUIRY)) {
				pathParamMap.put("mdn", mdn);
				endPointUrl = StringSubstitutor.replace(endPointUrl, pathParamMap, "${", "}");
			}
			if (operationName.equalsIgnoreCase(CommonConstants.VALIDATE_DEVICE)) {
				pathParamMap.put("imei", deviceId);
				endPointUrl = StringSubstitutor.replace(endPointUrl, pathParamMap, "${", "}");
			}
			if (operationName.equalsIgnoreCase(CommonConstants.SUBSCRIBERGROUP_INQUIRY)) {
				pathParamMap.put("mdn", mdn);
				endPointUrl = StringSubstitutor.replace(endPointUrl, pathParamMap, "${", "}");
			}
			if (operationName.equalsIgnoreCase(CommonConstants.PORTIN_INQUIRY)) {
				pathParamMap.put("mdn", mdn);
				endPointUrl = StringSubstitutor.replace(endPointUrl, pathParamMap, "${", "}");
			}
			if (operationName.equalsIgnoreCase(CommonConstants.VALIDATE_SIM)) {
				pathParamMap.put("iccid", iccid);
				endPointUrl = StringSubstitutor.replace(endPointUrl, pathParamMap, "${", "}");
			}
			log.debug("endPointUrl" + endPointUrl);
			return endPointUrl;
		} catch (Exception e) {
			log.error("ErrorCode : " + ErrorCodes.CECC0007 + " : Exception in getResponseFromGetClient{} ", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getHeaderInfo(String request, String transId) throws JSONException {
		Map<String, String> headerMap = null;
		log.info("request :: " + request);
		if (request.startsWith("[")) {
			JsonArray formattedJsonArray = new JsonParser().parse(request).getAsJsonArray();
			JsonObject formattedjson = formattedJsonArray.get(0).getAsJsonObject();
			log.debug("formattedJson in Hotline Subscriber1::" + formattedjson);
			if (formattedjson.has("syniverseAddSubscriber")) {
				JsonObject formattedjsonSyniverse = formattedjson.getAsJsonObject("syniverseAddSubscriber");
				if (formattedjsonSyniverse.has("header")) {
					formattedjsonSyniverse = formattedjsonSyniverse.getAsJsonObject("header");
					formattedjsonSyniverse.remove("MsgId");
					formattedjsonSyniverse.addProperty("MsgId", transId);
					log.debug("formattedJson in Hotline Subscriber2::" + formattedjson);
				}
				request = "[" + formattedjson.toString() + "]";

			}
			JSONArray reqJsonArr = new JSONArray(request);
			for (int i = 0; i < reqJsonArr.length(); i++) {
				JSONObject obj = reqJsonArr.getJSONObject(i);
				Iterator<String> objItr = obj.keys();
				while (objItr.hasNext()) {
					String key = objItr.next();
					JSONObject reqObj = obj.getJSONObject(key);
					Iterator<String> reqObjItr = reqObj.keys();
					while (reqObjItr.hasNext()) {
						String groupKey = reqObjItr.next();
						if (ApolloNEConstants.HEADER.equalsIgnoreCase(groupKey)) {
							headerMap = new HashMap<>();
							JSONObject headerObj = reqObj.getJSONObject(groupKey);
							Iterator<String> headerObjItr = headerObj.keys();
							while (headerObjItr.hasNext()) {
								String paramKey = headerObjItr.next();
								headerMap.put(paramKey, headerObj.getString(paramKey));
							}
						}
					}
				}
			}
		}
		return headerMap;
	}

	public String jsonFormatter2(String inputJson) {
		log.debug("inputJson:::::" + inputJson);
		String response = null;
		JSONObject finalObj = new JSONObject();
		try {
			if (inputJson.startsWith("[")) {
				JSONArray jsonarr = new JSONArray(inputJson);
				for (int i = 0; i < jsonarr.length(); i++) {
					JSONObject obj = jsonarr.getJSONObject(i);
					response = "[" + printJsonObject(obj, finalObj) + "]";
					log.debug("JsonArray Formatter Final Response::" + response);
				}
			} else {
				JSONObject object = new JSONObject(inputJson);
				response = "[" + printJsonObject(object, finalObj) + "]";
				log.debug("JsonObject Formatter Final Response::" + response);
			}
		} catch (JSONException e) {
			log.error("ErrorCode : " + ErrorCodes.CECC0007 + " : jsonFormatter Exception{}", e);
		}
		return response;
	}

	public String printJsonObject(JSONObject jsonObj, JSONObject obj) {
		try {
			Iterator a = jsonObj.keys();
			while (a.hasNext()) {
				String keyStr = a.next().toString();
				Object keyvalue = jsonObj.get(keyStr);
				JSONObject object = null;

				if ("mdn".equalsIgnoreCase(keyStr)) {
					if (keyvalue instanceof JSONArray) {
						JSONArray array = (JSONArray) keyvalue;
						for (int i = 0; i < array.length(); i++) {
							JSONObject jsonObject1 = array.getJSONObject(i);
							String str = jsonObject1.toString();
							str = str.replace("value", "mdn");
							object = new JSONObject(str);
						}
					}
				}
				if ("simid".equalsIgnoreCase(keyStr)) {
					if (keyvalue instanceof JSONArray) {
						JSONArray array = (JSONArray) keyvalue;
						for (int i = 0; i < array.length(); i++) {
							JSONObject jsonObject1 = array.getJSONObject(i);
							String str = jsonObject1.toString();
							str = str.replace("value", "iccid");
							object = new JSONObject(str);
						}
					}
				}
				if ("deviceid".equalsIgnoreCase(keyStr)) {
					if (keyvalue instanceof JSONArray) {
						JSONArray array = (JSONArray) keyvalue;
						for (int i = 0; i < array.length(); i++) {
							JSONObject jsonObject1 = array.getJSONObject(i);
							String str = jsonObject1.toString();
							str = str.replace("value", "imei");
							object = new JSONObject(str);
						}
					} else {
						obj.put("imei", keyvalue.toString());
					}
				}
				if ("feature".equalsIgnoreCase(keyStr)) {
					if (keyvalue instanceof JSONArray) {
						JSONArray array = (JSONArray) keyvalue;
						// JSONObject jsonObject1 = array.getJSONObject(0);
						// String str = jsonObject1.toString();
						keyvalue = array;
					}
				}
				if (keyStr.matches("deviceId")) {
					if (keyvalue instanceof JSONArray) {
						JSONArray array = (JSONArray) keyvalue;
						for (int i = 0; i < array.length(); i++) {
							JSONObject jsonObject1 = array.getJSONObject(i);
							obj.put(jsonObject1.getString("type"), jsonObject1.getString("value"));
						}
					}
				}
				if (object != null) {
					keyvalue = object;
				}

				if (!(keyvalue instanceof JSONObject) && !(keyvalue instanceof JSONArray)) {
					obj.put(keyStr, keyvalue.toString());
				}
				if (keyvalue instanceof JSONObject) {
					printJsonObject((JSONObject) keyvalue, obj);
				}
				if ((keyvalue instanceof JSONArray)) {
					if (!(keyStr.equalsIgnoreCase("feature"))) {
						printJsonArray((JSONArray) keyvalue, obj);
					} else {
						obj.put(keyStr, keyvalue);
					}
				}
			}
		} catch (Exception e) {
			log.error("ErrorCode : " + ErrorCodes.CECC0007 + " : printJsonObject Exception{}", e);
		}
		return obj.toString();
	}

	public String printJsonArray(JSONArray array, JSONObject obj) {
		try {
			for (int i = 0; i < array.length(); i++) {
				JSONObject jsonObject1 = array.getJSONObject(i);
				printJsonObject(jsonObject1, obj);
			}
		} catch (Exception e) {
			log.error("ErrorCode : " + ErrorCodes.CECC0007 + " : printJsonArray exception {}", e);
		}
		return null;
	}

	public ResponseEntity<String> callExtSysForQueue(RcsIntegrationServiceBean rcsServiceBean,
			OutboundRequest outReqBean) throws JSONException, URISyntaxException, IOException, UnknownHostException {
		ResponseEntity<String> resp = new ResponseEntity<String>(ApolloNEConstants.NOT_RECEIVED_RESP,
				HttpStatus.INTERNAL_SERVER_ERROR);
		HttpEntity<String> entity = null;
		JsonObject bodyInfo = null;
		JsonObject bodyInfoTempJSON = null;
		String bodyInfoTemp = null;
		try {
			// outReqBean.setTransId(rcsDao.getPrimaryKey());
			log.debug("getHttpMethod:: " + rcsServiceBean.getHttpMethod().toString());
			if (!rcsServiceBean.getHttpMethod().toString().equalsIgnoreCase(CommonConstants.GET)) {
				if (rcsServiceBean.getRequest() != null) {
					bodyInfoTemp = changeReferenceNumToTransId(rcsServiceBean.getRequest(), outReqBean.getTransId());
					bodyInfo = new JsonParser().parse(bodyInfoTemp).getAsJsonObject();
					if (bodyInfo.has("data")) {
						bodyInfo.get("data").getAsJsonObject().remove("internalOrder");
						bodyInfo.get("data").getAsJsonObject().remove("relatedTransactionId");
						bodyInfo.get("data").getAsJsonObject().remove("relatedLineId");
					}
				}
			}
			String token = getToken(rcsServiceBean.getAuthorization());
			HttpHeaders headers = new HttpHeaders();
			if (rcsServiceBean.getHeaderMap() != null) {
				Iterator<String> objItr = rcsServiceBean.getHeaderMap().keySet().iterator();
				while (objItr.hasNext()) {
					String key = objItr.next();
					headers.add(key, rcsServiceBean.getHeaderMap().get(key));
				}
			}
			headers.setContentType(MediaType.APPLICATION_JSON);
			// if (StringUtils.hasText(token))
			log.debug("token:" + token);
			if (StringUtils.hasText(token) && token.startsWith("{")) {
				Map<String, Object> dbReqMap = new Gson().fromJson(token, Map.class);
				if (dbReqMap.get("header") != null) {
					Map<String, String> headerMap = (Map<String, String>) dbReqMap.get("header");
					headerMap.entrySet().stream().forEach(p -> headers.add(p.getKey(), p.getValue()));
				} else {
					headers.add("x-api-key", token);
				}
			} else {
				headers.add("Authorization", token);
			}
			if (bodyInfo != null) {
				JSONObject bodyInfoobject = new JSONObject(bodyInfo.toString());
				rcsServiceBean.setRequestInfo(bodyInfoobject);
				entity = new HttpEntity<String>(bodyInfo.toString(), headers);
				outReqBean.setRequestJson(bodyInfo.toString());
			} else {
				entity = new HttpEntity<String>(headers);
				outReqBean.setRequestJson(rcsServiceBean.getNcmSouthBoundUrl());
			}
			// rcsDao.insertSouthBoundTransaction(outReqBean);

			resp = restTemplate.exchange(rcsServiceBean.getNcmSouthBoundUrl(),
					HttpMethod.valueOf(rcsServiceBean.getHttpMethod().toString()), entity, String.class);
		} catch (HttpClientErrorException e) {
			log.debug("inside HttpClientErrorException");
			log.error("ErrorCode : " + ErrorCodes.CECC0006 + " : Error in callExtSys{}", e);
			try {
				// int httpStatusCode = e.getRawStatusCode();
				// if (httpStatusCode == 401) {
				// throw new OutboundRetryException(e.getMessage(), e);
				// }
				JSONObject tokenJson = new JSONObject();
				tokenJson.put("errorCode", e.getRawStatusCode());
				tokenJson.put("errorMessage", e.getResponseBodyAsString());
				resp = new ResponseEntity<String>(tokenJson.toString(), HttpStatus.valueOf(e.getRawStatusCode()));
				return resp;
			} catch (Exception e1) {
				log.error("ErrorCode : " + ErrorCodes.CECC0006 + " : Exception - ", e);
				return resp;
			}

		}
		return resp;

	}

	@SuppressWarnings("unchecked")
	public String changeReferenceNumToTransId(String requestJson, Long transId) {
		log.debug("inside changeReferenceNumToTransId::" + requestJson + "::" + transId);

		try {
			if (!requestJson.contains(ApolloNEConstants.ERR_RESPONSE_CODE)) {
				if (requestJson.startsWith("[")) {
					JsonArray jsonArr = new JsonParser().parse(requestJson).getAsJsonArray();
					Set<Entry<String, JsonElement>> objItr = jsonArr.get(0).getAsJsonObject().entrySet();
					for (Map.Entry<String, JsonElement> entry : objItr) {
						if (!entry.getKey().equalsIgnoreCase(ApolloNEConstants.MESSAGE_HEADER)) {
							if (jsonArr.get(0).getAsJsonObject() != null
									&& jsonArr.get(0).getAsJsonObject().get(entry.getKey()) != null
									&& jsonArr.get(0).getAsJsonObject().get(entry.getKey()).isJsonObject()) {
								jsonArr.get(0).getAsJsonObject().get(entry.getKey()).getAsJsonObject()
										.get(ApolloNEConstants.MESSAGE_HEADER).getAsJsonObject()
										.addProperty(ApolloNEConstants.REF_NUM, transId);
								break;
							}
						} else {
							jsonArr.get(0).getAsJsonObject().get(ApolloNEConstants.MESSAGE_HEADER).getAsJsonObject()
									.addProperty(ApolloNEConstants.REF_NUM, transId);
							break;
						}
					}
					requestJson = jsonArr.toString();
				} else if (requestJson.startsWith("{")) {
					JsonObject jsonObj = new JsonParser().parse(requestJson).getAsJsonObject();
					Set<Entry<String, JsonElement>> objItr = jsonObj.entrySet();
					for (Map.Entry<String, JsonElement> entry : objItr) {
						if (!entry.getKey().equalsIgnoreCase(ApolloNEConstants.MESSAGE_HEADER)) {
							if (jsonObj.get(entry.getKey()).getAsJsonObject().has(ApolloNEConstants.MESSAGE_HEADER)) {
								jsonObj.get(entry.getKey()).getAsJsonObject().get(ApolloNEConstants.MESSAGE_HEADER)
										.getAsJsonObject().addProperty(ApolloNEConstants.REF_NUM, transId);
							}
							break;
						} else {
							jsonObj.get(ApolloNEConstants.MESSAGE_HEADER).getAsJsonObject()
									.addProperty(ApolloNEConstants.REF_NUM, transId);
							break;
						}
					}
					requestJson = jsonObj.toString();
				}
			}
			log.info("changeReferenceNumToTransId requestJson::" + requestJson);
		} catch (Exception e) {
			log.error("ErrorCode : " + ErrorCodes.CECC0007 + " : Error in convertJsonForInboundResponse Method{}", e);
		}
		return requestJson;
	}

	public String getToken(String auth) throws JSONException {
		String token = "";
		if (StringUtils.hasText(auth)) {
			ServiceInfo authInfo = rcsDao.getServiceInfoDetails(auth, properties.getServer());
			if (authInfo != null) {
				switch (authInfo.getAuthType()) {
				case "OAUTH": {
					String tokenJsonStr = this.getOauthToken(authInfo.getEndPointUrl(), authInfo.getMethod(),
							authInfo.getServiceRequest());
					if (StringUtils.hasText(tokenJsonStr) && tokenJsonStr.startsWith("{")) {
						JSONObject tokenJson = new JSONObject(tokenJsonStr);
						if (tokenJson.has("access_token")) {
							token = tokenJson.getString("access_token");
						} else if (tokenJson.has("message")) {
							token = tokenJson.getJSONObject("message").getString("token");
						}
						log.debug("token");
					}
				}
					break;
				case "BASIC": {
					token = authInfo.getServiceRequest();
				}
					break;
				default: {
					log.debug("No Auth Defined in Connection Paramter");
				}
				}
			}
		}
		return token;
	}

	@SuppressWarnings("unchecked")
	public String getOauthToken(String url, String method, String dbServiceRequest) {
		log.debug("Calling Oauth Endpoint::", url);
		try {
			Map<String, Object> dbReqMap = new Gson().fromJson(dbServiceRequest, Map.class);
			HttpHeaders headers = new HttpHeaders();
			headers.setOrigin(ApolloNEConstants.NSL);
			HttpEntity<String> entity = null;
			if (dbReqMap.get("header") != null) {
				Map<String, String> headerMap = (Map<String, String>) dbReqMap.get("header");
				headerMap.entrySet().stream().forEach(p -> headers.add(p.getKey(), p.getValue()));
			}
			if (dbReqMap.get("query") != null) {
				Map<String, String> queryMap = (Map<String, String>) dbReqMap.get("query");
				url = this.constructQueryParams(url, queryMap);
			}
			if (dbReqMap.get("body") != null) {
				Map<String, Object> bodyMap = (Map<String, Object>) dbReqMap.get("body");
				JSONObject bodyJson = new JSONObject(bodyMap);
				headers.setContentType(MediaType.APPLICATION_JSON);
				entity = new HttpEntity<String>(bodyJson.toString(), headers);
			} else {
				entity = new HttpEntity<>(headers);
			}

			ResponseEntity<String> respStr = restTemplate.exchange(url, HttpMethod.valueOf(method), entity,
					String.class);
			log.debug("After token response::", respStr);
			return respStr.hasBody() ? respStr.getBody() : "";
		} catch (Exception e) {
			log.error("ErrorCode : " + ErrorCodes.CECC0006 + " : Exception in calling OAuth EndPoint{}", e);
			return "";
		}
	}


	private String constructQueryParams(String url, Map<String, String> queryMap)
			throws JSONException, URISyntaxException {
		String query = queryMap.entrySet().stream().map(p -> p.getKey() + "=" + p.getValue())
				.reduce((p1, p2) -> p1 + "&" + p2).orElse("");
		return StringUtils.hasText(query) ? url + "?" + query : url;
	}
}
