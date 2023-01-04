/**
 * 
 */
package com.excelacom.century.apolloneoutbound.logger;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.ThreadContext;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.excelacom.century.apolloneoutbound.bean.ErrorCodes;
import com.excelacom.century.apolloneoutbound.bean.SendClientRequest;
import com.excelacom.century.apolloneoutbound.dao.RcsDao;
import com.excelacom.century.apolloneoutbound.properties.ApolloNEServiceProperties;
import com.excelacom.century.apolloneoutbound.utils.constants.CommonConstants;
import com.excelacom.century.apolloneoutbound.utils.constants.ResourceConstants;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import lombok.extern.log4j.Log4j2;

/**
 * @author Dhayanand.B
 *
 */
@Component
@Log4j2
public class UtilityService {
	
	@Autowired
	ApolloNEServiceProperties apolloNEProperties;
	
	@Autowired
	private RcsDao rcsDao;

	public static void addTraceDetails(String transationId, String referenceNumber) {
		try {
			String traceId = "t-" + transationId + " : r-" + referenceNumber;
			ThreadContext.put("trackId", traceId);
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0015+" : Exception - {}", e);
		}
	}

	public static void updateTrace(String transationId, String referenceNumber) {
		try {
			if (transationId != null && referenceNumber != null) {
				String traceId = "t-" + transationId + " : r-" + referenceNumber;
				ThreadContext.put("trackId", traceId);
			}
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0015+" : Exception - {}", e);
		}
	}

	public static void removeTrace() {
		try {
			ThreadContext.remove("trackId");
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0015+" : Exception :", e);
		}
	}

	public LinkedTreeMap<?, ?> jsonToMap(String jsonString) throws JSONException {
		Gson gson = new Gson();
		LinkedTreeMap<?, ?> map = gson.fromJson(jsonString, LinkedTreeMap.class);
		return map;
	}

	public String getReferenceNumber(String jsonString) {
		String referenceNumber = null;
		try {
			LinkedTreeMap<?, ?> requestMap = jsonToMap(jsonString);
			if (requestMap != null && requestMap.containsKey("messageHeader")) {
				LinkedTreeMap<?, ?> messageHeader = (LinkedTreeMap<?, ?>) requestMap.get("messageHeader");
				if (messageHeader.containsKey("referenceNumber") && messageHeader.get("referenceNumber") != null) {
					log.debug("referenceNumber :" + messageHeader.get("referenceNumber"));
					referenceNumber = (String) messageHeader.get("referenceNumber");
				}
			}
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0015+" : Exception - {} ", e);
		}
		return referenceNumber;
	}
	
	public void callFalloutResourceService(SendClientRequest sendClientRequest) {
		Gson requestGson = new Gson();
		HttpHeaders httpHeaders = new HttpHeaders();
		RestTemplate restTemplate = new RestTemplate();
		ExecutorService executor = Executors.newFixedThreadPool(25);
		try {
			String url = apolloNEProperties.getResourceServiceURL()+ResourceConstants.resourceUpdateServiceUrl.valueOf("FALLOUT_SAVE").getServiceUrl();
			String transacId = rcsDao.getTransIdFromUid(sendClientRequest.getTransId());
			int tenantId = rcsDao.getTenantIdFromTid(transacId);
			Map<String, Object> requestMap = new LinkedHashMap<>();
			requestMap.put("transactionId", transacId);
			requestMap.put("rootTransactionId", sendClientRequest.getResponseId());
			requestMap.put("externalTransactionId", getReferenceNumber(sendClientRequest.getRequest()));
			requestMap.put("transactionStatus", CommonConstants.QUEUED);
			requestMap.put("serviceURL", sendClientRequest.getRcsServiceBean().getNcmSouthBoundUrl());
			requestMap.put("transObj", requestGson.toJson(sendClientRequest).getBytes(StandardCharsets.UTF_8));
			requestMap.put("retryCount", sendClientRequest.getRetryCount());
			requestMap.put("queueName", sendClientRequest.getQueueName());
			requestMap.put("createdDate", getTimeStamp());
			requestMap.put("createdBy", "API");
			requestMap.put("errorTrace", sendClientRequest.getStackTrace());
			requestMap.put("tenantId", tenantId);
			requestMap.put("httpResponse", sendClientRequest.getStatusCode());
			String request = requestGson.toJson(requestMap);
			httpHeaders.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
			HttpEntity<?> httpReq = new HttpEntity<>(request, httpHeaders);
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						restTemplate.exchange(url, HttpMethod.POST, httpReq, String.class);
					} catch (Exception e) {
						log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Exception - "+e.getMessage(), e);
					}
				}
			});
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Exception - "+e.getMessage(), e);
		} finally {
			if (executor != null)
				executor.shutdown();
		}
	}
	
	public String getTimeStamp() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		Calendar cal = Calendar.getInstance();
		log.debug("Current Date: " + sdf.format(cal.getTime()));
		String date = sdf.format(cal.getTime());
		return date;
	}

}
