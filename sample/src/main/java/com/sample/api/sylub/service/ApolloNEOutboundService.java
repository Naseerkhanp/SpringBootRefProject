package com.excelacom.century.apolloneoutbound.service;

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
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.excelacom.century.apolloneoutbound.bean.Account;
import com.excelacom.century.apolloneoutbound.bean.Data;
import com.excelacom.century.apolloneoutbound.bean.Device;
import com.excelacom.century.apolloneoutbound.bean.DeviceGsmaHistory;
import com.excelacom.century.apolloneoutbound.bean.EHttpMethods;
import com.excelacom.century.apolloneoutbound.bean.ErrorCodes;
import com.excelacom.century.apolloneoutbound.bean.GroupParameter;
import com.excelacom.century.apolloneoutbound.bean.Line;
import com.excelacom.century.apolloneoutbound.bean.LinePlan;
import com.excelacom.century.apolloneoutbound.bean.NpaNxx;
import com.excelacom.century.apolloneoutbound.bean.OutboundRequest;
import com.excelacom.century.apolloneoutbound.bean.Parameter;
import com.excelacom.century.apolloneoutbound.bean.ProcessMetadata;
import com.excelacom.century.apolloneoutbound.bean.RcsIntegrationServiceBean;
import com.excelacom.century.apolloneoutbound.bean.RefErrorRules;
import com.excelacom.century.apolloneoutbound.bean.RefPilotPrg;
import com.excelacom.century.apolloneoutbound.bean.RequestBean;
import com.excelacom.century.apolloneoutbound.bean.ResourceInfo;
import com.excelacom.century.apolloneoutbound.bean.ResourceUpdateRequest;
import com.excelacom.century.apolloneoutbound.bean.ResponseBean;
import com.excelacom.century.apolloneoutbound.bean.Root;
import com.excelacom.century.apolloneoutbound.bean.ScreenDetails;
import com.excelacom.century.apolloneoutbound.bean.SearchEnvironmentResultDto;
import com.excelacom.century.apolloneoutbound.bean.SendClientRequest;
import com.excelacom.century.apolloneoutbound.bean.Sim;
import com.excelacom.century.apolloneoutbound.bean.SubOrder;
import com.excelacom.century.apolloneoutbound.bean.TransactionHistory;
import com.excelacom.century.apolloneoutbound.consumer.ApolloNEOutbountClientSender;
import com.excelacom.century.apolloneoutbound.consumer.ApolloNeServiceConsumer;
import com.excelacom.century.apolloneoutbound.consumer.ConsumerUtilityClass;
import com.excelacom.century.apolloneoutbound.dao.RcsDao;
import com.excelacom.century.apolloneoutbound.entity.ServiceInfo;
import com.excelacom.century.apolloneoutbound.logger.UtilityService;
import com.excelacom.century.apolloneoutbound.properties.ApolloNEQueueProperties;
import com.excelacom.century.apolloneoutbound.properties.ApolloNEServiceProperties;
import com.excelacom.century.apolloneoutbound.utils.constants.ApolloNEConstants;
import com.excelacom.century.apolloneoutbound.utils.constants.CommonConstants;
import com.excelacom.century.apolloneoutbound.bean.OfferingDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class ApolloNEOutboundService {

	@Autowired
	private ApolloNEQueueProperties properties;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private RabbitTemplate customRabbitTemplate;

	@Autowired
	private ApolloNEOutboundClientService apolloNEOutboundClientService;

	@Autowired
	private ApolloNEQueueProperties apolloNEQueueProperties;

	@Autowired
	private RcsDao rcsDao;

	@Autowired
	private ApolloNEServiceProperties apolloNEServiceProperties;
	
	@Autowired
	private ConsumerUtilityClass consumerUtilityClass;
	
	Boolean searchEnvTargetSystem = null;

	public Message initiateApolloNEOutbound(Message message) throws Exception {
		String operationName = "", requestJson = "", destName = "", entityId = "", destination = "",
				transactionResp = "", endpointURL = "", endpointOperation = "", response = "", gpName = "",
				gpName1 = "", groupId = "", processPlanId = "", requestParams = "", responseId = "", appName = "";
		String processPlanName = "";
		String workflowName = "";
		Integer tenantId= 0;
		RcsIntegrationServiceBean rcsServiceBean = new RcsIntegrationServiceBean();
		SendClientRequest sendClientRequest = new SendClientRequest();
		Map<String, String> dataMap = new HashMap<String, String>();
		byte[] body = message.getBody();
		String request = new String(body);
		
		log.debug("sendClientRequestForZip request logger::", request.toString());
		try {
			if (request.contains("retryZipCodeCount")) {
				Gson gsonZip = new Gson();
				String ziptransUid = UUID.randomUUID().toString();
				SendClientRequest sendClientRequestForZip = gsonZip.fromJson(request, SendClientRequest.class);
				//log.debug("sendClientRequestForZip string logger::", sendClientRequestForZip.toString());
				//log.debug("sendClientRequestForZip normal logger::", sendClientRequestForZip);
				//log.debug("sendClientRequestForZip logger::",
						//sendClientRequestForZip.getRetryZipCodeCount().toString());
				if (sendClientRequestForZip.getRetryZipCodeCount() < 3) {
					sendClientRequestForZip.getOutReqBean().setTransId(rcsDao.getPrimaryKey());
					sendClientRequestForZip.getOutReqBean().setTransUid(ziptransUid);
					//log.debug("sendClientRequestForZip sendClientRequestForZip.getOutReqBean()::",
							//sendClientRequestForZip.getOutReqBean());
					rcsDao.insertSouthBoundTransaction(sendClientRequestForZip.getOutReqBean());
					sendClientRequest = consumerUtilityClass
							.sendMessageToClient(sendClientRequestForZip);
					sendClientRequest.getRcsServiceBean().setTranscationId(ziptransUid);
					updateTransactionDetails(sendClientRequest.getRcsServiceBean(), sendClientRequest.getOutReqBean(),
							sendClientRequest.getOperationName(), sendClientRequest.getResponseId(),
							CommonConstants.APOLLOTARGET_SYSTEM, sendClientRequest.getResponse(),
							sendClientRequest.getStatusCode(), "", sendClientRequest);
					if (sendClientRequest.getResponse() != null) {
						return MessageBuilder.withBody(sendClientRequest.getResponse().getBytes())
								.setContentType(MediaType.APPLICATION_JSON_VALUE).build();
					} else {
						return MessageBuilder.withBody(ApolloNEConstants.NOT_RECEIVED_RESP.getBytes())
								.setContentType(MediaType.APPLICATION_JSON_VALUE).build();
					}
				} else {
					return MessageBuilder.withBody(sendClientRequestForZip.getResponse().getBytes())
							.setContentType(MediaType.APPLICATION_JSON_VALUE).build();
				}
			}
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0004+" : Exception - ",e);
			return MessageBuilder.withBody(ApolloNEConstants.NOT_RECEIVED_RESP.getBytes())
					.setContentType(MediaType.APPLICATION_JSON_VALUE).build();
		}

		String[] nameValuePairs = request.split(CommonConstants.AMP);
		String[] arrayOfString1 = nameValuePairs;
		int i = arrayOfString1.length;
		for (int j = 0; j < i; ++j) {
			String nameValuePair = arrayOfString1[j];
			if (nameValuePair.startsWith(CommonConstants.SOAPACTION)) {
				operationName = nameValuePair.split(CommonConstants.EQUALTO)[1];
			}
			if (nameValuePair.startsWith(CommonConstants.JSON)) {
				requestJson = nameValuePair.split(CommonConstants.EQUALTO)[1];
			}
			if (nameValuePair.startsWith(CommonConstants.DESTNAME)) {
				destName = nameValuePair.split(CommonConstants.EQUALTO)[1];
			}
			if (nameValuePair.startsWith(CommonConstants.ENTITYID)) {
				entityId = nameValuePair.split(CommonConstants.EQUALTO)[1];
				if (entityId != null) {
					groupId = entityId.substring(entityId.indexOf("~") + 1, entityId.length());
					processPlanId = entityId.substring(0, entityId.indexOf("~"));
				}
			}
			if (nameValuePair.startsWith(CommonConstants.DESTINATION)) {
				destination = nameValuePair.split(CommonConstants.EQUALTO)[1];
			}
			if (nameValuePair.startsWith(CommonConstants.ENDPOINTURLEQ)) {
				endpointURL = nameValuePair.split(CommonConstants.EQUALTO)[1];
			}
			if (nameValuePair.startsWith(CommonConstants.ENDPOINTOPERATION)) {
				endpointOperation = nameValuePair.split(CommonConstants.EQUALTO)[1];
			}
			if (nameValuePair.startsWith(CommonConstants.REQUESTJSON)) {
				requestParams = nameValuePair.split(CommonConstants.EQUALTO)[1];
			}
			if (nameValuePair.startsWith(CommonConstants.RESPONSEID)) {
				responseId = nameValuePair.split(CommonConstants.EQUALTO)[1];
			}
			if (nameValuePair.startsWith(CommonConstants.PROCESS_PLAN_NAME)) {
				processPlanName = nameValuePair.split(CommonConstants.EQUALTO)[1];
			}
			if (nameValuePair.startsWith(CommonConstants.WORKFLOW_NAME)) {
				workflowName = nameValuePair.split(CommonConstants.EQUALTO)[1];
			}
		}
		if (endpointURL != null && !CommonConstants.EMPTYSTRING.equalsIgnoreCase(endpointURL)
				&& !CommonConstants.NULL.equalsIgnoreCase(endpointURL)) {
			destName = endpointURL;
			operationName = endpointOperation;
		}

		String referenceNumber = "NA";
		String transationId = responseId;// get transationId number from request
		UtilityService.addTraceDetails(transationId, referenceNumber);
		dataMap.put("processPlanName", processPlanName);
		dataMap.put("workflowName", workflowName);
		log.debug("Operation name :: " + operationName + "Request JSON :: " + requestJson + "Dest name :: " + destName
				+ "Entity ID :: " + entityId + "destination :: " + destination + "EndpointURL :: " + endpointURL
				+ "EndpointOperation :: " + endpointOperation + "requestParams:: " + requestParams + "responseId:: "
				+ responseId);

		if (operationName.equals("Syniverse Register Subscriber")
				|| operationName.equals("Syniverse De-Register Subscriber")) {
			appName = ApolloNEConstants.SYNIVERSE;
		} else if (operationName.equals("Activate Subscriber PSIM") || operationName.equals("Activate Subscriber ESIM")
				|| operationName.equals("ChangeESIM") || operationName.equals("Add Wearable")
				|| operationName.equals("Imsi Inquiry") || operationName.equals("Validate Device")
				|| operationName.equals("Update Port-Out") || operationName.equals("Change Feature")
				|| operationName.equals("Change SIM") || operationName.equals("Validate MDN Portability")
				|| operationName.equalsIgnoreCase("UpdateSubscriber Group")
				|| operationName.equals(CommonConstants.PROMOTION_INQUIRY)
				|| operationName.equals(CommonConstants.ACTIVATESUBSCRIBER)
				|| operationName.equals(CommonConstants.ACTIVATESUBSCRIBER_PORTIN)
				|| operationName.equalsIgnoreCase(CommonConstants.RECONNECT_SERVICE)
				|| operationName.equalsIgnoreCase(CommonConstants.CHANGE_MDN)
				|| operationName.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER)
				|| operationName.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER_PORTIN)
				|| operationName.equalsIgnoreCase(CommonConstants.RETRIEVE_DEVICE)
				|| operationName.equalsIgnoreCase("Validate BYOD")
				|| operationName.equalsIgnoreCase(CommonConstants.CHANGE_BCD)
				|| operationName.equalsIgnoreCase("Change Wholesale Rate Plan")
				|| operationName.equalsIgnoreCase("Cancel Port-In") || operationName.equalsIgnoreCase("Update Due-Date")
				|| operationName.equalsIgnoreCase("Update Customer Information")
				|| operationName.equalsIgnoreCase(CommonConstants.RESET_FEATURE)
				|| operationName.equalsIgnoreCase(CommonConstants.MANAGE_PROMOTION)
				|| operationName.equalsIgnoreCase("Device Detection Change Rate Plan")
				|| operationName.equalsIgnoreCase("Change Rate Plan")
				|| operationName.equalsIgnoreCase("Subscribergroup Inquiry")
				|| operationName.equalsIgnoreCase(CommonConstants.UPDATE_WIFI_ADDRESS)
				|| operationName.equalsIgnoreCase(CommonConstants.VALIDATE_WIFI_ADDRESS)
				|| operationName.equalsIgnoreCase(CommonConstants.GET_WIFI_ADDRESS)
				|| operationName.equalsIgnoreCase(CommonConstants.PORTIN_INQUIRY)
				|| operationName.equalsIgnoreCase(CommonConstants.VALIDATE_SIM)
				|| (operationName.equalsIgnoreCase("Restore Service"))
				|| (operationName.equalsIgnoreCase("Remove Hotline"))
				|| (operationName.equalsIgnoreCase("Reconnect Service"))
				|| (operationName.equalsIgnoreCase("CP-Reconnect Mdn"))
				|| (operationName.equalsIgnoreCase(CommonConstants.SUSPEND_SUBSCRIBER))
				|| (operationName.equalsIgnoreCase(CommonConstants.HOTLINE_SUBSCRIBER))
				|| (operationName.equalsIgnoreCase(CommonConstants.DELETE_SUBSCRIBER))
				|| (operationName.equalsIgnoreCase(CommonConstants.TW_DEACTIVATE_SUBSCRIBER))
				|| operationName.equalsIgnoreCase(CommonConstants.LINE_INQUIRY)
				|| operationName.equalsIgnoreCase(CommonConstants.LINE_INQ)
				|| operationName.equalsIgnoreCase(CommonConstants.UPDATE_SHARED_NAME)
				|| operationName.equalsIgnoreCase(CommonConstants.QUERY_SHARED_NAME)
				|| operationName.equalsIgnoreCase(CommonConstants.DEVICE_CHECK_GSMA)
				|| operationName.equalsIgnoreCase("GSMA Device Check")) {
			appName = ApolloNEConstants.APOLLONE;
		} else if (operationName.equalsIgnoreCase(CommonConstants.CBRSUSAGENOTIFICATION)) {
			appName = ApolloNEConstants.HMNO;
		} else {
			appName = ApolloNEConstants.CBRS_NE;
		}

		if ((destination != null) && (!(destination.equalsIgnoreCase(CommonConstants.EMPTYSTRING)))) {
			rcsServiceBean.setNcmOutboundServiceName(destName);
		}
		// Authentication Changes Start requestJson
		dataMap.put("initRequest", requestParams);
		Gson gson = new Gson();
		List<GroupParameter> groupParameterList = gson.fromJson(requestJson, new TypeToken<List<GroupParameter>>() {
		}.getType());
		gpName = groupParameterList.get(0).getGroupParamList().get(0).getEntityName();
		log.debug("gpName:: " + gpName);
		gpName1 = groupParameterList.get(0).getGroupParamList().get(1).getEntityName();
		log.debug("gpName1:: " + gpName1);

		GroupParameter groupParam = groupParameterList.get(0).getGroupParamList().get(0);
		String groupJson = gson.toJson(groupParam);
		groupJson = "[" + groupJson + "]";

		if ("Connection".equalsIgnoreCase(gpName)) {
			String converttionResponse = convertionLogic(groupJson);
			String mergeResponse = converttionResponse.substring(converttionResponse.indexOf('[') + 1,
					converttionResponse.indexOf(']'));
			JSONObject jObject = new JSONObject(mergeResponse.trim());
			Iterator<?> keys = jObject.keys();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				String value = jObject.getString(key);
				dataMap.put(key, value);
			}
			log.debug("DataMap:: " + dataMap.toString());
		}

		/*
		 * GroupParameter groupParam1 = groupParameterList1.get(0); String groupJson1 =
		 * gson.toJson(groupParam1); groupJson1 = "[" + groupJson1 + "]";
		 */

		// rcsServiceBean.setHttpMethod(EHttpMethods.valueOf(dataMap.get(ApolloNEConstants.ENDPOINT_HTTP_METHOD)));
		rcsServiceBean.setAuthorization(dataMap.getOrDefault(ApolloNEConstants.AUTH_TYPE, ""));
		if (appName.equalsIgnoreCase(ApolloNEConstants.HMNO) || appName.equalsIgnoreCase(ApolloNEConstants.CBRS_NE)) {
			tenantId = 3;
		} else {
			tenantId = 1;
		}
		OutboundRequest requestBean = OutboundRequest.builder().processPlanId(processPlanId).groupId(groupId)
				.entityId(entityId).applicationName(appName).tenantId(tenantId).responseId(responseId).processPlanName(processPlanName).workflowName(workflowName).build();

		response = rcsServiceCall(requestBean, rcsServiceBean, requestJson, dataMap, requestParams, responseId,sendClientRequest);
		if (response == null || response.contains(ApolloNEConstants.UNAUTH_RESP)) {
			response = ApolloNEConstants.NOT_RECEIVED_RESP;
		}
		log.debug("before final response:: " + response);
		if (StringUtils.hasText(operationName) && (operationName.equalsIgnoreCase("Update Port-Out")
				|| operationName.equalsIgnoreCase(CommonConstants.VALIDATE_SIM) || operationName.equalsIgnoreCase(CommonConstants.RETRIEVE_DEVICE))) {
			log.debug("operationName0:: " + operationName);
			if (response != null) {
				if (response.contains("~")) {
					if (response.split("~").length == 3) {
						response = response.split("~")[0];
					} else if (response.split("~").length == 2) {
						response = response.split("~")[0];
					}
				}
			}
		}
		log.debug("Final response:: " + response);
		transactionResp = rcsServiceBean.getTranscationId();
		log.debug("transactionResp:: " + transactionResp);

		if (response == null || response.contains(ApolloNEConstants.UNAUTH_RESP)) {
			response = ApolloNEConstants.NOT_RECEIVED_RESP;
		}
		return MessageBuilder.withBody(response.getBytes()).setContentType(MediaType.APPLICATION_JSON_VALUE).build();
	}

	public String rcsServiceCall(OutboundRequest outReqBean, RcsIntegrationServiceBean rcsServiceBean,
			String requestJson, Map<String, String> dataMap, String requestParams, String responseId, SendClientRequest sendClientRequest) throws Exception {
		try {
			log.debug("ncmAccountsIntegrationServiceCall:: " + requestJson);
			log.debug("ncmAccounts outReqBean:: " + outReqBean.toString());
			String outputString = CommonConstants.EMPTYSTRING;
			String str = requestJson;
			String formattedJson = "";
			String NEFlag = "";
			String url = "";
			boolean searchEnv=true;
			boolean enableErrorQueue = true;
			boolean transferReconnectFlag = false;
			String isPilotPlan="";
			boolean isPilotPlanMigrationflag=false;
			RequestBean requestParamBean = new RequestBean();
			if (dataMap.get(CommonConstants.ENDPOINTSERVICETYPE) != null
					&& !"0".equalsIgnoreCase(dataMap.get(CommonConstants.ENDPOINTSERVICETYPE))
					&& "REST".equals(dataMap.get(CommonConstants.ENDPOINTSERVICETYPE))) {
				String operationName = dataMap.get("EndpointOperation");
				String transId = UUID.randomUUID().toString();
				rcsServiceBean.setTranscationId(transId);
				log.debug("operationName:: " + operationName);
				log.debug("str:: " + str);
				formattedJson = jsonFormatter(str, "", requestParams, "", operationName);
				log.debug("formatted Json::STEP 1:: " + formattedJson);
				formattedJson = convertJsonForNSLResponseFormat(formattedJson);

				try {
				if (operationName.equalsIgnoreCase(CommonConstants.DELETE_SUBSCRIBER)||operationName.equalsIgnoreCase("CP-Reconnect Mdn")) {
					String cpReconnectMdn="";
					String mDNValue = "";
					String newMdnValue = "";
					String ibReqMsg = dataMap.get("initRequest");
					log.debug("ibReqMsg::" + ibReqMsg);
					JSONObject dataObj = new JSONObject(ibReqMsg);
					if (dataObj.has("data")) {
						dataObj = dataObj.getJSONObject("data");
						if (dataObj.has("subOrder")) {
							JSONArray subOrderArray = dataObj.getJSONArray("subOrder");
							JSONObject suborderObj = subOrderArray.getJSONObject(0);
							String mdntype = "";
							
							if (suborderObj.has("mdn")) {
								JSONArray mdnArray = suborderObj.getJSONArray("mdn");
								for(int i=0;i<mdnArray.length();i++) {
									JSONObject mdnObj1 = mdnArray.getJSONObject(i);
									if (mdnObj1.has("type")) {
										mdntype = mdnObj1.getString("type");
									}
									if (mdnObj1.has("value")) {
										mDNValue = mdnObj1.getString("value");
										log.debug("ibReqMsg mDNValue::" + mDNValue);
									}
									if("newMDN".equalsIgnoreCase(mdntype)) {
										newMdnValue= mdnObj1.getString("value");
									}
									if (mdntype.equalsIgnoreCase("oldMDN")) {
										JsonObject changeMdnCPformattedDataObj = new JsonParser().parse(formattedJson).getAsJsonObject();
										JsonObject formattedDataObj = null;
										JsonArray mdnSubOrderArray =null;
										cpReconnectMdn=mDNValue;
										if (changeMdnCPformattedDataObj.has("data")) {
											formattedDataObj = changeMdnCPformattedDataObj.getAsJsonObject("data");
											if (formattedDataObj.has("subOrder")) {
												mdnSubOrderArray = formattedDataObj.get("subOrder").getAsJsonArray();
												formattedDataObj = mdnSubOrderArray.get(0).getAsJsonObject();
											}
											JsonArray formatMdnArray = new JsonArray();
											JsonObject formatMdnObj = new JsonObject();
											if (formattedDataObj.has("mdn")) {
												formattedDataObj.remove("mdn");
												formatMdnObj.addProperty("type", "mdn");
												formatMdnObj.addProperty("value", mDNValue);
												formatMdnArray.add(formatMdnObj); 
												formattedDataObj.add("mdn", formatMdnArray);
											}
											formattedJson = changeMdnCPformattedDataObj.toString();
										}
										
									}
								}
							}
						}
					}
					if(operationName.equalsIgnoreCase("CP-Reconnect Mdn") ) {
						try {
							if(StringUtils.hasText(cpReconnectMdn)) {
								Gson gson=new Gson();
								Line line=gson.fromJson(apolloNEOutboundClientService.getLineDetails(cpReconnectMdn), Line.class);
								Device deviceBean=new Device();
								deviceBean.seteLineId(line.geteLineId());
								deviceBean=apolloNEOutboundClientService.callDeviceResourceService(deviceBean);
								formattedJson=formattedJson.replace("\"type\":\"IMEI\",\"value\":\"\"", "\"type\":\"IMEI\",\"value\":\""+deviceBean.getImei()+"\"");
								Sim simBean=new Sim();
								simBean.seteLineId(line.geteLineId());
								simBean=apolloNEOutboundClientService.callSimResourceService(simBean);
								formattedJson=formattedJson.replace("\"type\":\"ICCID\",\"value\":\"\"", "\"type\":\"ICCID\",\"value\":\""+simBean.getIccid()+"\"");
								formattedJson=formattedJson.replace("\"type\":\"mdn\",\"value\":\""+newMdnValue+"\"", "\"type\":\"mdn\",\"value\":\""+cpReconnectMdn+"\"");
							}
							else if(StringUtils.hasText(mDNValue)) {
								Gson gson=new Gson();
								Line line=gson.fromJson(apolloNEOutboundClientService.getLineDetails(mDNValue), Line.class);
								Device deviceBean=new Device();
								deviceBean.seteLineId(line.geteLineId());
								deviceBean=apolloNEOutboundClientService.callDeviceResourceService(deviceBean);
								formattedJson=formattedJson.replace("\"type\":\"IMEI\",\"value\":\"\"", "\"type\":\"IMEI\",\"value\":\""+deviceBean.getImei()+"\"");
								Sim simBean=new Sim();
								simBean.seteLineId(line.geteLineId());
								simBean=apolloNEOutboundClientService.callSimResourceService(simBean);
								formattedJson=formattedJson.replace("\"type\":\"ICCID\",\"value\":\"\"", "\"type\":\"ICCID\",\"value\":\""+simBean.getIccid()+"\"");
							}
						}
						catch(Exception e) {
							log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Exception cpReconnect--{}",e);
						}
					}
				}
				} catch (Exception e) {
					log.error("ErrorCode : "+ErrorCodes.CECC0004+" : Error in inputmessgae format{}", e);
				}
				
				log.debug("formatted Json::STEP 2:: " + formattedJson);
				Boolean isAP = true;
				if (formattedJson.contains("HMNO")) {
					isAP = false;
				}
				log.debug("isAP::" + isAP);
				if(requestParams.contains("\"transactionType\":\"SE\"") || requestParams.contains("\"transactionType\": \"SE\"")) {
					searchEnv= false;
					rcsServiceBean.setSearchEnvFlag("success");
				}
				
				if (operationName.equalsIgnoreCase("Subscribergroup Inquiry")) {
					rcsServiceBean.setSearchEnvFlag("SubscribergroupInquiry");
				}
				 
				if (operationName.equalsIgnoreCase(CommonConstants.CBRS_ADD_SUB)
						|| operationName.equalsIgnoreCase(CommonConstants.CHANGE_MDN)
						|| operationName.equalsIgnoreCase(CommonConstants.CBRS_RECONNECT_SUB)
						|| operationName.equalsIgnoreCase(CommonConstants.CBRS_CR)
						|| operationName.equals(CommonConstants.VALIDATE_MDN_PORTABILITY)
						|| operationName.equals(CommonConstants.ACTIVATESUBSCRIBER)
						|| operationName.equals(CommonConstants.ACTIVATESUBSCRIBER_PORTIN)
						|| operationName.equalsIgnoreCase(CommonConstants.UPDATE_PORT_OUT)
						|| operationName.equals("Change Feature")
						|| operationName.equalsIgnoreCase("UpdateSubscriber Group")
						|| operationName.equalsIgnoreCase(CommonConstants.RECONNECT_SERVICE)
						|| operationName.equalsIgnoreCase(CommonConstants.VALIDATEBYOD)
						|| operationName.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER)
						|| operationName.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER_PORTIN)
						|| operationName.equalsIgnoreCase("Change SIM")
						|| operationName.equalsIgnoreCase("Change Rate Plan")
						|| operationName.equalsIgnoreCase(CommonConstants.RETRIEVE_DEVICE)
						|| operationName.equalsIgnoreCase(CommonConstants.CBRSUSAGENOTIFICATION)
						|| operationName.equalsIgnoreCase(CommonConstants.SUSPEND_SUBSCRIBER)
						|| operationName.equalsIgnoreCase(CommonConstants.HOTLINE_SUBSCRIBER)
						|| operationName.equalsIgnoreCase(CommonConstants.DELETE_SUBSCRIBER)
						|| operationName.equalsIgnoreCase(CommonConstants.TW_DEACTIVATE_SUBSCRIBER)) {
					// formattedJson = requestParams;
					JsonObject reqJson = new JsonParser().parse(formattedJson).getAsJsonObject();
					try {

						if (reqJson.has("data")) {
							if (reqJson.get("data").getAsJsonObject().has("subOrder")) {
								if (reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray().get(0)
										.getAsJsonObject().has("simId")) {
									if (reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray().get(0)
											.getAsJsonObject().get("simId").getAsJsonArray().size() > 0) {
										if (reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray()
												.get(0).getAsJsonObject().get("simId").getAsJsonArray().get(0)
												.getAsJsonObject().has("type")) {
											if (reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray()
													.get(0).getAsJsonObject().get("simId").getAsJsonArray().get(0)
													.getAsJsonObject().get("type").getAsString().isEmpty()) {
												reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray()
														.get(0).getAsJsonObject().remove("simId");
											}

										} else {
											reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray()
													.get(0).getAsJsonObject().remove("simId");
										}
									} else {
										reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray().get(0)
												.getAsJsonObject().remove("simId");
									}

								}

								if (reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray().get(0)
										.getAsJsonObject().has("additionalData")) {
									if (reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray().get(0)
											.getAsJsonObject().get("additionalData").getAsJsonArray().size() > 0) {
										if (reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray()
												.get(0).getAsJsonObject().get("additionalData").getAsJsonArray().get(0)
												.getAsJsonObject().has("name")) {
											if (reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray()
													.get(0).getAsJsonObject().get("additionalData").getAsJsonArray()
													.get(0).getAsJsonObject().get("name").getAsString().isEmpty()) {
												reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray()
														.get(0).getAsJsonObject().remove("additionalData");
											}

										} else {
											reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray()
													.get(0).getAsJsonObject().remove("additionalData");
										}
									} else {
										reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray().get(0)
												.getAsJsonObject().remove("additionalData");
									}

								}

							}
							if (reqJson.get("data").getAsJsonObject().has("additionalData")) {
								log.debug(" additionalData empty Condition check:: " + reqJson);
								if (reqJson.get("data").getAsJsonObject().get("additionalData").getAsJsonArray()
										.size() > 0) {
									if (reqJson.get("data").getAsJsonObject().get("additionalData").getAsJsonArray()
											.get(0).getAsJsonObject().has("name")) {
										if (reqJson.get("data").getAsJsonObject().get("additionalData").getAsJsonArray()
												.get(0).getAsJsonObject().get("name").getAsString().isEmpty()) {
											reqJson.get("data").getAsJsonObject().remove("additionalData");
										}

									} else {
										reqJson.get("data").getAsJsonObject().remove("additionalData");
									}
								} else {
									reqJson.get("data").getAsJsonObject().remove("additionalData");
								}

							}
						}
					} catch (Exception e) {
						log.error("ErrorCode : "+ErrorCodes.CECC0007+" : Error in inputmessgae format{}", e);
					}
					formattedJson = reqJson.toString();
					log.debug("formatted Json::STEP 3:: " + formattedJson);

				}

				// formattedJson=valueEmptyValidation(formattedJson);
				log.debug("formatted Json::STEP 4::" + formattedJson);
				JsonObject obj = new JsonParser().parse(formattedJson).getAsJsonObject();
				boolean deviceIdFlag = false;
				// if(formattedJson.contains("\"NBOP\"")) {
				log.debug("operationName CHECK1::" + operationName);
				if ((!operationName.equalsIgnoreCase("Imsi Inquiry"))
						&& (!operationName.equalsIgnoreCase("Validate Device"))
						&& (!operationName.equalsIgnoreCase(CommonConstants.PROMOTION_INQUIRY))
						&& (!operationName.equalsIgnoreCase("Subscribergroup Inquiry"))
						&& (!operationName.equalsIgnoreCase(CommonConstants.GET_WIFI_ADDRESS))
						&& (!operationName.equalsIgnoreCase(CommonConstants.VALIDATE_WIFI_ADDRESS))
						&& (!operationName.equalsIgnoreCase(CommonConstants.UPDATE_WIFI_ADDRESS))
						&& (!operationName.equalsIgnoreCase(CommonConstants.PORTIN_INQUIRY))
						&& (!operationName.equalsIgnoreCase(CommonConstants.VALIDATE_SIM))
						&& (!operationName.equalsIgnoreCase(CommonConstants.UPDATE_SHARED_NAME))
						&& (!operationName.equalsIgnoreCase(CommonConstants.QUERY_SHARED_NAME))
						&& (!operationName.equalsIgnoreCase(CommonConstants.DEVICE_CHECK_GSMA))
						&& (!operationName.equalsIgnoreCase("GSMA Device Check"))) {
					log.debug("inside operationName CHECK2::" + operationName);
					if (operationName.equalsIgnoreCase("Activate Subscriber PSIM")
							|| operationName.equalsIgnoreCase("Activate Subscriber ESIM")
							|| operationName.equalsIgnoreCase("Add Wearable")
							|| operationName.equalsIgnoreCase(CommonConstants.ACTIVATESUBSCRIBER)
							|| operationName.equalsIgnoreCase("ChangeESIM")
							|| operationName.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER)) {
						outReqBean.setTargetSystem("Apollo-NE");
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().remove("returnURL");
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().remove("asyncErrorURL");
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("returnURL",
								properties.getDns() + CommonConstants.ActivateRETURNURL);
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("asyncErrorURL",
								properties.getDns() + CommonConstants.ActivateASYNCERRORURL);
					} else if (StringUtils.hasText(operationName)
							&& (operationName.equalsIgnoreCase(CommonConstants.VALIDATE_MDN_PORTABILITY)
									|| operationName.equalsIgnoreCase(CommonConstants.UPDATE_SUBSCRIBER_GROUP)
									|| operationName.equalsIgnoreCase("Update Due-Date")
									|| operationName.equalsIgnoreCase("Update Customer Information")
									|| operationName.equalsIgnoreCase(CommonConstants.RESET_FEATURE)
									|| operationName.equalsIgnoreCase("Remove Hotline")
									|| operationName.equalsIgnoreCase("Restore Service"))) {
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().remove("returnURL");
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().remove("asyncErrorURL");
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("returnURL",
								properties.getDns() + CommonConstants.VALIDATE_MDN_PORTABILITY_RETURNURL);
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("asyncErrorURL",
								properties.getDns() + CommonConstants.VALIDATE_MDN_PORTABILITY_ASYNCERRORURL);
					} else if (operationName.equalsIgnoreCase("Change MDN")) {
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().remove("returnURL");
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().remove("asyncErrorURL");
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("returnURL",
								properties.getDns() + CommonConstants.ChangeMdnRETURNURL);
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("asyncErrorURL",
								properties.getDns() + CommonConstants.ActivateASYNCERRORURL);
					} else if (operationName.equalsIgnoreCase("Change Feature")
							|| operationName.equalsIgnoreCase(CommonConstants.CHANGE_BCD)
							|| operationName.equalsIgnoreCase("Change Wholesale Rate Plan")
							|| operationName.equalsIgnoreCase(CommonConstants.CHANGE_SIM)
							|| operationName.equalsIgnoreCase("Device Detection Change Rate Plan")
							|| operationName.equalsIgnoreCase("Change Rate Plan")
							|| operationName.equalsIgnoreCase(CommonConstants.DELETE_SUBSCRIBER)) {
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().remove("returnURL");
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().remove("asyncErrorURL");
						if (!isAP && operationName.equalsIgnoreCase(CommonConstants.DELETE_SUBSCRIBER)) {
							obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("returnURL",
									properties.getDns() + CommonConstants.RETURNURL);
							obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("asyncErrorURL",
									properties.getDns() + CommonConstants.ASYNCERRORURL);
						} else {
							obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("returnURL",
									properties.getDns() + CommonConstants.ChangeFeatureRETURNURL);
							obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("asyncErrorURL",
									properties.getDns() + CommonConstants.ChangeFeatureAsyncErrorurl);
						}
					} else if ((operationName.equalsIgnoreCase("Reconnect Service")||operationName.equalsIgnoreCase("CP-Reconnect Mdn")) && isAP) {
						outReqBean.setTargetSystem("Apollo-NE");
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().remove("returnURL");
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().remove("asyncErrorURL");
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("returnURL",
								properties.getDns() + CommonConstants.ReconnectServiceRETURNURL);
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("asyncErrorURL",
								properties.getDns() + CommonConstants.ReconnectServiceAsyncErrorurl);
					} else if (operationName.equalsIgnoreCase(CommonConstants.ACTIVATESUBSCRIBER_PORTIN)
							|| operationName.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER_PORTIN)) {
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().remove("returnURL");
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().remove("asyncErrorURL");
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("returnURL",
								properties.getDns() + CommonConstants.ACTIVATEPORTIN_RETURNURL);
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("asyncErrorURL",
								properties.getDns() + CommonConstants.ACTIVATEPORTIN_ASYNCERRORURL);
					} else if (operationName.equalsIgnoreCase(CommonConstants.RETRIEVE_DEVICE)) {
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().remove("returnURL");
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().remove("asyncErrorURL");
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("returnURL",
								properties.getDns() + CommonConstants.RETRIEVEDEVICE_RETURNURL);
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("asyncErrorURL",
								properties.getDns() + CommonConstants.RETRIEVEDEVICE_ASYNCERRORURL);
					} else if (operationName.equalsIgnoreCase(CommonConstants.VALIDATEBYOD)) {
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().remove("returnURL");
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().remove("asyncErrorURL");
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("returnURL",
								properties.getDns() + CommonConstants.VALIDATEBYOD_RETURNURL);
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("asyncErrorURL",
								properties.getDns() + CommonConstants.VALIDATEBYOD_ASYNCERRORURL);
					} else if (operationName.equalsIgnoreCase(CommonConstants.MANAGE_PROMOTION)
							|| operationName.equalsIgnoreCase(CommonConstants.SUSPEND_SUBSCRIBER)
							|| operationName.equalsIgnoreCase(CommonConstants.HOTLINE_SUBSCRIBER)							
							|| operationName.equalsIgnoreCase(CommonConstants.TW_DEACTIVATE_SUBSCRIBER)) {
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().remove("returnURL");
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().remove("asyncErrorURL");
						/*if (!isAP && operationName.equalsIgnoreCase(CommonConstants.DELETE_SUBSCRIBER)) {
							obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("returnURL",
									properties.getDns() + CommonConstants.RETURNURL);
							obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("asyncErrorURL",
									properties.getDns() + CommonConstants.ASYNCERRORURL);
						} else {*/
						if (!isAP && (operationName.equalsIgnoreCase(CommonConstants.SUSPEND_SUBSCRIBER) 
								|| operationName.equalsIgnoreCase(CommonConstants.HOTLINE_SUBSCRIBER))) {
							obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("returnURL",
									properties.getDns() + CommonConstants.RETURNURL);
							obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("asyncErrorURL",
									properties.getDns() + CommonConstants.ASYNCERRORURL);
						} else {
						
							obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("returnURL",
									properties.getDns() + CommonConstants.MANAGE_PROMOTION_RETURNURL);
							obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("asyncErrorURL",
									properties.getDns() + CommonConstants.MANAGE_PROMOTION_ASYNCERRORURL);
						}
					} else if (operationName.equalsIgnoreCase(CommonConstants.CANCEL_PORTIIN)) {
						log.info("Inside CANCEL_PORTIIN before::" + formattedJson);
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().remove("returnURL");
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().remove("asyncErrorURL");
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("returnURL",
								properties.getDns() + CommonConstants.VALIDATE_MDN_PORTABILITY_RETURNURL);
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("asyncErrorURL",
								properties.getDns() + CommonConstants.VALIDATE_MDN_PORTABILITY_ASYNCERRORURL);
						if (obj.get(CommonConstants.DATA).getAsJsonObject().has(CommonConstants.SUBORDER)) {
							/*
							 * if
							 * (obj.get(CommonConstants.DATA).getAsJsonObject().get(CommonConstants.SUBORDER
							 * ).getAsJsonArray().get(0) .getAsJsonObject().has("originalRefNumber")) {
							 * obj.get(CommonConstants.DATA).getAsJsonObject().get(CommonConstants.SUBORDER)
							 * .getAsJsonArray().get(0) .getAsJsonObject().remove("originalRefNumber"); }
							 */
							if (obj.get(CommonConstants.DATA).getAsJsonObject().get(CommonConstants.SUBORDER).getAsJsonArray().get(0)
									.getAsJsonObject().has("lineId")) {
								obj.get(CommonConstants.DATA).getAsJsonObject().get(CommonConstants.SUBORDER).getAsJsonArray().get(0)
										.getAsJsonObject().remove("lineId");
							}
							if (obj.get(CommonConstants.DATA).getAsJsonObject().get(CommonConstants.SUBORDER).getAsJsonArray().get(0)
									.getAsJsonObject().has("remark")) {
								obj.get(CommonConstants.DATA).getAsJsonObject().get(CommonConstants.SUBORDER).getAsJsonArray().get(0)
										.getAsJsonObject().remove("remark");
							}
						}
						if (obj.get(CommonConstants.DATA).getAsJsonObject().has(CommonConstants.ADDITIONAL_DATA)) {
							obj.get(CommonConstants.DATA).getAsJsonObject().remove(CommonConstants.ADDITIONAL_DATA);
						}
						if (obj.get(CommonConstants.DATA).getAsJsonObject().has(CommonConstants.ACCOUNT)) {
							obj.get(CommonConstants.DATA).getAsJsonObject().remove(CommonConstants.ACCOUNT);
						}
						log.info("Inside CANCEL_PORTIIN after::" + formattedJson);
					}
					else {
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().remove("returnURL");
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().remove("asyncErrorURL");
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("returnURL",
								properties.getDns() + CommonConstants.RETURNURL);
						obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("asyncErrorURL",
								properties.getDns() + CommonConstants.ASYNCERRORURL);
					}
				}
				
				if (operationName.equalsIgnoreCase("Add Wearable")) {
					log.debug("requestParams in Add Wearable::" + requestParams);
					log.debug("formattedJson in Add Wearable::" + formattedJson);
					JsonObject requestparamsJsonDataForAW = new JsonObject();
					if (requestParams.startsWith("[")) {
						JsonArray requestparamsJsonArray = new JsonParser().parse(requestParams).getAsJsonArray();
						requestparamsJsonDataForAW = requestparamsJsonArray.get(0).getAsJsonObject();
					} else {
						requestparamsJsonDataForAW = new JsonParser().parse(requestParams).getAsJsonObject();
					}
					log.debug("requestparamsJsonDataForAW in Add Wearable::" + requestparamsJsonDataForAW);
					if (requestparamsJsonDataForAW.has("data")) {
						requestparamsJsonDataForAW = requestparamsJsonDataForAW.getAsJsonObject("data");
						if (requestparamsJsonDataForAW.has("transactionType")) {
							String transferWearableType = requestparamsJsonDataForAW.get("transactionType")
									.getAsString();
							if (transferWearableType.equalsIgnoreCase("TW")) {
								transferReconnectFlag = true;
								obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().remove("returnURL");
								obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().remove("asyncErrorURL");
								obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("returnURL",
										properties.getDns() + CommonConstants.TRANSFERWATCH_RETURNURL);
								obj.get(CommonConstants.MESSAGEHEADER).getAsJsonObject().addProperty("asyncErrorURL",
										properties.getDns() + CommonConstants.RETRIEVEDEVICE_ASYNCERRORURL);
							}
						}
					}
				}
				
				log.debug("formattedJson after Add Wearable TW::" + formattedJson);
				log.debug("transferReconnectFlag Add Wearable TW::" + transferReconnectFlag);
				
				if (operationName.equalsIgnoreCase(CommonConstants.IMSI_INQUIRY)
						|| operationName.equalsIgnoreCase(CommonConstants.VALIDATE_DEVICE)
						|| operationName.equalsIgnoreCase(CommonConstants.CHANGE_MDN)
						|| operationName.equalsIgnoreCase(CommonConstants.VALIDATE_MDN_PORTABILITY)
						|| operationName.equalsIgnoreCase(CommonConstants.PROMOTION_INQUIRY)
						|| operationName.equalsIgnoreCase(CommonConstants.MANAGE_PROMOTION)
						|| operationName.equalsIgnoreCase(CommonConstants.CBRSUSAGENOTIFICATION)
						|| operationName.equalsIgnoreCase(CommonConstants.PORTIN_INQUIRY)
						|| operationName.equalsIgnoreCase(CommonConstants.VALIDATE_SIM)
						|| operationName.equalsIgnoreCase(CommonConstants.SUSPEND_SUBSCRIBER)
						|| operationName.equalsIgnoreCase(CommonConstants.HOTLINE_SUBSCRIBER)
						|| operationName.equalsIgnoreCase(CommonConstants.DELETE_SUBSCRIBER)
						|| operationName.equalsIgnoreCase(CommonConstants.TW_DEACTIVATE_SUBSCRIBER)
						|| operationName.equalsIgnoreCase("Remove Hotline")
						|| operationName.equalsIgnoreCase("Restore Service")
						|| operationName.equalsIgnoreCase(CommonConstants.DEVICE_CHECK_GSMA)
						|| operationName.equalsIgnoreCase("GSMA Device Check")) {
					outReqBean.setTargetSystem("Apollo NE");
				}
				formattedJson = obj.toString();
				
				
				if (operationName.equalsIgnoreCase("Reconnect Service")) {
					log.debug("requestParams in Reconnect Service::" + requestParams);
					log.debug("formattedJson in Reconnect Service::" + formattedJson);
					JsonObject requestparamsJsonDataForAW = new JsonObject();
					if (requestParams.startsWith("[")) {
						JsonArray requestparamsJsonArray = new JsonParser().parse(requestParams).getAsJsonArray();
						requestparamsJsonDataForAW = requestparamsJsonArray.get(0).getAsJsonObject();
					} else {
						requestparamsJsonDataForAW = new JsonParser().parse(requestParams).getAsJsonObject();
					}
					if (requestparamsJsonDataForAW.has("ICCID")) {
						String iccid = requestparamsJsonDataForAW.get("ICCID").getAsString();
						log.debug("requestparamsJsonDataForAW iccid::" + iccid);
						if (formattedJson.contains("\"simId\":[{\"type\":\"ICCID\",\"value\":\"\"}]")) {
							formattedJson = formattedJson.replace("\"simId\":[{\"type\":\"ICCID\",\"value\":\"\"}]",
									"\"simId\":[{\"type\":\"ICCID\",\"value\":\"" + iccid + "\"}]");
						}
					}
					log.debug("transfer reconnect formattedJson::" + formattedJson);

				}
				
				log.debug("transfer reconnect formattedJson::" + formattedJson);
				if (operationName.equalsIgnoreCase("IMSI LineInquiry")) {

					requestParamBean = getRequestParamBean(requestParams);
					if (formattedJson.contains("\"mdn\":\"\"")) {
						log.info("formattedJson without space LineInquiry mdn:::" + formattedJson);
						String lineInquiryMdnValue = "";
						String lineInquirylineIdValue = "";

						Line line = new Line();
						if (requestParamBean.getLineId() != null) {
							lineInquirylineIdValue = requestParamBean.getLineId();
							if (lineInquirylineIdValue != null && !lineInquirylineIdValue.isEmpty()) {
								line = apolloNEOutboundClientService
										.callLineResourceServiceByLineId(lineInquirylineIdValue);
								if (line != null) {
									lineInquiryMdnValue = line.getMdn();
									log.info("lineInquiryMdnValue:: " + lineInquiryMdnValue);
								}
							}
						} else if (requestParamBean.getMdn() != null) {
							lineInquiryMdnValue = requestParamBean.getMdn();
						}
						formattedJson = formattedJson.replace("\"mdn\":\"\"",
								"\"mdn\":\"" + lineInquiryMdnValue + "\"");
					}
				}
				log.debug("formatted Json:::STEP 5:" + formattedJson);
				log.debug("operationName:::STEP 5:" + operationName);
								
				
				if (operationName.equalsIgnoreCase("Activate Subscriber")) {
					log.debug("request in Activate Subscriber::" + formattedJson);
					if (formattedJson.startsWith("{")) {
						if (formattedJson.contains("\"feature\":[{}]")) {
							log.debug("TRUE11::");
							String action = "\"feature\":[{}],";
							formattedJson = formattedJson.replace(action, "");
						}
					}
				}
			
				log.debug("Final remove of feature::"+formattedJson);
				if (operationName.equalsIgnoreCase(CommonConstants.MANAGE_PROMOTION)) {
					String action = "";
					String promotionId = "";
					String promotionID = "";
					String startDate = "";
					String promotionPrefix = "0000PR";

					if (formattedJson.contains("\"action\":\"A\"")) {
						action = "\"action\":\"ADD\"";
						formattedJson = formattedJson.replace("\"action\":\"A\"", action);
					}
					if (formattedJson.contains("\"action\":\"D\"")) {
						action = "\"action\":\"DELETE\"";
						formattedJson = formattedJson.replace("\"action\":\"D\"", action);
					}
					if (formattedJson.contains(",\"additionalData\":[{}]")) {
						action = "";
						formattedJson = formattedJson.replace(",\"additionalData\":[{}]", action);
					}

					if (formattedJson.contains("\"promotionID\":\"\"")) {
						startDate = getTimeStamp();
						log.info("startDate::" + startDate);
						String promotionSufix = promotionSufix(startDate);
						promotionId = promotionPrefix + promotionSufix;
						promotionID = "\"promotionID\":\"" + promotionId + "\"";
						formattedJson = formattedJson.replace("\"promotionID\":\"\"", promotionID);
						log.info("formattedJson::" + formattedJson);
					}
					JSONObject dataOject = new JSONObject();
					JSONObject subOrderObjct = new JSONObject();
					JSONArray subOrderArray = new JSONArray();
					
					JSONObject promotionObjct = new JSONObject();
					JSONArray promotionArray = new JSONArray();
					JSONObject object = new JSONObject(formattedJson);
					log.debug("object::" + object);
					String promotionStartDate = "";
					if (object.has("data")) {
						dataOject = object.getJSONObject("data");
						if (dataOject.has("subOrder")) {
							subOrderArray = dataOject.getJSONArray("subOrder");
							subOrderObjct = subOrderArray.getJSONObject(0);
							if (subOrderObjct.has("promotionDetails")) {
								promotionArray = subOrderObjct.getJSONArray("promotionDetails");
								promotionObjct = promotionArray.getJSONObject(0);
								if(promotionObjct.has("startDate")) {
									promotionStartDate = promotionObjct.getString("startDate");
									log.debug("promotionStartDate::" + promotionStartDate);
								}
							}
						}
					}
					if (formattedJson.contains("\"productType\":\"WSTIMEPROMO\"") 
							|| formattedJson.contains("\"productType\":\"WSHSDPRPROMO\"")
							|| formattedJson.contains("\"productType\":\"WSHSPROMO\"")
							|| formattedJson.contains("\"productType\":\"WSLSPROMO\"")
							|| formattedJson.contains("\"productType\":\"WSLSDPRPROMO\"")) {
						action = "";
						formattedJson = formattedJson.replace(",\"startDate\":\"" + promotionStartDate + "\"", action);
					}
					log.debug("formattedJson-promotion::" + formattedJson);

				}
				// requestparamsJsonData - inbound
				// reqJson - outbound
				if (operationName.equalsIgnoreCase(CommonConstants.UPDATE_WIFI_ADDRESS)
						|| operationName.equalsIgnoreCase(CommonConstants.VALIDATE_WIFI_ADDRESS)) {
					JsonObject requestparamsJsonData = new JsonObject();
					JsonObject reqJsonData = new JsonParser().parse(formattedJson).getAsJsonObject();
					JsonObject reqJson = reqJsonData;
					if (requestParams.startsWith("[")) {
						JsonArray requestparamsJsonArray = new JsonParser().parse(requestParams).getAsJsonArray();
						requestparamsJsonData = requestparamsJsonArray.get(0).getAsJsonObject();
					} else {
						requestparamsJsonData = new JsonParser().parse(requestParams).getAsJsonObject();
					}
					if (StringUtils.hasText(reqJson.toString()) && reqJson.has("data")) {
						if (reqJson.get("data").getAsJsonObject().has("subOrder")) {
							if (reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray().get(0)
									.getAsJsonObject().has("additionalData")) {
								if (requestparamsJsonData.has("data")) {
									if (!requestparamsJsonData.get("data").getAsJsonObject().has("additionalData")) {
										reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray().get(0)
												.getAsJsonObject().remove("additionalData");
										log.info("UPDATE_WIFI_ADDRESS additionalData Json::" + reqJson);
									}
								}
							}
						}
					}
					formattedJson = reqJsonData.toString();
				}
				if (operationName.equalsIgnoreCase(CommonConstants.GET_WIFI_ADDRESS)) {
					String requestTypeChange = "\"requestType\":\"MNO\"";
					if (formattedJson.contains("\"requestType\":\"SM-VZW\"")) {
						formattedJson = formattedJson.replace("\"requestType\":\"SM-VZW\"", requestTypeChange);
					}
				}
				log.debug("Inside Activate Subscriber Port-in requestParams::" + requestParams);
				log.debug("Inside Activate Subscriber Port-in::" + operationName);
				log.debug("Inside Activate Subscriber Port-in formattedJson::" + formattedJson);

				if (operationName.equalsIgnoreCase("Reconnect Service")) {
					JsonArray deviceIdArray = new JsonArray();
					log.debug("Inside Reconnect Service formattedJson::" + formattedJson);
					log.debug("Inside Reconnect Service requestParams::" + requestParams);
					if (requestParams.startsWith("[")) {
						JsonArray requestparamsJsonArray = new JsonParser().parse(requestParams).getAsJsonArray();
						JsonObject requestparamsJsonData = requestparamsJsonArray.get(0).getAsJsonObject();
						String updatedRequest = retrieveDeviceIdFromArray(requestparamsJsonData.toString());
						log.debug("Inside Reconnect Service updatedRequest::" + updatedRequest);
						requestparamsJsonData = new JsonParser().parse(updatedRequest).getAsJsonObject();
						if (requestparamsJsonData.has("data")) {
							if (requestparamsJsonData.get("data").getAsJsonObject().has("subOrder")) {
								if (requestparamsJsonData.get("data").getAsJsonObject().get("subOrder").getAsJsonArray()
										.get(0).getAsJsonObject().has("deviceId")) {
									deviceIdArray = requestparamsJsonData.get("data").getAsJsonObject().get("subOrder")
											.getAsJsonArray().get(0).getAsJsonObject().get("deviceId").getAsJsonArray();
									log.debug("Before Reconnect Service deviceIdArray::" + deviceIdArray);
								}
							}
						}
					}
					if (formattedJson.startsWith("{")) {
						JsonObject reconnectSubscriber = new JsonParser().parse(formattedJson).getAsJsonObject();
						if (reconnectSubscriber.has("data")) {
							JsonObject reconnectSubscriberData = reconnectSubscriber.get("data").getAsJsonObject();
							if (reconnectSubscriberData.has("subOrder")) {
								JsonObject subOrderObj= reconnectSubscriberData.get("subOrder").getAsJsonArray().get(0).getAsJsonObject();
								if (subOrderObj.has("deviceId")) {
									log.debug("Reconnect Service deviceIdArray::" + deviceIdArray);
									if (deviceIdArray != null && !deviceIdArray.isEmpty()) {
										subOrderObj.remove("deviceId");
										subOrderObj.add("deviceId", deviceIdArray);
										formattedJson = reconnectSubscriber.toString();
										log.debug("After changes Reconnect Service formattedJson::" + formattedJson);
									}
								}
							}
						}
					}
				}
		
				if (operationName.equalsIgnoreCase("ESIM Activate Subscriber Port-in")
						|| operationName.equalsIgnoreCase("Activate Subscriber Port-in") || operationName.equalsIgnoreCase("Activate Subscriber PSIM")
						|| operationName.equalsIgnoreCase("Activate Subscriber ESIM")) {
					log.debug("Inside Change SIM formattedJson::" + formattedJson);
					JSONObject object = new JSONObject(formattedJson);
					log.debug("formatted Json Change Feature STEP 6::" + formattedJson);
					JsonObject requestparamsJsonData = new JsonParser().parse(formattedJson).getAsJsonObject();
					log.debug("Inside ESIM Activate Subscriber Port-in requestparamsJsonData::" + requestparamsJsonData
							+ "requestParams::" + requestParams);
					try {
						if (operationName.equalsIgnoreCase("ESIM Activate Subscriber Port-in")) {
							log.debug("ESIM Activate Subscriber Port-in responseId::" + responseId + "::operationName::" +operationName);
							String changeMdnCPServiceName = rcsDao.getServiceName(responseId);
								if (!changeMdnCPServiceName.equalsIgnoreCase("Change-MDN")) {
									if (!(requestParams.contains("\"name\":{\"firstName\":") || requestParams.contains("\"name\": {\"firstName\":"))) {
										log.debug("true cond");
										if (requestparamsJsonData.has("data")) {
											if (requestparamsJsonData.get("data").getAsJsonObject().get("subOrder")
													.getAsJsonArray().get(0).getAsJsonObject().get("lnp").getAsJsonObject()
													.get("lnpName").getAsJsonObject().has("name")) {
												log.debug("true" + requestparamsJsonData);
												requestparamsJsonData.get("data").getAsJsonObject().get("subOrder")
														.getAsJsonArray().get(0).getAsJsonObject().get("lnp").getAsJsonObject()
														.get("lnpName").getAsJsonObject().remove("name");
												log.debug(
														"After removing requestparamsJsonData in ESIM Activate Subscriber Port-in"
																+ requestparamsJsonData);
											}
										}
									}
							 }
						}
						
						if (operationName.equalsIgnoreCase("Activate Subscriber Port-in") || operationName.equalsIgnoreCase("Activate Subscriber PSIM")
								|| operationName.equalsIgnoreCase("Activate Subscriber ESIM")) {
							if (!(requestParams.contains("\"name\":{\"firstName\":")
									|| requestParams.contains("\"name\": {\"firstName\":")
									||requestParams.contains("\"name\": {\"LeadName\":")||requestParams.contains("\"name\":{\"LeadName\":") 
									|| requestParams.contains("\"LeadName\""))) {
								log.debug("requestParams cond:: " + requestParams);
								if (requestparamsJsonData.has("data")) {
									if (requestparamsJsonData.get("data").getAsJsonObject().get("subOrder")
											.getAsJsonArray().get(0).getAsJsonObject().get("lnp").getAsJsonObject()
											.get("lnpName").getAsJsonObject().has("name")) {
										log.debug("Activate Subscriber Port-in true" + requestparamsJsonData);
										requestparamsJsonData.get("data").getAsJsonObject().get("subOrder")
												.getAsJsonArray().get(0).getAsJsonObject().get("lnp").getAsJsonObject()
												.get("lnpName").getAsJsonObject().remove("name");
										log.debug("After removing requestparamsJsonData in Activate Subscriber Port-in"
												+ requestparamsJsonData);
									}
								}
							}
						}
						formattedJson = requestparamsJsonData.toString();
						log.debug("After removing requestparamsJsonData for formattedJson" + formattedJson);
					} catch (Exception e) {
						log.error("ErrorCode : "+ErrorCodes.CECC0007+" : ESIM Activate Subscriber Port-in", e);
					}
				}

				if (operationName.equalsIgnoreCase("Change SIM")) {
					log.debug("Inside Change SIM formattedJson::" + formattedJson);
					String requestType = "";
					String simIdType = "";
					String simidValue = "";
					String simTypeFormatted = "";
					String simValueFormatted = "";
					String simValue1Formatted = "";
					JSONObject dataOject = new JSONObject();
					JSONObject subOrderObjct = new JSONObject();
					JSONArray subOrderArray = new JSONArray();
					JSONArray simIdArray = new JSONArray();
					JSONObject simIdObj = new JSONObject();
					JsonArray simIdFormattedArray = new JsonArray();
					JsonObject simIdFormattedObj = new JsonObject();
					JsonArray deviceIdFormattedArray = new JsonArray();
					JsonObject deviceIdFormattedObj = new JsonObject();
					JSONObject object = new JSONObject(formattedJson);
					String deviceidValue = "";
					String deviceIdType = "";
					String deviceTypeFormatted = "";
					String deviceValueFormatted = "";
					JSONArray deviceIdArray = new JSONArray();
					JSONObject deviceIdObj = new JSONObject();
					log.debug("formatted Json Change Feature STEP 6::" + formattedJson);
					JsonObject reqJsonData = new JsonParser().parse(formattedJson).getAsJsonObject();
					JsonObject requestparamsJsonData = new JsonParser().parse(requestParams).getAsJsonObject();
					if (object.has("data")) {
						dataOject = object.getJSONObject("data");
						if (dataOject.has("subOrder")) {
							subOrderArray = dataOject.getJSONArray("subOrder");
							subOrderObjct = subOrderArray.getJSONObject(0);
							if (subOrderObjct.has("simId")) {
								simIdArray = subOrderObjct.getJSONArray("simId");
								simIdObj = simIdArray.getJSONObject(0);
								if (simIdObj.has("type")) {
									simIdType = simIdObj.getString("type");
								}
								if (simIdObj.has("value")) {
									simidValue = simIdObj.getString("value");
								}
							}
							if (subOrderObjct.has("deviceId")) {
								deviceIdArray = subOrderObjct.getJSONArray("deviceId");
								deviceIdObj = deviceIdArray.getJSONObject(0);
								if (deviceIdObj.has("type")) {
									deviceIdType = deviceIdObj.getString("type");
								}
								if (deviceIdObj.has("value")) {
									deviceidValue = deviceIdObj.getString("value");
								}
							}
						}
					}
					if (reqJsonData.has("data")) {
						JsonObject reqJson = reqJsonData;
						formattedJson = reqJsonData.toString();
						String result = rcsDao.getRequestDetails(responseId);
						log.debug("Inside result Json::" + result);
						JsonObject reqJsonDataObject = new JsonParser().parse(result).getAsJsonObject();
						if (reqJson.get("messageHeader").getAsJsonObject().has("futureDateTime")) {
							log.debug("Inside messageHeader futureDateTime Json0::" + reqJson);
							reqJson.get("messageHeader").getAsJsonObject().addProperty("futureDateTime",
									getTimeStamp());
						}
						if (requestparamsJsonData.has("data")) {
							if (requestparamsJsonData.get("data").getAsJsonObject().has("subOrder")) {
								if (requestparamsJsonData.get("data").getAsJsonObject().get("subOrder").getAsJsonArray()
										.get(0).getAsJsonObject().has("simId")) {
									simIdFormattedArray = requestparamsJsonData.get("data").getAsJsonObject()
											.get("subOrder").getAsJsonArray().get(0).getAsJsonObject().get("simId")
											.getAsJsonArray();
									for (int i = 0; i < simIdFormattedArray.size(); i++) {
										simIdFormattedObj = simIdFormattedArray.get(i).getAsJsonObject();
										if (simIdFormattedObj.has("type")) {
											simTypeFormatted = simIdFormattedObj.get("type").getAsString();
											if (simTypeFormatted.equalsIgnoreCase("oldICCID")) {
												simValueFormatted = simIdFormattedObj.get("value").getAsString();
												log.debug("simValueFormatted" + simValueFormatted);
											}
											if (simTypeFormatted.equalsIgnoreCase("ICCID")) {
												simValue1Formatted = simIdFormattedObj.get("value").getAsString();
												log.debug("simValue1Formatted" + simValue1Formatted);
											}
										}
									}
								}
								if (requestparamsJsonData.get("data").getAsJsonObject().get("subOrder").getAsJsonArray()
										.get(0).getAsJsonObject().has("deviceId")) {
									deviceIdFormattedArray = requestparamsJsonData.get("data").getAsJsonObject()
											.get("subOrder").getAsJsonArray().get(0).getAsJsonObject().get("deviceId")
											.getAsJsonArray();
									for (int i = 0; i < deviceIdFormattedArray.size(); i++) {
										deviceIdFormattedObj = deviceIdFormattedArray.get(i).getAsJsonObject();
										if (deviceIdFormattedObj.has("type")) {
											 deviceTypeFormatted = deviceIdFormattedObj.get("type").getAsString();
											if (deviceTypeFormatted.equalsIgnoreCase("newIMEI")) {
												 deviceValueFormatted = deviceIdFormattedObj.get("value").getAsString();
												log.debug("deviceValueFormatted" + deviceValueFormatted);
											}
										}
									}
								}
							}
						}if (deviceidValue.isEmpty()) {
							JSONObject reqObj = new JSONArray(consumerUtilityClass.jsonFormatter2(result)).getJSONObject(0);
							Device deviceBean = new Device();
							deviceBean.seteLineId(reqObj.getString("lineId"));
							deviceBean = apolloNEOutboundClientService.callDeviceResourceService(deviceBean);
							if (Objects.nonNull(deviceBean)) {
								deviceidValue = deviceBean.getImei();
								String replaceDeviceValue0 = "\"deviceId\":[{\"type\":\"IMEI\",\"value\":\""
										+ deviceidValue + "\"}]";
								log.debug("replaceDeviceValue0" + replaceDeviceValue0);
								formattedJson = formattedJson.replace(
										"\"deviceId\":[{\"type\":\"" + deviceIdType + "\",\"value\":\"\"}]",
										replaceDeviceValue0);
								log.debug("formattedJson::" + formattedJson);
							}
						}
					}
					if (formattedJson.contains(
							"\"simId\":[{\"type\":\"" + simIdType + "\",\"value\":\"" + simidValue + "\"}]")) {
						String replaceSimValue = "\"simId\":[{\"type\":\"oldICCID\",\"value\":\"" + simValueFormatted
								+ "\"},{\"type\":\"newICCID\",\"value\":\"" + simValue1Formatted + "\"}]";
						formattedJson = formattedJson.replace(
								"\"simId\":[{\"type\":\"" + simIdType + "\",\"value\":\"" + simidValue + "\"}]",
								replaceSimValue);
					}
					if(deviceValueFormatted!=null&&!deviceValueFormatted.equalsIgnoreCase("")){
						if (formattedJson.contains("\"deviceId\":[{\"type\":\""+deviceIdType+"\",\"value\":\""+deviceidValue+"\"}]")) {
							String replaceDeviceValue0 = "\"deviceId\":[{\"type\":\"IMEI\",\"value\":\""+deviceValueFormatted+"\"}]";
							log.debug("replaceDeviceValue0" + replaceDeviceValue0);
							formattedJson = formattedJson.replace(
							"\"deviceId\":[{\"type\":\""+deviceIdType+"\",\"value\":\""+deviceidValue+"\"}]",
							replaceDeviceValue0);                    
						}
					}else if (formattedJson.contains("\"deviceId\":[{\"type\":\"oldIMEI\",\"value\":\""+deviceidValue+"\"}]")) {
							String replaceDeviceValue0 = "\"deviceId\":[{\"type\":\"IMEI\",\"value\":\""+deviceidValue+"\"}]";
							log.debug("replaceDeviceValue0" + replaceDeviceValue0);
							formattedJson = formattedJson.replace(
							"\"deviceId\":[{\"type\":\"oldIMEI\",\"value\":\""+deviceidValue+"\"}]",
							replaceDeviceValue0);                    
						}

					log.debug("formattedJson after Change SIM Json::" + formattedJson);
				}
				
				log.debug("formattedJson and opearionName::" + formattedJson + "::operationName::" +operationName);
				if (operationName.equalsIgnoreCase("Activate Subscriber Port-in")
						|| operationName.equalsIgnoreCase("ESIM Activate Subscriber Port-in")
						|| operationName.equalsIgnoreCase("Update Customer Information")) {
					String decodedLoginPd = "";
					String lnpPin = "";
					String requestJsonForPin = "";
					try {
						if (formattedJson != null && formattedJson.startsWith("{")) {
							JsonObject requestdJsonObject = new JsonParser().parse(formattedJson).getAsJsonObject();
							log.debug("requestdJsonObject::" + requestdJsonObject);
							if (requestdJsonObject.has("data")) {
								if (requestdJsonObject.get("data").getAsJsonObject().has("subOrder")) {
									if (requestdJsonObject.get("data").getAsJsonObject().get("subOrder")
											.getAsJsonArray().get(0).getAsJsonObject().has("lnp")) {
										log.debug("Inside lnp true cond::");
										JsonObject lnpObject = requestdJsonObject.get("data").getAsJsonObject()
												.get("subOrder").getAsJsonArray().get(0).getAsJsonObject().get("lnp")
												.getAsJsonObject();
										log.debug("lnpObject::" + lnpObject);
										if (lnpObject.has("pin")) {
											lnpPin = lnpObject.get("pin").getAsString();
											log.debug("lnpPin::" + lnpPin);
											if (lnpPin != null && !lnpPin.isEmpty()) {
												byte[] decodedBytes = Base64.getDecoder().decode(lnpPin);
												decodedLoginPd = new String(decodedBytes);
												log.debug("decodedLoginPd::" + decodedLoginPd);
											}
											lnpObject.remove("pin");
											lnpObject.addProperty("pin", decodedLoginPd);
											log.debug("lnpObject::" + lnpObject + "::requestdJsonObject::"
													+ requestdJsonObject);
										}
									} else {
										if (requestdJsonObject.get("data").getAsJsonObject().get("subOrder")
												.getAsJsonArray().get(0).getAsJsonObject().has("lnpUpdate")) {
											log.debug("Inside lnpUpdate lnpUpdate::");
											JsonObject lnpObject = requestdJsonObject.get("data").getAsJsonObject()
													.get("subOrder").getAsJsonArray().get(0).getAsJsonObject()
													.get("lnpUpdate").getAsJsonObject();
											log.debug("Inside lnpUpdate lnpObject::" + lnpObject);
											if (lnpObject.has("pin")) {
												lnpPin = lnpObject.get("pin").getAsString();
												log.debug("lnpPin::" + lnpPin);
												if (lnpPin != null && !lnpPin.isEmpty()) {
													byte[] decodedBytes = Base64.getDecoder().decode(lnpPin);
													decodedLoginPd = new String(decodedBytes);
													log.debug("decodedLoginPd::" + decodedLoginPd);
												}
												lnpObject.remove("pin");
												lnpObject.addProperty("pin", decodedLoginPd);
												log.debug("lnpObject::" + lnpObject + "::requestdJsonObject::"
														+ requestdJsonObject);
											}
										}
									}
								}
							}
							formattedJson = requestdJsonObject.toString();
							log.info("formattedJson after Decoded::" + formattedJson);
						} else {
							JsonArray formattedJsonArray = new JsonParser().parse(formattedJson).getAsJsonArray();
							log.debug("formattedJsonArray::" + formattedJsonArray);
							JsonObject requestdJsonObject = formattedJsonArray.get(0).getAsJsonObject();
							log.debug("requestdJsonObject::" + requestdJsonObject);
							if (requestdJsonObject.has("data")) {
								if (requestdJsonObject.get("data").getAsJsonObject().has("subOrder")) {
									if (requestdJsonObject.get("data").getAsJsonObject().get("subOrder")
											.getAsJsonArray().get(0).getAsJsonObject().has("lnp")) {
										log.debug("Inside lnp true cond::");
										JsonObject lnpObject = requestdJsonObject.get("data").getAsJsonObject()
												.get("subOrder").getAsJsonArray().get(0).getAsJsonObject().get("lnp")
												.getAsJsonObject();
										log.debug("lnpObject::" + lnpObject);
										if (lnpObject.has("pin")) {
											lnpPin = lnpObject.get("pin").getAsString();
											log.debug("lnpPin::" + lnpPin);
											if (lnpPin != null && !lnpPin.isEmpty()) {
												byte[] decodedBytes = Base64.getDecoder().decode(lnpPin);
												decodedLoginPd = new String(decodedBytes);
												log.debug("decodedLoginPd::" + decodedLoginPd);
											}
											lnpObject.remove("pin");
											lnpObject.addProperty("pin", decodedLoginPd);
											log.debug("lnpObject::" + lnpObject + "::requestdJsonObject::"
													+ requestdJsonObject);
										}
									} else {
										if (requestdJsonObject.get("data").getAsJsonObject().get("subOrder")
												.getAsJsonArray().get(0).getAsJsonObject().has("lnpUpdate")) {
											JsonObject lnpObject = requestdJsonObject.get("data").getAsJsonObject()
													.get("subOrder").getAsJsonArray().get(0).getAsJsonObject()
													.get("lnpUpdate").getAsJsonObject();
											if (lnpObject.has("pin")) {
												lnpPin = lnpObject.get("pin").getAsString();
												log.debug("lnpPin::" + lnpPin);
												if (lnpPin != null && !lnpPin.isEmpty()) {
													byte[] decodedBytes = Base64.getDecoder().decode(lnpPin);
													decodedLoginPd = new String(decodedBytes);
													log.debug("decodedLoginPd::" + decodedLoginPd);
												}
												lnpObject.remove("pin");
												lnpObject.addProperty("pin", decodedLoginPd);
												log.debug("lnpObject::" + lnpObject + "::requestdJsonObject::"
														+ requestdJsonObject);
											}
										}
									}
								}
							}
							formattedJson = formattedJsonArray.toString();
							log.info("formattedJson for Decoded::" + formattedJson);
						}
					} catch (Exception e) {
						log.error("Error in lnpPin for Activate Suvbsciber portin - {}", e);
					}
				}
				
				if (operationName.equalsIgnoreCase(CommonConstants.RETRIEVE_DEVICE)) {
					log.debug("Inside RETRIEVE DEVICE operationName::" + operationName);
					log.debug("Inside RETRIEVE DEVICE formattedJson::" + formattedJson);
					String simIdType = "";
					String simidValue = "";
					String simTypeFormatted = "";
					String simValueFormatted = "";
					String eLineId = "";
					String deviceImei = "";
					JsonArray simIdFormattedArray = new JsonArray();
					JsonObject simIdFormattedObj = new JsonObject();
					Device deviceBean = new Device();
					JsonObject requestparamsJsonData = new JsonParser().parse(requestParams).getAsJsonObject();
					/*if (requestparamsJsonData.has("data")) {
						if (requestparamsJsonData.get("data").getAsJsonObject().has("subOrder")) {
							if (requestparamsJsonData.get("data").getAsJsonObject().get("subOrder").getAsJsonArray()
									.get(0).getAsJsonObject().has("simId")) {
								simIdFormattedArray = requestparamsJsonData.get("data").getAsJsonObject()
										.get("subOrder").getAsJsonArray().get(0).getAsJsonObject().get("simId")
										.getAsJsonArray();
								for (int i = 0; i < simIdFormattedArray.size(); i++) {
									simIdFormattedObj = simIdFormattedArray.get(i).getAsJsonObject();
									if (simIdFormattedObj.has("type")) {
										simTypeFormatted = simIdFormattedObj.get("type").getAsString();
										if (simTypeFormatted.equalsIgnoreCase("ICCID")) {
											simValueFormatted = simIdFormattedObj.get("value").getAsString();
											log.debug("simValue1Formatted::" + simValueFormatted);
										}
									}
								}
							}
						}
					}
					if(simValueFormatted != null && !simValueFormatted.isEmpty()) {
						 eLineId = centuryCIFTemplate.queryForObject(CommonConstants.GETLINEBYICCID,
								new Object[] { simValueFormatted }, String.class);
					}
					if (eLineId != null && !eLineId.isEmpty()) {
						deviceBean.seteLineId(eLineId);
						deviceBean = apolloNEOutboundClientService.callDeviceResourceService(deviceBean);
						if(deviceBean != null) {
							deviceImei=deviceBean.getImei();
						}
					}*/
					if (formattedJson.contains("\"deviceId\":[{\"type\":\"IMEI\",\"value\":\"\"}]") || formattedJson.contains("\"deviceId\": [{\"type\":\"IMEI\",\"value\":\"\"}]")) {
						String replaceIMEI="";
						log.debug("Before Replacing Retrieve Device for IMEI::" + formattedJson);
						formattedJson = formattedJson.replace("\"deviceId\":[{\"type\":\"IMEI\",\"value\":\"\"}],",replaceIMEI);
					}
					if (formattedJson.contains("\"simId\":[{\"type\":\"ICCID\",\"value\":\"\"}]") || formattedJson.contains("\"simId\": [{\"type\":\"ICCID\",\"value\":\"\"}]")) {
						String replaceICCID="";
						log.debug("Before Replacing Retrieve Device for simid::" + formattedJson);
						formattedJson = formattedJson.replace(",\"simId\":[{\"type\":\"ICCID\",\"value\":\"\"}]",replaceICCID);
					}
					log.debug("Inside final Retrieve Device formattedJson::" + formattedJson);
				}
				if (formattedJson.contains("\"subscribe\":\"A\"")) {
					formattedJson = formattedJson.replace("\"subscribe\":\"A\"", "\"subscribe\":\"true\"");
				}
				if (formattedJson.contains("\"subscribe\":\"D\"")) {
					formattedJson = formattedJson.replace("\"subscribe\":\"D\"", "\"subscribe\":\"false\"");
				}
				if (formattedJson.contains("\"subscribe\":\"T\"")) {
					formattedJson = formattedJson.replace("\"subscribe\":\"T\"", "\"subscribe\":\"true\"");
				}
				if (formattedJson.contains("\"subscribe\":\"F\"")) {
					formattedJson = formattedJson.replace("\"subscribe\":\"F\"", "\"subscribe\":\"false\"");
				}
				if (operationName.equalsIgnoreCase("Change Feature")) {
					log.debug("Inside Change Feature formattedJson::" + formattedJson);
					String requestType = "";
					String deviceIdType = "";
					String deviceidValue = "";
					JSONObject dataOject = new JSONObject();
					JSONObject subOrderObjct = new JSONObject();
					JSONArray subOrderArray = new JSONArray();
					JSONArray deviceIdArray = new JSONArray();
					JSONObject deviceIdObj = new JSONObject();
					JSONObject object = new JSONObject(formattedJson);
					if (object.has("messageHeader")) {
						JSONObject messageObj = object.getJSONObject("messageHeader");
						log.info("messageObj for Change Feature0::" + messageObj);
						/*
						 * if (messageObj.has("requestType")) { requestType =
						 * messageObj.getString("requestType"); if
						 * (requestType.equalsIgnoreCase("MNO-AP")) {
						 */
						
						/*
						 * } }
						 */
					}
					if (object.has("data")) {
						dataOject = object.getJSONObject("data");
						if (dataOject.has("subOrder")) {
							subOrderArray = dataOject.getJSONArray("subOrder");
							subOrderObjct = subOrderArray.getJSONObject(0);
							if (subOrderObjct.has("deviceId")) {
								deviceIdArray = subOrderObjct.getJSONArray("deviceId");
								deviceIdObj = deviceIdArray.getJSONObject(0);
								if (deviceIdObj.has("type")) {
									deviceIdType = deviceIdObj.getString("type");
								}
								if (deviceIdObj.has("value")) {
									deviceidValue = deviceIdObj.getString("value");
								}

								if (formattedJson.contains(
										"\"deviceId\":[{\"type\":\"IMEI\",\"value\":\"" + deviceidValue + "\"}]")) {
									String replaceDeviceValue = "\"deviceId\":[{\"type\":\"dataType\",\"value\":\"\"}]";
									formattedJson = formattedJson.replace(
											"\"deviceId\":[{\"type\":\"IMEI\",\"value\":\"" + deviceidValue + "\"}]",
											replaceDeviceValue);
								}
								if (formattedJson.contains(
										"\"deviceId\":[{\"type\":\"newIMEI\",\"value\":\"" + deviceidValue + "\"}]")) {
									String replaceDeviceValue0 = "\"deviceId\":[{\"type\":\"dataType\",\"value\":\"\"}]";
									formattedJson = formattedJson.replace(
											"\"deviceId\":[{\"type\":\"newIMEI\",\"value\":\"" + deviceidValue + "\"}]",
											replaceDeviceValue0);
								}

							}
						}
					}
					log.debug("formatted Json Change Feature STEP 6::" + formattedJson);
					String server = properties.getServer();
					String server_url = properties.getDns();
					JsonObject reqJsonData = new JsonParser().parse(formattedJson).getAsJsonObject();
					JsonObject requestparamsJsonData = new JsonParser().parse(requestParams).getAsJsonObject();
					if (reqJsonData.has("data")) {
						JsonObject reqJson = reqJsonData;
						JsonObject deviceIdJson = null;
						String imei = "";
						String dataType = "";
						String result = rcsDao.getRequestDetails(responseId);
						log.debug("Inside result Json::" + result);
						JsonObject reqJsonDataObject = new JsonParser().parse(result).getAsJsonObject();

						if (reqJson.get("messageHeader").getAsJsonObject().has("futureDateTime")) {
							log.debug("Inside messageHeader futureDateTime Json0::" + reqJson);
							reqJson.get("messageHeader").getAsJsonObject().addProperty("futureDateTime",
									getTimeStamp());
						}
						if (operationName.equalsIgnoreCase("Change Feature")) {
							if (reqJson.has("data")) {
								if (reqJson.get("data").getAsJsonObject().has("subOrder")) {
									if (reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray().get(0)
											.getAsJsonObject().has("deviceId")) {
										deviceIdJson = reqJson.get("data").getAsJsonObject().get("subOrder")
												.getAsJsonArray().get(0).getAsJsonObject().get("deviceId")
												.getAsJsonArray().get(0).getAsJsonObject();
										if (deviceIdJson.has("type")) {
											String deviceIdValue = "";
											String lineId = "";
											String deviceType = "4G";
											String lineType ="";
											String mdnValue ="";
											Line lineBean = new Line();
											Device deviceBean = new Device();
											deviceIdValue = deviceIdJson.get("value").getAsString();
											log.debug("Change feature deviceIdValue ::" + deviceIdValue);
											log.debug("requestparamsJsonData for Change Feature::"
													+ requestparamsJsonData);
											if (requestparamsJsonData.has("data")) {
												if (requestparamsJsonData.get("data").getAsJsonObject()
														.has("subOrder")) {
													if (requestparamsJsonData.get("data").getAsJsonObject()
															.get("subOrder").getAsJsonArray().get(0).getAsJsonObject()
															.has("deviceId")) {
														deviceIdFlag = true;
													}
													/*
													 * if (deviceIdValue == null || deviceIdValue.equalsIgnoreCase("")
													 * || deviceIdValue.isEmpty()) {
													 */
														if (requestparamsJsonData.get("data").getAsJsonObject()
																.get("subOrder").getAsJsonArray().get(0)
																.getAsJsonObject().has("lineId")) {
															lineId = requestparamsJsonData.get("data").getAsJsonObject()
																	.get("subOrder").getAsJsonArray().get(0)
																	.getAsJsonObject().get("lineId").getAsString();															
															if (lineId != null && !lineId.isEmpty()
																	&& !lineId.equalsIgnoreCase("")) {																
																lineBean=apolloNEOutboundClientService.callLineResourceServiceByLineId(lineId);
																log.info("callLineResourceService :: lineBean :: " + lineBean);
																if (lineBean != null) {
																	lineType = lineBean.getLineType();
																	mdnValue = lineBean.getMdn();
																	if (lineType != null && lineType
																			.equalsIgnoreCase("SMARTWATCH")) {
																		log.info("lineType ::" + lineType
																				+ " mdnValue:: " + mdnValue);
																		JsonArray mdnArray = new JsonArray();
																		JsonObject mdnObj = new JsonObject();
																		mdnObj.addProperty("type", "mdn");
																		mdnObj.addProperty("value", mdnValue);
																		mdnArray.add(mdnObj);
																		reqJson.get("data").getAsJsonObject()
																				.get("subOrder").getAsJsonArray().get(0)
																				.getAsJsonObject().add("mdn", mdnArray);

																	}
																}
																deviceBean.seteLineId(lineId);
																deviceBean = apolloNEOutboundClientService
																		.callDeviceResourceService(deviceBean);
																if (deviceBean != null) {
																	deviceType = deviceBean.getDeviceType();
																	if (deviceType != null && !lineType
																			.equalsIgnoreCase("SMARTWATCH")) {
																		if (deviceType.equalsIgnoreCase("5GDevice")) {
																			deviceType = "5G";
																		} else if (deviceType.equalsIgnoreCase("5G")) {
																			deviceType = "5G";
																		} else {
																			deviceType = "4G";
																		}
																	}
																	if(lineType != null && lineType
																			.equalsIgnoreCase("SMARTWATCH")) {
																		deviceType = "4G";
																	}
																	log.info("deviceType 1::" + deviceType);
																	if (deviceBean.getImei() != null
																			&& !deviceBean.getImei().isEmpty()
																			&& !deviceBean.getImei()
																					.equalsIgnoreCase("")) {
																		log.info("deviceType 2::" + deviceType);																		
																		reqJson.get("data").getAsJsonObject()
																				.get("subOrder").getAsJsonArray().get(0)
																				.getAsJsonObject().get("deviceId")
																				.getAsJsonArray().get(0)
																				.getAsJsonObject()
																				.addProperty("type", "dataType");
																		reqJson.get("data").getAsJsonObject()
																				.get("subOrder").getAsJsonArray().get(0)
																				.getAsJsonObject().get("deviceId")
																				.getAsJsonArray().get(0)
																				.getAsJsonObject()
																				.addProperty("value", deviceType);
																	}

																}
																
															}
														}
													//}
												}
											}
										}
									}
								}
							}
							if (reqJsonDataObject.has("data")) {
								if (reqJsonDataObject.get("data").getAsJsonObject().has("transactionType")) {
									String CFTransType = reqJsonDataObject.get("data").getAsJsonObject()
											.get("transactionType").getAsString();
									log.debug("CFTransType Change Feature::" + CFTransType);
									if (CFTransType.equalsIgnoreCase("CR")) {
										String lineId = reqJsonDataObject.get("data").getAsJsonObject().get("subOrder")
												.getAsJsonArray().get(0).getAsJsonObject().get("lineId").getAsString();
										if (lineId != null && !lineId.isEmpty() && !lineId.equalsIgnoreCase("")) {
											LinePlan lineplan = new LinePlan();
											lineplan.setELineId(lineId);
											lineplan = apolloNEOutboundClientService
													.callLinePlanResourceService(lineplan);
											if (lineplan.getPlanGroup().equalsIgnoreCase("OCS")) {
												if (lineplan.getWhsPlan() != null) {
													String whlsPlanCode = lineplan.getWhsPlan();
													if (reqJsonDataObject.get("data").getAsJsonObject().get("subOrder")
															.getAsJsonArray().get(0).getAsJsonObject()
															.has("newRatePlan")) {
														String RetailPlanCode = reqJsonDataObject.get("data")
																.getAsJsonObject().get("subOrder").getAsJsonArray()
																.get(0).getAsJsonObject().get("newRatePlan")
																.getAsString();
														String dataLimitValues = getDataLimitValues(whlsPlanCode,
																RetailPlanCode);
														String[] dataLimitValuesSplit = dataLimitValues.split("~");
														String customPDL = dataLimitValuesSplit[0];
														String mhsCustomPDL = dataLimitValuesSplit[1];
														JsonArray additionalDataArray = new JsonArray();

														if (reqJson.get("data").getAsJsonObject().get("subOrder")
																.getAsJsonArray().get(0).getAsJsonObject()
																.has("additionalData")) {
															additionalDataArray = reqJson.get("data").getAsJsonObject()
																	.get("subOrder").getAsJsonArray().get(0)
																	.getAsJsonObject().get("additionalData")
																	.getAsJsonArray();
															reqJson.get("data").getAsJsonObject().get("subOrder")
																	.getAsJsonArray().get(0).getAsJsonObject()
																	.remove("additionalData");
														}
														JsonObject customPDLObj = new JsonObject();
														JsonObject mhsCustomPDLObj = new JsonObject();
														customPDLObj.addProperty("name", "customPDL");
														customPDLObj.addProperty("value", customPDL);
														additionalDataArray.add(customPDLObj);
														mhsCustomPDLObj.addProperty("name", "MHSCustomPDL");
														mhsCustomPDLObj.addProperty("value", mhsCustomPDL);
														additionalDataArray.add(mhsCustomPDLObj);

														reqJson.get("data").getAsJsonObject().get("subOrder")
																.getAsJsonArray().get(0).getAsJsonObject()
																.add("additionalData", additionalDataArray);
													}
												}
											}
										}
									} else if (CFTransType.equalsIgnoreCase("WC")) {
										log.debug("CFTransType WC scenario::" + requestparamsJsonData);
										String customPDL = "";
										String mhsCustomPDL = "";
										JsonArray newfeatureArr = new JsonArray();
										if (requestparamsJsonData.has("data")) {
											if (requestparamsJsonData.get("data").getAsJsonObject().has("subOrder")) {
												if (requestparamsJsonData.get("data").getAsJsonObject().get("subOrder")
														.getAsJsonArray().get(0).getAsJsonObject().has("customPDL")) {
													customPDL = requestparamsJsonData.get("data").getAsJsonObject()
															.get("subOrder").getAsJsonArray().get(0).getAsJsonObject()
															.get("customPDL").getAsString();
												}
												if (requestparamsJsonData.get("data").getAsJsonObject().get("subOrder")
														.getAsJsonArray().get(0).getAsJsonObject()
														.has("mhsCustomPDL")) {
													mhsCustomPDL = requestparamsJsonData.get("data").getAsJsonObject()
															.get("subOrder").getAsJsonArray().get(0).getAsJsonObject()
															.get("mhsCustomPDL").getAsString();
												}
												if(requestparamsJsonData.get("data").getAsJsonObject()
															.get("subOrder").getAsJsonArray().get(0).getAsJsonObject()
														.has("feature")) {
													newfeatureArr = requestparamsJsonData.get("data").getAsJsonObject()
															.get("subOrder").getAsJsonArray().get(0).getAsJsonObject()
															.get("feature").getAsJsonArray();
													log.debug("WC newfeatureArr:: " + newfeatureArr);
													reqJson.get("data").getAsJsonObject().get("subOrder")
															.getAsJsonArray().get(0).getAsJsonObject()
															.remove("feature");
													reqJson.get("data").getAsJsonObject().get("subOrder")
															.getAsJsonArray().get(0).getAsJsonObject()
															.add("feature", newfeatureArr);
												}
											}
										}
										log.debug("formattedJson WC customPDL:: " + customPDL + " ::mhsCustomPDL:: "+mhsCustomPDL);
										if (StringUtils.hasText(customPDL) && StringUtils.hasText(mhsCustomPDL)) {
											JsonArray additionalDataArray = new JsonArray();
											if (reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray()
													.get(0).getAsJsonObject().has("additionalData")) {
												additionalDataArray = reqJson.get("data").getAsJsonObject()
														.get("subOrder").getAsJsonArray().get(0).getAsJsonObject()
														.get("additionalData").getAsJsonArray();
												reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray()
														.get(0).getAsJsonObject().remove("additionalData");
											}
											JsonObject customPDLObj = new JsonObject();
											JsonObject mhsCustomPDLObj = new JsonObject();
											customPDLObj.addProperty("name", "customPDL");
											customPDLObj.addProperty("value", customPDL);
											additionalDataArray.add(customPDLObj);
											mhsCustomPDLObj.addProperty("name", "MHSCustomPDL");
											mhsCustomPDLObj.addProperty("value", mhsCustomPDL);
											additionalDataArray.add(mhsCustomPDLObj);
											reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray()
													.get(0).getAsJsonObject()
													.add("additionalData", additionalDataArray);
											log.debug("formattedJson WC after customPDL-mhsCustomPDL:: " + reqJson);
										}
									}
								}
							}
						}
					}
					formattedJson = reqJsonData.toString();
					if (formattedJson.contains("\"subscribe\":\"T\"")) {
						formattedJson = formattedJson.replace("\"subscribe\":\"T\"", "\"subscribe\":\"true\"");
					}
					if (formattedJson.contains("\"subscribe\":\"F\"")) {
						formattedJson = formattedJson.replace("\"subscribe\":\"F\"", "\"subscribe\":\"false\"");
					}
					log.debug("formattedJson after subscriberGroup Json::" + formattedJson);
				}
				
				log.debug("opearionName::" + operationName);
				if (operationName.equalsIgnoreCase("Device Detection Change Rate Plan")) {
						log.debug("Inside Device Detection Change Rate Plan formattedJson::" + formattedJson);
						String deviceType="5G";
						String dataValType="";
						JSONObject dataOject = new JSONObject();
						JSONObject messageOject = new JSONObject();
						JSONObject subOrderObjct = new JSONObject();
						JSONArray subOrderArray = new JSONArray();
						JSONObject object = new JSONObject(formattedJson);
						String deviceidValue = "";
						String deviceIdType = "";
						String refNumb = "";
						JSONArray deviceIdArray = new JSONArray();
						JSONObject deviceIdObj = new JSONObject();	
						String rootTransId="";
						String outboundResponse="";
						log.debug("formatted Json Device Detection Change Rate Plan::" + formattedJson);
						try {
							if(object.has("messageHeader")) {
								messageOject = object.getJSONObject("messageHeader");
								if(messageOject.has("referenceNumber")) {
									refNumb=messageOject.getString("referenceNumber");
									if(refNumb != null) {
										rootTransId = rcsDao.getRootTransactionId(refNumb);
										 outboundResponse = rcsDao.getOutboundCallResponseMsg(rootTransId,"Validate Device");
										 log.debug("outboundResponse::"+outboundResponse);
									}
								}
							}
							if (object.has("data")) {
								dataOject = object.getJSONObject("data");
								if (dataOject.has("subOrder")) {
									subOrderArray = dataOject.getJSONArray("subOrder");
									subOrderObjct = subOrderArray.getJSONObject(0);
									if (subOrderObjct.has("deviceId_New")) {
										deviceIdArray = subOrderObjct.getJSONArray("deviceId_New");
										deviceIdObj = deviceIdArray.getJSONObject(0);
										if (deviceIdObj.has("type")) {
											deviceIdType = deviceIdObj.getString("type");
										}
										if (deviceIdObj.has("value")) {
											deviceidValue = deviceIdObj.getString("value");
										}
									}else if(subOrderObjct.has("deviceId")) {
										deviceIdArray = subOrderObjct.getJSONArray("deviceId");
										deviceIdObj = deviceIdArray.getJSONObject(0);
										if (deviceIdObj.has("type")) {
											deviceIdType = deviceIdObj.getString("type");
										}
										if (deviceIdObj.has("value")) {
											deviceidValue = deviceIdObj.getString("value");
										}
									}
								}
								log.debug("deviceIdType::" + deviceIdType + "::deviceidValue::"+deviceidValue);
							}
							
							if(outboundResponse!=null && !outboundResponse.equalsIgnoreCase("null")){
								JsonObject outboundResponseObj = new JsonParser().parse(outboundResponse)
									.getAsJsonObject();
								if (outboundResponseObj.has("data")) {
									outboundResponseObj = outboundResponseObj.get("data").getAsJsonObject();
									if(outboundResponseObj.has("deviceType")) {
										deviceType=outboundResponseObj.get("deviceType").getAsString();
									}else if (outboundResponseObj.has("equipmentInfo")) {
										JsonArray equipmentArr = outboundResponseObj.get("equipmentInfo").getAsJsonArray();
										for (int i = 0; i < equipmentArr.size(); i++) {
											JsonObject equipmentObj = equipmentArr.get(i).getAsJsonObject();
											if (equipmentObj.has("type")) {
												String deviceidType = equipmentObj.get("type").getAsString();
												if (deviceidType.equals("mode")) {
													if(equipmentObj.has("value")){
														dataValType = equipmentObj.get("value").getAsString();
														deviceType = dataValType.substring(0, 2);
													}
													log.debug("dataType VALUE::" + deviceType);
												}
											}
										}
									}
								}
							}
							log.debug("dataType VALUE::" + deviceType);
							if (formattedJson.contains("\"deviceId_New\":[{\"type\":\""+deviceIdType+"\",\"value\":\""+deviceidValue+"\"}]")) {
								String replaceDeviceValue = "\"deviceId_New\":[{\"type\":\"dataType\",\"value\":\""+deviceType+"\"}]";
								log.debug("replaceDeviceValue::" + replaceDeviceValue);
								formattedJson = formattedJson.replace("\"deviceId_New\":[{\"type\":\""+deviceIdType+"\",\"value\":\""+deviceidValue+"\"}]",
										replaceDeviceValue);                    
							}else if (formattedJson.contains("\"deviceId\":[{\"type\":\""+deviceIdType+"\",\"value\":\""+deviceidValue+"\"}]")) {
								String replaceDeviceValue = "\"deviceId\":[{\"type\":\"dataType\",\"value\":\""+deviceType+"\"}]";
								log.debug("replaceDeviceValue1::" + replaceDeviceValue);
								formattedJson = formattedJson.replace("\"deviceId\":[{\"type\":\""+deviceIdType+"\",\"value\":\""+deviceidValue+"\"}]",replaceDeviceValue);                    
							}
							log.debug("formattedJson after Device Detection Change Rate Plan Json::" + formattedJson);
						}catch(Exception e) {
							log.error("Error in Device Detection Change Rate Plan::",e);
						}
				}
				
				if (operationName.equalsIgnoreCase(CommonConstants.TW_DEACTIVATE_SUBSCRIBER)) {
					log.debug("Inside TW Deactivate Subscriber ::");
					if (formattedJson.contains("\"type\":\"hostMDN\"")) {
						formattedJson = formattedJson.replace("\"type\":\"hostMDN\"", "\"type\":\"hostMdn\"");
						log.debug("TW Deactivate Subscriber ::" + formattedJson);
					}
					log.debug("TW_DEACTIVATE_SUBSCRIBER formattedJson::" + formattedJson);
					log.debug("TW_DEACTIVATE_SUBSCRIBER requestParams::" + requestParams);
					String wearableMdnValue = "";
					String lineInquirylineIdValue = "";
					requestParamBean = getRequestParamBean(requestParams);
					log.debug("TW_DEACTIVATE_SUBSCRIBER requestParamBean::" + requestParamBean);
					if (requestParamBean != null) {
						Line line = new Line();
						if (requestParamBean.getLineId() != null) {
							lineInquirylineIdValue = requestParamBean.getLineId();
							if (lineInquirylineIdValue != null && !lineInquirylineIdValue.isEmpty()) {
								line = apolloNEOutboundClientService
										.callLineResourceServiceByLineId(lineInquirylineIdValue);
								log.info("TW_DEACTIVATE_SUBSCRIBER line:: " + line);
								if (line != null) {
									wearableMdnValue = line.getMdn();
									log.info("TW_DEACTIVATE_SUBSCRIBER wearableMdnValue:: " + wearableMdnValue);
								}
							}
						}
					}
					if (formattedJson.startsWith("{")) {
						JsonObject reqJsonTW = new JsonParser().parse(formattedJson).getAsJsonObject();
						if (reqJsonTW.has("data")) {
							JsonObject reqJsonTWData = reqJsonTW.get("data").getAsJsonObject();
							if (reqJsonTWData.has("subOrder")) {
								if (reqJsonTWData.get("subOrder").getAsJsonArray().get(0).getAsJsonObject()
										.has("mdn")) {
									JsonObject reqMdnJson = reqJsonTWData.get("subOrder")
											.getAsJsonArray().get(0).getAsJsonObject().get("mdn").getAsJsonArray()
											.get(0).getAsJsonObject();
									log.debug("TW_DEACTIVATE_SUBSCRIBER reqMdnJson Json::" + reqMdnJson);
									if (reqMdnJson.has("value")) {
										reqMdnJson.remove("value");
										reqMdnJson.addProperty("value", wearableMdnValue);
									}
									formattedJson = reqJsonTW.toString();
									log.debug("reqMdnJson formattedJson::" + formattedJson);
								}
							}
						}
					}
				}
				log.debug("TW_DEACTIVATE_SUBSCRIBER formattedJson after replacement with werable mdn::" + requestParamBean);
				
				if (operationName.equalsIgnoreCase("Change Feature")) {
					if (formattedJson.startsWith("{")) {
						JsonObject changeFeaturerequest = new JsonParser().parse(requestParams).getAsJsonObject();
						if (changeFeaturerequest.has("data")) {
							if (changeFeaturerequest.get("data").getAsJsonObject().has("subOrder")) {
								if (changeFeaturerequest.get("data").getAsJsonObject().get("subOrder").getAsJsonArray()
										.get(0).getAsJsonObject().has("isPilotPlanMigration")) {
									isPilotPlan = changeFeaturerequest.get("data").getAsJsonObject()
											.get("subOrder").getAsJsonArray().get(0).getAsJsonObject()
											.get("isPilotPlanMigration").getAsString();
									isPilotPlanMigrationflag=true;
									log.info("isPilotPlanMigrationflag ::"+isPilotPlanMigrationflag);
								}else if (changeFeaturerequest.get("data").getAsJsonObject().get("subOrder").getAsJsonArray()
										.get(0).getAsJsonObject().has("isPlanMigration")) {
									isPilotPlan = changeFeaturerequest.get("data").getAsJsonObject()
											.get("subOrder").getAsJsonArray().get(0).getAsJsonObject()
											.get("isPlanMigration").getAsString();
									isPilotPlanMigrationflag=true;
									log.info("isPilotPlanMigrationflag ::"+isPilotPlanMigrationflag);
								}
							}
						}
					}
				}
				if ((operationName.equalsIgnoreCase("Activate Subscriber PSIM"))
						|| (operationName.equalsIgnoreCase("Activate Subscriber ESIM"))
						|| (operationName.equalsIgnoreCase(" Activate Subscriber ESIM"))
						|| (operationName.equalsIgnoreCase("Add Wearable"))
						|| (operationName.equalsIgnoreCase("ChangeESIM"))
						|| (operationName.equalsIgnoreCase(CommonConstants.ACTIVATESUBSCRIBER))
						|| (operationName.equalsIgnoreCase(CommonConstants.ACTIVATESUBSCRIBER_PORTIN))
						|| (operationName.equalsIgnoreCase("UpdateSubscriber Group"))
						|| (operationName.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER))
						|| (operationName.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER_PORTIN))
						|| operationName.equalsIgnoreCase("Change Wholesale Rate Plan")
						|| (operationName.equalsIgnoreCase(CommonConstants.CHANGE_BCD))
						|| operationName.equalsIgnoreCase(CommonConstants.RESET_FEATURE)
						|| operationName.equalsIgnoreCase("Device Detection Change Rate Plan")
						|| (operationName.equalsIgnoreCase("Change Feature") && isPilotPlanMigrationflag)
						|| operationName.equalsIgnoreCase("Change Rate Plan")) {
					log.debug("Inside subscriberGroup Json::" + formattedJson);
					log.debug("Inside requestParams Json::" + requestParams);
					Boolean pilotTableFlag = false;
					// Code for zipCode Inventory
					/*
					 * if(operationName.equalsIgnoreCase("Activate Subscriber PSIM") ||
					 * operationName.equalsIgnoreCase("Activate Subscriber ESIM")) {
					 * log.debug("Inside formattedJson zipCode Inventory::" + formattedJson); String
					 * zipCodePrimary = ""; String nearestZipCodeFromInventory = ""; if
					 * (formattedJson.startsWith("{")) { JSONObject formattedDataObj0 = new
					 * JSONObject(formattedJson); if (formattedDataObj0.has("data")) {
					 * formattedDataObj0 = formattedDataObj0.getJSONObject("data"); log.debug(
					 * "formattedJson in ActivateSubscriber PSIM or ActivateSubscriber ESIM ::" +
					 * formattedDataObj0); if (formattedDataObj0.has("subOrder")) { JSONArray
					 * subOrderArr = formattedDataObj0.getJSONArray("subOrder"); JSONObject
					 * subOrderObj = subOrderArr.getJSONObject(0);
					 * 
					 * if (subOrderObj.has("nextAvailableMDN")) { subOrderObj =
					 * subOrderObj.getJSONObject("nextAvailableMDN"); if
					 * (subOrderObj.has("zipCode")) { zipCodePrimary =
					 * subOrderObj.getString("zipCode"); log.debug(
					 * "ActivateSubscriber PSIM or ActivateSubscriber ESIM zipCode Inventory zipCodePrimary::"
					 * + zipCodePrimary); String response =
					 * apolloNEOutboundClientService.getNpaNxxDetails(zipCodePrimary); log.debug(
					 * "ActivateSubscriber PSIM or ActivateSubscriber ESIM  zipCode Inventory response::"
					 * + response); if (response != null) { JSONArray result = new
					 * JSONArray(response); if (result != null && result.length() > 0) { for (int i
					 * = 0; i < result.length(); i++) { if (result.getJSONObject(i).get("zipCode")
					 * != null) { nearestZipCodeFromInventory = result.getJSONObject(i)
					 * .getString("zipCode"); log.debug( "zipCode Inventory " +
					 * nearestZipCodeFromInventory); if (nearestZipCodeFromInventory !=
					 * zipCodePrimary) { break; } } } } } } } } } } // if
					 * (nearestZipCodeFromInventory != null &&
					 * !nearestZipCodeFromInventory.isEmpty()) { // if
					 * (formattedJson.contains("\"nextAvailableMDN\":{\"zipCode\":\"" +
					 * zipCodePrimary + "\"}")) { // formattedJson = formattedJson.replace( //
					 * "\"nextAvailableMDN\":{\"zipCode\":\"" + zipCodePrimary + "\"}", //
					 * "\"nextAvailableMDN\":{\"zipCode\":\"" + nearestZipCodeFromInventory +
					 * "\"}"); // log.debug( //
					 * "Activate Subscriber or CHANGE_MDN or ESIM ActivateSubscriber zipCode Inventory formattedJson after inventory replacement::"
					 * // + formattedJson); // } // } }
					 */

					String server = properties.getServer();
					String server_url = properties.getDns();
					JsonObject requestparamsJsonData = new JsonObject();
					JsonObject reqJsonData = new JsonParser().parse(formattedJson).getAsJsonObject();
					if (operationName.equalsIgnoreCase(CommonConstants.ACTIVATESUBSCRIBER)
							|| operationName.equalsIgnoreCase(CommonConstants.ACTIVATESUBSCRIBER_PORTIN)) {
						if (requestParams.startsWith("[")) {
							JsonArray requestparamsJsonArray = new JsonParser().parse(requestParams).getAsJsonArray();
							requestparamsJsonData = requestparamsJsonArray.get(0).getAsJsonObject();
						} else {
							requestparamsJsonData = new JsonParser().parse(requestParams).getAsJsonObject();
						}
					} else {
						requestparamsJsonData = new JsonParser().parse(requestParams).getAsJsonObject();
					}
					if (reqJsonData.has("data")) {
						log.debug("Inside reqJsonData Json::" + reqJsonData);
						JsonObject reqJson = reqJsonData;
						JsonObject deviceIdJson = null;
						JsonObject reqMdnJson = reqJsonData;
						String accountnumber = "";
						String billCycleDay = "";
						String changeBcdBillCycleDay = "";
						String contextid = "";
						String subscriberGroupCdVal = "";
						String mdnValue = "";
						String acctType = "";
						String imei = "";
						String dataType = "";
						String customPDL = "";
						String mhsCustomPDL = "";
						String dataValType = "";
						String retailPlancode = "";
						String wholesalePlanCode = "";
						String isPilotPlanMigration = "";
						String mdnValfromrequest="";
						JsonArray additionalDataArray = new JsonArray();
						String result = rcsDao.getRequestDetails(responseId);
						log.debug("Inside result Json::" + result);
						JsonObject reqJsonDataObject = new JsonParser().parse(result).getAsJsonObject();
						if (reqJson.get("data").getAsJsonObject().has("account")) {
							log.debug("Inside account Json::" + reqJson);
							JsonObject accountObj = reqJson.get("data").getAsJsonObject().get("account")
									.getAsJsonObject();
							if (accountObj.has("accountNumber")) {
								accountnumber = accountObj.get("accountNumber").getAsString();

							} else {

								JsonObject accJsonDataObject = reqJsonDataObject.get("data").getAsJsonObject()
										.get("account").getAsJsonObject();
								accountnumber = accJsonDataObject.get("accountNumber").getAsString();
							}
							if (reqJsonDataObject.get("data").getAsJsonObject().has("subOrder")) {
								JsonObject dataObject = reqJsonDataObject.get("data").getAsJsonObject().get("subOrder")
										.getAsJsonArray().get(0).getAsJsonObject();

								if (dataObject.has("deviceId")) {
									JsonArray deviceIdArr = dataObject.get("deviceId").getAsJsonArray();
									for (int i = 0; i < deviceIdArr.size(); i++) {
										JsonObject deviceidObj = deviceIdArr.get(i).getAsJsonObject();
										if (deviceidObj.has("type")) {
											String deviceidType = deviceidObj.get("type").getAsString();
											if (deviceidType.equals("IMEI")) {
												imei = deviceidObj.get("value").getAsString();
												log.debug("IMEI_VALUE::" + imei);
												if (operationName.equalsIgnoreCase("Activate Subscriber ESIM")
												|| operationName.equalsIgnoreCase("Activate Subscriber PSIM")
												|| operationName.equalsIgnoreCase(CommonConstants.ACTIVATESUBSCRIBER)
												|| operationName.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER)
												|| operationName.equalsIgnoreCase(CommonConstants.ACTIVATESUBSCRIBER_PORTIN)
												|| operationName
														.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER_PORTIN)
												|| operationName.equalsIgnoreCase("Add Wearable")) {
													if (imei != null && !imei.isEmpty()) {
														RefPilotPrg refpilotPrg = new RefPilotPrg();
														refpilotPrg.setImei(imei);
														refpilotPrg = apolloNEOutboundClientService
																.callRefPilotTableFromImei(refpilotPrg);
														if (refpilotPrg != null) {
															log.debug("refpilotPrg response::" + refpilotPrg);
															log.debug("refpilotPrg response::"
																	+ refpilotPrg.getPilotProgram());
														}
														if (refpilotPrg.getPilotProgram() != null) {
															pilotTableFlag = true;
														}
													}
												}
											}
											if (deviceidType.equals("mode")) {
												if(deviceidObj.has("value")){
													dataType = deviceidObj.get("value").getAsString();
													dataType = dataType.substring(0, 2);
													log.debug("dataType_VALUE::" + dataType);
												}
											}
										}
									}
									if (dataObject.has("customPDL")) {
										customPDL = dataObject.get("customPDL").getAsString();
										log.debug("customPDL VALUE::" + customPDL);
									}
									if (dataObject.has(CommonConstants.mhsCustomPDL)) {
										mhsCustomPDL = dataObject.get(CommonConstants.mhsCustomPDL).getAsString();
										log.debug("mhsCustomPDL VALUE::" + mhsCustomPDL);
									}
								}
								if(dataObject.has("lineId")){
									String lineId = dataObject.get("lineId").getAsString();
									if (!operationName.equalsIgnoreCase("Activate Subscriber ESIM")
										&& !operationName.equalsIgnoreCase("Activate Subscriber PSIM")
										&& !operationName.equalsIgnoreCase(CommonConstants.ACTIVATESUBSCRIBER)
										&& !operationName.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER)
										&& !operationName.equalsIgnoreCase(CommonConstants.ACTIVATESUBSCRIBER_PORTIN)
										&& !operationName
												.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER_PORTIN)
										&& !operationName.equalsIgnoreCase("Add Wearable")) {
										LinePlan lineplan = new LinePlan();
										lineplan.setELineId(lineId);
										lineplan = apolloNEOutboundClientService
												.callLinePlanResourceService(lineplan);
										if (lineplan.getRetailPlan() != null) {
											retailPlancode = lineplan.getRetailPlan();
										}
										if(lineplan.getPlanCategory() != null){
											if(lineplan.getPlanGroup() != null&&("OCS").equalsIgnoreCase(lineplan.getPlanGroup())){
											pilotTableFlag = true;

											}
										}
										
									}
								}
								if (dataObject.has("planCode")) {
									retailPlancode = dataObject.get("planCode").getAsString();
								}
							}

							if (accountnumber == null || accountnumber.equalsIgnoreCase("")) {
								if (reqJson.get("data").getAsJsonObject().has("subOrder")) {
									if (reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray().get(0)
											.getAsJsonObject().has("mdn")) {
										reqMdnJson = reqJson.get("data").getAsJsonObject().get("subOrder")
												.getAsJsonArray().get(0).getAsJsonObject().get("mdn").getAsJsonArray()
												.get(0).getAsJsonObject();
										log.debug("reqMdnJson Json::" + reqMdnJson);
										mdnValue = reqMdnJson.get("value").getAsString();
										if (mdnValue != null && !mdnValue.isEmpty()) {
											Line line = new Line();
											line.setMdn(mdnValue);
											line = apolloNEOutboundClientService.callLineResourceServiceFromMdn(line);
											if (line != null) {
												if (line.getAccountNumber() != null) {
													accountnumber = line.getAccountNumber();
													log.debug("line accountnumber::" + accountnumber);
												}
												if (line.getBcd() != null) {
													billCycleDay = line.getBcd();
													log.debug("line billCycleDay::" + billCycleDay);
												}
												if (accountnumber == null) {
													accountnumber = "";
												}
												if (billCycleDay == null) {
													billCycleDay = "";
												}
											}
										}
									}
								}
							}
							if (accountnumber != null && !accountnumber.isEmpty()) {
								Account account = new Account();
								account = apolloNEOutboundClientService.callAccountResourceService(accountnumber);
								if (account != null) {
									if (account.getAccountNumber() != null) {
										acctType = account.getAccountType();
										log.debug("account type::" + acctType);
									}
									if (acctType == null) {
										acctType = "";
									}
								}
							}
							if (operationName.equalsIgnoreCase("Add Wearable")) {
								if (reqJson.get("data").getAsJsonObject().get("account").getAsJsonObject()
										.has("type")) {
									reqJson.get("data").getAsJsonObject().get("account").getAsJsonObject()
											.addProperty("type", acctType);
								}
								if (reqJson.get("data").getAsJsonObject().get("account").getAsJsonObject()
										.has("accountNumber")) {
									reqJson.get("data").getAsJsonObject().get("account").getAsJsonObject()
											.addProperty("accountNumber", accountnumber);
								}
							}
							contextid = accountObj.get("contextId").getAsString();
							subscriberGroupCdVal = server_url.contains("dev")
									? "ND" + "-" + contextid + "-" + accountnumber
									: server_url.contains("qar1.sit") ? "NS" + "-" + contextid + "-" + accountnumber
											: server_url.contains("sit") ? "NQ" + "-" + contextid + "-" + accountnumber
													: server_url.contains("qa-uat")
															? "NU" + "-" + contextid + "-" + accountnumber
																: server_url.contains("hf.preprod")
																	? "NH" + "-" + contextid + "-" + accountnumber
															: server_url.contains("prod")
																	? "NP" + "-" + contextid + "-" + accountnumber
																	: server.equalsIgnoreCase("NSL_SITQA")
																			? "NS" + "-" + contextid + "-"
																					+ accountnumber
																			: server + "-" + contextid + "-"
																					+ accountnumber;

							log.debug("Inside subscriberGroupCdVal Json::" + subscriberGroupCdVal);
							reqJson.get("data").getAsJsonObject().remove("account");
							if (operationName.equalsIgnoreCase(CommonConstants.ACTIVATESUBSCRIBER)
									|| operationName.equalsIgnoreCase(CommonConstants.ACTIVATESUBSCRIBER_PORTIN)
									|| operationName.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER)
									|| operationName.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER_PORTIN)) {
								log.debug("Inside reqJson before map Json::" + reqJson);
									if (reqJson.get("data").getAsJsonObject().has("subOrder")) {
										if (reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray().get(0)
												.getAsJsonObject().has("dataValType")) {
													reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray().get(0)
													.getAsJsonObject().remove("dataValType");
										}
									}
									if (!StringUtils.hasText(dataType)) {
										String outboundResponse = rcsDao.getOutboundCallResponseMsg(responseId,
												"Validate Device");
										if(outboundResponse!=null&&!outboundResponse.equalsIgnoreCase("null")){
											JsonObject outboundResponseObj = new JsonParser().parse(outboundResponse)
												.getAsJsonObject();
											if (outboundResponseObj.has("data")) {
												outboundResponseObj = outboundResponseObj.get("data").getAsJsonObject();
												if (outboundResponseObj.has("equipmentInfo")) {
													JsonArray equipmentArr = outboundResponseObj.get("equipmentInfo")
															.getAsJsonArray();
													for (int i = 0; i < equipmentArr.size(); i++) {
														JsonObject equipmentObj = equipmentArr.get(i).getAsJsonObject();
														if (equipmentObj.has("type")) {
															String deviceidType = equipmentObj.get("type").getAsString();
															if (deviceidType.equals("mode")) {
																if(equipmentObj.has("value")){
																	dataValType = equipmentObj.get("value").getAsString();
																	dataValType = dataValType.substring(0, 2);
																}
																log.debug("dataType_VALUE::" + dataValType);
															}
														}
													}
												}
											}
										}
									} else {
										dataValType = dataType;
									}
							}
						} else if (operationName.equalsIgnoreCase("Change Wholesale Rate Plan")) {
							if (reqJsonDataObject.get("data").getAsJsonObject().has("subOrder")) {
								JsonObject dataObject = reqJsonDataObject.get("data").getAsJsonObject().get("subOrder")
										.getAsJsonArray().get(0).getAsJsonObject();

								if (dataObject.has("customPDL")) {
									customPDL = dataObject.get("customPDL").getAsString();
									log.debug("customPDL VALUE::" + customPDL);
								}
								if (dataObject.has(CommonConstants.mhsCustomPDL)) {
									mhsCustomPDL = dataObject.get(CommonConstants.mhsCustomPDL).getAsString();
									log.debug("mhsCustomPDL VALUE::" + mhsCustomPDL);
								}
							}
						}
						if (reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray().get(0)
								.getAsJsonObject().has("planCode")) {
							wholesalePlanCode = reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray()
									.get(0).getAsJsonObject().get("planCode").getAsString();
						}
						if (reqJson.get("messageHeader").getAsJsonObject().has("futureDateTime")) {
							log.debug("Inside messageHeader futureDateTime Json::" + reqJson);
							reqJson.get("messageHeader").getAsJsonObject().addProperty("futureDateTime",
									getTimeStamp());
						}
						if (reqJson.get("data").getAsJsonObject().has("transactionTimeStamp")) {
							log.debug("Inside data transactionTimeStamp Json::" + reqJson);
							reqJson.get("data").getAsJsonObject().addProperty("transactionTimeStamp", getTimeStamp());
						}
						if (operationName.equalsIgnoreCase("ChangeESIM")) {
							if (reqJson.has("data")) {
								if (reqJson.get("data").getAsJsonObject().has("subOrder")) {
									if (reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray().get(0)
											.getAsJsonObject().has("deviceId")) {
										deviceIdJson = reqJson.get("data").getAsJsonObject().get("subOrder")
												.getAsJsonArray().get(0).getAsJsonObject().get("deviceId")
												.getAsJsonArray().get(0).getAsJsonObject();
										if (deviceIdJson.has("type")) {
											String deviceIdValue = "";
											String lineId = "";
											deviceIdValue = deviceIdJson.get("value").getAsString();
											log.debug("ChangeESIM deviceIdValue ::" + deviceIdValue);
											log.debug("requestparamsJsonData ::" + requestparamsJsonData);
											if (requestparamsJsonData.has("data")) {
												if (requestparamsJsonData.get("data").getAsJsonObject()
														.has("subOrder")) {
													if (requestparamsJsonData.get("data").getAsJsonObject()
															.get("subOrder").getAsJsonArray().get(0).getAsJsonObject()
															.has("deviceId")) {
														
														deviceIdFlag = true;
													}
													JSONObject reqObj = new JSONArray(
															consumerUtilityClass.jsonFormatter2(requestparamsJsonData.toString()))
																	.getJSONObject(0);
													if (reqObj.has("newIMEI")) {
														reqJson.get("data").getAsJsonObject().get("subOrder")
																.getAsJsonArray().get(0).getAsJsonObject()
																.get("deviceId").getAsJsonArray().get(0)
																.getAsJsonObject().addProperty("type", "IMEI");
														reqJson.get("data").getAsJsonObject().get("subOrder")
																.getAsJsonArray().get(0).getAsJsonObject()
																.get("deviceId").getAsJsonArray().get(0)
																.getAsJsonObject()
																.addProperty("value", reqObj.getString("newIMEI"));
													}
													else if (deviceIdValue == null || deviceIdValue.equalsIgnoreCase("")
															|| deviceIdValue.isEmpty()) {
														if (requestparamsJsonData.get("data").getAsJsonObject()
																.get("subOrder").getAsJsonArray().get(0)
																.getAsJsonObject().has("lineId")) {
															lineId = requestparamsJsonData.get("data").getAsJsonObject()
																	.get("subOrder").getAsJsonArray().get(0)
																	.getAsJsonObject().get("lineId").getAsString();
															if (lineId != null && !lineId.isEmpty()
																	&& !lineId.equalsIgnoreCase("")) {
																Device deviceBean = new Device();
																deviceBean.seteLineId(lineId);
																deviceBean = apolloNEOutboundClientService
																		.callDeviceResourceService(deviceBean);
																if (deviceBean != null) {
																	if (deviceBean.getImei() != null
																			&& !deviceBean.getImei().isEmpty()
																			&& !deviceBean.getImei()
																					.equalsIgnoreCase("")) {
																		reqJson.get("data").getAsJsonObject()
																				.get("subOrder").getAsJsonArray().get(0)
																				.getAsJsonObject().get("deviceId")
																				.getAsJsonArray().get(0)
																				.getAsJsonObject()
																				.addProperty("type", "IMEI");
																		reqJson.get("data").getAsJsonObject()
																				.get("subOrder").getAsJsonArray().get(0)
																				.getAsJsonObject().get("deviceId")
																				.getAsJsonArray().get(0)
																				.getAsJsonObject().addProperty("value",
																						deviceBean.getImei());
																	}
																}
															}
														} else if (requestparamsJsonData.get("data").getAsJsonObject()
																.get("subOrder").getAsJsonArray().get(0)
																.getAsJsonObject().has("mdn")) {

															String mdn = "";
															mdn = requestparamsJsonData.get("data").getAsJsonObject()
																	.get("subOrder").getAsJsonArray().get(0)
																	.getAsJsonObject().get("mdn").getAsJsonArray()
																	.get(0).getAsJsonObject().get("value")
																	.getAsString();
															if (StringUtils.hasText(mdn)) {

																Line line = new Line();
																line.setMdn(mdn);
																line = apolloNEOutboundClientService
																		.callLineResourceServiceFromMdn(line);
																if (Objects.nonNull(line)
																		&& StringUtils.hasText(line.geteLineId())) {

																	Device deviceBean = new Device();
																	deviceBean.seteLineId(line.geteLineId());
																	deviceBean = apolloNEOutboundClientService
																			.callDeviceResourceService(deviceBean);
																	if (Objects.nonNull(deviceBean) && StringUtils
																			.hasText(deviceBean.getImei())) {
																		reqJson.get("data").getAsJsonObject()
																				.get("subOrder").getAsJsonArray().get(0)
																				.getAsJsonObject().get("deviceId")
																				.getAsJsonArray().get(0)
																				.getAsJsonObject()
																				.addProperty("type", "IMEI");
																		reqJson.get("data").getAsJsonObject()
																				.get("subOrder").getAsJsonArray().get(0)
																				.getAsJsonObject().get("deviceId")
																				.getAsJsonArray().get(0)
																				.getAsJsonObject().addProperty("value",
																						deviceBean.getImei());
																	}
																}

															}
															log.debug("ChangeESIM reqJson ::" + reqJson);
														}
													}
												}
											}
										}
									} 
								}
							}
						}
						if (operationName.equalsIgnoreCase("Activate Subscriber PSIM")
								|| operationName.equalsIgnoreCase("Activate Subscriber ESIM")) {
							if (reqJson.get("data").getAsJsonObject().has("additionalData")) {
								if (!requestparamsJsonData.get("data").getAsJsonObject().has("additionalData")) {
									reqJson.get("data").getAsJsonObject().remove("additionalData");
									log.debug("formattedJson after additionalData Json::" + reqJson);
								}
								reqJson.get("data").getAsJsonObject().remove("additionalData");
							}
							/*
							 * log.
							 * debug("formattedJson before additionalData customPDL and mhsCustomPDL Json::"
							 * + reqJson); log.debug("formattedJson before customPDL::" + customPDL);
							 * log.debug("formattedJson before mhsCustomPDL::" + mhsCustomPDL); JsonObject
							 * customPDLObj = new JsonObject(); JsonObject mhsCustomPDLObj = new
							 * JsonObject(); JsonArray additionalDataArray = new JsonArray(); if (customPDL
							 * != null && !customPDL.isEmpty()) { customPDLObj.addProperty("name",
							 * "customPDL"); customPDLObj.addProperty("value", customPDL);
							 * additionalDataArray.add(customPDLObj); } if (mhsCustomPDL != null &&
							 * !mhsCustomPDL.isEmpty()) { mhsCustomPDLObj.addProperty("name",
							 * "mhsCustomPDL"); mhsCustomPDLObj.addProperty("value", mhsCustomPDL);
							 * additionalDataArray.add(mhsCustomPDLObj); } if (additionalDataArray != null
							 * && additionalDataArray.size() != 0) {
							 * reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray().get(0)
							 * .getAsJsonObject().add("additionalData", additionalDataArray); } log.
							 * debug("formattedJson after additionalData customPDL and mhsCustomPDL Json::"
							 * + reqJson);
							 */
						}
						if (reqJson.get("data").getAsJsonObject().has("subOrder")
								&& !operationName.equalsIgnoreCase(CommonConstants.CHANGE_BCD)) {
							if (reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray().get(0)
									.getAsJsonObject().has("subscriberGroup")) {
								reqJson = reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray().get(0)
										.getAsJsonObject();
								if (reqJson.has("subscriberGroup")) {
									reqJson.remove("subscriberGroup");
									JsonObject subscriberGroup = new JsonObject();
									subscriberGroup.addProperty("subscriberGroupCd", subscriberGroupCdVal);
									subscriberGroup.addProperty("subscriberGroupTypeCd", "F10");
									reqJson.add("subscriberGroup", subscriberGroup);
									log.debug("subscriberGroup Json::" + reqJson);
								}
								if (operationName.equalsIgnoreCase("Add Wearable")) {
									if (reqJson.has("mdn")) {
										log.debug("inside mdn replace condition::" + reqJson);
										reqJson.get("mdn").getAsJsonArray().get(0).getAsJsonObject().addProperty("type",
												"hostMdn");
									}
									if (reqJson.has("deviceId")) {
										log.debug("inside deviceId replace condition::" + reqJson);
										JsonObject deviceType = new JsonObject();
										JsonObject deviceObj = new JsonObject();
										JsonArray deviceTypeArr = new JsonArray();
										deviceType.addProperty("type", "dataType");
										deviceType.addProperty("value", "4G");
										deviceObj = reqJson.get("deviceId").getAsJsonArray().get(0).getAsJsonObject();
										log.debug("deviceObj json from request::" + deviceObj);
										log.debug("deviceType json::" + deviceType);
										reqJson.remove("deviceId");
										deviceTypeArr.add(deviceObj);
										deviceTypeArr.add(deviceType);
										reqJson.add("deviceId", deviceTypeArr);
										log.debug("after deviceType json ::" + reqJson);
									}
									if (!requestparamsJsonData.get("data").getAsJsonObject().has("additionalData")) {
										reqJson.remove("additionalData");
									}
									if (!reqJson.has("billCycleResetDay")) {
										reqJson.addProperty("billCycleResetDay", billCycleDay);
									}
								}
							} else if (operationName.equalsIgnoreCase("Change Wholesale Rate Plan")
									|| operationName.equalsIgnoreCase("Device Detection Change Rate Plan")
									|| operationName.equalsIgnoreCase("Change Rate Plan")) {
								reqJson = reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray().get(0)
										.getAsJsonObject();
								if (requestparamsJsonData.has("data")) {
									if (requestparamsJsonData.get("data").getAsJsonObject().has("subOrder")) {
										if (!requestparamsJsonData.get("data").getAsJsonObject().get("subOrder")
												.getAsJsonArray().get(0).getAsJsonObject().has("simId")) {
											reqJson.remove("simId");
										}
									}
								}
								if (operationName.equalsIgnoreCase("Change Wholesale Rate Plan")) {
									String lineId = reqJsonDataObject.get("data").getAsJsonObject().get("subOrder")
											.getAsJsonArray().get(0).getAsJsonObject().get("lineId").getAsString();
									if (lineId != null && !lineId.isEmpty() && !lineId.equalsIgnoreCase("")) {
										LinePlan lineplan = new LinePlan();
										lineplan.setELineId(lineId);
										lineplan = apolloNEOutboundClientService.callLinePlanResourceService(lineplan);
										Device deviceBean = new Device();
										deviceBean.seteLineId(lineId);
										deviceBean = apolloNEOutboundClientService
												.callDeviceResourceService(deviceBean);
										if (deviceBean.getImei() != null) {
											imei = deviceBean.getImei();
											log.debug("Change Wholesale Rate Plan imei::" + imei);
										}
										if (lineplan != null && lineplan.getPlanType() != null) {
											String planType = lineplan.getPlanType();
											if (planType.equalsIgnoreCase("5GDevice")) {
												dataType = "5G";
											} else if (planType.equalsIgnoreCase("5G")) {
												dataType = "5G";
											} else {
												dataType = "4G";
											}
										}
									}
								}
							}
							if (reqJson.has("deviceId")) {
								if (operationName.equalsIgnoreCase("Activate Subscriber ESIM")
										|| operationName.equalsIgnoreCase("Activate Subscriber PSIM")
										|| operationName.equalsIgnoreCase(CommonConstants.ACTIVATESUBSCRIBER)
										|| operationName.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER)
										|| operationName.equalsIgnoreCase(CommonConstants.ACTIVATESUBSCRIBER_PORTIN)
										|| operationName
												.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER_PORTIN)
										|| operationName.equalsIgnoreCase("Change Wholesale Rate Plan")) {
									// reqJson=reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray().get(0).getAsJsonObject();
									if (reqJson.has("deviceId")) {
										log.debug("operationName::" + operationName);
										JsonObject deviceIdObj = reqJson.get("deviceId").getAsJsonArray().get(0)
												.getAsJsonObject();
										reqJson.remove("deviceId");
										JsonArray deviceTypeArr = new JsonArray();
										if (operationName.equalsIgnoreCase(CommonConstants.ACTIVATESUBSCRIBER)
												|| operationName
														.equalsIgnoreCase(CommonConstants.ACTIVATESUBSCRIBER_PORTIN)
												|| operationName
														.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER)
												|| operationName.equalsIgnoreCase(
														CommonConstants.ESIM_ACTIVATESUBSCRIBER_PORTIN)) {
											if (StringUtils.hasText(dataValType)) {
												JsonObject newdeviceIdObj = new JsonObject();
												newdeviceIdObj.addProperty("type", "dataType");
												newdeviceIdObj.addProperty("value", dataValType);
												deviceTypeArr.add(newdeviceIdObj);
											}

										} else if (StringUtils.hasText(dataType)) {
											JsonObject newdeviceIdObj = new JsonObject();
											newdeviceIdObj.addProperty("type", "dataType");
											newdeviceIdObj.addProperty("value", dataType);
											deviceTypeArr.add(newdeviceIdObj);
										}
										if (operationName.equalsIgnoreCase("Activate Subscriber ESIM")
												|| operationName
														.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER)
												|| operationName.equalsIgnoreCase(
														CommonConstants.ESIM_ACTIVATESUBSCRIBER_PORTIN)) {
											JsonObject newdeviceIdObj = new JsonObject();
											newdeviceIdObj.addProperty("type", "IMEI2");
											newdeviceIdObj.addProperty("value", imei);
											deviceTypeArr.add(newdeviceIdObj);
										} else if (operationName.equalsIgnoreCase("Change Wholesale Rate Plan")) {
											JsonObject newdeviceIdObj1 = new JsonObject();
											newdeviceIdObj1.addProperty("type", "IMEI");
											newdeviceIdObj1.addProperty("value", imei);
											deviceTypeArr.add(newdeviceIdObj1);
										} else if(StringUtils.hasText(imei)){
											JsonObject newdeviceIdObj = new JsonObject();
											newdeviceIdObj.addProperty("type", "IMEI");
											newdeviceIdObj.addProperty("value", imei);
											deviceTypeArr.add(newdeviceIdObj);
										}
										if(deviceTypeArr.size()>0) {
										reqJson.add("deviceId", deviceTypeArr);
										}
										else {
											try {
												if (operationName
														.equalsIgnoreCase(CommonConstants.ACTIVATESUBSCRIBER_PORTIN)) {
													String dataRequest = dataMap.get("initRequest");
													JSONObject initReq=new JSONObject(dataRequest);
													String lineIdFromReq=initReq.getJSONObject("data").getJSONArray("subOrder").getJSONObject(0).getString("lineId");
													Device deviceBean=new Device();
													deviceBean.seteLineId(lineIdFromReq);
													deviceBean=apolloNEOutboundClientService.callDeviceResourceService(deviceBean);
													dataType = deviceBean.getDeviceType().substring(0, 2);
													JsonObject newdeviceIdObj = new JsonObject();
													newdeviceIdObj.addProperty("type", "dataType");
													newdeviceIdObj.addProperty("value", dataType);
													deviceTypeArr.add(newdeviceIdObj);
													log.debug("dataType_VALUE::" + dataType);
												}
											} catch (Exception e) {
												log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Exception--{}",e);
											}
											deviceTypeArr.add(deviceIdObj);
										reqJson.add("deviceId", deviceTypeArr);
										}
										log.debug("reqJson device::" + reqJson);
									}
								}
							}
							if (operationName.equalsIgnoreCase("Activate Subscriber PSIM")
									|| operationName.equalsIgnoreCase("Activate Subscriber ESIM")
									|| operationName.equalsIgnoreCase("Change Wholesale Rate Plan")) {
								JsonArray featureArr = new JsonArray();
								if (reqJson.has("feature")) {
									log.debug("requestparamsJsonData in feature::" + requestparamsJsonData);
									if (requestparamsJsonData.has("data")) {
										if (requestparamsJsonData.get("data").getAsJsonObject().has("subOrder")) {
											if (requestparamsJsonData.get("data").getAsJsonObject().get("subOrder")
													.getAsJsonArray().get(0).getAsJsonObject().has("feature")) {
												featureArr = requestparamsJsonData.get("data").getAsJsonObject()
														.get("subOrder").getAsJsonArray().get(0).getAsJsonObject()
														.get("feature").getAsJsonArray();
												log.debug("Activate Subscriber PSIM features::" + featureArr);
												if (operationName.equalsIgnoreCase("Change WholeSale Rate Plan")) {
													JsonArray newfeatureArr = new JsonArray();
													for (int i = 0; i < featureArr.size(); i++) {
														JsonObject featureobj = featureArr.get(i).getAsJsonObject();
														if (featureobj.has("subscribe")) {
															String subscribe = featureobj.get("subscribe")
																	.getAsString();
															boolean newSubscribe = (subscribe.equalsIgnoreCase("T"))
																	? true
																	: (subscribe.equalsIgnoreCase("A")) ? true
																			: (subscribe.equalsIgnoreCase("True"))
																					? true
																					: false;
															JsonObject featureCodeValObj = new JsonObject();
															featureCodeValObj.addProperty("featureCode",
																	featureobj.get("featureCode").getAsString());
															featureCodeValObj.addProperty("subscribe", newSubscribe);
															newfeatureArr.add(featureCodeValObj);
															reqJson.remove("feature");
															reqJson.add("feature", newfeatureArr);
														}
													}
												} else {
													reqJson.remove("feature");
													reqJson.add("feature", featureArr);
												}
											} else {
												if (operationName.equalsIgnoreCase("Change Wholesale Rate Plan")) {
													if (requestparamsJsonData.get("data").getAsJsonObject()
															.get("subOrder").getAsJsonArray().get(0).getAsJsonObject()
															.has("isPilotPlanMigration")) {
														isPilotPlanMigration = requestparamsJsonData.get("data")
																.getAsJsonObject().get("subOrder").getAsJsonArray()
																.get(0).getAsJsonObject().get("isPilotPlanMigration")
																.getAsString();
													} else if (requestparamsJsonData.get("data").getAsJsonObject()
															.get("subOrder").getAsJsonArray().get(0).getAsJsonObject()
															.has("isPlanMigration")) {
														isPilotPlanMigration = requestparamsJsonData.get("data")
																.getAsJsonObject().get("subOrder").getAsJsonArray()
																.get(0).getAsJsonObject().get("isPlanMigration")
																.getAsString();
													} else {
														reqJson.remove("feature");
													}
												} else {
													reqJson.remove("feature");
												}
											}
										}
									}
								}
							}
							if (operationName.equalsIgnoreCase("Change Feature")) {
								if (requestparamsJsonData.get("data").getAsJsonObject().get("subOrder").getAsJsonArray()
										.get(0).getAsJsonObject().has("isPilotPlanMigration")) {
									isPilotPlanMigration = requestparamsJsonData.get("data").getAsJsonObject()
											.get("subOrder").getAsJsonArray().get(0).getAsJsonObject()
											.get("isPilotPlanMigration").getAsString();
									
									mdnValfromrequest=requestparamsJsonData.get("data").getAsJsonObject()
											.get("subOrder").getAsJsonArray().get(0).getAsJsonObject()
											.get("mdn").getAsJsonArray().get(0).getAsJsonObject()
											.get("value").getAsString();
								}
								if (requestparamsJsonData.get("data").getAsJsonObject().get("subOrder").getAsJsonArray()
										.get(0).getAsJsonObject().has("isPlanMigration")) {
									isPilotPlanMigration = requestparamsJsonData.get("data").getAsJsonObject()
											.get("subOrder").getAsJsonArray().get(0).getAsJsonObject()
											.get("isPlanMigration").getAsString();
									
									mdnValfromrequest=requestparamsJsonData.get("data").getAsJsonObject()
											.get("subOrder").getAsJsonArray().get(0).getAsJsonObject()
											.get("mdn").getAsJsonArray().get(0).getAsJsonObject()
											.get("value").getAsString();
								}
							}
							if (operationName.equalsIgnoreCase("Device Detection Change Rate Plan")
									|| operationName.equalsIgnoreCase("Change Rate Plan")) {
								String ratePlanIMEI = "";
								if (operationName.equalsIgnoreCase("Device Detection Change Rate Plan")) {
									if (reqJson.has("simId")) {
										reqJson.remove("simId");
									}

								}
								if (operationName.equalsIgnoreCase("Change Rate Plan")) {
									if (requestparamsJsonData.get("data").getAsJsonObject().has("subOrder")) {
										if (requestparamsJsonData.get("data").getAsJsonObject().get("subOrder")
												.getAsJsonArray().get(0).getAsJsonObject().has("simId")) {
											JsonArray simIdArr = new JsonArray();
											simIdArr = requestparamsJsonData.get("data").getAsJsonObject()
													.get("subOrder").getAsJsonArray().get(0).getAsJsonObject()
													.get("simId").getAsJsonArray();
											reqJson.remove("simId");
											reqJson.add("simId", simIdArr);
										}
									}
									if (requestparamsJsonData.get("data").getAsJsonObject().has("subOrder")) {
										if (requestparamsJsonData.get("data").getAsJsonObject().get("subOrder")
												.getAsJsonArray().get(0).getAsJsonObject().has("newRatePlan")) {
											String newRatePlan = "";
											String oldRatePlan = "";
											newRatePlan = requestparamsJsonData.get("data").getAsJsonObject()
													.get("subOrder").getAsJsonArray().get(0).getAsJsonObject()
													.get("newRatePlan").getAsString();
											oldRatePlan = requestparamsJsonData.get("data").getAsJsonObject()
													.get("subOrder").getAsJsonArray().get(0).getAsJsonObject()
													.get("oldRatePlan").getAsString();
											if (formattedJson.contains("\"newRatePlan\":\"\"")) {
												formattedJson = formattedJson.replace("\"newRatePlan\":\"\"",
														"\"newRatePlan\":\"" + newRatePlan + "\"");
											}
											if (formattedJson.contains("\"oldRatePlan\":\"\"")) {
												formattedJson = formattedJson.replace("\"oldRatePlan\":\"\"",
														"\"oldRatePlan\":\"" + oldRatePlan + "\"");
											}
										}
									}

									if (requestparamsJsonData.get("data").getAsJsonObject().has("subOrder")) {
										if (requestparamsJsonData.get("data").getAsJsonObject().get("subOrder")
												.getAsJsonArray().get(0).getAsJsonObject().has("deviceId")) {
											String deviceTypeFormatted = "";
											String deviceValueFormatted = "";
											JsonArray deviceIdArr = new JsonArray();
											deviceIdArr = requestparamsJsonData.get("data").getAsJsonObject()
													.get("subOrder").getAsJsonArray().get(0).getAsJsonObject()
													.get("deviceId").getAsJsonArray();
											for (int i = 0; i < deviceIdArr.size(); i++) {
												JsonObject deviceFormattedObj = deviceIdArr.get(i).getAsJsonObject();
												if (deviceFormattedObj.has("type")) {
													deviceTypeFormatted = deviceFormattedObj.get("type").getAsString();
													if (deviceTypeFormatted.equalsIgnoreCase("newIMEI")) {
														ratePlanIMEI = deviceFormattedObj.get("value").getAsString();
														log.debug("deviceFormattedObj" + deviceFormattedObj);
													}
												}
											}
											reqJson.remove("deviceId");
											JsonArray newdeviceIdArr = new JsonArray();
											JsonObject deviceFormattedObj = new JsonObject();
											deviceFormattedObj.addProperty("type", "IMEI");
											deviceFormattedObj.addProperty("value", ratePlanIMEI);
											newdeviceIdArr.add(deviceFormattedObj);
											reqJson.add("deviceId", newdeviceIdArr);
										}
									}

								}

								if (reqJson.has("feature")) {
									JsonArray featureArr = new JsonArray();
									featureArr = reqJson.get("feature").getAsJsonArray();
									JsonArray newfeatureArr = new JsonArray();
									for (int i = 0; i < featureArr.size(); i++) {
										JsonObject featureobj = featureArr.get(i).getAsJsonObject();
										if (featureobj.has("subscribe")) {
											String subscribe = featureobj.get("subscribe").getAsString();
											boolean newSubscribe = (subscribe.equalsIgnoreCase("T")) ? true
													: (subscribe.equalsIgnoreCase("A")) ? true
															: (subscribe.equalsIgnoreCase("True")) ? true : false;
											JsonObject featureCodeValObj = new JsonObject();
											featureCodeValObj.addProperty("featureCode",
													featureobj.get("featureCode").getAsString());
											featureCodeValObj.addProperty("subscribe", newSubscribe);
											newfeatureArr.add(featureCodeValObj);
											reqJson.remove("feature");
											reqJson.add("feature", newfeatureArr);
										}
									}
								}
								if (reqJson.has("mdnArray")) {
									String DDmdnValue = "";
									JsonArray mdnArray = new JsonArray();
									JsonObject mdnObj = new JsonObject();
									DDmdnValue = reqJson.get("mdnArray").getAsJsonArray().get(0).getAsJsonObject()
											.get("value").getAsString();
									mdnObj.addProperty("type", "mdn");
									mdnObj.addProperty("value", DDmdnValue);
									mdnArray.add(mdnObj);
									reqJson.remove("mdnArray");
									reqJson.add("mdn", mdnArray);
								}
								if (reqJson.has("deviceId_New")) {
									JsonArray deviceIdArray = new JsonArray();
									deviceIdArray = reqJson.get("deviceId_New").getAsJsonArray();
									ratePlanIMEI = reqJson.get("deviceId_New").getAsJsonArray().get(0).getAsJsonObject()
											.get("value").getAsString();
									reqJson.remove("deviceId_New");
									reqJson.add("deviceId", deviceIdArray);
								}
								if (reqJson.has("mdn")) {
									String DDmdnValue = reqJson.get("mdn").getAsJsonArray().get(0).getAsJsonObject()
											.get("value").getAsString();
									Line line = new Line();
									line.setMdn(DDmdnValue);
									line = apolloNEOutboundClientService.callLineResourceServiceFromMdn(line);
									if (line != null) {
										if (line.geteLineId() != null) {
											LinePlan lineplan = new LinePlan();
											lineplan.setELineId(line.geteLineId());
											lineplan = apolloNEOutboundClientService
													.callLinePlanResourceService(lineplan);
											if (lineplan.getRetailPlan() != null) {
												retailPlancode = lineplan.getRetailPlan();
											}
											if(lineplan.getPlanCategory() != null){
												if(lineplan.getPlanGroup() != null&&("OCS").equalsIgnoreCase(lineplan.getPlanGroup())){
													pilotTableFlag = true;
												}
											}
										}
									}
								}
								if (reqJson.has("newRatePlan")) {
									wholesalePlanCode = reqJson.get("newRatePlan").getAsString();
								}
								log.debug("IMEI_VALUE::" + ratePlanIMEI);
								/*if (ratePlanIMEI != null && !ratePlanIMEI.isEmpty()) {
									RefPilotPrg refpilotPrg = new RefPilotPrg();
									refpilotPrg.setImei(ratePlanIMEI);
									refpilotPrg = apolloNEOutboundClientService.callRefPilotTableFromImei(refpilotPrg);
									log.debug("refpilotPrg response::" + refpilotPrg);
									if (refpilotPrg != null) {
										log.debug("refpilotPrg response::" + refpilotPrg);
										log.debug("refpilotPrg response::" + refpilotPrg.getPilotProgram());
									}
									if (refpilotPrg.getPilotProgram() != null) {
										pilotTableFlag = true;
									}
								}*/
							}
							if (operationName.equalsIgnoreCase("Change Wholesale Rate Plan")) {
								log.info("isPilotPlanMigration in CF :"+isPilotPlanMigration);
								if (isPilotPlanMigration.equalsIgnoreCase("Y")) {
									LinePlan lineplan = new LinePlan();
									if (reqJson.has("mdn")) {
										String DDmdnValue = reqJson.get("mdn").getAsJsonArray().get(0).getAsJsonObject()
												.get("value").getAsString();
										log.info("isPilotPlanMigration in mdn :"+isPilotPlanMigration);
										Line line = new Line();
										line.setMdn(DDmdnValue);
										line = apolloNEOutboundClientService.callLineResourceServiceFromMdn(line);
										if (line != null) {
											if (line.geteLineId() != null) {

												lineplan.setELineId(line.geteLineId());
												lineplan = apolloNEOutboundClientService
														.callLinePlanResourceService(lineplan);
												if (lineplan.getRetailPlan() != null) {
													retailPlancode = lineplan.getRetailPlan();
												}
											}
										}
									}
									if (reqJson.has("newRatePlan")) {
										wholesalePlanCode = reqJson.get("newRatePlan").getAsString();
									} else {
										if (lineplan != null && lineplan.getWhsPlan() != null) {
											wholesalePlanCode = lineplan.getWhsPlan();
										}
									}
									pilotTableFlag = true;
								}
							}
							if (operationName.equalsIgnoreCase("Change Feature")) {
								log.info("isPilotPlanMigration in CF :"+isPilotPlanMigration);
								if (isPilotPlanMigration.equalsIgnoreCase("Y")) {
									LinePlan lineplan = new LinePlan();
									if (!mdnValfromrequest.equalsIgnoreCase("") && StringUtils.hasText(mdnValfromrequest) && mdnValfromrequest!=null) {
										
										log.info("isPilotPlanMigration in mdn :"+mdnValfromrequest);
										Line line = new Line();
										line.setMdn(mdnValfromrequest);
										line = apolloNEOutboundClientService.callLineResourceServiceFromMdn(line);
										if (line != null) {
											if (line.geteLineId() != null) {

												lineplan.setELineId(line.geteLineId());
												lineplan = apolloNEOutboundClientService
														.callLinePlanResourceService(lineplan);
												if (lineplan.getRetailPlan() != null) {
													retailPlancode = lineplan.getRetailPlan();
												}
											}
										}
									}
									
										if (lineplan != null && lineplan.getWhsPlan() != null) {
											wholesalePlanCode = lineplan.getWhsPlan();
										
									}
									pilotTableFlag = true;
								}
							}
							log.debug("retailPlancode VALUE::" + retailPlancode);
							log.debug("wholesalePlanCode VALUE::" + wholesalePlanCode);
							log.debug("reqJson::" + reqJsonDataObject);
							if (operationName.equalsIgnoreCase(CommonConstants.ACTIVATESUBSCRIBER_PORTIN)) {
								if (reqJsonDataObject.get("data").getAsJsonObject().has("subOrder")) {
									JsonObject dataObject = reqJsonDataObject.get("data").getAsJsonObject()
											.get("subOrder").getAsJsonArray().get(0).getAsJsonObject();
									if (dataObject.has("mdn")) {
										JsonArray mdnArr = dataObject.getAsJsonArray("mdn");
										for (int i = 0; i < mdnArr.size(); i++) {
											JsonObject mdnObj = mdnArr.get(i).getAsJsonObject();
											if (mdnObj.has("type")) {
												String mdnType = mdnObj.get("type").getAsString();
												if (mdnType.equalsIgnoreCase("oldMDN")) {
													String DDmdnValue = mdnObj.get("value").getAsString();
													log.debug("DDmdnValue::" + DDmdnValue);
													if (DDmdnValue != null && !DDmdnValue.isEmpty()) {
														Line line = new Line();
														line.setMdn(DDmdnValue);
														log.debug("chamgemdn portIn mdn::" + DDmdnValue);
														line = apolloNEOutboundClientService
																.callLineResourceServiceFromMdn(line);
														log.debug("chamgemdn portIn line::" + line);
														if (line != null) {
															if (line.geteLineId() != null) {
																LinePlan lineplan = new LinePlan();
																lineplan.setELineId(line.geteLineId());
																lineplan = apolloNEOutboundClientService
																		.callLinePlanResourceService(lineplan);
																log.debug("chamgemdn portIn lineplan::" + lineplan);
																if (lineplan.getRetailPlan() != null) {
																	retailPlancode = lineplan.getRetailPlan();
																}
																if (lineplan.getPlanCategory() != null) {
																	if (lineplan.getPlanGroup() != null
																			&& ("OCS").equalsIgnoreCase(
																					lineplan.getPlanGroup())) {
																		pilotTableFlag = true;
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
							if(!pilotTableFlag){
								ProcessMetadata processMetadata=new ProcessMetadata();
								processMetadata.setRootTransactionId(responseId);
								ProcessMetadata[] processMetaData=apolloNEOutboundClientService.getProcessMetadata(processMetadata);
								if(processMetaData != null && processMetaData.length>0){
									for(ProcessMetadata processMetadatas : processMetaData){
										Gson offeringGson = new Gson();
										if(processMetadatas.getProcessData() != null){
											if (processMetadatas.getProcessData().startsWith("{")) {
												OfferingDetails offeringDetails = offeringGson.fromJson(
														processMetadatas.getProcessData(), OfferingDetails.class);
												log.debug("offeringDetails value::" + offeringDetails);
												if (Objects.nonNull(offeringDetails)) {
													log.debug("offeringDetails plan value::" + offeringDetails);
													log.debug("offeringDetails getPlanGroup value::"
															+ offeringDetails.getPlanGroup());
													if (offeringDetails.getPlanGroup() != null && ("OCS")
															.equalsIgnoreCase(offeringDetails.getPlanGroup())) {
														pilotTableFlag = true;
														break;
													}
												}
											}
										}
									}
								}
							}
							if (pilotTableFlag) {
								if (customPDL.equalsIgnoreCase("") || mhsCustomPDL.equalsIgnoreCase("")) {
									log.debug("retailPlancode VALUE::" + retailPlancode);
									log.debug("wholesalePlanCode VALUE::" + wholesalePlanCode);
									if (!retailPlancode.equalsIgnoreCase("")
											&& !wholesalePlanCode.equalsIgnoreCase("")) {
										List<Map<String, Object>> res = null;
										res = rcsDao.getThrottleDetails(retailPlancode, wholesalePlanCode);
										String Throttle_LTE = "";
										String Throttle_MHS = "";
										String data_LTE = "";
										String data_LTE_Units = "";
										String data_MHS = "";
										String data_MHS_Units = "";
										log.info("response res :: " + res);
										if (res != null && res.size() > 0) {
											for (int i = 0; i < res.size(); i++) {
												Throttle_LTE = (String) res.get(i).get("THROTTLE_LIMIT_LTE");
												Throttle_MHS = (String) res.get(i).get("THROTTLE_LIMIT_MHS");
											}
											StringBuffer alpha = new StringBuffer(), num = new StringBuffer();
											for (int j = 0; j < Throttle_LTE.length(); j++) {
												if (Character.isDigit(Throttle_LTE.charAt(j)))
													num.append(Throttle_LTE.charAt(j));
												else {
													alpha.append(Throttle_LTE.charAt(j));
												}
											}
											data_LTE = num.toString();
											data_LTE_Units = alpha.toString();
											StringBuffer alpha1 = new StringBuffer(), num1 = new StringBuffer();
											for (int j = 0; j < Throttle_MHS.length(); j++) {
												if (Character.isDigit(Throttle_MHS.charAt(j)))
													num1.append(Throttle_MHS.charAt(j));
												else
													alpha1.append(Throttle_MHS.charAt(j));
											}
											data_MHS = num1.toString();
											data_MHS_Units = alpha1.toString();
											log.info("data_LTE :: " + data_LTE + "data_MHS ::" + data_MHS);
											customPDL = data_LTE;
											mhsCustomPDL = data_MHS;
										}
									}
								}
							}
							if (operationName.equalsIgnoreCase("Activate Subscriber PSIM")
									|| operationName.equalsIgnoreCase("Activate Subscriber ESIM")
									|| operationName.equals(CommonConstants.ACTIVATESUBSCRIBER)
									|| operationName.equals(CommonConstants.ACTIVATESUBSCRIBER_PORTIN)
									|| operationName.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER)
									|| operationName.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER_PORTIN)
									|| operationName.equalsIgnoreCase("Change Wholesale Rate Plan")
									|| operationName.equalsIgnoreCase("Device Detection Change Rate Plan")
									|| operationName.equalsIgnoreCase("Change Feature")
									|| operationName.equalsIgnoreCase("Change Rate Plan")) {

								if (requestparamsJsonData.has("data")) {
									if (!requestparamsJsonData.get("data").getAsJsonObject().has("additionalData")) {
										reqJson.remove("additionalData");
									}
									if (requestparamsJsonData.get("data").getAsJsonObject().has("additionalData")) {
										additionalDataArray = requestparamsJsonData.get("data").getAsJsonObject()
												.get("additionalData").getAsJsonArray();
										log.debug("formattedJson inbound additionalDataArray Json::"
												+ additionalDataArray);
									}
								}

								log.debug("formattedJson before additionalData customPDL and mhsCustomPDL Json::"
										+ reqJson);
								log.debug("formattedJson before customPDL::" + customPDL);
								log.debug("formattedJson before mhsCustomPDL::" + mhsCustomPDL);
								
								JsonObject customPDLObj = new JsonObject();
								JsonObject mhsCustomPDLObj = new JsonObject();
								// JsonArray additionalDataArray = new JsonArray();
								if (customPDL != null && !customPDL.isEmpty()) {
									customPDLObj.addProperty("name", "customPDL");
									customPDLObj.addProperty("value", customPDL);
									additionalDataArray.add(customPDLObj);
								}
								if (mhsCustomPDL != null && !mhsCustomPDL.isEmpty()) {
									mhsCustomPDLObj.addProperty("name", CommonConstants.MHSCustomPDL);
									mhsCustomPDLObj.addProperty("value", mhsCustomPDL);
									additionalDataArray.add(mhsCustomPDLObj);
								}
								if (additionalDataArray != null && additionalDataArray.size() != 0) {
									if(!(operationName.equalsIgnoreCase("Change Feature") && isPilotPlanMigrationflag))
									{
										
										reqJson.add("additionalData", additionalDataArray);
									}
									
								}
							}
							log.debug("formattedJson after additionalData customPDL and mhsCustomPDL Json::" + reqJson);
							if (operationName.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER_PORTIN)) {
								log.debug("ESIM Activate Subscriber Port-in reqJson::" + reqJson + "::responseId::"+ responseId);
								JsonObject requestJsonData = new JsonParser().parse(requestParams).getAsJsonObject();
								log.debug("ESIM Activate Subscriber Port-in requestJsonData::" + requestJsonData);
								String changeMdnCPServiceName = "";
								String ElineId ="";
								String imeiValue="";
								String deviceType="";
								try {
									changeMdnCPServiceName = rcsDao.getServiceName(responseId);
									if (changeMdnCPServiceName.equalsIgnoreCase("Change-MDN")) {
										if (requestJsonData.get("data").getAsJsonObject().get("subOrder")
												.getAsJsonArray().get(0).getAsJsonObject().has("lineId")) {
											 	ElineId = requestJsonData.get("data").getAsJsonObject()
													.get("subOrder").getAsJsonArray().get(0).getAsJsonObject()
													.get("lineId").getAsString();
											if (ElineId != null) {
												log.debug("ESIM Activate Subscriber Port-in ElineId::" + ElineId);
												Device deviceBean = new Device();
												deviceBean.seteLineId(ElineId);
												deviceBean = apolloNEOutboundClientService.callDeviceResourceService(deviceBean);
												if (deviceBean != null) {
													 imeiValue = deviceBean.getImei();
													 deviceType = deviceBean.getDeviceType();
													log.debug("ESIM Activate Subscriber Port-in imeiValue::" + imeiValue
															+ "::deviceType::" + deviceType);
													if (reqJson.has("deviceId")) {
														reqJson.remove("deviceId");
														log.debug("ESIM Activate Subscriber Port-in reqJson remove::"+ reqJson);
														JsonArray newdeviceIdArr = new JsonArray();
														JsonObject deviceFormattedObj = new JsonObject();
														JsonObject deviceFormattedObj1 = new JsonObject();
														deviceFormattedObj1.addProperty("type", "dataType");
														deviceFormattedObj1.addProperty("value", deviceType);
														deviceFormattedObj.addProperty("type", "IMEI2");
														deviceFormattedObj.addProperty("value", imeiValue);
														newdeviceIdArr.add(deviceFormattedObj1);
														newdeviceIdArr.add(deviceFormattedObj);
														reqJson.add("deviceId", newdeviceIdArr);
														log.debug("ESIM Activate Subscriber Port-in reqJson after::"+ reqJson);
													}
												}
											}
										}
									}
								} catch (Exception e) {
									log.error("Error in ESIM Activate Subsciner Portin - {}", e);
								}
							}

							if (reqJson.has("SubOrgID")) {
								reqJson.remove("SubOrgID");
								if (dataType.equalsIgnoreCase("4G") || dataValType.equalsIgnoreCase("4G")) {
									reqJson.addProperty("SubOrgID", "WCHARTERCOMMzzzzzzzzz0000");
								}
							} else {
								if (dataType.equalsIgnoreCase("4G") || dataValType.equalsIgnoreCase("4G")) {
									reqJson.addProperty("SubOrgID", "WCHARTERCOMMzzzzzzzzz0000");
								}
							}
							if (reqJson.has("MPNPoolName")) {
								reqJson.remove("MPNPoolName");
								if (dataType.equalsIgnoreCase("4G") || dataValType.equalsIgnoreCase("4G")) {
									reqJson.addProperty("MPNPoolName", "CHARTERPOOLA");
								}
							} else {
								if (dataType.equalsIgnoreCase("4G") || dataValType.equalsIgnoreCase("4G")) {
									reqJson.addProperty("MPNPoolName", "CHARTERPOOLA");
								}
							}

						}

						// reqJson - OUTBOUND REQUEST
						// requestparamsJsonData - INBOUND REQUEST
						if (operationName.equalsIgnoreCase(CommonConstants.CHANGE_BCD)
								|| operationName.equalsIgnoreCase(CommonConstants.RESET_FEATURE)) {
							log.debug("Inside CHANGE_BCD|RESET_FEATURE ::" + operationName);
							log.info("CHANGE_BCD|RESET_FEATURE reqJson::" + reqJson);
							if (StringUtils.hasText(reqJson.toString()) && reqJson.has("data")) {
								if (reqJson.get("data").getAsJsonObject().has("subOrder")) {
									if (reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray().get(0)
											.getAsJsonObject().has("additionalData")) {
										if (requestparamsJsonData.has("data")) {
											if (!requestparamsJsonData.get("data").getAsJsonObject()
													.has("additionalData")) {
												reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray()
														.get(0).getAsJsonObject().remove("additionalData");
												log.info("CHANGE_BCD|RESET_FEATURE additionalData Json::" + reqJson);
											}
										}
									}
									log.info(
											"CHANGE_BCD|RESET_FEATURE requestparamsJsonData::" + requestparamsJsonData);
									if (!reqJson.get("data").getAsJsonObject().get("subOrder").getAsJsonArray().get(0)
											.getAsJsonObject().has("billCycleResetDay")) {
										if (requestparamsJsonData.get("data").getAsJsonObject().get("subOrder")
												.getAsJsonArray().get(0).getAsJsonObject().has("mdns")) {
											if (requestparamsJsonData.get("data").getAsJsonObject().get("subOrder")
													.getAsJsonArray().get(0).getAsJsonObject().get("mdns")
													.getAsJsonArray().get(0).getAsJsonObject().has("mdn")) {
												reqMdnJson = requestparamsJsonData.get("data").getAsJsonObject()
														.get("subOrder").getAsJsonArray().get(0).getAsJsonObject()
														.get("mdns").getAsJsonArray().get(0).getAsJsonObject()
														.get("mdn").getAsJsonArray().get(0).getAsJsonObject();
												mdnValue = reqMdnJson.get("value").getAsString();
												log.debug("CHANGE_BCD mdnValue::" + mdnValue);
												if (mdnValue != null && !mdnValue.isEmpty()) {
													Line line = new Line();
													line.setMdn(mdnValue);
													line = apolloNEOutboundClientService
															.callLineResourceServiceFromMdn(line);
													log.debug("CHANGE_BCD line::" + line);
													if (line != null) {
														if (line.getBcd() != null) {
															changeBcdBillCycleDay = line.getBcd();
															log.debug("line changeBcdBillCycleDay::"
																	+ changeBcdBillCycleDay);
														}
														if (changeBcdBillCycleDay == null) {
															changeBcdBillCycleDay = "";
														}
													}
													reqJson.get("data").getAsJsonObject().get("subOrder")
															.getAsJsonArray().get(0).getAsJsonObject()
															.addProperty("billCycleResetDay", changeBcdBillCycleDay);

													log.info("CHANGE_BCD after  billCycleResetDay::" + reqJson);
												}
											}
										}
									}
								}
							}
						}
					}
					formattedJson = reqJsonData.toString();
					log.debug("reqJsonData.toString()>>::" + formattedJson);
					log.debug("requestparamsJsonData()>>::" + requestparamsJsonData);

					// Change PDL Request in MDN
					if (operationName.equalsIgnoreCase(CommonConstants.CHANGE_BCD)) {
						String eLineId = "";
						String pdlMDN = "";
						log.debug("change pdl requestparamsJsonData::" + requestparamsJsonData);
						if (requestparamsJsonData.get("data").getAsJsonObject().get("subOrder").getAsJsonArray().get(0)
								.getAsJsonObject().has("lineId")) {
							eLineId = requestparamsJsonData.get("data").getAsJsonObject().get("subOrder")
									.getAsJsonArray().get(0).getAsJsonObject().get("lineId").getAsString();
							log.debug("change pdl requestparamsJsonData lineId::" + eLineId);

							if (!requestparamsJsonData.get("data").getAsJsonObject().get("subOrder").getAsJsonArray()
									.get(0).getAsJsonObject().has("mdn")) {
								log.info("Inside not requestparamsJsonData 1351::" + requestparamsJsonData);
								if (eLineId != null && !eLineId.isEmpty() && !eLineId.equalsIgnoreCase("")) {
									Line line = new Line();
									line.seteLineId(eLineId);
									line = apolloNEOutboundClientService.callLineResourceServiceByLineId(eLineId);
									log.debug("change requestparamsJsonData mdn from DB::" + line);
									if (line != null) {
										if (line.getMdn() != null) {
											pdlMDN = line.getMdn();
											log.debug("line table mdn Value::" + pdlMDN);
										}
										if (pdlMDN == null) {
											pdlMDN = "";
										}
									}

								}
								log.debug("change formattedJsons::" + formattedJson);
								JsonObject mdnJsonObject1 = new JsonParser().parse(formattedJson).getAsJsonObject();
								JsonArray mdnArray1 = mdnJsonObject1.get("data").getAsJsonObject().get("subOrder")
										.getAsJsonArray().get(0).getAsJsonObject().get("mdn").getAsJsonArray();
								log.debug("change formattedJsons mdnArray::" + mdnArray1);
								JsonObject mdnValueObj1 = mdnArray1.get(0).getAsJsonObject();

								mdnValueObj1.addProperty("type", "mdn");
								mdnValueObj1.addProperty("value", pdlMDN);
								log.debug("mdnJsonObject::" + mdnJsonObject1.toString());
								formattedJson = mdnJsonObject1.toString();

								log.debug("change pdl after formattedJsons::" + formattedJson);

							}

						}
					}
					if (operationName.equalsIgnoreCase(CommonConstants.CHANGE_BCD)) {
						String seServiceName = rcsDao.getServiceName(responseId);
						if (seServiceName.equalsIgnoreCase(CommonConstants.CHANGE_PDL)) {
							JsonObject changebcdJsonObject = new JsonParser().parse(formattedJson).getAsJsonObject();
							if (!changebcdJsonObject.get("data").getAsJsonObject().get("subOrder").getAsJsonArray()
									.get(0).getAsJsonObject().has("billCycleResetDay")) {
								if (changebcdJsonObject.get("data").getAsJsonObject().get("subOrder").getAsJsonArray()
										.get(0).getAsJsonObject().has("mdn")) {
									JsonArray mdnArray = changebcdJsonObject.get("data").getAsJsonObject()
											.get("subOrder").getAsJsonArray().get(0).getAsJsonObject().get("mdn")
											.getAsJsonArray();
									JsonObject mdnValueObj = mdnArray.get(0).getAsJsonObject();
									String mdnValue = mdnValueObj.get("value").getAsString();
									String changeBcdBillCycleDay = "";
									if (mdnValue != null && !mdnValue.isEmpty()) {
										Line line = new Line();
										line.setMdn(mdnValue);
										line = apolloNEOutboundClientService.callLineResourceServiceFromMdn(line);
										log.debug("CHANGE_BCD line from DB::" + line);
										if (line != null) {
											if (line.getBcd() != null) {
												changeBcdBillCycleDay = line.getBcd();
												log.debug("line changeBcdBillCycleDay Value::" + changeBcdBillCycleDay);
											}
											if (changeBcdBillCycleDay == null) {
												changeBcdBillCycleDay = "";
											}
										}
										changebcdJsonObject.get("data").getAsJsonObject().get("subOrder")
												.getAsJsonArray().get(0).getAsJsonObject()
												.addProperty("billCycleResetDay", changeBcdBillCycleDay);
										log.info("CHANGE_BCD after  billCycleResetDay::" + changebcdJsonObject);
										log.info("CHANGE_BCD after  requestparamsJsonData::" + requestparamsJsonData);
										if (requestparamsJsonData.get("data").getAsJsonObject().get("subOrder")
												.getAsJsonArray().get(0).getAsJsonObject().has("lineId")) {

											LinePlan lineplan = new LinePlan();
											LinePlan newlineplan = new LinePlan();
											String elineId = "";
											String RetailPlan = "";
											String Throttle_LTE = "";
											String Throttle_MHS = "";
											String data_LTE = "";
											String data_LTE_Units = "";
											String data_MHS = "";
											String data_MHS_Units = "";
											String retailplancode_db = "";
											elineId = requestparamsJsonData.get("data").getAsJsonObject()
													.get("subOrder").getAsJsonArray().get(0).getAsJsonObject()
													.get("lineId").getAsString();
											lineplan.setELineId(elineId);
											newlineplan = apolloNEOutboundClientService
													.callLinePlanResourceService(lineplan);
											String planCategory = newlineplan.getPlanCategory();
											String planGroup = newlineplan.getPlanGroup();
											String planCode = newlineplan.getWhsPlan();
											log.info("CHANGE_BCD after  planGroup::" + planGroup + "planCode::"
													+ planCode);

											if (planGroup != null && planGroup.equalsIgnoreCase("OCS")) {
												if (requestparamsJsonData.get("data").getAsJsonObject().get("subOrder")
														.getAsJsonArray().get(0).getAsJsonObject().has("newRatePlan")) {

													RetailPlan = requestparamsJsonData.get("data").getAsJsonObject()
															.get("subOrder").getAsJsonArray().get(0).getAsJsonObject()
															.get("newRatePlan").getAsString();
													List<Map<String, Object>> res = null;
													res = rcsDao.getThrottleDetails(RetailPlan, planCode);
													log.info("response res :: " + res);
													if (res != null && res.size() > 0) {
														for (int i = 0; i < res.size(); i++) {
															Throttle_LTE = (String) res.get(i)
																	.get("THROTTLE_LIMIT_LTE");
															Throttle_MHS = (String) res.get(i)
																	.get("THROTTLE_LIMIT_MHS");
														}
														StringBuffer alpha = new StringBuffer(),
																num = new StringBuffer();
														for (int j = 0; j < Throttle_LTE.length(); j++) {
															if (Character.isDigit(Throttle_LTE.charAt(j)))
																num.append(Throttle_LTE.charAt(j));
															else {
																alpha.append(Throttle_LTE.charAt(j));
															}
														}
														data_LTE = num.toString();
														data_LTE_Units = alpha.toString();
														StringBuffer alpha1 = new StringBuffer(),
																num1 = new StringBuffer();
														for (int j = 0; j < Throttle_MHS.length(); j++) {
															if (Character.isDigit(Throttle_MHS.charAt(j)))
																num1.append(Throttle_MHS.charAt(j));
															else
																alpha1.append(Throttle_MHS.charAt(j));
														}
														data_MHS = num1.toString();
														data_MHS_Units = alpha1.toString();
														// response = data_LTE + "~" + data_LTE_Units + "~" + data_MHS +
														// "~" + data_MHS_Units;
														log.info("data_LTE :: " + data_LTE + "data_MHS ::" + data_MHS);

														JsonArray AdditionalTypeArr = new JsonArray();
														JsonObject CustompdlObj = new JsonObject();
														JsonObject mhsCustomPDL = new JsonObject();
														CustompdlObj.addProperty("name", "customPDL");
														CustompdlObj.addProperty("value", data_LTE);
														mhsCustomPDL.addProperty("name", "mhsCustomPDL");
														mhsCustomPDL.addProperty("value", data_MHS);
														AdditionalTypeArr.add(CustompdlObj);
														AdditionalTypeArr.add(mhsCustomPDL);

														changebcdJsonObject.get("data").getAsJsonObject()
																.get("subOrder").getAsJsonArray().get(0)
																.getAsJsonObject()
																.add("additionalData", AdditionalTypeArr);

														/*
														 * changebcdJsonObject.get("data").getAsJsonObject().get(
														 * "subOrder") .getAsJsonArray().get(0).getAsJsonObject()
														 * .addProperty("customPDL", data_LTE);
														 * changebcdJsonObject.get("data").getAsJsonObject().get(
														 * "subOrder") .getAsJsonArray().get(0).getAsJsonObject()
														 * .addProperty("mhsCustomPDL", data_MHS);
														 */
													}
												} else if (requestparamsJsonData.get("data").getAsJsonObject()
														.get("subOrder").getAsJsonArray().get(0).getAsJsonObject()
														.has("mhsCustomPDL")) {
													log.debug("inside changeBCD MhsCustomdpl::");
													String mdhCustompdl = requestparamsJsonData.get("data")
															.getAsJsonObject().get("subOrder").getAsJsonArray().get(0)
															.getAsJsonObject().get("mhsCustomPDL").getAsString();
													String custompdl = requestparamsJsonData.get("data")
															.getAsJsonObject().get("subOrder").getAsJsonArray().get(0)
															.getAsJsonObject().get("customPDL").getAsString();

													log.debug("inside changeBCD MhsCustomdpl Val::" + mdhCustompdl
															+ "custompdl::" + custompdl);

													if (changebcdJsonObject.get("data").getAsJsonObject()
															.get("subOrder").getAsJsonArray().get(0).getAsJsonObject()
															.has("customPDL")) {
														changebcdJsonObject.get("data").getAsJsonObject()
																.get("subOrder").getAsJsonArray().get(0)
																.getAsJsonObject().remove("customPDL");
														changebcdJsonObject.get("data").getAsJsonObject()
																.get("subOrder").getAsJsonArray().get(0)
																.getAsJsonObject().remove("mhsCustomPDL");
													}
													String addName = "";
													String addValue = "";
													JsonObject addvaluesObj = new JsonObject();
													JsonArray AdditionalTypeArr = new JsonArray();
													if (requestparamsJsonData.get("data").getAsJsonObject()
															.has("additionalData")) {
														log.debug("inside changeBCD requestparamsJsonData 1833::"
																+ requestparamsJsonData);
														JsonArray addArray = requestparamsJsonData.get("data")
																.getAsJsonObject().get("additionalData")
																.getAsJsonArray();
														log.debug("inside changeBCD addArray 1837::" + addArray);
														JsonObject addValueObj = addArray.get(0).getAsJsonObject();
														log.debug("inside changeBCD addValueObj 1839::"
																+ addValueObj.toString());
														addName = addValueObj.get("name").getAsString();
														addValue = addValueObj.get("value").getAsString();
														log.info("CHANGE_BCD after  addName::" + addName + "addValue::"
																+ addValue);
														addvaluesObj.addProperty("name", addName);
														addvaluesObj.addProperty("value", addValue);
														AdditionalTypeArr.add(addvaluesObj);
														log.debug("inside changeBCD addvaluesObj::" + addvaluesObj);
														log.debug("inside changeBCD AdditionalTypeArr::"
																+ AdditionalTypeArr);
													}

													JsonObject CustompdlObj = new JsonObject();
													JsonObject mhsCustomPDL = new JsonObject();
													CustompdlObj.addProperty("name", "customPDL");
													CustompdlObj.addProperty("value", custompdl);
													mhsCustomPDL.addProperty("name", "mhsCustomPDL");
													mhsCustomPDL.addProperty("value", mdhCustompdl);
													AdditionalTypeArr.add(CustompdlObj);
													AdditionalTypeArr.add(mhsCustomPDL);
													changebcdJsonObject.get("data").getAsJsonObject().get("subOrder")
															.getAsJsonArray().get(0).getAsJsonObject()
															.add("additionalData", AdditionalTypeArr);

													log.debug("inside changeBCD changebcdJsonObject::"
															+ changebcdJsonObject);

												}

											}
										}
										formattedJson = changebcdJsonObject.toString();
									}

								}

							}
						}
					}
					log.debug("formattedJson after subscriberGroup Json::" + formattedJson);

					if (formattedJson.contains("\"mobileIPAddress\":\"empty\"")) {
						formattedJson = formattedJson.replace("\"mobileIPAddress\":\"empty\"",
								"\"mobileIPAddress\":\"\"");
					}
					if (formattedJson.contains("\"mobileIPAddress\":\"remove\"")) {
						formattedJson = formattedJson.replace("\"mobileIPAddress\":\"remove\",", "");
					}

				}
				if (operationName.equalsIgnoreCase(CommonConstants.VALIDATE_DEVICE)) {
					String newDeviceId = "";
					String initReq = dataMap.get("initRequest").toString();
					JsonObject reqJsonData = new JsonParser().parse(formattedJson).getAsJsonObject();
					JsonObject reqJson = reqJsonData;
					newDeviceId = getNewDeviceId(initReq);
					log.debug("VALIDATE_DEVICE newDeviceId ::" + newDeviceId);
					if (!"".equalsIgnoreCase(newDeviceId)) {
						// validateDeviceObj.put("deviceId", newDeviceId);
						// formattedJson = validateDeviceReqArr.toString();
						if (reqJson.has(CommonConstants.VALIDATE_DEVICE_INQUIRY)) {
							if (reqJson.get(CommonConstants.VALIDATE_DEVICE_INQUIRY).getAsJsonObject()
									.has(CommonConstants.DATA)) {
								reqJson.get(CommonConstants.VALIDATE_DEVICE_INQUIRY).getAsJsonObject()
										.get(CommonConstants.DATA).getAsJsonObject()
										.addProperty("deviceId", newDeviceId);
							}
						}
					}
					formattedJson = reqJsonData.toString();
				}

				if (operationName.equalsIgnoreCase(CommonConstants.VALIDATEBYOD)) {
					String request = dataMap.get("initRequest").toString();
					if (request.startsWith("{")) {
						JSONObject initReq = new JSONObject(request);
						if (initReq.has("data")) {
							JSONObject datanewObj = initReq.getJSONObject("data");
							if (datanewObj.has("transactionType")) {
								String transactionType = datanewObj.getString("transactionType");
								if (transactionType.equals("DE")) {
									if (formattedJson.contains("newIMEI")) {
										formattedJson = formattedJson.replace("newIMEI", "IMEI");
										log.debug("After newIMEI Replace VALIDATEBYOD formattedJson::" + formattedJson);
									}
								}
							}
						}
					}
				}

				log.debug("After  VALIDATE_DEVICE formattedJson::" + formattedJson);
				log.debug("ChangeESIM deviceIdFlag ::" + deviceIdFlag);
				if ("Add Wearable".equalsIgnoreCase(operationName)
						|| ("ChangeESIM".equalsIgnoreCase(operationName) && deviceIdFlag == true)) {
					// log.info("After BYOD::Before sleep::"+System.currentTimeMillis());
					Thread.sleep(60000);
					// log.info("After BYOD::After sleep::"+System.currentTimeMillis());
				}

				if (operationName.equalsIgnoreCase(CommonConstants.CBRS_CR)) {

					// JSONObject reqpar = new JSONObject(requestParams);
					JsonObject dataobj = new JsonObject();
					JsonArray suborderArray = new JsonArray();
					JsonObject obj1 = new JsonObject();
					JsonArray featArray = new JsonArray();
					JsonArray changeRateFeatArray = new JsonArray();
					JsonObject reqpar = new JsonParser().parse(formattedJson).getAsJsonObject();
					log.debug("cbrs change rate plan::reqpar:: " + reqpar);
					if (reqpar.has("data")) {
						dataobj = reqpar.get("data").getAsJsonObject();
					}
					if (dataobj.has("subOrder")) {
						suborderArray = dataobj.get("subOrder").getAsJsonArray();
						obj1 = suborderArray.get(0).getAsJsonObject();
					}
					log.debug("cbrs change rate plan::obj1:: " + obj1);
					if (obj1.has("feature")) {
						featArray = obj1.get("feature").getAsJsonArray();
						log.debug("cbrs change rate plan::featArray:: " + featArray);
						for (int a = 0; a < featArray.size(); a++) {
							if (featArray.get(a).getAsJsonObject().get("subscribe").getAsString().equalsIgnoreCase("A")
									|| featArray.get(a).getAsJsonObject().get("subscribe").getAsString()
											.equalsIgnoreCase("true")) {
								changeRateFeatArray.add(featArray.get(a).getAsJsonObject());
							}
						}
					}
					log.debug("cbrs change rate plan:::changeRateFeatArray:: " + changeRateFeatArray);
					obj1.remove("feature");
					obj1.add("feature", featArray);
					log.debug("cbrs change rate plan:::obj1 final:::" + obj1);
					formattedJson = reqpar.toString();
					formattedJson = formattedJson.replaceAll("\"A\"", "\"true\"");
					formattedJson = formattedJson.replaceAll("\"D\"", "\"false\"");
					log.debug("cbrs change rate plan::formattedJson:: " + formattedJson);
				}
				
				if(operationName.equalsIgnoreCase("Cancel Port-In") || operationName.equalsIgnoreCase("Update Due-Date")
						|| operationName.equalsIgnoreCase("Update Customer Information")) {
					String updatePortInTransId = "";
					String requestNo= "";
					log.debug("responseId::" + responseId );
					if (responseId != null && !responseId.isEmpty()) {
						updatePortInTransId = rcsDao.getUpdatePortInTransactionId(responseId);
						log.debug("updatePortInTransId::" + updatePortInTransId );
					}
					
					if (updatePortInTransId != null && !updatePortInTransId.isEmpty()) {
						requestNo = rcsDao.getUpdatePortInActivateTransactionId(updatePortInTransId);
						log.debug("requestNo::" + requestNo );
					}
					
					JsonObject reqpar = new JsonParser().parse(formattedJson).getAsJsonObject();
					JsonObject dataobj = new JsonObject();
					JsonArray suborderArray = new JsonArray();
					JsonObject obj1 = new JsonObject();
					String oldOriginalRefNumber = "";
					log.debug("Update portIn::reqpar:: " + reqpar);
					if (reqpar.has("data")) {
						dataobj = reqpar.get("data").getAsJsonObject();
					}
					if (dataobj.has("subOrder")) {
						suborderArray = dataobj.get("subOrder").getAsJsonArray();
						obj1 = suborderArray.get(0).getAsJsonObject();
					}
					
					if (obj1.has("originalRefNumber")) {
						oldOriginalRefNumber = obj1.get("originalRefNumber").getAsString();
						log.debug("Update portIn::oldOriginalRefNumber:: " + reqpar);
						
					}
					String requestUpdate="\"originalRefNumber\":\"" + requestNo + "\"";
					
					if (formattedJson.contains("\"originalRefNumber\":\"" + oldOriginalRefNumber + "\"")) {
						formattedJson = formattedJson.replace("\"originalRefNumber\":\"" + oldOriginalRefNumber + "\"", requestUpdate);
					}
				}
				
				if("success".equalsIgnoreCase(rcsServiceBean.getSearchEnvFlag())) {
					log.debug("Inside SearchEnvFlag Scenario::" + rcsServiceBean.getSearchEnvFlag());
					JSONObject dataObj = new JSONObject();
					JSONArray subOrderArray = new JSONArray();
					JSONArray simIdArray = new JSONArray();
					JSONObject simObj = new JSONObject();
					JSONObject subOrderObj = new JSONObject();
					SearchEnvironmentResultDto searchEnvironmentResultDto = null;
					String eLineId="";
					String migrationStatus = "";
					String subGroupCd ="";
					String contextId = null;
					String iccid="";
					try {
						String dataRequest = dataMap.get("initRequest");
						log.debug("formatted Json dataRequest::" + dataRequest);
						JSONObject dataObjectForSeachEnv = new JSONObject(dataRequest);
						if(dataObjectForSeachEnv.has("data")) {
							dataObj = dataObjectForSeachEnv.getJSONObject("data");
							if(dataObj.has("subOrder")) {
								subOrderArray=dataObj.getJSONArray("subOrder");
								subOrderObj=subOrderArray.getJSONObject(0);
								if(subOrderObj.has("simId")) {
									simIdArray=subOrderObj.getJSONArray("simId");
									simObj=simIdArray.getJSONObject(0);
									if(simObj.has("value")) {
										iccid=simObj.getString("value");
									}
								}
							}
						}
						if ((dataObj.getString("extInquiry").equalsIgnoreCase("NO")
								|| dataObj.getString("extInquiry").equalsIgnoreCase("null") 
								|| dataObj.getString("extInquiry").equalsIgnoreCase("FALSE") 
								|| dataObj.getString("extInquiry").equalsIgnoreCase("N"))) {
							if(!iccid.isEmpty() && iccid != null) {
								Sim sim = new Sim();
								sim.setIccid(iccid);
								outReqBean.setRequestJson(formattedJson);
								outReqBean.setSourceSystem(CommonConstants.NSL);
								log.info("formattedJson0:: " + formattedJson); 
								rcsServiceBean.setRequest(formattedJson);
								outReqBean.setTransUid(transId);
								outReqBean.setOperationName(operationName);
								searchEnvironmentResultDto = apolloNEOutboundClientService.callSimResourceServiceForSearchEnvironment(sim);
								if (Objects.nonNull(searchEnvironmentResultDto) && searchEnvironmentResultDto.getLine() != null) {
									log.debug("Search Environment iccid::" + iccid);
									if(searchEnvironmentResultDto.getLine() != null) {
										migrationStatus = searchEnvironmentResultDto.getLine().getMigStatus();
									}
									if(searchEnvironmentResultDto.getAccount() != null) {
										subGroupCd = searchEnvironmentResultDto.getAccount().getSubgroupcd();
										if (!StringUtils.hasText(subGroupCd)) {
											contextId = searchEnvironmentResultDto.getAccount().getContextId();
										}
									}
									this.insertTransaction(rcsServiceBean, outReqBean);
									outputString = searchEnvironmentResponse(migrationStatus, dataObjectForSeachEnv, eLineId, subGroupCd,
											responseId, contextId);
									searchEnvTargetSystem=true;
									rcsDao.updateSouthBoundTransaction(rcsServiceBean.getTranscationId(), outReqBean.getEntityId(),
											outReqBean.getGroupId(), outputString, "200", "NSL", responseId, "",operationName, null,sendClientRequest);
									return outputString;
								}
							}
							
						}
					}catch(Exception e) {
						log.error("ErrorCode : "+ErrorCodes.CECC0006+" : ERROR in Search Env-{}",e);
					}	
				}

				outReqBean.setRequestJson(formattedJson);
				outReqBean.setSourceSystem(CommonConstants.NSL);
				// String trialFormattedJson=jsonFormatter(str);
				// log.info("trialFormattedJson::::" + trialFormattedJson);

				log.info("formattedJson:: " + formattedJson); // CP_SPEED_TESTS_POST
				rcsServiceBean.setRequest(formattedJson);
				outReqBean.setTransUid(transId);
				outReqBean.setOperationName(operationName);

				switch (checkNEFlag()) {
				case CommonConstants.AP_NE: {
					url = rcsDao.getEndpointUrl2(dataMap.get("EndpointURL"), properties.getServer());
					outputString = getResponseFromStub(rcsServiceBean, dataMap, outReqBean, url, responseId,sendClientRequest);
					break;
				}
				case CommonConstants.NO: {
					if (operationName.equalsIgnoreCase("Deactivate Subscriber")
							|| operationName.equalsIgnoreCase("Validate Device")
							|| operationName.equalsIgnoreCase("Validate BYOD")) {
						// outputString = getResponseFromClient(rcsServiceBean, dataMap, outReqBean,
						// operationName,responseId);
						this.insertTransaction(rcsServiceBean, outReqBean);
						outputString = sendRequestToExternalClient(rcsServiceBean, dataMap, outReqBean, operationName,
								responseId,false);
					} else {
						url = rcsDao.getServiceUrl(dataMap.get("EndpointURL"), properties.getServer());
						if (outReqBean.getTransId() == null) {
							outReqBean.setTransId(rcsDao.getPrimaryKey());
						}
						log.debug("transId::" + outReqBean.getTransId());
						outReqBean.setRequestJson(consumerUtilityClass.changeReferenceNumToTransId(outReqBean.getRequestJson(), outReqBean.getTransId()));
						outputString = getResponseFromStub(rcsServiceBean, dataMap, outReqBean, url, responseId,sendClientRequest);
					}
					break;
				}
				default:
					if (properties.getEnableQueueCheck()) {
						this.insertTransaction(rcsServiceBean, outReqBean);
						outputString = sendRequestToExternalClient(rcsServiceBean, dataMap, outReqBean, operationName,
								responseId,false);
					} else {
						outputString = getResponseFromClient(rcsServiceBean, dataMap, outReqBean, operationName,
								responseId,sendClientRequest);
					}
				}
				log.info("Response outputString::" + outputString);
				try {
					if (outputString != null && outputString.startsWith("{")) {
						JSONObject responseObj = new JSONObject(outputString);
						JSONObject data = new JSONObject();
						JSONObject accountObj = new JSONObject();
						String transactionStatus = CommonConstants.FAILED;
						String notificationStatus = CommonConstants.FAILED;
						String accNo = "";
						Line line = new Line();
						String lineId = "";
						if (!(responseObj.toString().contains("success") || responseObj.toString().contains("Success")
								|| responseObj.toString().contains("SUCCESS")
								|| responseObj.toString().contains("\"code\":\"200\""))) {
							try {
								String requestMsg = dataMap.get("initRequest");
								if (requestMsg.startsWith("[")) {
									JSONArray requestArrObj = new JSONArray(requestMsg);

									if (requestArrObj != null && requestArrObj.length() > 0) {
										JSONObject requestObj = requestArrObj.getJSONObject(0);
										JSONArray suborderArray = new JSONArray();
										if (requestObj.has("data")) {
											data = requestObj.getJSONObject("data");
											if (data.has("account")) {
												accountObj = data.getJSONObject("account");
												if (accountObj.has("accountNumber")) {
													accNo = accountObj.getString("accountNumber");
													log.debug("accountObj accNo " + accNo);
													line.setAccountNumber(accNo);
												}
											}
											if (data.has("subOrder")) {
												suborderArray = data.getJSONArray("subOrder");
												log.debug("inside suborderArray::" + suborderArray);
												data = suborderArray.getJSONObject(0);
												if (data.has("lineId")) {
													lineId = data.getString("lineId");
													log.debug("lineId ::" + lineId);
													line.seteLineId(lineId);
													line.setInflightTransStatus("COMPLETED");
													log.info("before updateInflightTransStatus " + line);
													apolloNEOutboundClientService.updateInflightTransStatus(line);
												}
											}
										}
									}
								}

								log.debug("responseObj ::" + responseObj);
								/*
								 * if (responseObj.has("data")) { data = responseObj.getJSONObject("data"); if
								 * (data.has("returnCode")) { returnCode = data.getString("returnCode"); } } if
								 * (responseObj.has("messageHeader")) { messageHeaderObj =
								 * responseObj.getJSONObject("messageHeader"); if
								 * (messageHeaderObj.has("referenceNumber")) { swReferenceNumber =
								 * messageHeaderObj.getString("referenceNumber");
								 * log.debug("swReferenceNumber...." + swReferenceNumber);
								 * requestBean.setReferenceNumber(swReferenceNumber); } }
								 */

								// Line lineBean = sbNEResourceService.getLineDetailsWithELineId(lineId);
								/*
								 * if (returnCode != null && (returnCode.equals("E2601") ||
								 * returnCode.equals("E2302") || returnCode.equals("E2301") ||
								 * returnCode.equals("E1034")) &&
								 * lineBean.getLineType().equalsIgnoreCase("SMARTWATCH")) { TransactionHistory
								 * transactionHistory = new TransactionHistory();
								 * transactionHistory.setTransactionId(responseId);
								 * transactionHistory.setTransactionStatus(transactionSuccessStatus);
								 * transactionHistory.setNotificationStatus(notificationSuccessStatus);
								 * log.debug("requestBean before insertMNODomainDetails in mnoservice::" +
								 * requestBean); sbNEResourceService.insertMNODomainDetails(requestBean);
								 * log.debug("transactionHistory in Outbound::" + transactionHistory);
								 * sbNEResourceService.updateTransactionHistoryForNSUrl(transactionHistory); }
								 * else if (operationName.equalsIgnoreCase(Constants.RETRIEVE_DEVICE)) { if
								 * (responseObj != null && responseObj.toString().contains("\"contextId\":")) {
								 * TransactionHistory transactionHistory = new TransactionHistory();
								 * transactionHistory.setTransactionId(responseId);
								 * transactionHistory.setTransactionStatus(transactionSuccessSE);
								 * transactionHistory.setNotificationStatus(notificationStatus); log.debug(
								 * "transactionHistory in Outbound for retrieve device of search Environment::"
								 * + transactionHistory);

								 * sbNEResourceService.updateTransactionHistoryForNSUrl(transactionHistory); } }
								 * else {
								 */
								/*
								 * TransactionHistory transactionHistory = new TransactionHistory();
								 * transactionHistory.setTransactionId(responseId);
								 * transactionHistory.setTransactionStatus(transactionStatus);
								 * transactionHistory.setNotificationStatus(notificationStatus);
								 * log.debug("transactionHistory in Outbound::" + transactionHistory);
								 * apolloNEOutboundClientService.updateTransactionHistoryForNSUrl(
								 * transactionHistory);
								 */// }

							} catch (Exception e) {
								log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Exception {}", e);
							}
						}
					}
				} catch (Exception e) {
					log.error("ErrorCode : "+ErrorCodes.CECC0004+" : Exception {}", e);
				}

				log.debug("Inside updateTransactionDetails operationName::" + operationName);
				log.debug("Before Inside updateTransactionDetails outputString::" + outputString);
				RefErrorRules[] RefErrorRuleslst = null;
				String errorcodes = "";
				boolean isRetryMDNCodePresent = false;
				boolean isRetryAsyncCallBack = false;
				Integer retryLimit = 0;

				if ((operationName.equalsIgnoreCase("Activate Subscriber PSIM")
						|| operationName.equalsIgnoreCase("Activate Subscriber ESIM")
						|| operationName.equalsIgnoreCase(CommonConstants.ACTIVATESUBSCRIBER)
						|| operationName.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER)
						|| operationName.equalsIgnoreCase("Add Wearable")
						|| operationName.equalsIgnoreCase(CommonConstants.CHANGE_MDN))) {
					log.debug(
							"Inside outputString Activate Subscriber PSIM or Activate Subscriber ESIM zipCode Inventory::"
									+ outputString);
					log.debug(
							"Inside retryZipCodeCount() Activate Subscriber PSIM or Activate Subscriber ESIM zipCode Inventory::"
									+ outReqBean.getRetryZipCodeCount());
					RefErrorRules refErrorRules = new RefErrorRules();
					refErrorRules.setRuleDetails("RETRIEVE_ZIPCODE_RETRY");
					RefErrorRuleslst = apolloNEOutboundClientService.getErrorRulesDetails(refErrorRules);
					if (RefErrorRuleslst != null) {
						for (RefErrorRules refErrorRulesFromlst : RefErrorRuleslst) {
							errorcodes = "\"" + refErrorRulesFromlst.getErrorCode() + "\"";
							log.debug("errorcodes::" + errorcodes);
							if (StringUtils.hasText(outputString)) {
								if (outputString.contains(errorcodes)) {
									isRetryMDNCodePresent = true;
									isRetryAsyncCallBack = true;
									retryLimit = Integer.valueOf(refErrorRulesFromlst.getRetryLimit());
									log.debug("retryLimit ::" + retryLimit);
									log.debug(
											"outReqBean.getRetryZipCodeCount() ::" + outReqBean.getRetryZipCodeCount());
									if (outReqBean.getRetryZipCodeCount() != null) {
										if (retryLimit >= outReqBean.getRetryZipCodeCount()) {
											isRetryAsyncCallBack = false;
										}
										if (retryLimit > outReqBean.getRetryZipCodeCount()) {
											isRetryMDNCodePresent = false;
										}
									}
									break;
								}
							}
						}
					}
				}
				log.debug("Change SIM outReqBean.getRetryZipCodeCount() ::" + outReqBean.getRetryZipCodeCount());
				if (operationName.equalsIgnoreCase("Change SIM")) {
					String rootTransName = rcsDao.getRootTransName(responseId);
					String transMileStone = rcsDao.getTransactionMileStoneFromMetadata(responseId);
					if ("Swap MDN".equalsIgnoreCase(rootTransName)
							&& ("TEMP SIM Reserved".equalsIgnoreCase(transMileStone))) {
						RefErrorRules refErrorRules = new RefErrorRules();
						refErrorRules.setRuleDetails("RETRIEVE_MDNSWAP_RETRY");
						RefErrorRuleslst = apolloNEOutboundClientService.getErrorRulesDetails(refErrorRules);
						if (RefErrorRuleslst != null) {
							for (RefErrorRules refErrorRulesFromlst : RefErrorRuleslst) {
								errorcodes = "\"" + refErrorRulesFromlst.getErrorCode() + "\"";
								log.debug("errorcodes::" + errorcodes);
								if (outputString.contains(errorcodes)) {
									isRetryMDNCodePresent = true;
									isRetryAsyncCallBack = true;
									retryLimit = Integer.valueOf(refErrorRulesFromlst.getRetryLimit());
									log.debug("retryLimit ::" + retryLimit);
									if (outReqBean.getRetryZipCodeCount() != null) {
										if (retryLimit >= outReqBean.getRetryZipCodeCount()) {
											isRetryAsyncCallBack = false;
										}
									}
									break;
								}
							}
						}
					}
				}

				/*
				 * if (operationName.equalsIgnoreCase(Constants.RESTORE_SERVICE) ||
				 * operationName.equalsIgnoreCase(Constants.REMOVE_HOTLINE) ||
				 * operationName.equalsIgnoreCase(Constants.HOTLINE_SUBSCRIBER) ||
				 * operationName.equalsIgnoreCase(Constants.SUSPEND_SUBSCRIBER)) {
				 * 
				 * }
				 */
				log.info("before If Loop :: operationName::" + operationName + " - outputString::" + outputString);
				log.info("before If Loop :: isRetryAsyncCallBack::" + isRetryAsyncCallBack + " - isRetryMDNCodePresent::" + isRetryMDNCodePresent);
				if (operationName != null
						&& (operationName.equalsIgnoreCase("Validate MDN Portability")
								|| operationName.equalsIgnoreCase("Change MDN")
								|| operationName.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER)
								|| operationName.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER_PORTIN)
								|| operationName.equals(CommonConstants.ACTIVATESUBSCRIBER)
								|| operationName.equals(CommonConstants.ACTIVATESUBSCRIBER_PORTIN)
						|| operationName.equalsIgnoreCase("Validate Device")
						|| operationName.equalsIgnoreCase("Validate BYOD")
						|| operationName.equalsIgnoreCase("Change Wholesale Rate Plan")
						|| operationName.equalsIgnoreCase(CommonConstants.CHANGE_SIM)
						|| operationName.equalsIgnoreCase(CommonConstants.SUSPEND_SUBSCRIBER)
						|| operationName.equalsIgnoreCase(CommonConstants.HOTLINE_SUBSCRIBER)
						|| operationName.equalsIgnoreCase(CommonConstants.DELETE_SUBSCRIBER)
						|| operationName.equalsIgnoreCase(CommonConstants.TW_DEACTIVATE_SUBSCRIBER)
						|| operationName.equalsIgnoreCase("Restore Service")
						|| operationName.equalsIgnoreCase("Remove Hotline")
						|| operationName.equalsIgnoreCase(CommonConstants.CHANGE_RATE_PLAN)
						|| operationName.equalsIgnoreCase(CommonConstants.CHANGE_FEATURE)
						|| operationName.equalsIgnoreCase("Reconnect Service") || operationName.equalsIgnoreCase("ChangeESIM") || (operationName.equalsIgnoreCase("CP-Reconnect Mdn"))) && (!isRetryAsyncCallBack) && !(operationName.equalsIgnoreCase("Update Port-Out")
								|| operationName.equalsIgnoreCase("Update Due-Date")
								|| operationName.equalsIgnoreCase("Cancel Port-In")
								|| operationName.equalsIgnoreCase("Update Customer Information")) && (!isRetryMDNCodePresent)) {
					log.debug("Before Validating Error Response to ITMBO for operationName:: " + operationName);
					log.debug("Before Validating Error Response to ITMBO for requestParams:: " + requestParams);
					JSONArray jsonarr = new JSONArray();
					JSONObject jsonObj = new JSONObject();
					String lineType="";
					if (requestParams.startsWith("[") && requestParams.endsWith("]")) {
						jsonarr = new JSONArray(requestParams);
						jsonObj = new JSONObject();
						jsonObj = jsonarr.getJSONObject(0);
					} else {
						jsonObj=new JSONObject(requestParams);
					}
					if (operationName.equalsIgnoreCase(CommonConstants.RESTORE_SERVICE)
							|| operationName.equalsIgnoreCase(CommonConstants.REMOVE_HOTLINE)
							|| operationName.equalsIgnoreCase(CommonConstants.HOTLINE_SUBSCRIBER)
							|| operationName.equalsIgnoreCase(CommonConstants.SUSPEND_SUBSCRIBER)) {
						if (jsonObj.has("data")) {
							JSONObject dataObj = jsonObj.getJSONObject("data");
							if (dataObj.has("subOrder")) {
								JSONArray subOrderArray = dataObj.getJSONArray("subOrder");
								JSONObject suborderObj = subOrderArray.getJSONObject(0);
								String removeHotlineElineId = "";
								if (suborderObj.has("lineId")) {
									removeHotlineElineId = suborderObj.getString("lineId");
								}
								Line lineBean = null;
								if (removeHotlineElineId != null && !removeHotlineElineId.isEmpty()) {
									lineBean = apolloNEOutboundClientService.getLineDetailsWithELineId(removeHotlineElineId);
								}
								if (lineBean != null) {
									lineType = lineBean.getLineType();
								}
							}
						}
						if ((outputString.contains("returnCode\":\"E2601")
								|| outputString.contains("returnCode\":\"E2302")
								|| outputString.contains("returnCode\":\"E2301")
								|| outputString.contains("returnCode\":\"E1034")
								||outputString.contains("responseCode\":\"E2601")
								|| outputString.contains("responseCode\":\"E2302")
								|| outputString.contains("responseCode\":\"E2301")
								|| outputString.contains("responseCode\":\"E1034"))
								&& (lineType.equalsIgnoreCase("SMARTWATCH"))) {
							getActivationLineResponseFromClient(requestParams, responseId, responseId, operationName);
						}
					}

					if (outputString != null && (outputString.contains("\"returnCode\":\"E")
							|| outputString.contains("\"errorCode\":\"E") || outputString.contains("\"returnCode\":400")
							|| outputString.contains("\"responseType\":\"X\"")
							|| outputString.contains("\"responseType\":\"R\"")
							|| outputString.contains("\"returnCode\":\"400\"")
							|| outputString.contains("\"returnCode\":\"500\"")
							|| outputString.contains("\"responseCode\":\"E")
							|| outputString.contains("\"errorCode\":400")
							|| outputString.contains("\"errorCode\":404")) && (!lineType.equalsIgnoreCase("SMARTWATCH"))) {
						log.info("Sending Error Response to ITMBO system for operationName " + operationName);
						apolloNESyncFailureToITMBO(outputString, requestParams, responseId, operationName, outReqBean,
								dataMap, transId,sendClientRequest,rcsServiceBean);
					} else {
						log.info("Sync Success for operationName " + operationName);
						//US-1058
						if((operationName.equalsIgnoreCase("CP-Reconnect Mdn")))
						{						 
							
							TransactionHistory transactionHistory = new TransactionHistory();
							transactionHistory.setTransactionId(responseId);
							transactionHistory.setTransactionStatus(CommonConstants.TRANSACTION_SUCESS);
							transactionHistory.setNotificationStatus(CommonConstants.TRANSACTION_CANCELLED);
							 
							log.debug("transactionHistory in Outbound==> Sync Success with ResponseType X::" + transactionHistory);
							apolloNEOutboundClientService.updateTransactionHistoryForNSUrl(transactionHistory);
						}
					}
				} else if (outputString != null && (outputString.contains("\"returnCode\":\"E")
						|| outputString.contains("\"errorCode\":\"E") || outputString.contains("\"returnCode\":400")
						|| outputString.contains("\"responseType\":\"X\"")
						|| outputString.contains("\"responseType\":\"R\"")
						|| outputString.contains("\"returnCode\":\"400\"")
						|| outputString.contains("\"returnCode\":\"500\"")
						|| outputString.contains("\"responseCode\":\"E") || outputString.contains("\"responseCode\":E")
						|| outputString.contains("\"errorCode\":400"))
						&& operationName.equals("UpdateSubscriber Group")) {
					log.info("Sync failure for operationName " + operationName);
					requestParams = consumerUtilityClass.jsonFormatter2(requestParams);
					log.debug("requestParams after json formatter ::" + requestParams);
					JSONArray jsonarr1 = new JSONArray(requestParams);
					JSONObject jsonObj1 = new JSONObject();
					jsonObj1 = jsonarr1.getJSONObject(0);
					log.debug("Inside Rollback UpdateSubscriber Group jsonObj1::" + jsonObj1);
					String transaction_Id = null;
					if (jsonObj1.has("referenceNumber")) {
						String referenceNumber = jsonObj1.getString("referenceNumber");
						transaction_Id = rcsDao.gettransactionId(referenceNumber);
					}
					getManageRollbackClient(dataMap.get("initRequest"), transaction_Id, transaction_Id);
				} else if (operationName.equals("Validate Device")
						&& !(outputString.contains("\"euiccCapable\":\"Y\""))) {
					apolloNESyncFailureToITMBO(outputString, requestParams, responseId, operationName, outReqBean,
							dataMap, transId,sendClientRequest,rcsServiceBean);
				}
				
				log.debug("requestParams transferReconnectFlag::" + requestParams + "responseId::" + responseId);
				log.debug("transferReconnectFlag to call reconnect::" + transferReconnectFlag);
				log.debug("transferReconnectFlag outputString::" + outputString);
				if ("Add Wearable".equalsIgnoreCase(operationName) && transferReconnectFlag) {
					if (outputString.contains("\"returnCode\":\"E") || outputString.contains("\"errorCode\":\"E")
							|| outputString.contains("\"returnCode\":400") || outputString.contains("{\"responseCode\":\"E") || outputString.contains("{\"responseCode\": \"E")) {
						log.debug("inside transferReconnectFlag reconnect::" + outputString);
						getReconnectMdnClientCall(requestParams, responseId, responseId);
					}
				}
				if ("Validate Device".equalsIgnoreCase(operationName)) {
					if (outputString.contains("\"returnCode\":\"E") || outputString.contains("\"errorCode\":\"E")
							|| outputString.contains("\"returnCode\":400")
							|| outputString.contains("{\"responseCode\":\"E")
							|| outputString.contains("{\"responseCode\": \"E")) {
						log.debug("validate device outputString::" + outputString);
						outputString = "Failure";
						return outputString;
					}
				}
				
				
				/* else if (operationName.equalsIgnoreCase("CP-Reconnect Mdn")) {
					apolloNESyncFailureToITMBO(outputString, requestParams, responseId, operationName, outReqBean,
							dataMap, transId);}*/
			}

			return outputString;
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0004+" : Exception -", e);
		}
		return null;
	}
	
	public String searchEnvironmentResponse(String migrationStatus, JSONObject obj, String eLineId, String subGroupCd,
			String responseId, String contextId) {
		String serviceId = "";
		String requestType = "";
		String referenceNumber = "";
		String name = "";
		String value = "";
		String outputString = "";
		String migrationStatusType = "migrationStatus";
		JSONObject messageHeaderObject = new JSONObject();
		JSONArray additionalDataArray = new JSONArray();
		JSONObject additionalDataObject = new JSONObject();
		try {
			// String jsonResponse = jsonFormatter(requestParams);
			if(obj.has("messageHeader")) {
				messageHeaderObject= obj.getJSONObject("messageHeader");
				if(messageHeaderObject.has("requestType")) {
					requestType=messageHeaderObject.getString("requestType");
				}
				if(messageHeaderObject.has("referenceNumber")) {
					referenceNumber=messageHeaderObject.getString("referenceNumber");
				}
				if(messageHeaderObject.has("serviceId")) {
					serviceId=messageHeaderObject.getString("serviceId");
				}
			}
			if(obj.has("additionalData")) {
				additionalDataArray = obj.getJSONArray("additionalData");
				additionalDataObject=additionalDataArray.getJSONObject(0);
				if(additionalDataObject.has("name")) {
					name=additionalDataObject.getString("name");
				}
				if(additionalDataObject.has("value")) {
					value=additionalDataObject.getString("value");
				}
			}
			
			if (StringUtils.hasText(subGroupCd)) {
				contextId = getContextIdFromSearchEnvCode(subGroupCd);
				log.debug("SEARCH ENVIRONMENT CONTEXT_ID:::" + contextId);
			} else if (!StringUtils.hasText(contextId)) {
				contextId = "";
			}

			StringBuilder parameterJson = new StringBuilder();
			parameterJson.append("{\r\n");
			parameterJson.append("\"messageHeader\": {\r\n");
			parameterJson.append("\"serviceId\": \"" + serviceId + "\",\r\n");
			parameterJson.append("\"requestType\": \"" + requestType + "\",\r\n");
			parameterJson.append("\"referenceNumber\": \"" + referenceNumber + "\",\r\n");
			parameterJson.append("\"transactionId\": \"" + responseId + "\"\r\n");
			parameterJson.append("},\r\n");
			parameterJson.append("\"data\": {\r\n");
			parameterJson.append("\"contextId\": \"" + contextId + "\"\r\n");
			if (StringUtils.hasText(migrationStatus)) {
				parameterJson.append(",\r\n");
				parameterJson.append("\"status\": [\r\n");
				parameterJson.append("{\r\n");
				parameterJson.append("\"name\": \"" + migrationStatusType + "\",\r\n");
				parameterJson.append("\"value\": \"" + migrationStatus + "\"\r\n");
				parameterJson.append("}\r\n");
				parameterJson.append("]\r\n");
			}
			parameterJson.append("}\r\n");
			if (!name.isEmpty() && !value.isEmpty()) {
				parameterJson.append(",\r\n");
				parameterJson.append("\"additionalData\": [\r\n");
				parameterJson.append("{\r\n");
				parameterJson.append("\"name\": \"" + name + "\",\r\n");
				parameterJson.append("\"value\": \"" + value + "\"\r\n");
				parameterJson.append("}\r\n");
				parameterJson.append("]\r\n");
			}
			parameterJson.append("}");
			outputString = parameterJson.toString();

		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0007+" : Error in searchEnvironmentResponse{}", e);
		}
		return outputString;
	}
	
	public String getReconnectMdnClientCall(String requestJson, String transId, String responseId) {
		String request = null;
		String outputString = "";
		String serviceName = "";
		String operationName = "";
		JSONObject requestJsonObject = null;
		HttpURLConnection conn = null;
		try {

			JsonObject requestparamsJsonDataForAW = new JsonObject();
			if (requestJson.startsWith("[")) {
				JsonArray requestparamsJsonArray = new JsonParser().parse(requestJson).getAsJsonArray();
				requestparamsJsonDataForAW = requestparamsJsonArray.get(0).getAsJsonObject();
			} else {
				requestparamsJsonDataForAW = new JsonParser().parse(requestJson).getAsJsonObject();
			}
			log.debug("getReconnectMdnClientCall::" + requestparamsJsonDataForAW);
			if (requestparamsJsonDataForAW.has("messageHeader")) {
				requestparamsJsonDataForAW = requestparamsJsonDataForAW.getAsJsonObject("messageHeader");
				if (requestparamsJsonDataForAW.has("requestType")) {
					String transferWearableType = requestparamsJsonDataForAW.get("requestType").getAsString();
					if (transferWearableType.equalsIgnoreCase("MNO-AP")) {
						serviceName = "TWReconnectPP";
						operationName = "TWReconnectWFAP";
					} else {
						serviceName = "TWReconnectPP";
						operationName = "TWReconnectWF";
					}
				}
			}
			if (requestJson != null && !requestJson.isEmpty()) {
				requestJson = consumerUtilityClass.jsonFormatter2(requestJson);
				log.debug("ReconnectMdnClientCall formattedjson" + requestJson);
			}
			
			JSONObject initReq = new JSONObject();
			if (requestJson.startsWith("[")) {
				JSONArray requestJsonArr = new JSONArray(requestJson);
				initReq = requestJsonArr.getJSONObject(0);
			} else {
				initReq = new JSONObject(requestJson);
			}
			// JSONArray requestJsonArr = new JSONArray(requestJson);
			JSONArray newDeviceArr = new JSONArray();
			JSONArray newSimArr = new JSONArray();
			JSONObject newDeviceObject = new JSONObject();
			JSONObject newSimObject = new JSONObject();
			// JSONObject initReq = new JSONObject();
			// initReq = requestJsonArr.getJSONObject(0);
			String url = apolloNEServiceProperties.getRouterserviceurl();
			log.debug("ReconnectMdnClientCall initReq" + initReq);
			String lineId = "";
			String oldWatchMdn = "";
			String hostMdn = "";
			String transType = "";
			String iccid = "";
			String bcd = "";
			String nextAvailMdnZip = "";
			Line line = new Line();
			Sim simBean = new Sim();
			if (initReq != null) {
				if (initReq.has("mdn")) {
					hostMdn = initReq.getString("mdn");
				} else if (initReq.has("hostMDN")) {
					hostMdn = initReq.getString("hostMDN");
				}
				if (initReq.has("lineId")) {
					lineId = initReq.getString("lineId");
				}
				if (initReq.has("transactionType")) {
					transType = initReq.getString("transactionType");
				}
				if (initReq.has("imei")) {
					newDeviceObject.put("type", "IMEI");
					newDeviceObject.put("value", initReq.getString("imei"));
					newDeviceArr.put(newDeviceObject);
					initReq.remove("type");
					initReq.put("deviceId", newDeviceArr);
				} else if (initReq.has("IMEI")) {
					newDeviceObject.put("type", "IMEI");
					newDeviceObject.put("value", initReq.getString("IMEI"));
					newDeviceArr.put(newDeviceObject);
					initReq.remove("type");
					initReq.put("deviceId", newDeviceArr);
				}

				if (initReq.has("zipCode")) {
					nextAvailMdnZip = initReq.getString("zipCode");
					initReq.put("mdnzipCode", nextAvailMdnZip);
					initReq.put("addresszipCode", nextAvailMdnZip);
				}
			}
			log.debug("lineid ::: " + lineId);
			line = apolloNEOutboundClientService.getLineDetailsWithELineId(lineId);
			log.debug("line bean ::: " + line);
			oldWatchMdn = line.getMdn();
			bcd = line.getBcd();
			simBean.seteLineId(lineId);
			simBean = apolloNEOutboundClientService.callSimResourceService(simBean);
			log.debug("sim bean ::: " + simBean);
			iccid = simBean.getIccid();
			if (iccid != null && !iccid.isEmpty()) {
				newSimObject.put("type", "ICCID");
				newSimObject.put("value", iccid);
				newSimArr.put(newSimObject);
				initReq.put("simId", newSimArr);
			}
			initReq.put("ICCID", iccid);
			//initReq.put("simValue", iccid);
			initReq.put("DPFOResetDay", bcd);
			initReq.put("mdnValue", oldWatchMdn);
			// log.info("iccid ::: "+iccid+"bcd:::"+bcd);
			requestJson = initReq.toString();
			// log.info("inside reconnect oldWatchMdn::: "+oldWatchMdn+"::hostMdn::
			// "+hostMdn);
			requestJson = requestJson.replace(hostMdn, oldWatchMdn);
			requestJson = requestJson.replace(transType, "RM");
			// log.info("formattedJson after replace reconnect:::"+requestJson);
			if (requestJson.contains("&")) {
				requestJson = requestJson.replaceAll("&", "u+00000026");
			}
			request = "json=" + requestJson + "&serviceName=" + serviceName + "&operationName=" + operationName
					+ "&transId=" + responseId + "&responseId=" + responseId + "&ruleParam=validateDevice4Activation";
			log.info("request---:::" + request + "url:::" + url);
			URL serviceUrl = new URL(url);
			conn = (HttpURLConnection) serviceUrl.openConnection();
			byte[] requestData = request.getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("charset", "utf-8");
			conn.setRequestProperty("Content-Length", Integer.toString(requestData.length));
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("POST");
			conn.setUseCaches(false);
			OutputStream os = conn.getOutputStream();
			os.write(requestData);
			conn.connect();
			// log.info("code::: " + conn.getResponseCode() + ":::Inside http url connection
			// url:::" + url);
			InputStreamReader isr = new InputStreamReader(conn.getInputStream());
			BufferedReader in = new BufferedReader(isr);
			String responseString = null;
			while ((responseString = in.readLine()) != null) {
				outputString = outputString + responseString;
			}
			log.info("outputString in ReconnectMdnClientCall" + outputString);
			isr.close();
			os.close();
			return outputString;
		} catch (Exception e) {
			log.error("ErrorCode : " + ErrorCodes.CECC0006 + " : Exception - {}", e);

		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return null;
	}
	

	private RequestBean getRequestParamBean(String requestParams) throws JSONException {
		Gson requestParamGson = new Gson();
		String jsonResponse = consumerUtilityClass.jsonFormatter2(requestParams);
		JSONObject reqTypeObj = new JSONObject();
		JSONArray jsonArray = new JSONArray(jsonResponse);
		reqTypeObj = jsonArray.getJSONObject(0);
		return requestParamGson.fromJson(reqTypeObj.toString(), RequestBean.class);
	}

	void apolloNESyncFailureToITMBO(String outputString, String requestParams, String responseId, String operationName,
			OutboundRequest outReqBean, Map<String, String> dataMap, String transId,SendClientRequest sendClientRequest,RcsIntegrationServiceBean rcsServiceBean) {
		log.info("apolloNESyncFailureToITMBO:: outputString ::" + outputString + " - requestParams::" + requestParams
				+ " - responseId ::" + responseId + " - operationName :: " + operationName + "- outReqBean :: "
				+ outReqBean + " - dataMap ::" + dataMap + "- transId ::" + transId);
		try {
			log.info("outReqBean inside apolloNESyncFailureToITMBO" +outReqBean.toString()+":rcsServiceBean:"+rcsServiceBean.toString());
			requestParams = consumerUtilityClass.jsonFormatter2(requestParams);
			log.info("requestParams after json formatter ::" + requestParams);
			JSONArray requestjsonArray = new JSONArray(requestParams);
			log.info("requestjsonArray ::" + requestjsonArray);
			JSONObject channelobj = new JSONObject();
			channelobj = requestjsonArray.getJSONObject(0);
			log.info("channelobj ::" + channelobj);
			OutboundRequest outReqBeanMBO = outReqBean;
			log.info("outReqBeanMBO ::" + outReqBeanMBO);
			boolean channelCheck = true;
			String requestType =  "";
			if (channelobj.has("requestType")) {
				 requestType = channelobj.getString("requestType");
				 log.debug("requestType::" + requestType);
			}
			if (channelobj.has("data")) {
				channelobj = channelobj.getJSONObject("data");
				if (channelobj.has("channel")) {
					String channelName = channelobj.getString("channel");
					log.info("channelCheck::" + channelName);
					if (channelName.equalsIgnoreCase("NBOP") || channelName.equalsIgnoreCase("WEBSHEET")) {
						channelCheck = false;
					} else {
						channelCheck = true;
					}
				} else {
					channelCheck = true;
				}
			} else if (channelobj.has("channel")) {
				String channelName = channelobj.getString("channel");
				log.info("channelCheck::" + channelName);
				if (channelName.equalsIgnoreCase("NBOP") || channelName.equalsIgnoreCase("WEBSHEET")) {
					channelCheck = false;
				} else {
					channelCheck = true;
				}
			} else {
				channelCheck = true;
			}
			log.info("channelCheck ::" + channelCheck);
			boolean isChangeMdnPortIn=false;
			String changeMdnCPServiceName = "";
			changeMdnCPServiceName = rcsDao.getServiceName(responseId);
			if(changeMdnCPServiceName.equalsIgnoreCase("Transfer-Device")){
				if(operationName.equalsIgnoreCase("Change Feature")){
					swapLineIdforTransferDevice(responseId);
				}
			}
			if (operationName.equalsIgnoreCase("Activate Subscriber Port-in") || operationName.equalsIgnoreCase("ESIM Activate Subscriber Port-in")) {
						if (responseId != null && !responseId.isEmpty()) {
							/*String changeMdnCPServiceName = "";
							changeMdnCPServiceName = transactionDAO.getServiceName(responseId);*/
							String requestJson = rcsDao.getRequest(responseId);
							log.info("changeMdnCPServiceName::" + changeMdnCPServiceName + " requestJson::"
									+ requestJson);
					if (changeMdnCPServiceName.equalsIgnoreCase("Change-MDN")
							&& (operationName.equalsIgnoreCase("Activate Subscriber Port-in")
									|| operationName.equalsIgnoreCase("ESIM Activate Subscriber Port-in"))) {
								isChangeMdnPortIn=true;
								
								getChangeMdnCPClientCall(requestJson, responseId, responseId,requestType);
								rcsDao.requestlnpInformationDetailsMask(responseId);
								final String transactionIdLnp = responseId;
								ExecutorService executor = null;
								try {
									executor = Executors.newSingleThreadExecutor();
									executor.execute(new Runnable() {
										@Override
										public void run() {
											rcsDao.requestlnpInformationDetailsMask(transactionIdLnp);

										}
									});
									
								} catch (Exception e) {
									log.error("ErrorCode : "+ErrorCodes.CECC0009+" : Error ::", e);
								} finally {
									if (executor != null)
										executor.shutdown();
								}
							}
						}
					}
			if (channelCheck) {
				UUID uuid = null;
				uuid = UUID.randomUUID();
				String transIdMBO = uuid.toString();
				String returnURL = "";
				String endUrl = requestParams;
				
				
				JSONArray arr = new JSONArray(endUrl);
				if ((arr.getJSONObject(0).has("asyncErrorURL") || arr.getJSONObject(0).has("returnURL"))
						&& !("true".equalsIgnoreCase(outReqBean.getIsRetryMDNCodePresent()))) {
					if (arr.getJSONObject(0).has("asyncErrorURL")) {
						endUrl = arr.getJSONObject(0).getString("asyncErrorURL");
						returnURL = arr.getJSONObject(0).getString("returnURL");
						if (endUrl.equalsIgnoreCase("")) {
							endUrl = arr.getJSONObject(0).getString("returnURL");
						}
					} else {
						endUrl = arr.getJSONObject(0).getString("returnURL");
					}
					log.info("endUrl ::" + endUrl);
					Double trans_id = Double.parseDouble(responseId);
					log.info("trans_id ::" + trans_id);
					String returnMessage = "";
					String returnCode = "";
					String errorCode = "";
					String errorMessage = "";
					String res = "";
					String referenceNumber = rcsDao.getRefNumber(trans_id);
					log.info("referenceNumber ::" + referenceNumber);
					String returnMessages = consumerUtilityClass.jsonFormatter2(outputString);
					String returnCodes = consumerUtilityClass.jsonFormatter2(outputString);
					JSONArray arr1 = new JSONArray(returnMessages);
					JSONArray arr2 = new JSONArray(returnCodes);
					if (arr1.getJSONObject(0).has("returnMessage")) {
						returnMessage = arr1.getJSONObject(0).getString("returnMessage");
					}
					if (arr1.getJSONObject(0).has("description")) {
						returnMessage = arr1.getJSONObject(0).getString("description");
					}
					if (arr2.getJSONObject(0).has("returnCode")) {
						returnCode = arr2.getJSONObject(0).getString("returnCode");
					}
					if (arr2.getJSONObject(0).has("responseCode")) {
						returnCode = arr2.getJSONObject(0).getString("responseCode");
					}
					if (arr1.getJSONObject(0).has("errorCode")) {
						errorCode = arr1.getJSONObject(0).getString("errorCode");
					}
					if (arr2.getJSONObject(0).has("errorMessage")) {
						errorMessage = arr2.getJSONObject(0).getString("errorMessage");
					}
					if (operationName.equals("Validate Device")) {
						String esimServiceName = "";
						if (responseId != null && !responseId.isEmpty()) {
							esimServiceName = rcsDao.getServiceName(responseId);
						}
						if (esimServiceName.equalsIgnoreCase("addESimSubscriber")
								|| esimServiceName.equalsIgnoreCase("addESimChangeSubscriber")) {
							errorCode = "ERR50";
							errorMessage = "Unable to determine Rateplan/Device compatibility";
						}
					}
					if (operationName.equalsIgnoreCase("Change SIM") || operationName.equalsIgnoreCase("Activate Subscriber")) {
						int j = 0;
						String des = "";
						String errorCode1 ="";
						log.info("outputString for Change SIM  and Activate subsciber::" + outputString);
						JSONObject json = new JSONObject(outputString);
						if (json.has("errorMessage")) {
							String errorMessage1 = json.getString("errorMessage");
							log.debug("errorMessage1::"+errorMessage1);
							JSONObject json1 = new JSONObject(errorMessage1);
							log.debug("json1::"+json1.toString());
							if (json1.has("errors")) {
								JSONArray errorsArr = json1.getJSONArray("errors");								
								for (j = 0; j < errorsArr.length(); j++) {
									JSONObject errorsObj = errorsArr.getJSONObject(j);
									if (errorsObj.has("fieldName")) {
										errorMessage = errorsObj.getString("fieldName");
									}
									/*
									 * if (errorsObj.has("code")) { errorCode1 = errorsObj.getString("code"); }
									 * errorMessage = des + "\"code\":" + errorCode1;
									 */
									log.info("errorMessage for Change SIM  and Activate subsciber::" + errorMessage);
								}
							}
						}
					}
										
					/*boolean isChangeMdnPortIn=false;
					if (operationName.equalsIgnoreCase("Activate Subscriber Port-in")) {
								if (responseId != null && !responseId.isEmpty()) {
									String changeMdnCPServiceName = "";
									changeMdnCPServiceName = transactionDAO.getServiceName(responseId);
									String requestJson = rcsDao.getRequest(responseId);
									log.info("changeMdnCPServiceName::" + changeMdnCPServiceName + " requestJson::"
											+ requestJson);
									if (changeMdnCPServiceName.equalsIgnoreCase("Change-MDN")
											&& operationName.equalsIgnoreCase("Activate Subscriber Port-in")) {
										isChangeMdnPortIn=true;
										
										getChangeMdnCPClientCall(requestJson, responseId, responseId,requestType);
										rcsDao.requestlnpInformationDetailsMask(responseId);
										final String transactionIdLnp = responseId;
										ExecutorService executor = null;
										try {
											executor = Executors.newSingleThreadExecutor();
											executor.execute(new Runnable() {
												@Override
												public void run() {
													rcsDao.requestlnpInformationDetailsMask(transactionIdLnp);

												}
											});
											
										} catch (Exception e) {
											log.error("ErrorCode : "+ErrorCodes.CECC0009+" : Error ::", e);
										} finally {
											if (executor != null)
												executor.shutdown();
										}
									}
								}
							}*/
					if (!isChangeMdnPortIn) {
						String transMileStone="";
						if(operationName.equalsIgnoreCase(CommonConstants.CHANGE_SIM))
						{
						String rootTransName=rcsDao.getRootTransName(responseId);
						transMileStone=rcsDao.getTransactionMileStoneFromMetadata(responseId);
						if("Swap MDN".equalsIgnoreCase(rootTransName)&&("MDN2 Swapped to SIM1".equalsIgnoreCase(transMileStone))||("Rollback MDN1 to SIM1".equalsIgnoreCase(transMileStone))
								||("Rollback MDN2 to SIM2".equalsIgnoreCase(transMileStone))||("MDN1 Swapped to SIM2".equalsIgnoreCase(transMileStone))) {
							
							errorMessage="Swap MDN Failed";
							errorCode="ERR102";
							returnCode="ERR102";
							returnMessage="Swap MDN Failed";
							
							
						}
						else if("Swap MDN".equalsIgnoreCase(rootTransName)&&("Swap MDN Failed".equalsIgnoreCase(transMileStone)))
								{
							
							errorMessage="Swap MDN Failed - Rollback Failed";
							errorCode="ERR103";
							returnCode="ERR103";
							returnMessage="Swap MDN Failed - Rollback Failed";
							
							
						}
						else if("Swap MDN".equalsIgnoreCase(rootTransName)&&("TEMP SIM Reserved".equalsIgnoreCase(transMileStone))) {
							errorMessage="Swap MDN Failed";
							errorCode="ERR102";
							returnCode="ERR102";
							returnMessage="Swap MDN Failed";
						}
						}
						if(!"Rollback MDN1 to SIM1".equalsIgnoreCase(transMileStone)) {
						res = asyncErrorCall(returnMessage, returnCode, errorCode, errorMessage, referenceNumber,
								responseId, operationName);
						outReqBeanMBO.setRequestJson(res);
						log.info("transIdMBO ::" + transIdMBO);
						outReqBeanMBO.setTransUid(transIdMBO);
						outReqBeanMBO.setTransId(rcsDao.getPrimaryKey());
						outReqBeanMBO.setOperationName("Async Error Callback");
						outReqBeanMBO.setApplicationName("MNO");
						outReqBean.setErrorCode(errorCode);
						log.info("transaction Id ::" + outReqBeanMBO.getTransId());
						if (endUrl != null && !endUrl.equalsIgnoreCase("batchprocessing")) {
							rcsDao.insertSouthBoundTransaction(outReqBeanMBO);
							/*
							 * insertSBoundTransactionAsync(res, outReqBean.getEntityId(), operationName,
							 * transId, outReqBean.getGroupId(), outReqBean.getProcessPlanId(), "MNO",
							 * responseId, "");
							 */
							String responses = getResponseFromMboClient(endUrl, res, dataMap, operationName,rcsServiceBean,outReqBean,responseId);
							log.info("response from ITMBO ::" + responses);
							Boolean enableErrorQueue = apolloNEQueueProperties.getEnableQueueCheck();
							if (!enableErrorQueue) {
								if (responses == null) {
									rcsDao.updateSouthBoundTransactionFailure(transIdMBO, outReqBeanMBO.getEntityId(),
											responses, outReqBeanMBO.getGroupId(), responseId, "MBO", null);
								} else if (responses != null) {
									if (!responses.startsWith("[") && !responses.startsWith("{")
											&& !responses.startsWith("<")) {
										rcsDao.updateSouthBoundTransactionFailure(transIdMBO, outReqBeanMBO.getEntityId(),
												responses, outReqBeanMBO.getGroupId(), responseId, "MBO", null);
									} else {
										rcsDao.updateSouthBoundTransaction(transIdMBO, outReqBeanMBO.getEntityId(),
												outReqBeanMBO.getGroupId(), responses, errorCode, "MBO", responseId, "",
												operationName, null,sendClientRequest);
									}
								}
							}
							
						}
					}
				}
					if (operationName.equalsIgnoreCase("Validate Device")
							|| operationName.equalsIgnoreCase("Validate BYOD")
							|| operationName.equalsIgnoreCase("ESIM Activate Subscriber Port-in")) {
						final String transactionIdLnp = responseId;
						ExecutorService executor = null;
						try {
							executor = Executors.newSingleThreadExecutor();
							executor.execute(new Runnable() {
								@Override
								public void run() {
									rcsDao.requestlnpInformationDetailsMask(transactionIdLnp);

								}
							});

						} catch (Exception e) {
							log.error("Error ::", e);
						} finally {
							if (executor != null)
								executor.shutdown();
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0004+" : Error in apolloNESyncFailureToITMBO{}", e);
		}

	}

	private String getResponseFromStub(RcsIntegrationServiceBean rcsServiceBean, Map<String, String> dataMap,
			OutboundRequest outReqBean, String url, String responseId,SendClientRequest sendClientRequest) throws JSONException {

		String queueStatus = CommonConstants.EMPTYSTRING;
		String target = CommonConstants.TARGET_SYSTEM;
		String outputString = CommonConstants.EMPTYSTRING;
		String statusCode = CommonConstants.EMPTYSTRING;
		String operationName = outReqBean.getOperationName();
		rcsDao.insertSouthBoundTransaction(outReqBean);
		log.info("getResponseFromStub endPointUrl::" + url);
		ResponseEntity<String> response = callStubSys(outReqBean, url);
		if (response != null) {
			statusCode = String.valueOf(response.getStatusCode().value());
		}
		if (response != null && response.getBody() != null) {
			outputString = response.getBody();
		}
		log.info("outputString::" + outputString);
		if (response != null && outputString != null) {
			if (!outputString.startsWith("[") && !outputString.startsWith("{")) {
				rcsDao.updateSouthBoundTransaction(rcsServiceBean.getTranscationId(), outReqBean.getEntityId(),
						outReqBean.getGroupId(), outputString, statusCode, target, responseId, queueStatus,operationName, url,sendClientRequest);
			} else {
				rcsDao.updateSouthBoundTransaction(rcsServiceBean.getTranscationId(), outReqBean.getEntityId(),
						outReqBean.getGroupId(), outputString, statusCode, target, responseId, queueStatus,operationName, url,sendClientRequest);
			}
		} else {
			rcsDao.updateSouthBoundTransaction(rcsServiceBean.getTranscationId(), outReqBean.getEntityId(),
					outReqBean.getGroupId(), outputString, statusCode, target, responseId, queueStatus,operationName, url,sendClientRequest);
		}

		return outputString;
	}

	public ResponseEntity<String> callStubSys(OutboundRequest outReqBean, String url) {

		ResponseEntity<String> response = null;
		try {

			RestTemplate rest = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
			map.add(CommonConstants.REQ_JSON, outReqBean.getRequestJson());
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map,
					headers);
			response = rest.postForEntity(url, request, String.class);

		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Error in callResourceService{}", e);
		}
		return response;
	}

	private String checkNEFlag() {

		return rcsDao.getNEFlag(CommonConstants.NE_FLAG);
	}

	public String getResponseFromClient(RcsIntegrationServiceBean rcsServiceBean, Map<String, String> dataMap,
			OutboundRequest outReqBean, String operationName, String responseId,SendClientRequest sendClientRequest) throws Exception {
		log.debug("Inside getResponseFromClient::");
		log.debug("Inside operationName::" + operationName);
		String outputString = CommonConstants.EMPTYSTRING;
		String target = CommonConstants.EMPTYSTRING;
		String queueStatus = CommonConstants.EMPTYSTRING;
		searchEnvTargetSystem = false;
		if (operationName.equalsIgnoreCase("Activate Subscriber PSIM")
				|| operationName.equalsIgnoreCase("Activate Subscriber ESIM")
				|| (operationName.equalsIgnoreCase("Add Wearable")) || (operationName.equalsIgnoreCase("ChangeESIM"))
				|| (operationName.equalsIgnoreCase("Imsi Inquiry"))
				|| (operationName.equalsIgnoreCase("Update Port-Out")) || (operationName.equalsIgnoreCase("Change MDN"))
				|| (operationName.equalsIgnoreCase("Change Feature")) || (operationName.equalsIgnoreCase("Change SIM"))
				|| (operationName.equalsIgnoreCase("Validate Device"))
				|| (operationName.equalsIgnoreCase("UpdateSubscriber Group"))
				|| (operationName.equalsIgnoreCase(CommonConstants.RETRIEVE_DEVICE))
				|| (operationName.equalsIgnoreCase(CommonConstants.ACTIVATESUBSCRIBER))
				|| (operationName.equalsIgnoreCase(CommonConstants.ACTIVATESUBSCRIBER_PORTIN))
				|| (operationName.equals(CommonConstants.VALIDATE_MDN_PORTABILITY))
				|| (operationName.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER))
				|| (operationName.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER_PORTIN))
				|| (operationName.equalsIgnoreCase("Validate BYOD"))
				|| (operationName.equalsIgnoreCase(CommonConstants.CHANGE_BCD))
				|| (operationName.equalsIgnoreCase("Change Wholesale Rate Plan"))
				|| (operationName.equalsIgnoreCase(CommonConstants.RESET_FEATURE))
				|| (operationName.equalsIgnoreCase(CommonConstants.MANAGE_PROMOTION))
				|| (operationName.equalsIgnoreCase("Device Detection Change Rate Plan"))
				|| (operationName.equalsIgnoreCase("Change Rate Plan"))
				|| (operationName.equalsIgnoreCase(CommonConstants.UPDATE_WIFI_ADDRESS))
				|| (operationName.equalsIgnoreCase(CommonConstants.VALIDATE_WIFI_ADDRESS))
				|| (operationName.equalsIgnoreCase(CommonConstants.GET_WIFI_ADDRESS))
				|| (operationName.equalsIgnoreCase(CommonConstants.PORTIN_INQUIRY))
				|| (operationName.equalsIgnoreCase(CommonConstants.VALIDATE_SIM))
				|| (operationName.equalsIgnoreCase(CommonConstants.SUSPEND_SUBSCRIBER))
				|| (operationName.equalsIgnoreCase(CommonConstants.HOTLINE_SUBSCRIBER))
				|| (operationName.equalsIgnoreCase(CommonConstants.DELETE_SUBSCRIBER))
				|| (operationName.equalsIgnoreCase(CommonConstants.TW_DEACTIVATE_SUBSCRIBER))
				|| (operationName.equalsIgnoreCase("Restore Service"))
				|| (operationName.equalsIgnoreCase("Remove Hotline"))
				|| (operationName.equalsIgnoreCase("Reconnect Service")
				|| operationName.equalsIgnoreCase("CP-Reconnect Mdn"))
				|| (operationName.equalsIgnoreCase(CommonConstants.DEVICE_CHECK_GSMA))
				||operationName.equalsIgnoreCase("GSMA Device Check")) {
			target = CommonConstants.APOLLOTARGET_SYSTEM;
			log.debug("Setting Target System as APollo NE :: ");
		} else if (searchEnvTargetSystem != null && searchEnvTargetSystem) {
			target = CommonConstants.TARGET_SEARCH_SYSTEM;
			log.debug("Setting Target System as SEARCH ENV:: ");
		} else {
			target = CommonConstants.TARGET_SYSTEM;
			log.debug("Setting Target System as CBRS NE :: ");
		}
		String statusCode = "500";
		ResponseEntity<String> response = null;
		String endPointUrl = rcsDao.getEndpointUrl(dataMap.get("EndpointURL"), properties.getServer());
		log.debug("endPointUrl :: " + endPointUrl);
		log.debug("updated Target system :: " + target);
		if (StringUtils.hasText(endPointUrl)) {
			try {

				/*
				 * Map<String, String> queryMap =
				 * this.getRequiredInfo(rcsServiceBean.getRequest(),
				 * ApolloNEConstants.REQUEST_PARAM); log.info("queryMap : " + queryMap);
				 * endPointUrl = this.constructQueryParams(endPointUrl, queryMap);
				 */
				if (endPointUrl.contains("${")) {
					/*
					 * Map<String, String> pathMap =
					 * this.getRequiredInfo(rcsServiceBean.getRequest(),
					 * ApolloNEConstants.PATH_PARAM);
					 */
					endPointUrl = consumerUtilityClass.getResponseFromGetClient(endPointUrl, rcsServiceBean.getRequest(), dataMap,
							operationName, responseId);
					log.debug("endPointUrl:: " + endPointUrl);
					// log.debug("pathMap::" + pathMap);
					// endPointUrl = this.formatUrlForPathParam(endPointUrl, pathMap);
					rcsServiceBean.setNcmSouthBoundUrl(endPointUrl);
					rcsServiceBean.setHttpMethod(EHttpMethods.valueOf(CommonConstants.GET));
					log.debug("endPointUrl :: Inside if " + endPointUrl);
				} else {
					rcsServiceBean.setNcmSouthBoundUrl(endPointUrl);
					rcsServiceBean.setHttpMethod(EHttpMethods.valueOf(CommonConstants.POST));
				}
				Map<String, String> headerMap = consumerUtilityClass.getHeaderInfo(rcsServiceBean.getRequest(),
						rcsServiceBean.getTranscationId());
				if (headerMap != null) {
					rcsServiceBean.setHeaderMap(headerMap);
				}
				log.debug("rcsServiceBean.getHttpMethod() :: " + rcsServiceBean.getHttpMethod());

				response = this.callExtSys(rcsServiceBean, outReqBean);
				log.debug("response::" + response.toString());
				if (response != null) {
					statusCode = String.valueOf(response.getStatusCode().value());
				}
				if (response != null && response.getBody() != null) {
					outputString = response.getBody();
					/*
					 * if (!operationName.equals("Syniverse Register Subscriber") &&
					 * !operationName.equals("Syniverse De-Register Subscriber")) { outputString =
					 * this.constructOutboundResponse(response.getStatusCode(), outputString); }
					 * commented with US validation
					 */
				}
				outputString = outputString + "~" + statusCode + "~" + endPointUrl;
			} catch (Exception e) {
				log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Exception in getResponseFromClient{}", e);
			}

			log.debug("outputString::" + outputString);
			if (response != null && outputString != null) {
				if (!outputString.startsWith("[") && !outputString.startsWith("{")) {
					rcsDao.updateSouthBoundTransaction(rcsServiceBean.getTranscationId(), outReqBean.getEntityId(),
							outReqBean.getGroupId(), outputString, statusCode, target, responseId, queueStatus,operationName, endPointUrl,sendClientRequest);
				} else if (operationName.equals("Syniverse Register Subscriber")
						|| operationName.equals("Syniverse De-Register Subscriber")) {
					rcsDao.updateSouthBoundTransaction(rcsServiceBean.getTranscationId(), outReqBean.getEntityId(),
							outReqBean.getGroupId(), outputString, statusCode, ApolloNEConstants.SYNIVERSE, responseId,
							queueStatus,operationName, endPointUrl,sendClientRequest);
				} else {
					rcsDao.updateSouthBoundTransaction(rcsServiceBean.getTranscationId(), outReqBean.getEntityId(),
							outReqBean.getGroupId(), outputString, statusCode, target, responseId, queueStatus,operationName, endPointUrl,sendClientRequest);
				}
			} else {
				rcsDao.updateSouthBoundTransaction(rcsServiceBean.getTranscationId(), outReqBean.getEntityId(),
						outReqBean.getGroupId(), outputString, statusCode, target, responseId, queueStatus,operationName, endPointUrl,sendClientRequest);
			}
		} else {
			rcsDao.updateSouthBoundTransaction(rcsServiceBean.getTranscationId(), outReqBean.getEntityId(),
					outReqBean.getGroupId(), outputString, statusCode, target, responseId, queueStatus,operationName, endPointUrl,sendClientRequest);
		}
		log.debug("Exit getResponseFromClient::");
		return outputString;
	}
	public String getSwapMdnFromClient(String requestJson, String transId, String responseId,
			String transactionType,String operationName,String serviceName) {
		String request = null;
		String outputString = "";

		String taskName = "";

		HttpURLConnection conn = null;
		try {
			String url = apolloNEServiceProperties.getRouterserviceurl();
	
		
			if (requestJson.contains("&")) {
				requestJson = requestJson.replaceAll("&", "u+00000026");
			}

			log.debug("swapMDN in ReqDetails---::" + requestJson);
			
			request = "json=" + requestJson + "&serviceName=" + serviceName + "&operationName=" + operationName
					+ "&transId=" + responseId + "&responseId=" + responseId + "&ruleParam=returnSuccess";
			log.info("request---::" + request+"url---::" + url);

			 //url ="http://10.90.3.73:4545/Framework/services/flowComponent/router";
			URL serviceUrl = new URL(url);
			conn = (HttpURLConnection) serviceUrl.openConnection();
			byte[] requestData = request.getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("charset", "utf-8");
			conn.setRequestProperty("Content-Length", Integer.toString(requestData.length));
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("POST");
			conn.setUseCaches(false);
			OutputStream os = conn.getOutputStream();
			os.write(requestData);
			InputStreamReader isr = new InputStreamReader(conn.getInputStream());
			BufferedReader in = new BufferedReader(isr);
			String responseString = null;
			while ((responseString = in.readLine()) != null) {
				outputString = outputString + responseString;
			}
			isr.close();
			os.close();
			return outputString;
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Exception in SwapMdnCheckFromClient{}" ,e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return null;
	}

	public ResponseEntity<String> callExtSys(RcsIntegrationServiceBean rcsServiceBean, OutboundRequest outReqBean) {
		ResponseEntity<String> resp = new ResponseEntity<String>(ApolloNEConstants.NOT_RECEIVED_RESP,
				HttpStatus.INTERNAL_SERVER_ERROR);
		HttpEntity<String> entity = null;
		JsonObject bodyInfo = null;
		JsonObject bodyInfoTempJSON = null;
		String bodyInfoTemp = null;
		try {
			// bodyInfo = this.getRequestInfo(rcsServiceBean.getRequest(),
			// ApolloNEConstants.BODY_PARAM);
			outReqBean.setTransId(rcsDao.getPrimaryKey());

			// bodyInfo = new JSONObject(rcsServiceBean.getRequest());
			if (!rcsServiceBean.getHttpMethod().toString().equalsIgnoreCase(CommonConstants.GET)) {
				if (rcsServiceBean.getRequest() != null) {
					bodyInfoTemp = consumerUtilityClass.changeReferenceNumToTransId(rcsServiceBean.getRequest(), outReqBean.getTransId());
					// bodyInfoTempJSON = new JSONObject(bodyInfoTemp);
					bodyInfo = new JsonParser().parse(bodyInfoTemp).getAsJsonObject();
					if (bodyInfo.has("data")) {
						bodyInfo.get("data").getAsJsonObject().remove("internalOrder");
						bodyInfo.get("data").getAsJsonObject().remove("relatedTransactionId");
						bodyInfo.get("data").getAsJsonObject().remove("relatedLineId");
					}
				}
			}
			String token = this.getToken(rcsServiceBean.getAuthorization());
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
			headers.add("Authorization", token);
			if (bodyInfo != null) {
				JSONObject bodyInfoobject = new JSONObject(bodyInfo.toString());
				rcsServiceBean.setRequestInfo(bodyInfoobject);
				entity = new HttpEntity<String>(bodyInfo.toString(), headers);
				outReqBean.setRequestJson(bodyInfo.toString());
			} else {
				entity = new HttpEntity<String>(headers);
				outReqBean.setRequestJson(rcsServiceBean.getNcmSouthBoundUrl());
			}
			// outReqBean.setRequestJson(bodyInfoTemp);
			log.debug("outReqBean::" + outReqBean.toString());
			rcsDao.insertSouthBoundTransaction(outReqBean);
			resp = restTemplate.exchange(rcsServiceBean.getNcmSouthBoundUrl(),
					HttpMethod.valueOf(rcsServiceBean.getHttpMethod().toString()), entity, String.class);

			return resp;
		} catch (HttpClientErrorException e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Error in callExtSys{}", e);
			try {
				JSONObject tokenJson = new JSONObject();
				tokenJson.put("errorCode", e.getRawStatusCode());
				tokenJson.put("errorMessage", e.getResponseBodyAsString());
				resp = new ResponseEntity<String>(tokenJson.toString(), HttpStatus.valueOf(e.getRawStatusCode()));

				return resp;
			} catch (Exception e1) {
				log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Exception - ",e);
				return resp;
			}

		} catch (HttpServerErrorException ie) {
			log.debug("inside HttpServerErrorException");
			log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Error in callExtSys{}", ie);
			try {
				JSONObject tokenJson5XX = new JSONObject();
				tokenJson5XX.put("errorCode", ie.getRawStatusCode());
				tokenJson5XX.put("errorMessage", ie.getResponseBodyAsString());
				resp = new ResponseEntity<String>(tokenJson5XX.toString(), HttpStatus.valueOf(ie.getRawStatusCode()));

				return resp;
			} catch (Exception e2) {
				log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Exception - ",e2);
				return resp;
			}

		} catch (Exception e) {
			log.debug("inside general Exception");
			log.error("ErrorCode : "+ErrorCodes.CECC0004+" : Error in callExtSys{}", e);
			// e.printStackTrace();
			return resp;
		}
	}

	private String getToken(String auth) throws JSONException {
		String token = "";
		if (StringUtils.hasText(auth)) {
			ServiceInfo authInfo = rcsDao.getServiceInfoDetails(auth, properties.getServer());
			if (authInfo != null) {
				switch (authInfo.getAuthType()) {
				case "OAUTH": {
					String tokenJsonStr = consumerUtilityClass.getOauthToken(authInfo.getEndPointUrl(), authInfo.getMethod(),
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

	public String convertionLogic(String inputJson) {
		String response = null;
		try {
			log.debug("Inside  convertionLogic::" + inputJson);
			Gson gson = new Gson();
			JSONArray array = new JSONArray();
			List<GroupParameter> screenDetailsList = gson.fromJson(inputJson, new TypeToken<List<GroupParameter>>() {
			}.getType());
			log.debug("Inside Header Map  screenDetailsList:::" + screenDetailsList.toString());
			List<Parameter> finalParamList = new ArrayList<Parameter>();
			log.debug("Inside Header Map  finalParamList:::" + finalParamList.toString());
			JSONObject json = constructGroupParameter(screenDetailsList, finalParamList);
			log.debug("Inside Header Map  json::" + json.toString());
			array.put(json);
			response = array.toString();
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0013+" : Exception{}", e);
		}
		return response;
	}

	private JSONObject constructGroupParameter(List<GroupParameter> layOutRes, List<Parameter> paramList)
			throws org.json.JSONException {
		for (GroupParameter gp : layOutRes) {
			if (gp.getParameterList() != null && gp.getGroupParamList().isEmpty()) {
				paramList.addAll(gp.getParameterList());
			}
			if (gp.getGroupParamList() != null && gp.getGroupParamList().isEmpty()) {
				constructGroupParameter(gp.getGroupParamList(), paramList);
			}
		}
		return constructParameter(paramList);
	}

	private JSONObject constructParameter(List<Parameter> parameterList) {
		JSONObject jObject = new JSONObject();
		log.info("Inside constructParameter Map  parameterList::" + parameterList.toString());
		for (Parameter c : parameterList) {

			try {
				log.debug("Inside constructParameter Map  c::" + c.toString());
				List<String> value = c.getValueList();
				log.debug("Inside constructParameter Map  value::" + value.toString());
				if ("0".equals(value.get(0))) {
					log.debug("Inside if Map value::" + value.get(0));
					String val = "";
					jObject.put(c.getName(), val);
				} else {
					String val = value.get(0);
					log.debug("Inside else value::" + val);
					jObject.put(c.getName(), val);
				}
			} catch (Exception e) {
				log.error("ErrorCode : "+ErrorCodes.CECC0007+" : Exception{}", e);
			}
		}
		return jObject;
	}

	public String jsonFormatter(String inputData) {
		String response = null;
		try {
			Gson gson = new Gson();
			List<ScreenDetails> screenDetailsList = gson.fromJson(inputData, new TypeToken<List<ScreenDetails>>() {
			}.getType());
			List<GroupParameter> groupParameterList = screenDetailsList.get(0).getGroupParamList();
			JSONObject json = groupParameterConstruction(groupParameterList);
			response = "[" + json.toString() + "]";
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0013+" : Exception{}", e);
		}
		return response;
	}

	public JSONObject groupParameterConstruction(List<GroupParameter> groupParameterList) {

		JSONObject jsonObj = new JSONObject();
		JSONObject jsonObj1 = new JSONObject();
		try {
			for (GroupParameter gp : groupParameterList) {
				JSONObject jsonObbj = groupParameterConstructionTemp(gp.getGroupParamList(), jsonObj);
				if (jsonObbj.length() > 0) {
					jsonObj1.put(gp.getEntityName(), jsonObbj);
				}
			}
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0007+" : Exception{}", e);
		}
		return jsonObj1;
	}

	public JSONObject groupParameterConstructionTemp(List<GroupParameter> groupParameterList, JSONObject jsonObj) {
		try {

			for (GroupParameter gp : groupParameterList) {
				List<Parameter> param = gp.getParameterList();
				if (gp.getGroupParamList().size() > 0) {
					JSONObject newObj = new JSONObject();
					if ("array".equalsIgnoreCase(gp.getDescription())) {
						JSONArray arr = new JSONArray();
						newObj = parameterConstruction(gp.getParameterList());
						arr.put(newObj);
						groupParameterConstructionTemp(gp.getGroupParamList(), newObj);
						jsonObj.put(gp.getEntityName(), arr);
					} else if ("object".equalsIgnoreCase(gp.getDescription())) {
						newObj = parameterConstruction(gp.getParameterList());
						groupParameterConstructionTemp(gp.getGroupParamList(), newObj);
						jsonObj.put(gp.getEntityName(), newObj);
					}

				} else {
					if ("array".equalsIgnoreCase(gp.getDescription())) {
						if ("feature".equalsIgnoreCase(gp.getEntityName())) {
							JSONArray featureArr = new JSONArray();
							featureArr.put(parameterConstruction(gp.getParameterList()));
							jsonObj.put(gp.getEntityName(), featureArr);
						}
						if ("deviceId".equalsIgnoreCase(gp.getEntityName())) {
							JSONArray featureArr = new JSONArray();
							featureArr.put(parameterConstruction(gp.getParameterList()));
							jsonObj.put(gp.getEntityName(), featureArr);
						}
						if ("simId".equalsIgnoreCase(gp.getEntityName())) {
							JSONArray featureArr = new JSONArray();
							featureArr.put(parameterConstruction(gp.getParameterList()));
							jsonObj.put(gp.getEntityName(), featureArr);
						}
						/*
						 * JSONArray arr = new JSONArray();
						 * arr.put(parameterConstruction(gp.getParameterList()));
						 * jsonObj.put(gp.getEntityName(), arr);
						 */
					} else if ("object".equalsIgnoreCase(gp.getDescription())) {
						jsonObj.put(gp.getEntityName(), parameterConstruction(param));
					}

				}
			}
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0007+" : Exception{}", e);
		}
		return jsonObj;
	}

	public JSONObject parameterConstruction(List<Parameter> param) {
		JSONObject jsonObj = new JSONObject();
		try {
			for (Parameter c : param) {
				if (c.getValue() != null && !"".equalsIgnoreCase(c.getValue()) && !"0".equalsIgnoreCase(c.getValue())) {
					jsonObj.put(c.getDisplayName(), c.getValue());
				} else {
					List<String> value = c.getValueList();
					System.out.println("Rule::::" + c.getRule());
					if (!"0".equals(value.get(0))) {
						log.debug("inside param3::" + c.getName() + "Rule:: " + c.getRule());
						String val = value.get(0);
						jsonObj.put(c.getDisplayName(), val);
					}
				}
			}
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0007+" : Exception in Parameterconstruction method{}", e);
		}
		return jsonObj;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getRequestInfo(String request, String param) throws JSONException {
		JSONObject reqBodyObj = null;
		if (request.startsWith("[")) {
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
						if (ApolloNEConstants.DATA.equalsIgnoreCase(groupKey)) {
							JSONObject dataObj = reqObj.getJSONObject(groupKey);
							Iterator<String> dataObjItr = dataObj.keys();
							while (dataObjItr.hasNext()) {
								String paramKey = dataObjItr.next();
								if (param.equalsIgnoreCase(paramKey)) {
									reqBodyObj = dataObj.getJSONObject(paramKey);
									return reqBodyObj;
								}
							}
						}
					}
				}
			}
		}
		return reqBodyObj;
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getRequiredInfo(String request, String requiredParamName) throws JSONException {
		Map<String, String> reqParamMap = new HashMap<>();
		log.info("request :: " + request);
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
					if (ApolloNEConstants.DATA.equalsIgnoreCase(groupKey)) {
						JSONObject dataObj = reqObj.getJSONObject(groupKey);
						Iterator<String> dataObjItr = dataObj.keys();
						while (dataObjItr.hasNext()) {
							String paramKey = dataObjItr.next();
							if (requiredParamName.equalsIgnoreCase(paramKey)) {
								JSONObject requiredObj = dataObj.getJSONObject(paramKey);
								Iterator<String> requiredObjItr = requiredObj.keys();
								while (requiredObjItr.hasNext()) {
									String requiredObjKey = requiredObjItr.next();
									reqParamMap.put(requiredObjKey, requiredObj.getString(requiredObjKey));
								}
							}
						}
					}
				}
			}
		}
		return reqParamMap;
	}

	public String trimVal(String val) {
		return val != null ? val.trim() : val;
	}

	public String constructOutboundResponse(HttpStatus httpStatus, String outputString) throws JSONException {

		JSONObject resp = new JSONObject();
		JSONObject data = new JSONObject();
		data.put("code", String.valueOf(httpStatus.value()));
		data.put("reason", httpStatus.getReasonPhrase());

		if (outputString.startsWith("{")) {
			JSONObject exResp = new JSONObject(outputString);
			data.put("serviceNotificationResponse", exResp);
		} else if (outputString.startsWith("[")) {
			JSONArray exResArr = new JSONArray(outputString);
			data.put("serviceNotificationResponse", exResArr);
		} else {
			data.put("serviceNotificationResponse", outputString);
		}

		resp.put("data", data);
		return resp.toString();
	}

	public String jsonFormatter(String inputData, String funcReqIdentifier, String requestParams, String server,
			String operationName) {
		String response = null;
		try {
			Gson gson = new Gson();
			List<ScreenDetails> screenDetailsList = gson.fromJson(inputData, new TypeToken<List<ScreenDetails>>() {
			}.getType());
			List<GroupParameter> groupParameterList = screenDetailsList.get(0).getGroupParamList();
			JsonObject json = groupParameterConstruction(groupParameterList, funcReqIdentifier, requestParams, server,
					operationName);
			response = "[" + json.toString() + "]";
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0013+" : Error in jsonFormatter{}", e);
		}
		return response;
	}

	public JsonObject groupParameterConstruction(List<GroupParameter> groupParameterList, String funcReqIdentifier,
			String requestParams, String server, String operationName) {

		JsonObject jsonObj = new JsonObject();
		JsonObject jsonObj1 = new JsonObject();
		try {
			for (GroupParameter gp : groupParameterList) {
				JsonObject jsonObbj = groupParameterConstructionTemp(gp.getGroupParamList(), jsonObj, funcReqIdentifier,
						requestParams, server, operationName);
				if (jsonObbj.entrySet() != null && !jsonObbj.entrySet().isEmpty()) {
					jsonObj1.add(gp.getEntityName(), jsonObbj);
				}
			}
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0007+" : Error in groupParameterConstruction{}", e);
		}
		return jsonObj1;
	}

	public JsonObject groupParameterConstructionTemp(List<GroupParameter> groupParameterList, JsonObject jsonObj,
			String funcReqIdentifier, String requestParams, String server, String operationName) {

		Iterator localIterator;
		try {
			localIterator = groupParameterList.iterator();

			while (true) {
				JsonArray arr;
				if (!(localIterator.hasNext()))
					break;
				GroupParameter gp = (GroupParameter) localIterator.next();
				List param = gp.getParameterList();
				JsonObject newObj = new JsonObject();

				if (gp.getGroupParamList().size() > 0) {
					if ("array".equalsIgnoreCase(gp.getDescription())) {
						log.debug("gp.getEntityName()::" + gp.getEntityName());
						if ("deviceId".equalsIgnoreCase(gp.getEntityName())) {
							JsonArray featureArr = new JsonArray();
							if (jsonObj.has(gp.getEntityName())) {
								JsonArray Arr = jsonObj.get(gp.getEntityName()).getAsJsonArray();
								log.debug("Arr1::" + Arr);
								Arr.add(parameterConstruction(gp.getParameterList(), funcReqIdentifier, requestParams,
										server, operationName,gp.getEntityName()));
								log.debug("Arr1::" + Arr);
								jsonObj.remove(gp.getEntityName());
								jsonObj.add(gp.getEntityName(), Arr);

							} else {
								featureArr.add(parameterConstruction(gp.getParameterList(), funcReqIdentifier,
										requestParams, server, operationName,gp.getEntityName()));
								jsonObj.add(gp.getEntityName(), featureArr);
							}

						} else if ("simId".equalsIgnoreCase(gp.getEntityName())) {
							JsonArray featureArr = new JsonArray();
							if (jsonObj.has(gp.getEntityName())) {

								JsonArray Arr = jsonObj.get(gp.getEntityName()).getAsJsonArray();
								log.debug("Arr::" + Arr);
								Arr.add(parameterConstruction(gp.getParameterList(), funcReqIdentifier, requestParams,
										server, operationName,gp.getEntityName()));
								log.debug("Arr1::" + Arr);
								jsonObj.remove(gp.getEntityName());
								jsonObj.add(gp.getEntityName(), Arr);

							} else {
								featureArr.add(parameterConstruction(gp.getParameterList(), funcReqIdentifier,
										requestParams, server, operationName,gp.getEntityName()));
								jsonObj.add(gp.getEntityName(), featureArr);
							}

						} else {
							arr = new JsonArray();
							newObj = parameterConstruction(gp.getParameterList(), funcReqIdentifier, requestParams,
									server, operationName,gp.getEntityName());
							log.debug("newObj.size()::" + newObj.size());
							arr.add(newObj);
							groupParameterConstructionTemp(gp.getGroupParamList(), newObj, funcReqIdentifier,
									requestParams, server, operationName);
							jsonObj.add(gp.getEntityName(), arr);
							if (arr.size() > 0) {
								jsonObj.add(gp.getEntityName(), arr);
							}
						}
					} else if ("object".equalsIgnoreCase(gp.getDescription())) {
						newObj = parameterConstruction(gp.getParameterList(), funcReqIdentifier, requestParams, server,
								operationName,gp.getEntityName());
						groupParameterConstructionTemp(gp.getGroupParamList(), newObj, funcReqIdentifier, requestParams,
								server, operationName);
						jsonObj.add(gp.getEntityName(), newObj);
					}

				} else if ("array".equalsIgnoreCase(gp.getDescription())) {
					log.debug("gp.getEntityName() feature::" + gp.getEntityName());
					// JsonArray featureArr = new JsonArray();
					if ("feature".equalsIgnoreCase(gp.getEntityName())) {
						JsonArray featureArr = new JsonArray();
						if (jsonObj.has(gp.getEntityName())) {
							JsonArray Arr = jsonObj.get(gp.getEntityName()).getAsJsonArray();
							log.debug("Arr1::" + Arr);
							Arr.add(parameterConstruction(gp.getParameterList(), funcReqIdentifier, requestParams,
									server, operationName,gp.getEntityName()));
							log.debug("Arr1::" + Arr);
							jsonObj.remove(gp.getEntityName());
							if (Arr.size() > 0) {
								jsonObj.add(gp.getEntityName(), Arr);
							}

						} else {
							featureArr.add(parameterConstruction(gp.getParameterList(), funcReqIdentifier,
									requestParams, server, operationName,gp.getEntityName()));
							log.info("featureArr::" + featureArr);
							if (featureArr.get(0).getAsJsonObject().size() > 0) {
								jsonObj.add(gp.getEntityName(), featureArr);
							}
						}

					} else if ("deviceId".equalsIgnoreCase(gp.getEntityName())) {
						JsonArray featureArr = new JsonArray();
						if (jsonObj.has(gp.getEntityName())) {
							JsonArray Arr = jsonObj.get(gp.getEntityName()).getAsJsonArray();
							log.debug("Arr1::" + Arr);
							Arr.add(parameterConstruction(gp.getParameterList(), funcReqIdentifier, requestParams,
									server, operationName,gp.getEntityName()));
							log.debug("Arr1::" + Arr);
							jsonObj.remove(gp.getEntityName());
							jsonObj.add(gp.getEntityName(), Arr);

						} else {
							featureArr.add(parameterConstruction(gp.getParameterList(), funcReqIdentifier,
									requestParams, server, operationName,gp.getEntityName()));
							jsonObj.add(gp.getEntityName(), featureArr);
						}

					} else if ("simId".equalsIgnoreCase(gp.getEntityName())) {
						JsonArray featureArr = new JsonArray();
						if (jsonObj.has(gp.getEntityName())) {
							JsonArray Arr = jsonObj.get(gp.getEntityName()).getAsJsonArray();
							log.debug("Arr1::" + Arr);
							Arr.add(parameterConstruction(gp.getParameterList(), funcReqIdentifier, requestParams,
									server, operationName,gp.getEntityName()));
							log.debug("Arr1::" + Arr);
							jsonObj.remove(gp.getEntityName());
							jsonObj.add(gp.getEntityName(), Arr);

						} else {
							featureArr.add(parameterConstruction(gp.getParameterList(), funcReqIdentifier,
									requestParams, server, operationName,gp.getEntityName()));

							jsonObj.add(gp.getEntityName(), featureArr);
						}

					}

					/*
					 * else if ("deviceid".equalsIgnoreCase(gp.getEntityName())) { newObj =
					 * constructTypeValueParam(gp.getParameterList()); if (newObj.has("\"IMEI\""))
					 * jsonObj.add("IMEI", newObj.get("\"IMEI\"")); if (newObj.has("\"oldIMEI\""))
					 * jsonObj.add("oldIMEI", newObj.get("\"oldIMEI\"")); if
					 * (newObj.has("\"newIMEI\"")) jsonObj.add("newIMEI",
					 * newObj.get("\"newIMEI\"")); }
					 */else {
						
						arr = new JsonArray();
						arr.add(parameterConstruction(gp.getParameterList(), funcReqIdentifier, requestParams, server,
								operationName,gp.getEntityName()));
						log.debug("arr.get(0).getAsJsonObject().size()::" + arr.get(0).getAsJsonObject().size());
						log.debug("gp.getEntityName()::" + gp.getEntityName());
						
						if (arr.get(0).getAsJsonObject().size() > 0) {
							jsonObj.add(gp.getEntityName(), arr);
						}
					}
				} else if ("object".equalsIgnoreCase(gp.getDescription())) {
					JsonObject paramobj = parameterConstruction(param, funcReqIdentifier, requestParams, server,
							operationName,gp.getEntityName());
					if (!paramobj.toString().equals("{}")) {
						jsonObj.add(gp.getEntityName(), paramobj);
					}
				}

			}

		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0007+" : Error in groupParameterConstructionTemp{}", e);
		}
		return jsonObj;
	}

	public JsonObject parameterConstruction(List<Parameter> param, String funcReqIdentifier, String requestParams,
			String server, String operationName, String grpPrameterName) {
		JsonObject jsonObj = new JsonObject();
		JsonObject namejsonObj = new JsonObject();
		try {
			for (Parameter c : param) {
				if (c.getValue() != null && !"".equalsIgnoreCase(c.getValue()) && !"0".equalsIgnoreCase(c.getValue())
						&& operationName.equalsIgnoreCase("managepromotion")
						&& (c.getName().equalsIgnoreCase("startDate") || c.getName().equalsIgnoreCase("endDate"))) {
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					Date date = formatter.parse(c.getValue());
					String startDate = formatter.format(date);
					jsonObj.addProperty(c.getDisplayName(), startDate);
				} else if (c.getValue() != null && c.getName().equalsIgnoreCase("billCycleResetDay")
						&& !"0".equalsIgnoreCase(c.getValue())) {
					String dpfo = c.getValue();
					if (dpfo.startsWith("0")) {
						dpfo = String.valueOf(dpfo.charAt(1));
					}
					if (!dpfo.isEmpty()) {
						jsonObj.addProperty(c.getDisplayName(), dpfo);
					}
				}
				else if (c.getValue() != null && !"".equalsIgnoreCase(c.getValue())
						&& !"0".equalsIgnoreCase(c.getValue())) {
					if("lnpName".equalsIgnoreCase(grpPrameterName)) {
						if(!"businessName".equalsIgnoreCase(c.getDisplayName())) {
							
							namejsonObj.addProperty(c.getDisplayName(), c.getValue());
						}
						else {
							jsonObj.addProperty(c.getDisplayName(), c.getValue());
						}
						if(namejsonObj.size()>0) {
							jsonObj.add("name",namejsonObj);
						}
					}
					else {
					jsonObj.addProperty(c.getDisplayName(), c.getValue());
					}
				} else {
					List<String> value = c.getValueList();
					if (!(c.getRule().equals("3"))) {
						if ("0".equals(value.get(0))) {
							String val = "";
							if (c.getName().equalsIgnoreCase("functionRequesterIdentifier")) {
								jsonObj.addProperty(c.getDisplayName(), funcReqIdentifier);
							} else if (c.getName().equalsIgnoreCase("subscriberGroupCd")) {
								jsonObj.addProperty(c.getDisplayName(), server);
							} else {
								jsonObj.addProperty(c.getDisplayName(), val);
							}
						}

						else {
							String val = value.get(0);
							jsonObj.addProperty(c.getDisplayName(), val);

						}
					}
				}
			}

		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0007+" : Error in parameterConstruction{}", e);
		}
		return jsonObj;
	}

	@SuppressWarnings("unchecked")
	public String convertJsonForNSLResponseFormat(String response) {
		log.debug("inside convertJsonForInboundResponse");
		JsonObject respJson = null;
		try {

			if (response.startsWith("[")) {
				JsonArray jsonArr = new JsonParser().parse(response).getAsJsonArray();
				for (int i = 0; i < jsonArr.size(); i++) {
					JsonObject obj = (JsonObject) jsonArr.get(i);
					Set<Entry<String, JsonElement>> objItr = obj.entrySet();
					for (Map.Entry<String, JsonElement> entry : objItr) {
						respJson = (JsonObject) entry.getValue();
					}
				}
			}

		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0007+" : Error in convertJsonForInboundResponse Method{}", e);
		}

		if (respJson != null) {
			return respJson.toString();
		}
		return "";
	}

	public String valueEmptyValidation(String req) {
		JsonObject reqJson = new JsonObject();
		JsonObject newreqJson = new JsonObject();
		try {
			log.info("UpdateSubscriberStatus::req:: " + req);
			if (req.startsWith("{")) {
				reqJson = new JsonParser().parse(req).getAsJsonObject();
				for (Map.Entry<String, JsonElement> entry : reqJson.entrySet()) {
					String reqKey = entry.getKey();
					JsonObject modifydata = new JsonObject();
					log.debug("Inside Validation" + reqKey);
					JsonObject dataJson = (JsonObject) reqJson.getAsJsonObject(reqKey);
					for (Map.Entry<String, JsonElement> dataentry : dataJson.entrySet()) {
						String dataKey = dataentry.getKey();
						boolean isflag = false;
						log.debug("Inside Validation::" + dataKey);
						if (dataJson.get(dataKey) instanceof JsonArray) {
							log.debug("Inside if");
							JsonArray newJsonarry = (JsonArray) dataJson.getAsJsonArray(dataKey);
							String hasType = "";
							String hasvalue = "";
							if (newJsonarry.size() > 0) {
								JsonObject newJsonobj = (JsonObject) newJsonarry.get(0).getAsJsonObject();
								log.debug("UpdateSubscriberStatus::newJsonobjLOOP::" + newJsonobj);

								for (Map.Entry<String, JsonElement> jsonInfoentry : newJsonobj.entrySet()) {

									if ("type".equalsIgnoreCase(jsonInfoentry.getKey())) {
										hasType = jsonInfoentry.getValue().getAsString();
									}
									if ("value".equalsIgnoreCase(jsonInfoentry.getKey())) {
										hasvalue = jsonInfoentry.getValue().getAsString();
									}
									if ("name".equalsIgnoreCase(jsonInfoentry.getKey())) {
										hasType = jsonInfoentry.getValue().getAsString();
									}
									log.debug("jsonInfoentry.getKey()::" + jsonInfoentry.getKey());
								}
							} else {
								isflag = true;
							}
							log.debug("hasvalue::" + hasvalue + "hasType::" + hasType);
							if (!hasType.equalsIgnoreCase("")) {
								if (hasvalue.equalsIgnoreCase("")) {
									isflag = true;
								}
							}
							/*
							 * else if(hasType.equalsIgnoreCase("")&&hasvalue.equalsIgnoreCase("")) { isflag
							 * = true; }
							 */
							if (!isflag) {
								modifydata.add(dataKey, dataentry.getValue());
							}

						} else {
							modifydata.add(dataKey, dataentry.getValue());
						}

					}
					newreqJson.add(reqKey, modifydata);
					log.info("After reqJson::" + newreqJson);
				}
			}

		} catch (Exception e) {

			log.error("ErrorCode : "+ErrorCodes.CECC0007+" : Exception in valueEmptyValidation method{}", e);
		}
		return newreqJson.toString();
	}

	public String getTimeStamp() {
		SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.TIME_STAMP);
		Calendar cal = Calendar.getInstance();
		String date = sdf.format(cal.getTime());
		log.debug("Current Start Date: " + date);
		return date;
	}

	public String sendRequestToExternalClient(RcsIntegrationServiceBean rcsServiceBean, Map<String, String> dataMap,
			OutboundRequest outReqBean, String operationName, String responseId, boolean invokeitmbo) {
		String response = null;
		SendClientRequest sendClient = new SendClientRequest();
		sendClient.setRcsServiceBean(rcsServiceBean);
		sendClient.setDataMap(dataMap);
		sendClient.setOperationName(operationName);
		sendClient.setOutReqBean(outReqBean);
		sendClient.setResponseId(responseId);
		sendClient.setInvokeitmbo(invokeitmbo);
		sendClient.setEndUrl(rcsServiceBean.getEndUrl());
		Gson gson = new Gson();
		String sendClientStr = "";

		ObjectMapper mapper = new ObjectMapper();
		try {
		 sendClientStr = gson.toJson(sendClient);		
		} catch (Exception e) {
			log.error("Connection Exception ::", e);
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			try {
				sendClientStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(sendClient);
			} catch (JsonProcessingException e1) {
				log.error("ErrorCode : "+ErrorCodes.CECC0007+" : Connection Exception", e1);
			}
		}
		log.debug("sendClientStr::" + sendClientStr);
		Message message = MessageBuilder.withBody(sendClientStr.getBytes())
				.setContentType(MediaType.APPLICATION_JSON_VALUE).build();
		log.debug("Before queue Value::" + message);
		message = customRabbitTemplate.sendAndReceive(apolloNEQueueProperties.getApolloNeSendClientExchange(),
				apolloNEQueueProperties.getApolloNeSendClientQueue(), message);
		log.debug("After queue Value::" + message);	
		if (message != null && message.getBody() != null) {
			String sendClientresponse = new String(message.getBody());
			if (StringUtils.hasText(sendClientresponse)) {
				sendClient = gson.fromJson(sendClientresponse, SendClientRequest.class);
				if (sendClient != null) {
					response = sendClient.getResponse();
				}
			}
		}	
		if (StringUtils.hasText(response) &&"GSMA Device Check".equalsIgnoreCase(operationName)) {
			//if(outputString.startsWith("{")) {
			log.debug("Inside device::");
				String clientEndUrl="";
				if (response.contains("~")) {
					if (response.split("~").length == 3) {
						clientEndUrl = response.split("~")[2];
						//httpCode = response.split("~")[1];
						response = response.split("~")[0];
					}
					if (response.split("~").length == 2) {
						//httpCode = response.split("~")[1];
						response = response.split("~")[0];
					}
				}
				log.debug("Inside device1::"+response);
				JSONObject obj=new JSONObject(response);
				String responseCode="";
				String description="";
				if(obj.has("errorcode")) {
					responseCode=obj.getString("errorcode");
				}
				if(obj.has("errordesc")) {
					description=obj.getString("errordesc");
				}
				String code="";
				code=responseCode;
				if(!StringUtils.hasText(responseCode)) {
					code="200";
					responseCode="SUCC01";
					description="Request Successfully Processed";
				}else {
					responseCode="ERR19";
				}
				com.excelacom.century.apolloneoutbound.bean.Message msg=com.excelacom.century.apolloneoutbound.bean.Message.builder().responseCode(responseCode).description(description).build();
				ArrayList msgList=new ArrayList<com.excelacom.century.apolloneoutbound.bean.Message>();
				msgList.add(msg);
				String reason="";
				try {
					reason=org.springframework.http.HttpStatus.valueOf(Integer.valueOf(code)).getReasonPhrase();
				}
				catch(Exception e) {
					
				}
				reason="OK";
				code="200";
				SubOrder sub=new Gson().fromJson(response, SubOrder.class);		
				ArrayList subOrderlst=new ArrayList<SubOrder>();
				subOrderlst.add(sub);
			    Data data=Data.builder().transactionId(responseId).code(code).reason(reason).message(msgList).subOrder(subOrderlst).build();
				Root root=Root.builder().data(data).build();
				response=new Gson().toJson(root);
			//}
				log.debug("OutputString::3"+response);
		}
		log.debug("EndpointURL::" + sendClient.getDataMap().get("EndpointURL") + "operationName::" + operationName);
		if (sendClient.getDataMap().get("EndpointURL").equalsIgnoreCase("ManageAccountAPNE_URL")
				&& operationName.equalsIgnoreCase("UpdateSubscriber Group") && response != null) {
			String value = "success";
			String manageAccountReq = "";
			String accountNumber = "";
			String billCycleResetDay = "";
			String action = "";
			String lineId = "";
			String referenceNumber = "";
			String transactionId = "";
			String mdnValue = "";
			String refNumber = "";
			Line line = null;
			JSONObject newObj = new JSONObject();
			JSONObject mdnObj = new JSONObject();
			JSONObject data = new JSONObject();
			JSONObject newAccount = new JSONObject();
			JSONArray suborderArray = new JSONArray();
			JSONArray mdnsArr = new JSONArray();
			JSONArray mdnArr = new JSONArray();
			try {
				if (response.startsWith("{")) {
					JSONObject responseObj = new JSONObject(response);
					if (responseObj.toString().contains("success") || responseObj.toString().contains("Success")
							|| responseObj.toString().contains("SUCCESS")) {
						if (responseObj.has("messageHeader")) {
							newObj = responseObj.getJSONObject("messageHeader");
							log.debug("messageHeader::" + newObj);
							if (newObj.has("referenceNumber")) {
								referenceNumber = newObj.getString("referenceNumber");
							}
						}
						refNumber = rcsDao.getReferenceNumber(referenceNumber);
						log.debug("refNumber in UpdateSubscriber Group::" + refNumber);
						transactionId = rcsDao.gettransactionId(refNumber);
						log.debug("inside responseObj::" + responseObj);
						manageAccountReq = rcsDao.getRequestDetails(transactionId);
						log.debug("inside manageAccountReq::" + manageAccountReq);
						JSONObject manageAccObj = new JSONObject(manageAccountReq);
						if (manageAccObj.has("data")) {
							data = manageAccObj.getJSONObject("data");
							log.debug("inside data::" + data);
							if (data.has("newAccount")) {
								newAccount = data.getJSONObject("newAccount");
								accountNumber = newAccount.getString("accountNumber");
								if (!(newAccount.has("billCycleResetDay"))) {
									line = apolloNEOutboundClientService.getMALineDetails(accountNumber);
									if (line != null) {
										billCycleResetDay = line.getBcd();
									}
									log.debug("bcdNewValue billCycleResetDay:: " + billCycleResetDay);
								} else {
									billCycleResetDay = newAccount.getString("billCycleResetDay");
								}
							}
							if (data.has("subOrder")) {
								suborderArray = data.getJSONArray("subOrder");
								log.debug("inside suborderArray::" + suborderArray);
								newObj = suborderArray.getJSONObject(0);
								if (newObj.has("mdns")) {
									mdnsArr = newObj.getJSONArray("mdns");
									log.debug("inside mdnsArr::" + mdnsArr);
									newObj = mdnsArr.getJSONObject(0);
									log.debug("inside newObj::" + newObj);
									mdnArr = newObj.getJSONArray("mdn");
									mdnObj = mdnArr.getJSONObject(0);
									if (mdnObj.has("value")) {
										mdnValue = mdnObj.get("value").toString();
									}
									action = newObj.get("action").toString();
									lineId = newObj.get("lineId").toString();
									log.debug("inside action::" + action);
								}
							}
						}
						RequestBean reqBean = null;
						Gson gson1 = new Gson();
						reqBean = gson1.fromJson(manageAccObj.toString(), RequestBean.class);
						reqBean.setErrorDescription(value);
						reqBean.setMdn(mdnValue);
						reqBean.setLineId(lineId);
						reqBean.setTransId(transactionId);
						log.debug("reqBean::" + reqBean.toString());
						if (reqBean != null && reqBean.getErrorDescription() != null) {
							String Response = reqBean.getErrorDescription();
							if (Response.equalsIgnoreCase("success") && action.equals("D")) {
								reqBean.setAccountNumber(null);
								reqBean.setBillCycleResetDay(null);
								apolloNEOutboundClientService.updateManageAccountDetails(reqBean);
							} else if (Response.equalsIgnoreCase("success") && action.equals("A")) {
								reqBean.setAccountNumber(accountNumber);
								reqBean.setBillCycleResetDay(billCycleResetDay);
								apolloNEOutboundClientService.updateManageAccountDetails(reqBean);
							}
						}
					}
				}
			} catch (Exception e) {
				log.info("ErrorCode : "+ErrorCodes.CECC0004+" : Exception in manage account update{}" + e);
			}

		}
		// SearchEnvironment
		if (operationName.equalsIgnoreCase("Subscribergroup Inquiry")) {
			String seServiceName = rcsDao.getServiceName(responseId);
			log.debug("Search Environment Subscribergroup Inquiry outputString::::" + response);
			if (StringUtils.hasText(response) && StringUtils.hasText(seServiceName)
					&& seServiceName.equalsIgnoreCase("Search-Environment")) {
				String serviceId = "";
				String requestType = "MNO";
				String referenceNumber = "";
				String seSubscriberGroupCd = "";
				String contextId = "";
				String addName = "";
				String addValue = "";
				List<Map<String, Object>> result = null;
				String serResponse = "";
				JSONArray subOrderArr = new JSONArray();
				JSONObject subOrderObj = new JSONObject();
				try {
					if (response.contains("messageHeader")) {
						JSONObject responseOutputObject = new JSONObject(response);
						JSONObject requestJsonObjectData = responseOutputObject.getJSONObject("messageHeader");
						serviceId = requestJsonObjectData.has("serviceId")
								&& requestJsonObjectData.getString("serviceId").length() > 0
										? requestJsonObjectData.getString("serviceId")
										: "";
						referenceNumber = rcsDao.getRefNumber(Double.parseDouble(responseId));
						if (responseOutputObject.has("data")) {
							JSONObject responseOutputStringData = responseOutputObject.getJSONObject("data");
							if (responseOutputStringData.has("subOrder")) {
								subOrderArr = responseOutputStringData.getJSONArray("subOrder");
								subOrderObj = subOrderArr.getJSONObject(0);
								log.debug("Search Environment subOrderObj::" + subOrderObj);
								if (subOrderObj.has("subscriberGroup")) {
									JSONObject resOutStrSubscriberGroup = subOrderObj.getJSONObject("subscriberGroup");
									seSubscriberGroupCd = resOutStrSubscriberGroup.has("subscriberGroupCd")
											&& resOutStrSubscriberGroup.getString("subscriberGroupCd").length() > 0
													? resOutStrSubscriberGroup.getString("subscriberGroupCd")
													: "";
									contextId = getContextIdFromSearchEnvCode(seSubscriberGroupCd);
									log.debug("Search Environment contextId after call outputString::::" + contextId);
								}
							}
						}
						if (StringUtils.hasText(responseId)) {
							result = rcsDao.getAdditionalData(responseId);
							if (result != null && result.size() > 0) {
								for (int j = 0; j < result.size(); j++) {
									addName = (String) result.get(j).get("REFNAME");
									addValue = (String) result.get(j).get("REFVALUE");
								}
							}
						}
					}
					StringBuilder parameterJson = new StringBuilder();
					parameterJson.append("{\r\n");
					parameterJson.append("\"messageHeader\": {\r\n");
					parameterJson.append("\"serviceId\": \"" + serviceId + "\",\r\n");
					parameterJson.append("\"requestType\": \"" + requestType + "\",\r\n");
					parameterJson.append("\"referenceNumber\": \"" + referenceNumber + "\",\r\n");
					parameterJson.append("\"transactionId\": \"" + responseId + "\"\r\n");
					parameterJson.append("},\r\n");
					parameterJson.append("\"data\": {\r\n");
					parameterJson.append("\"contextId\": \"" + contextId + "\"\r\n");
					parameterJson.append("}\r\n");
					if (StringUtils.hasText(addName) && StringUtils.hasText(addValue)) {
						parameterJson.append(",\r\n");
						parameterJson.append("\"additionalData\": [\r\n");
						parameterJson.append("{\r\n");
						parameterJson.append("\"name\": \"" + addName + "\",\r\n");
						parameterJson.append("\"value\": \"" + addValue + "\"\r\n");
						parameterJson.append("}\r\n");
						parameterJson.append("]\r\n");
					}
					parameterJson.append("}");
					serResponse = parameterJson.toString();
					log.debug("SEARCH ENVIRONMENT RESPONSE" + serResponse);
				} catch (Exception e) {
					log.error("ErrorCode : "+ErrorCodes.CECC0004+" : Exception in searchEnvironment before queue{}", e);
					serResponse = ApolloNEConstants.SER_ENV_INTERNAL_ERROR;
				}
				try {
					// Message message = null;
					String routingKey = "SEARCH_ENV_WAIT_TRANSACTIONID_" + responseId;
					if (StringUtils.hasText(serResponse)) {
						message = MessageBuilder.withBody(serResponse.getBytes())
								.setContentType(MediaType.APPLICATION_JSON_VALUE).build();
						customRabbitTemplate.send(apolloNEQueueProperties.getSearchEnvWaitTopicExchange(), routingKey,
								message);
					}
				} catch (Exception e) {
					log.error("ErrorCode : "+ErrorCodes.CECC0005+" : Exception in queue sender{}", e);
				}
			}
		}
		// SearchEnvironment
		return response;
	}

	public String getContextIdFromSearchEnvCode(String subGroupCd) {
		String contextID = "";
		try {
			if (!subGroupCd.isEmpty() && subGroupCd != null) {
				if (subGroupCd.contains("-")) {
					String subGroupCdFields[] = subGroupCd.split("-");
					if (subGroupCdFields != null && !"".equals(subGroupCdFields)) {
						contextID = subGroupCdFields[1];
					}
				}
			}
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0004+" : Error in getContextIdFromSearchEnvCode" + e);
		}
		log.info("Search Environment Response CONTEXTID:::" + contextID);
		return contextID;
	}

	public void updateMboTransactionDetails(OutboundRequest outReqBeanMBO, String responses,
			String queueStatus, SendClientRequest sendClientRequest) {
		String transIdMBO = outReqBeanMBO.getTransUid();
		String responseId = sendClientRequest.getResponseId();
		if (responses == null) {
			rcsDao.updateSouthBoundTransactionFailure(transIdMBO, outReqBeanMBO.getEntityId(), responses,
					outReqBeanMBO.getGroupId(), responseId, "MBO", queueStatus);
		} else if (responses != null) {
			if (!responses.startsWith("[") && !responses.startsWith("{") && !responses.startsWith("<")) {
				rcsDao.updateSouthBoundTransactionFailure(transIdMBO, outReqBeanMBO.getEntityId(), responses,
						outReqBeanMBO.getGroupId(), responseId, "MBO", queueStatus);
			} else {
				rcsDao.updateSouthBoundTransaction(transIdMBO, outReqBeanMBO.getEntityId(), outReqBeanMBO.getGroupId(),
						responses, outReqBeanMBO.getErrorCode(), "MBO", responseId, queueStatus,
						outReqBeanMBO.getOperationName(), null, sendClientRequest);
			}
		}
	}

	public void updateTransactionDetails(RcsIntegrationServiceBean rcsServiceBean, OutboundRequest outReqBean,
			String operationName, String responseId, String target, String outputString, String statusCode,
			String queueStatus, SendClientRequest sendClientRequest) {
		try {
			log.info("inside updateTransactionDetails ::");
			log.info("operationName:::" + operationName);
			//log.debug(requestBean.toString());
			String response = sendClientRequest.getResponse();
			ResponseBean bean = new ResponseBean();
			log.debug("operationName:::" + operationName);
			searchEnvTargetSystem = false;
			try {
			if (operationName.equalsIgnoreCase("Activate Subscriber")
					|| operationName.equalsIgnoreCase("Activate Subscriber Port-in")
					|| operationName.equalsIgnoreCase("Change Rate Plan")
					|| operationName.equalsIgnoreCase("Device Detection Change Rate Plan")
					|| operationName.equalsIgnoreCase(CommonConstants.ADDWEARABLE)
					|| operationName.equalsIgnoreCase(CommonConstants.RECONNETWEREABLEMDN)
					|| operationName.equalsIgnoreCase(CommonConstants.RESTORE_SERVICE)
					|| operationName.equalsIgnoreCase(CommonConstants.REMOVE_HOTLINE)
					|| operationName.equalsIgnoreCase(CommonConstants.CHANGE_SIM)
					|| operationName.equalsIgnoreCase(CommonConstants.HOTLINE_SUBSCRIBER)
					|| operationName.equalsIgnoreCase(CommonConstants.SUSPEND_SUBSCRIBER)
					|| operationName.equalsIgnoreCase(CommonConstants.RESET_FEATURE)
					|| operationName.equalsIgnoreCase(CommonConstants.UPDATE_DUE_DATE)
					|| operationName.equalsIgnoreCase(CommonConstants.UPDATE_CUSTOMER_INFORMATION)
					|| operationName.equalsIgnoreCase(CommonConstants.VALIDATE_MDN)
					|| operationName.equalsIgnoreCase(CommonConstants.CANCEL_PORTIIN)
					|| operationName.equalsIgnoreCase(CommonConstants.CHANGE_FEATURE)
					|| operationName.equalsIgnoreCase(CommonConstants.CHANGE_WHOLESALE_RATE_PLAN)
					|| operationName.equalsIgnoreCase(CommonConstants.MANAGEACCOUNT)
					|| operationName.equalsIgnoreCase(CommonConstants.RECONNECT_SERVICE)
					|| operationName.equalsIgnoreCase(CommonConstants.DEACTIVE_SUBSCRIBER)
					|| operationName.equalsIgnoreCase(CommonConstants.CHANGE_DEVICE)
					|| operationName.equalsIgnoreCase(CommonConstants.CHANGE_SIM_DEVICE)
					|| operationName.equalsIgnoreCase(CommonConstants.CHANGE_RATE_PLAN)
					|| operationName.equalsIgnoreCase(CommonConstants.DD_CHANGE_RATE_PLAN)
					|| operationName.equalsIgnoreCase(CommonConstants.CHANGE_BCD)
					|| operationName.equalsIgnoreCase(CommonConstants.RETRIEVE_DEVICE)
					|| operationName.equalsIgnoreCase("managepromotion")
					|| operationName.equalsIgnoreCase(CommonConstants.CHANGE_SIM)
					|| operationName.equalsIgnoreCase(CommonConstants.CHANGE_MDN_CP_RECONNECT_SERVICE)
					|| operationName.equalsIgnoreCase(CommonConstants.CHANGE_MDN_CM)
					|| operationName.equalsIgnoreCase("ESIM ActivateSubscriber")
					|| operationName.equalsIgnoreCase("ESIM Activate Subscriber Port-in")
					|| operationName.equalsIgnoreCase(CommonConstants.ESim_CHANGESIM)
					|| operationName.equalsIgnoreCase(CommonConstants.DEVICE_CHECK_GSMA)
					|| operationName.equalsIgnoreCase("GSMA Device Check")
					|| operationName.equalsIgnoreCase(CommonConstants.VALIDATE_BYOD)) {
				if (response != null && response.startsWith("{")) {
					String requestFormatted = outReqBean.getRequestJson();
					log.debug("request in json::" + requestFormatted);
					requestFormatted = apolloNEOutboundClientService.jsonFormatter(requestFormatted);
					JSONArray jsonArrayMdn = new JSONArray(requestFormatted);
					JSONObject obj = jsonArrayMdn.getJSONObject(0);
					String beanStr = obj.toString();
					Gson respGson = new Gson();
					RequestBean requestBean = respGson.fromJson(beanStr, RequestBean.class);
					
					
					JSONObject responseObj = new JSONObject(response);
					JSONObject data = new JSONObject();
					JSONObject messageHeaderObj = new JSONObject();
					JSONObject accountObj = new JSONObject();
					String transactionStatus = CommonConstants.FAILED;
					String notificationStatus = CommonConstants.FAILED;
					String returnCode = "";
					String swReferenceNumber = "";
					String accNo = "";
					Line line = new Line();
					String lineId = "";
					Line lineBean =new Line();
					String transactionName = "";
					String transactionSuccessStatus = CommonConstants.TRANSACTION_SUCESS;
					String notificationSuccessStatus = CommonConstants.NOTIFICATION_SUCCESS;
					String transactionSuccessSE = CommonConstants.TRANSACTION_SUCESS;
					if (!(responseObj.toString().contains("success") || responseObj.toString().contains("Success")
							|| responseObj.toString().contains("SUCCESS")
							|| responseObj.toString().contains("\"code\":\"200\""))) {
						try {

							String requestMsg = sendClientRequest.getDataMap().get("initRequest");
							log.debug("requestMsg" + requestMsg);
							if (requestMsg.startsWith("[")) {
								JSONArray requestArrObj = new JSONArray(requestMsg);

								if (requestArrObj != null && requestArrObj.length() > 0) {
									JSONObject requestObj = requestArrObj.getJSONObject(0);
									JSONArray suborderArray = new JSONArray();
									if (requestObj.has("data")) {
										data = requestObj.getJSONObject("data");
										if (data.has("account")) {
											accountObj = data.getJSONObject("account");
											if (accountObj.has("accountNumber")) {
												accNo = accountObj.getString("accountNumber");
												log.debug("accountObj accNo " + accNo);
												line.setAccountNumber(accNo);
											}
										}
										if (data.has("subOrder")) {
											suborderArray = data.getJSONArray("subOrder");
											log.debug("inside suborderArray::" + suborderArray);
											data = suborderArray.getJSONObject(0);
											if (data.has("lineId")) {
												lineId = data.getString("lineId");
												log.debug("lineId ::" + lineId);
												line.seteLineId(lineId);
												line.setInflightTransStatus("COMPLETED");
												log.info("before updateInflightTransStatus " + line);
												apolloNEOutboundClientService.updateInflightTransStatus(line);
											}
										}
									}
								}
							}

							log.debug("responseObj ::" + responseObj);
							try {
								if (responseObj.has("data")) {
									data = responseObj.getJSONObject("data");
									if (data.has("returnCode")) {
										returnCode = data.getString("returnCode");
									} else if (data.has("message")) {
										if (data.get("message") instanceof JSONArray) {
											returnCode = data.getJSONArray("message").getJSONObject(0)
													.getString("responseCode");
										}
									}
								} else if (responseObj.has("responsestatus")) {
									returnCode = responseObj.getString("responsestatus");
								}
							} catch (Exception e) {
								log.error("ErrorCode : "+ErrorCodes.CECC0007+" : Exception--{}", e);
							}
							log.debug("returnCode::" + returnCode);
							if (responseObj.has("messageHeader")) {
								messageHeaderObj = responseObj.getJSONObject("messageHeader");
								if (messageHeaderObj.has("referenceNumber")) {
									swReferenceNumber = messageHeaderObj.getString("referenceNumber");
									log.debug("swReferenceNumber...." + swReferenceNumber);
									requestBean.setReferenceNumber(swReferenceNumber);
								}
							}
							log.info(" search Environment before serviceName::" + sendClientRequest.getServiceName()
									+ " returnCode::" + returnCode + " operationName::" + operationName
									+ " responseId::" + responseId);
							if(StringUtils.hasText(responseId)) {
								transactionName = rcsDao.getRootTransName(responseId);
							}
							log.info("transactionName in updateTransactionDetails :: "+transactionName);
							if(!lineId.isEmpty()) {
							 lineBean = apolloNEOutboundClientService.getLineDetailsWithELineId(lineId);
							}
								//log.debug("lineBean.getLineType()::" +lineBean.getLineType());
							if (returnCode != null
									&& (returnCode.equals("E2601") || returnCode.equals("E2302")
											|| returnCode.equals("E2301") || returnCode.equals("E1034"))
									&& lineBean !=null && lineBean.getLineType().equalsIgnoreCase("SMARTWATCH")) {
								TransactionHistory transactionHistory = new TransactionHistory();
								transactionHistory.setTransactionId(responseId);
								transactionHistory.setTransactionStatus(transactionSuccessStatus);
								transactionHistory.setNotificationStatus(notificationSuccessStatus);
								log.debug("requestBean before insertMNODomainDetails in mnoservice::" + requestBean);
								apolloNEOutboundClientService.insertMNODomainDetails(requestBean);
								log.debug("transactionHistory in Outbound::" + transactionHistory);
								apolloNEOutboundClientService.updateTransactionHistoryForNSUrl(transactionHistory);
							} else if (operationName.equalsIgnoreCase(CommonConstants.RETRIEVE_DEVICE)) {
								if (responseObj != null && responseObj.toString().contains("\"contextId\":")) {
									TransactionHistory transactionHistory = new TransactionHistory();
									transactionHistory.setTransactionId(responseId);
									transactionHistory.setTransactionStatus(transactionSuccessSE);
									transactionHistory.setNotificationStatus(notificationStatus);
									log.debug(
											"transactionHistory in Outbound for retrieve device of search Environment::"
													+ transactionHistory);
									apolloNEOutboundClientService.updateTransactionHistoryForNSUrl(transactionHistory);
								}
							} else if (operationName.equalsIgnoreCase(CommonConstants.DEVICE_CHECK_GSMA)) {
								log.debug("responseId-Manage-Blacklist::" + responseId + "returnCode::" + returnCode);
								if (returnCode != null
										&& returnCode.equalsIgnoreCase("failure")){
									TransactionHistory transactionHistory = new TransactionHistory();
									transactionHistory.setTransactionId(responseId);
									transactionHistory.setTransactionStatus(transactionStatus);
									transactionHistory.setNotificationStatus(notificationStatus);
									log.debug("transactionHistory in Outbound::" + transactionHistory);
									apolloNEOutboundClientService.updateTransactionHistoryForNSUrl(transactionHistory);
								
								}
							} else if (StringUtils.hasText(transactionName)
									&& transactionName.equalsIgnoreCase("Transfer Device")) {
								if (operationName.equalsIgnoreCase(CommonConstants.CHANGE_SIM)
										|| operationName.equalsIgnoreCase(CommonConstants.CHANGE_RATE_PLAN)
										|| operationName.equalsIgnoreCase(CommonConstants.DEACTIVE_SUBSCRIBER)) {
									TransactionHistory transactionHistory = new TransactionHistory();
									transactionHistory.setTransactionId(responseId);
									transactionHistory.setTransactionStatus(transactionStatus);
									transactionHistory.setNotificationStatus(notificationStatus);
									log.debug("transactionHistory in Outbound for Transfer Device::"
											+ transactionHistory);
									apolloNEOutboundClientService.updateTransactionHistoryForNSUrl(transactionHistory);
								}
							}else {
								TransactionHistory transactionHistory = new TransactionHistory();
								transactionHistory.setTransactionId(responseId);
								transactionHistory.setTransactionStatus(transactionStatus);
								transactionHistory.setNotificationStatus(notificationStatus);
								log.debug("transactionHistory in Outbound::" + transactionHistory);
								apolloNEOutboundClientService.updateTransactionHistoryForNSUrl(transactionHistory);
							}

						} catch (Exception e) {
							log.error("ErrorCode : "+ErrorCodes.CECC0007+" : Exception {}", e);
						}
					} else {
						if(operationName.equalsIgnoreCase(CommonConstants.DEVICE_CHECK_GSMA)) {
							TransactionHistory transactionHistory = new TransactionHistory();
							transactionHistory.setTransactionId(responseId);
							transactionHistory.setTransactionStatus(transactionSuccessStatus);
							transactionHistory.setNotificationStatus(notificationSuccessStatus);
							log.debug(
									"transactionHistory in Outbound for retrieve device of manage blacklist::"
											+ transactionHistory);
							apolloNEOutboundClientService.updateTransactionHistoryForNSUrl(transactionHistory);
							
						}
					}
				}
			} 
			}catch (Exception e) {
				log.error("ErrorCode : "+ErrorCodes.CECC0007+" : Exception {}", e);
			}
			log.debug("Add wearable in operationName::" + operationName);
			if (operationName.equalsIgnoreCase("Add Wearable")){
				if (response != null && !response.isEmpty()) {
					log.debug("Add wearable in response::" + response);
					JSONObject responseObj = new JSONObject(response);
					JSONObject data = new JSONObject();
					String returnCode = "";
					if (!(responseObj.toString().contains("success") || responseObj.toString().contains("Success")
							|| responseObj.toString().contains("SUCCESS")
							|| responseObj.toString().contains("\"code\":\"200\""))) {
						String transactionStatusFailed = CommonConstants.FAILED;
						String notificationStatusFailed = CommonConstants.FAILED;
						TransactionHistory transactionHistory = new TransactionHistory();
						transactionHistory.setTransactionId(responseId);
						transactionHistory.setTransactionStatus(transactionStatusFailed);
						transactionHistory.setNotificationStatus(notificationStatusFailed);
						log.debug("transactionHistory in Outbound::" + transactionHistory);
						apolloNEOutboundClientService.updateTransactionHistoryForNSUrl(transactionHistory);
					}
				}
			}

			if (operationName.equalsIgnoreCase("Activate Subscriber PSIM")
					|| operationName.equalsIgnoreCase("Activate Subscriber ESIM")
					|| operationName.equalsIgnoreCase("Add Wearable") || operationName.equalsIgnoreCase("ChangeESIM")
					|| operationName.equalsIgnoreCase("Update Port-Out")
					|| operationName.equalsIgnoreCase("Change Feature") || operationName.equalsIgnoreCase("Change SIM")
					|| operationName.equalsIgnoreCase("Validate Device")
					|| operationName.equalsIgnoreCase("UpdateSubscriber Group")
					|| operationName.equalsIgnoreCase(CommonConstants.RETRIEVE_DEVICE)
					|| operationName.equalsIgnoreCase(CommonConstants.IMSI_INQUIRY)
					|| operationName.equalsIgnoreCase(CommonConstants.VALIDATE_MDN_PORTABILITY)
					|| operationName.equalsIgnoreCase(CommonConstants.CHANGE_MDN)
					|| operationName.equalsIgnoreCase(CommonConstants.ACTIVATESUBSCRIBER)
					|| operationName.equalsIgnoreCase(CommonConstants.ACTIVATESUBSCRIBER_PORTIN)
					|| operationName.equalsIgnoreCase(CommonConstants.PROMOTION_INQUIRY)
					|| operationName.equalsIgnoreCase(CommonConstants.RECONNECT_SERVICE)
					|| operationName.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER)
					|| operationName.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER_PORTIN)
					|| operationName.equalsIgnoreCase("Validate BYOD")
					|| operationName.equalsIgnoreCase(CommonConstants.CHANGE_BCD)
					|| operationName.equalsIgnoreCase("Change Wholesale Rate Plan")
					|| operationName.equalsIgnoreCase("Cancel Port-In")
					|| operationName.equalsIgnoreCase("Update Due-Date")
					|| operationName.equalsIgnoreCase("Update Customer Information")
					|| operationName.equalsIgnoreCase(CommonConstants.RESET_FEATURE)
					|| operationName.equalsIgnoreCase(CommonConstants.MANAGE_PROMOTION)
					|| operationName.equalsIgnoreCase("Device Detection Change Rate Plan")
					|| operationName.equalsIgnoreCase("Change Rate Plan")
					|| operationName.equalsIgnoreCase(CommonConstants.SUBSCRIBERGROUP_INQUIRY)
					|| operationName.equalsIgnoreCase(CommonConstants.UPDATE_WIFI_ADDRESS)
					|| operationName.equalsIgnoreCase(CommonConstants.VALIDATE_WIFI_ADDRESS)
					|| operationName.equalsIgnoreCase(CommonConstants.GET_WIFI_ADDRESS)
					|| operationName.equalsIgnoreCase(CommonConstants.PORTIN_INQUIRY)
					|| operationName.equalsIgnoreCase(CommonConstants.VALIDATE_SIM)
					|| operationName.equalsIgnoreCase(CommonConstants.LINE_INQUIRY)
					|| (operationName.equalsIgnoreCase(CommonConstants.LINE_INQ))
					|| (operationName.equalsIgnoreCase("Restore Service"))
					|| (operationName.equalsIgnoreCase("Remove Hotline"))
					|| (operationName.equalsIgnoreCase("Reconnect Service"))
					|| (operationName.equalsIgnoreCase("CP-Reconnect Mdn"))
					|| (operationName.equalsIgnoreCase(CommonConstants.SUSPEND_SUBSCRIBER))
					|| (operationName.equalsIgnoreCase(CommonConstants.HOTLINE_SUBSCRIBER))
					|| (operationName.equalsIgnoreCase(CommonConstants.DELETE_SUBSCRIBER))
					|| (operationName.equalsIgnoreCase(CommonConstants.TW_DEACTIVATE_SUBSCRIBER))
					|| (operationName.equalsIgnoreCase(CommonConstants.UPDATE_SHARED_NAME))
					|| operationName.equalsIgnoreCase(CommonConstants.QUERY_SHARED_NAME)
					|| operationName.equalsIgnoreCase(CommonConstants.DEVICE_CHECK_GSMA)
					|| operationName.equalsIgnoreCase("GSMA Device Check")) {
				target = CommonConstants.APOLLOTARGET_SYSTEM;
				log.info("setting target system Apollo NE of operation:::" + operationName);
			} else {
				target = CommonConstants.TARGET_SYSTEM;
				log.info("setting target system Apollo NE of operation:::" + operationName);
			}
			if (searchEnvTargetSystem != null && searchEnvTargetSystem) {
				target = CommonConstants.TARGET_SEARCH_SYSTEM;
				log.info("setting target TARGET_SEARCH_SYSTEM:::" + target + "::operation::" + operationName);
			}
			log.info("update Target system ::" + target);
			log.info("before updateSouthBoundTransaction outputString::" + outputString);
			if (outputString != null) {
				if (!outputString.startsWith("[") && !outputString.startsWith("{")) {
					rcsDao.updateSouthBoundTransaction(rcsServiceBean.getTranscationId(), outReqBean.getEntityId(),
							outReqBean.getGroupId(), outputString, statusCode, target, responseId, queueStatus,operationName, sendClientRequest.getEndpointURL(),sendClientRequest);
				} else if (operationName.equals("Syniverse Register Subscriber")
						|| operationName.equals("Syniverse De-Register Subscriber")) {
					rcsDao.updateSouthBoundTransaction(rcsServiceBean.getTranscationId(), outReqBean.getEntityId(),
							outReqBean.getGroupId(), outputString, statusCode, ApolloNEConstants.SYNIVERSE, responseId,
							queueStatus,operationName, sendClientRequest.getEndpointURL(),sendClientRequest);
				}else {
					rcsDao.updateSouthBoundTransaction(rcsServiceBean.getTranscationId(), outReqBean.getEntityId(),
							outReqBean.getGroupId(), outputString, statusCode, target, responseId, queueStatus,operationName, sendClientRequest.getEndpointURL(),sendClientRequest);
				}
			} else {
				rcsDao.updateSouthBoundTransaction(rcsServiceBean.getTranscationId(), outReqBean.getEntityId(),
						outReqBean.getGroupId(), outputString, statusCode, target, responseId, queueStatus,operationName, sendClientRequest.getEndpointURL(),sendClientRequest);
			}
			log.debug("Exit getResponseFromClient::StatusCode"+statusCode);
			
			// Manage-Blacklist
			log.debug("Manage-Blacklist-outputString::" + outputString + "operationName::" +  operationName);
			if (StringUtils.hasText(outputString) &&"Device Check GSMA".equalsIgnoreCase(operationName)) {
				String requestMsg = sendClientRequest.getDataMap().get("initRequest");
				log.debug("requestMsg" + requestMsg);
				log.debug("response::" + response);
				String blackliststatusFromGSMA = "";
				String blacklistAction = "";
				String gsmaResponseStatus = "";
				String gsmaErrorcode = "";
				String gsmaErrorDesc = "";
				String imeiValue = "";
				if (requestMsg.startsWith("{")) {
					JSONObject requestObj = new JSONObject(requestMsg);
					JSONObject data = new JSONObject();
					JSONArray suborderArray = new JSONArray();
					JSONObject suborderObj = new JSONObject();
					JSONObject blacklistObj = new JSONObject();
					
					JSONArray deviceIdArray = new JSONArray();
					JSONObject deviceIdObj = new JSONObject();
					
					if (requestObj.has("data")) {
							data = requestObj.getJSONObject("data");
							
							if (data.has("subOrder")) {
								suborderArray = data.getJSONArray("subOrder");
								log.debug("inside suborderArray::" + suborderArray);
								suborderObj = suborderArray.getJSONObject(0);
								blacklistObj = suborderObj.getJSONObject("blacklist");
								if(blacklistObj.has("action")){
									blacklistAction = blacklistObj.getString("action");
									//blacklistAction = blacklistAction.equalsIgnoreCase("A")?"YES":blacklistAction.equalsIgnoreCase("D")?"NO":"";
									log.debug("blacklistAction::" + blacklistAction);
								}
								if(blacklistObj.has("deviceId")){
									deviceIdArray = blacklistObj.getJSONArray("deviceId");
									log.debug("inside deviceIdArray::" + deviceIdArray);
									deviceIdObj = deviceIdArray.getJSONObject(0);
									if (deviceIdObj.has("value")) {
										imeiValue = deviceIdObj.getString("value");
										log.debug("imeiValue::" + imeiValue);
									}
									
								}
							}
						}
					
				}
				
				if (response != null && response.startsWith("{")) {

					JSONObject responseObj = new JSONObject(response);
					 if (responseObj.has("blackliststatus")) {
						 blackliststatusFromGSMA = responseObj.getString("blackliststatus");
						 log.debug("blackliststatusFromGSMA::" + blackliststatusFromGSMA );
					 }
					 
					 if (responseObj.has("responsestatus")) {
						 gsmaResponseStatus = responseObj.getString("responsestatus");
						 log.debug("gsmaResponseStatus::" + gsmaResponseStatus );
					 }
					 
					 if(responseObj.has("errorcode")) {
						 gsmaErrorcode = responseObj.getString("errorcode");
						 log.debug("gsmaErrorcode::" + gsmaErrorcode );
					 }
					 
					 if(responseObj.has("errordesc")) {
						 gsmaErrorDesc = responseObj.getString("errordesc");
						 log.debug("gsmaErrorDesc::" + gsmaErrorDesc );
					 }

				}
				ResourceInfo  resourceInfo=new ResourceInfo();
				List<DeviceGsmaHistory> saveDeviceGsmaHistorylst=new ArrayList<DeviceGsmaHistory>();
				
				resourceInfo.setResourceValue(imeiValue);
				resourceInfo.setResourceType("IMEI");
				//ResourceInfo resource=apolloNEOutboundClientService.getResourceInfoService(resourceInfo);
				//log.debug("resource::" + resource.toString());
				DeviceGsmaHistory[] deviceHisListfromdb=apolloNEOutboundClientService.getDeviceHistUsingImeis(imeiValue,"READY");
				
				if(blacklistAction != null && blackliststatusFromGSMA != null && gsmaResponseStatus != null) {
					if (gsmaResponseStatus.equalsIgnoreCase("success")) {
						if(blacklistAction.equals("A") && blackliststatusFromGSMA.equalsIgnoreCase("Yes")) {
							
							for(DeviceGsmaHistory deviceGsmaHistory1:deviceHisListfromdb) {
								deviceGsmaHistory1.setStatus("ERROR");
								deviceGsmaHistory1.setModifiedBy("NSL");
								deviceGsmaHistory1.setModifiedDate(getTimeStamp());
								deviceGsmaHistory1.setErrorCode("ERR116");
								deviceGsmaHistory1.setErrorMessage("Exist in GSMA DB");
								
								deviceGsmaHistory1.getResourceInfo().setStatus("ERROR");
								deviceGsmaHistory1.getResourceInfo().setModifiedBy("NSL");
								deviceGsmaHistory1.getResourceInfo().setModifiedDate(getTimeStamp());
								
								saveDeviceGsmaHistorylst.add(deviceGsmaHistory1);
							}
						} else if (blacklistAction.equals("A") && blackliststatusFromGSMA.equalsIgnoreCase("No")) {
							
							for(DeviceGsmaHistory deviceGsmaHistory1:deviceHisListfromdb) {
								deviceGsmaHistory1.setStatus("PENDING");
								deviceGsmaHistory1.setModifiedBy("NSL");
								deviceGsmaHistory1.setModifiedDate(getTimeStamp());
								
								deviceGsmaHistory1.getResourceInfo().setStatus("PENDING");
								deviceGsmaHistory1.getResourceInfo().setModifiedBy("NSL");
								deviceGsmaHistory1.getResourceInfo().setModifiedDate(getTimeStamp());
								
								saveDeviceGsmaHistorylst.add(deviceGsmaHistory1);
							}
						} else if (blacklistAction.equals("D") && blackliststatusFromGSMA.equalsIgnoreCase("No")) {
							for(DeviceGsmaHistory deviceGsmaHistory1:deviceHisListfromdb) {
								deviceGsmaHistory1.setStatus("ERROR");
								deviceGsmaHistory1.setModifiedBy("NSL");
								deviceGsmaHistory1.setModifiedDate(getTimeStamp());
								deviceGsmaHistory1.setErrorCode("ERR116");
								deviceGsmaHistory1.setErrorMessage("Exist in GSMA DB");
								
								deviceGsmaHistory1.getResourceInfo().setStatus("ERROR");
								deviceGsmaHistory1.getResourceInfo().setModifiedBy("NSL");
								deviceGsmaHistory1.getResourceInfo().setModifiedDate(getTimeStamp());
								
								saveDeviceGsmaHistorylst.add(deviceGsmaHistory1);
							}
						} else if (blacklistAction.equals("D") && blackliststatusFromGSMA.equalsIgnoreCase("Yes")) {
							
							for(DeviceGsmaHistory deviceGsmaHistory1:deviceHisListfromdb) {
								deviceGsmaHistory1.setStatus("PENDING");
								deviceGsmaHistory1.setModifiedBy("NSL");
								deviceGsmaHistory1.setModifiedDate(getTimeStamp());
								
								deviceGsmaHistory1.getResourceInfo().setStatus("PENDING");
								deviceGsmaHistory1.getResourceInfo().setModifiedBy("NSL");
								deviceGsmaHistory1.getResourceInfo().setModifiedDate(getTimeStamp());
								
								saveDeviceGsmaHistorylst.add(deviceGsmaHistory1);
							}
						}
					} else if (gsmaResponseStatus.equalsIgnoreCase("failure")) {
						
						for(DeviceGsmaHistory deviceGsmaHistory1:deviceHisListfromdb) {
							deviceGsmaHistory1.setStatus("ERROR");
							deviceGsmaHistory1.setModifiedBy("NSL");
							deviceGsmaHistory1.setModifiedDate(getTimeStamp());
							deviceGsmaHistory1.setErrorCode(gsmaErrorcode);
							deviceGsmaHistory1.setErrorMessage(gsmaErrorDesc);
							saveDeviceGsmaHistorylst.add(deviceGsmaHistory1);
						}
					}
					if(saveDeviceGsmaHistorylst!=null && saveDeviceGsmaHistorylst.size()>0) {
						apolloNEOutboundClientService.saveDevicegsmaHistlst(saveDeviceGsmaHistorylst);
					}
				}
			}
			
			
			
			//code for mdnSwap
			if(StringUtils.hasText(outputString)&&(!outputString.contains("responseCode\":\"E"))&&"Change SIM".equalsIgnoreCase(operationName)) {
				String rootTransName=rcsDao.getRootTransName(responseId);
				if("Swap MDN".equalsIgnoreCase(rootTransName)) { 
				String transMileStone=rcsDao.getTransactionMileStoneFromMetadata(responseId);
				switch (transMileStone) {
				case "TEMP SIM Reserved": {
					transMileStone = "MDN1 Swapped to Temp SIM";
					break;

				}
				case "MDN1 Swapped to Temp SIM": {
					transMileStone = "MDN2 Swapped to SIM1";
					break;

				}
				case "MDN2 Swapped to SIM1": {
					transMileStone = "MDN1 Swapped to SIM2";
					break;
				}
				case "MDN1 Swapped to SIM2": {
					transMileStone = "Released Temp SIM";
					break;
				}

				}
				rcsDao.updateTransactionMileStoneFromMetadata(responseId,transMileStone);
				}
				}
			
			// Code for zipCode Inventory
			try {
				log.debug("Inside updateTransactionDetails operationName::" + operationName);
				log.debug("Before Inside updateTransactionDetails outputString::" + outputString);
				RefErrorRules[] RefErrorRuleslst = null;
				String errorcodes = "";
				boolean isRetryMDNCodePresent = false;
				Integer retryLimit = 0;
				response = sendClientRequest.getResponse();
				if (operationName.equalsIgnoreCase(CommonConstants.LINE_INQUIRY) && response != null) {
					Gson gson = new Gson();
					log.info("line inquiry response::" + response.split("~")[0]);
					bean = gson.fromJson(response.split("~")[0], ResponseBean.class);
					String imsi = "";
					String mdn = "";
					String iccid = "";
					String elineId = "";
					String lId ="";
					Sim sim = new Sim();
					log.info("result lineInquiryObject:: " + bean);
					if (bean.getData() != null) {
						if (bean.getData().getImsi() != null) {
							imsi = bean.getData().getImsi();
							log.debug("LineInquiry Response imsi ::" + imsi);
						}
						if (bean.getData().getMdn() != null) {
							mdn = bean.getData().getMdn().get(0).getValue();
							log.debug("LineInquiry Response mdn ::" + mdn);
						}
						if (bean.getData().getSimId() != null) {
							iccid = bean.getData().getSimId().get(0).getValue();
							log.debug("LineInquiry Response iccid ::" + iccid);
						}
					}
					JSONObject resultobject = new JSONObject();
					String result = apolloNEOutboundClientService.getLineDetails(mdn);
					log.info("result getLineDetails:: " + result);
					if (result != null) {
						resultobject = new JSONObject(result);
						log.debug("result resultobject:: " + resultobject);
						if (resultobject != null) {
                            log.debug("IMSI resultobject:: " + resultobject);
                            elineId = resultobject.get("eLineId").toString();
                            lId=resultobject.get("lineId").toString();
							log.debug("elineId updateImsibylineid " + elineId);
						}
					}
					sim.seteLineId(elineId);
					sim.setImsi(imsi);
					sim.setIccid(iccid);
					sim.setLineId(lId);
					log.info("before updateImsibylineid " + sim);
					apolloNEOutboundClientService.updateImsibylineid(sim);
					if (responseId != null && !responseId.isEmpty()) {
						log.info("before updateImsibylineid imsi::" + imsi + " responseId::" + responseId);
						apolloNEOutboundClientService.callTransactionHistoryForUpdateIMSI(imsi, responseId, elineId);
					}
				}///
				boolean isRetryMDNSWAPPresent=false;
				boolean isSwapMDNTempSim=false;
				if((operationName.equalsIgnoreCase("Change SIM")&&StringUtils.hasText(outputString)&&outputString.contains("responseCode\":\"E"))||(StringUtils.hasText(outputString)&&outputString.contains("\"400\"") &&outputString.contains("\"Bad Request\"")))  {
					String rootTransName=rcsDao.getRootTransName(responseId);
					String transMileStone=rcsDao.getTransactionMileStoneFromMetadata(responseId);
					log.debug("rootTransName"+rootTransName+"transMileStone::"+transMileStone);
					if("Swap MDN".equalsIgnoreCase(rootTransName)&&("TEMP SIM Reserved".equalsIgnoreCase(transMileStone))) {
						isSwapMDNTempSim=true;
						RefErrorRules refErrorRules = new RefErrorRules();
						refErrorRules.setRuleDetails("RETRIEVE_MDNSWAP_RETRY");
						RefErrorRuleslst = apolloNEOutboundClientService.getErrorRulesDetails(refErrorRules);
						if (RefErrorRuleslst != null) {
							for (RefErrorRules refErrorRulesFromlst : RefErrorRuleslst) {
								errorcodes = "(.*)"+"\"" + refErrorRulesFromlst.getErrorCode()+"(.*)";
								log.debug("errorcodes::" + errorcodes);
								if (outputString.matches(errorcodes)) {
									isRetryMDNSWAPPresent = true;
									retryLimit = Integer.valueOf(refErrorRulesFromlst.getRetryLimit());
									break;
								}
							}
						}
					}
					if("Swap MDN".equalsIgnoreCase(rootTransName)&&("MDN1 Swapped to Temp SIM".equalsIgnoreCase(transMileStone))) {
						transMileStone="Rollback MDN1 to SIM1";
						rcsDao.updateTransactionMileStoneFromMetadata(responseId,transMileStone);
						final String asyncReqJson = sendClientRequest.getDataMap().get("initRequest").toString();
						final String trans_Id = responseId;
						final String transType = "";
						final String swapMdnoperationName = CommonConstants.SWAPMDN_WF;
						final String  swapMdnserviceName = CommonConstants.SWAPMDN_PP;
						
					
					ExecutorService executor = Executors.newSingleThreadExecutor();
						executor.execute(new Runnable() {
							@Override
							public void run() {
								getSwapMdnFromClient(asyncReqJson, trans_Id, trans_Id, transType,swapMdnoperationName,swapMdnserviceName);

							}
						});
						executor.shutdown();
					}
					else if("Swap MDN".equalsIgnoreCase(rootTransName)&&("Rollback MDN1 to SIM1".equalsIgnoreCase(transMileStone))) {
						transMileStone="Swap MDN Failed";
						rcsDao.updateTransactionMileStoneFromMetadata(responseId,transMileStone);
						final String asyncReqJson = sendClientRequest.getDataMap().get("initRequest").toString();
						final String trans_Id = responseId;
						final String transType = "";
						final String swapMdnoperationName = CommonConstants.SWAPMDN_WF;
						final String  swapMdnserviceName = CommonConstants.SWAPMDN_PP;
						
					
					ExecutorService executor = Executors.newSingleThreadExecutor();
						executor.execute(new Runnable() {
							@Override
							public void run() {
								getSwapMdnFromClient(asyncReqJson, trans_Id, trans_Id, transType,swapMdnoperationName,swapMdnserviceName);

							}
						});
						executor.shutdown();
					}
					else if("Swap MDN".equalsIgnoreCase(rootTransName)&&("MDN1 Swapped to SIM2".equalsIgnoreCase(transMileStone))) {
						transMileStone="Rollback MDN2 to SIM2";
						rcsDao.updateTransactionMileStoneFromMetadata(responseId,transMileStone);
						final String asyncReqJson = sendClientRequest.getDataMap().get("initRequest").toString();
						final String trans_Id = responseId;
						final String transType = "";
						final String swapMdnoperationName = CommonConstants.SWAPMDN_WF;
						final String  swapMdnserviceName = CommonConstants.SWAPMDN_PP;
						
					
					ExecutorService executor = Executors.newSingleThreadExecutor();
						executor.execute(new Runnable() {
							@Override
							public void run() {
								getSwapMdnFromClient(asyncReqJson, trans_Id, trans_Id, transType,swapMdnoperationName,swapMdnserviceName);

							}
						});
						executor.shutdown();
					}
				}
				if ((operationName.equalsIgnoreCase("Activate Subscriber PSIM")
						|| operationName.equalsIgnoreCase("Activate Subscriber ESIM")
						|| operationName.equalsIgnoreCase(CommonConstants.ACTIVATESUBSCRIBER)
						|| operationName.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER)
						|| operationName.equalsIgnoreCase("Add Wearable")
						|| operationName.equalsIgnoreCase(CommonConstants.CHANGE_MDN)) && outputString != null) {
					log.debug(
							"Inside outputString Activate Subscriber PSIM or Activate Subscriber ESIM zipCode Inventory::"
									+ outputString);
					log.debug(
							"Inside retryZipCodeCount() Activate Subscriber PSIM or Activate Subscriber ESIM zipCode Inventory::"
									+ sendClientRequest.getRetryZipCodeCount());

					RefErrorRules refErrorRules = new RefErrorRules();
					refErrorRules.setRuleDetails("RETRIEVE_ZIPCODE_RETRY");
					RefErrorRuleslst = apolloNEOutboundClientService.getErrorRulesDetails(refErrorRules);
					if (RefErrorRuleslst != null) {
						for (RefErrorRules refErrorRulesFromlst : RefErrorRuleslst) {
							errorcodes = "\"" + refErrorRulesFromlst.getErrorCode() + "\"";
							log.debug("errorcodes::" + errorcodes);
							if (outputString.contains(errorcodes)) {
								isRetryMDNCodePresent = true;
								retryLimit = Integer.valueOf(refErrorRulesFromlst.getRetryLimit());
								break;
							}
						}
					}
				}
				log.debug("isRetryMDNSWAPPresent::"+isRetryMDNSWAPPresent+"retryLimit::"+retryLimit+"sendClientRequest.getRetryZipCodeCount()::"+sendClientRequest.getRetryZipCodeCount());
				if (isRetryMDNSWAPPresent && (sendClientRequest.getRetryZipCodeCount() < retryLimit)) {
					String formattedJson = outReqBean.getRequestJson();
					log.debug("formattedJson::"+formattedJson);
					if (formattedJson.startsWith("{")) {
						ResourceInfo resourceInfo = new ResourceInfo();
						resourceInfo.setAvailability("Y");
						resourceInfo.setStatus("Ready");
						resourceInfo.setResourceType("ICCID");
						resourceInfo.setAllocatedDate(getTimeStamp());
						resourceInfo = apolloNEOutboundClientService.getResourceInfoService(resourceInfo);
						Boolean resource = false;
						if (resourceInfo != null) {
							log.debug("Inside resourceInfo" + resourceInfo.toString());
							resource = true;
							String iccidVal = resourceInfo.getResourceValue();
							log.debug("formattedJson1::" + formattedJson);
							JsonObject formattedJsonObj = new JsonParser().parse(formattedJson).getAsJsonObject();
							JsonArray SimArray = formattedJsonObj.get("data").getAsJsonObject().get("subOrder")
									.getAsJsonArray().get(0).getAsJsonObject().get("simId").getAsJsonArray();
							JsonArray newSimArray = new JsonArray();

							for (int i = 0; i < SimArray.size(); i++) {
								JsonObject newSimObject = new JsonObject();
								if (SimArray.get(i).getAsJsonObject().get("type").getAsString()
										.equalsIgnoreCase("oldICCID")) {
									newSimObject.addProperty("type", "oldICCID");
									newSimObject.addProperty("value",
											SimArray.get(i).getAsJsonObject().get("value").getAsString());
									newSimArray.add(newSimObject);
								}
								else {
								newSimObject.addProperty("type", "newICCID");
								newSimObject.addProperty("value", iccidVal);
								newSimArray.add(newSimObject);
								}
							}
							formattedJsonObj.get("data").getAsJsonObject().get("subOrder").getAsJsonArray().get(0)
									.getAsJsonObject().remove("simId");
							formattedJsonObj.get("data").getAsJsonObject().get("subOrder").getAsJsonArray().get(0)
									.getAsJsonObject().add("simId", newSimArray);
							formattedJson = formattedJsonObj.toString();
							log.debug("formattedJson2::" + formattedJson);
						}
						sendClientRequest.setRetryZipCodeCount((sendClientRequest.getRetryZipCodeCount()) + 1);
						log.debug("MDNSWAP sendClientRequest.getRetryZipCodeCount()"
								+ sendClientRequest.getRetryZipCodeCount());
						log.debug("MDNSWAP formattedJson" + formattedJson);
						outReqBean.setRetryZipCodeCount(sendClientRequest.getRetryZipCodeCount());
						sendClientRequest.getOutReqBean()
								.setRetryZipCodeCount(sendClientRequest.getRetryZipCodeCount());
						log.debug("MDNSWAP retryLimit123::" + retryLimit+"resource::"+resource);
						if (retryLimit != 0 && resource) {
							try {
								
								ExecutorService executor = Executors.newSingleThreadExecutor();
								executor.execute(new Runnable() {
									@Override
									public void run() {
										try {
											ProcessMetadata processMetadata = new ProcessMetadata();
											processMetadata.setRootTransactionId(responseId);
											ProcessMetadata[] processMetaData = apolloNEOutboundClientService
													.getProcessMetadata(processMetadata);
											ResourceInfo resourceInfo = new ResourceInfo();
											resourceInfo.setReleasedDate(getTimeStamp());
											resourceInfo.setResourceType("ICCID");
											resourceInfo.setAvailability("N");
											resourceInfo.setStatus("ERROR");
											resourceInfo.setResourceValue(
													new JSONObject(processMetaData[processMetaData.length - 1].getProcessData())
															.getString("iccid"));
											apolloNEOutboundClientService.updateResourceInfo(resourceInfo);
										} catch (Exception e) {
											log.error("Exception::", e);
										}

									}
								});
								executor.shutdown();
								
							}catch(Exception e) {
								log.error("Exception::",e);
							}
							ProcessMetadata processMetadata = new ProcessMetadata();
							processMetadata.setRootTransactionId(responseId);
							ProcessMetadata[] processMetaData = apolloNEOutboundClientService
									.getProcessMetadata(processMetadata);
							
							String	iccidVal=resourceInfo.getResourceValue();
							JsonObject iccdObject = new JsonObject();

								iccdObject.addProperty("iccid", iccidVal);
								ProcessMetadata processMetadata1=processMetaData[0];
								processMetadata1.setProcessData(iccdObject.toString());
								processMetadata1=apolloNEOutboundClientService.insertProcessMetadata(processMetadata1);

							
							sendClientRequest.getOutReqBean()
									.setIsRetryMDNCodePresent(String.valueOf(isRetryMDNCodePresent));
							sendClientRequest.setRequest(formattedJson);
							sendClientRequest.getOutReqBean().setRequestJson(formattedJson);
							sendClientRequest.getRcsServiceBean().setRequest(formattedJson);

//										Gson gson = new Gson();
//										String sendClientRequestJson = gson.toJson(sendClientRequest);
							log.debug("MDNSWAP sendClientRequest.getRetryZipCodeCount()" + formattedJson);

							ObjectMapper mapper = new ObjectMapper();
							mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
							String sendClientRequestJson = mapper.writerWithDefaultPrettyPrinter()
									.writeValueAsString(sendClientRequest);
							log.debug(
									"sendClientRequestJson in swapMDN123::"
											+ sendClientRequestJson);

							Message message = MessageBuilder.withBody(sendClientRequestJson.getBytes())
									.setContentType(MediaType.APPLICATION_JSON_VALUE).build();
//										Message result = customRabbitTemplate.sendAndReceive(sbNEQueueProperties.getSbNeOutboundServiceExchange(),
//												sbNEQueueProperties.getSbNeOutboundServiceQueue(), message);
							Message result = apolloNeSounthBoundCall(message);
							response = new String(result.getBody());
							log.debug("Inside response swapMDN123" + response);
						}else if(!resource) {
							apolloNESyncFailureToITMBO(outputString,sendClientRequest.getDataMap().get("initRequest"),responseId,operationName,sendClientRequest.getOutReqBean(),sendClientRequest.getDataMap(),sendClientRequest.getOutReqBean().getTransUid(),sendClientRequest,rcsServiceBean);
							try {
								
								ExecutorService executor = Executors.newSingleThreadExecutor();
								executor.execute(new Runnable() {
									@Override
									public void run() {
										try {
											ProcessMetadata processMetadata = new ProcessMetadata();
											processMetadata.setRootTransactionId(responseId);
											ProcessMetadata[] processMetaData = apolloNEOutboundClientService
													.getProcessMetadata(processMetadata);
											ResourceInfo resourceInfo = new ResourceInfo();
											resourceInfo.setReleasedDate(getTimeStamp());
											resourceInfo.setResourceType("ICCID");
											resourceInfo.setAvailability("N");
											resourceInfo.setStatus("ERROR");
											resourceInfo.setResourceValue(
													new JSONObject(processMetaData[processMetaData.length - 1].getProcessData())
															.getString("iccid"));
											apolloNEOutboundClientService.updateResourceInfo(resourceInfo);
										} catch (Exception e) {
											log.error("Exception::", e);
										}

									}
								});
								executor.shutdown();
								
							}catch(Exception e) {
								log.error("Exception::",e);
							}
						}
					}
				}else if(isSwapMDNTempSim){
					apolloNESyncFailureToITMBO(outputString,sendClientRequest.getDataMap().get("initRequest"),responseId,operationName,sendClientRequest.getOutReqBean(),sendClientRequest.getDataMap(),sendClientRequest.getOutReqBean().getTransUid(),sendClientRequest,rcsServiceBean);
					
						try {
							
							ExecutorService executor = Executors.newSingleThreadExecutor();
							executor.execute(new Runnable() {
								@Override
								public void run() {
									try {
										ProcessMetadata processMetadata = new ProcessMetadata();
										processMetadata.setRootTransactionId(responseId);
										ProcessMetadata[] processMetaData = apolloNEOutboundClientService
												.getProcessMetadata(processMetadata);
										ResourceInfo resourceInfo = new ResourceInfo();
										resourceInfo.setReleasedDate(getTimeStamp());
										resourceInfo.setResourceType("ICCID");
										resourceInfo.setAvailability("N");
										resourceInfo.setStatus("ERROR");
										resourceInfo.setResourceValue(
												new JSONObject(processMetaData[processMetaData.length - 1].getProcessData())
														.getString("iccid"));
										apolloNEOutboundClientService.updateResourceInfo(resourceInfo);
									} catch (Exception e) {
										log.error("Exception::", e);
									}

								}
							});
							executor.shutdown();
							
						}catch(Exception e) {
							log.error("Exception::",e);
						}
					
				}

				log.debug("isRetryMDNCodePresent::" + isRetryMDNCodePresent + "sendClientRequest.getRetryZipCodeCount()::"
						 + sendClientRequest.getRetryZipCodeCount() + "retryLimit::" + retryLimit);
				if ((operationName.equalsIgnoreCase("Activate Subscriber PSIM")
						|| operationName.equalsIgnoreCase("Activate Subscriber ESIM")
						|| operationName.equalsIgnoreCase(CommonConstants.ACTIVATESUBSCRIBER)
						|| operationName.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER)
						|| operationName.equalsIgnoreCase("Add Wearable")
						|| operationName.equalsIgnoreCase(CommonConstants.CHANGE_MDN)) && outputString != null) {
					log.info("sendClientRequest.getRetryZipCodeCount() ::" + sendClientRequest.getRetryZipCodeCount() + "retryLimit::" + retryLimit );
						
					if( (isRetryMDNCodePresent) && (sendClientRequest.getRetryZipCodeCount() <= retryLimit)) {
					log.debug("Inside formattedJson zipCode Inventory::" + outputString);
					String zipCodePrimary = "";
					String nearestZipCodeFromInventory = "";
					String formattedJson = outReqBean.getRequestJson();
					log.debug("Inside formattedJson zipCode Inventory::" + formattedJson);
					if (formattedJson.startsWith("{")) {
						JSONObject formattedDataObj0 = new JSONObject(formattedJson);
						if (formattedDataObj0.has("data")) {
							formattedDataObj0 = formattedDataObj0.getJSONObject("data");
							log.debug("formattedJson in ActivateSubscriber PSIM or ActivateSubscriber ESIM ::"
									+ formattedDataObj0);
							if (formattedDataObj0.has("subOrder")) {
								JSONArray subOrderArr = formattedDataObj0.getJSONArray("subOrder");
								JSONObject subOrderObj = subOrderArr.getJSONObject(0);

								if (subOrderObj.has("nextAvailableMDN")) {
									subOrderObj = subOrderObj.getJSONObject("nextAvailableMDN");
									if (subOrderObj.has("zipCode")) {
										zipCodePrimary = subOrderObj.getString("zipCode");
										log.debug(
												"ActivateSubscriber PSIM or ActivateSubscriber ESIM zipCode Inventory zipCodePrimary::"
														+ zipCodePrimary);
										List<NpaNxx> npanxxList = apolloNEOutboundClientService
												.getNpaNxxDetails(zipCodePrimary);
										log.debug(
												"ActivateSubscriber PSIM or ActivateSubscriber ESIM  zipCode Inventory response::"
														+ npanxxList);
										if (npanxxList.size() > 0) {
											for (int i = 0; i < npanxxList.size(); i++) {
												nearestZipCodeFromInventory = npanxxList.get(i).getZipCode();
												log.debug(
														"Activate Subscriber or CHANGE_MDN or ESIM ActivateSubscriber zipCode Inventory nearestZipCodeFromInventory List::"
																+ nearestZipCodeFromInventory);
												if (nearestZipCodeFromInventory.equalsIgnoreCase(zipCodePrimary)) {
													continue;
												}
												if (nearestZipCodeFromInventory != null
														&& !nearestZipCodeFromInventory.isEmpty()) {
													if (formattedJson.contains("\"nextAvailableMDN\":{\"zipCode\":\""
															+ zipCodePrimary + "\"}")) {
														formattedJson = formattedJson.replace(
																"\"nextAvailableMDN\":{\"zipCode\":\"" + zipCodePrimary
																		+ "\"}",
																"\"nextAvailableMDN\":{\"zipCode\":\""
																		+ nearestZipCodeFromInventory + "\"}");
														log.debug(
																"Activate Subscriber or CHANGE_MDN or ESIM ActivateSubscriber zipCode Inventory formattedJson after inventory replacement::"
																		+ formattedJson);
													}
												}

												sendClientRequest.setRetryZipCodeCount(
														(sendClientRequest.getRetryZipCodeCount()) + 1);
												log.debug(
														"Activate Subscriber or CHANGE_MDN or ESIM ActivateSubscriber sendClientRequest.getRetryZipCodeCount()::"
																+ sendClientRequest.getRetryZipCodeCount());
												outReqBean
														.setRetryZipCodeCount(sendClientRequest.getRetryZipCodeCount());
												sendClientRequest.getOutReqBean()
														.setRetryZipCodeCount(sendClientRequest.getRetryZipCodeCount());
												if (retryLimit != 0) {
													sendClientRequest.getOutReqBean().setIsRetryMDNCodePresent(
															String.valueOf(isRetryMDNCodePresent));
													sendClientRequest.setRequest(formattedJson);
													sendClientRequest.getOutReqBean().setRequestJson(formattedJson);
													sendClientRequest.getRcsServiceBean().setRequest(formattedJson);

//										Gson gson = new Gson();
//										String sendClientRequestJson = gson.toJson(sendClientRequest);
													log.debug(
															"formattedJson in ActivateSubscriber PSIM or ActivateSubscriber ESIM formattedJson replacement::"
																	+ formattedJson);

													ObjectMapper mapper = new ObjectMapper();
													mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
													String sendClientRequestJson = mapper
															.writerWithDefaultPrettyPrinter()
															.writeValueAsString(sendClientRequest);
													log.debug(
															"formattedJson in ActivateSubscriber PSIM or ActivateSubscriber ESIM sendClientRequestJson::"
																	+ sendClientRequestJson);

													Message message = MessageBuilder
															.withBody(sendClientRequestJson.getBytes())
															.setContentType(MediaType.APPLICATION_JSON_VALUE).build();
//										Message result = customRabbitTemplate.sendAndReceive(sbNEQueueProperties.getSbNeOutboundServiceExchange(),
//												sbNEQueueProperties.getSbNeOutboundServiceQueue(), message);
													Message result = apolloNeSounthBoundCall(message);
													response = new String(result.getBody());
													log.debug("Inside updateTransactionDetails response zipCode"
															+ response);
													if (response.contains("SUCCESS")
															|| response.contains("\"returnCode\":\"00\"")) {

														TransactionHistory transactionHistory = new TransactionHistory();
														transactionHistory.setAltZipCodeInd("Yes");
														transactionHistory.setTransactionId(responseId);
														apolloNEOutboundClientService
																.updateAltZipcode(transactionHistory);
													} else {
														log.info("sendClientRequest.getRetryZipCodeCount() ::" + sendClientRequest.getRetryZipCodeCount() + "retryLimit::" + retryLimit );
														
														if ((sendClientRequest.getRetryZipCodeCount() != 0) && 
																(sendClientRequest.getRetryZipCodeCount() == retryLimit)) {
															log.debug("Error call back ::" + sendClientRequest + "outputString::" + outputString );
															isRetryMDNCodePresent = false;
															sendClientRequest.getOutReqBean().setIsRetryMDNCodePresent(
																	String.valueOf(isRetryMDNCodePresent));
															apolloNESyncFailureToITMBO(outputString,sendClientRequest.getDataMap().get("initRequest"),responseId,operationName,sendClientRequest.getOutReqBean(),sendClientRequest.getDataMap(),sendClientRequest.getOutReqBean().getTransUid(),sendClientRequest,rcsServiceBean);
														}
													}
														break;
													}
												}
											}
										}
									}
								}
							}
						}
					}
					
				} 
				
				
				
			} catch (Exception e) {
				log.error("ErrorCode : "+ErrorCodes.CECC0004+" : Exception in zipCode", e);
			}
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0004+" : Exception updateTransaction", e);
		}

	}

	public void insertTransaction(RcsIntegrationServiceBean rcsServiceBean, OutboundRequest outReqBean) {
		log.debug("insertTransaction rcsServiceBean::" + rcsServiceBean);
		log.debug(" OutboundRequest::" + outReqBean);
		outReqBean.setTransId(rcsDao.getPrimaryKey());
		String bodyInfoTemp = null;
		JsonObject bodyInfo = null;
		if (StringUtils.hasText(outReqBean.getOperationName())
				&& !outReqBean.getOperationName().equalsIgnoreCase(CommonConstants.IMSI_INQUIRY)
				&& !outReqBean.getOperationName().equalsIgnoreCase(CommonConstants.VALIDATE_DEVICE)
				&& !outReqBean.getOperationName().equalsIgnoreCase(CommonConstants.PROMOTION_INQUIRY)
				&& !outReqBean.getOperationName().equalsIgnoreCase(CommonConstants.SUBSCRIBERGROUP_INQUIRY)) {
			if (rcsServiceBean.getRequest() != null) {
				bodyInfoTemp = consumerUtilityClass.changeReferenceNumToTransId(rcsServiceBean.getRequest(), outReqBean.getTransId());
				// bodyInfoTempJSON = new JSONObject(bodyInfoTemp);
				bodyInfo = new JsonParser().parse(bodyInfoTemp).getAsJsonObject();
				if (bodyInfo.has("data")) {
					bodyInfo.get("data").getAsJsonObject().remove("internalOrder");
					bodyInfo.get("data").getAsJsonObject().remove("relatedTransactionId");
					bodyInfo.get("data").getAsJsonObject().remove("relatedLineId");
				}
			}
		}
		if (bodyInfo != null) {
			outReqBean.setRequestJson(bodyInfo.toString());
		} else {
			outReqBean.setRequestJson(rcsServiceBean.getNcmSouthBoundUrl());
		}
		log.debug("outReqBean::" + outReqBean);
		rcsDao.insertSouthBoundTransaction(outReqBean);

	}

	public String asyncErrorCall(String returnMessage, String returnCode, String errorCode, String errorMessage,
			String referenceNumber, String responseId, String operationName) {
		log.info("asyncErrorCall:: - returnMessage" + returnMessage + " - returnCode::" + returnCode + " - errorCode::"
				+ errorCode + " - errorMessage::" + errorMessage + "- referenceNumber ::" + referenceNumber
				+ "- responseId ::" + responseId + " - operationName ::" + operationName);
		String res = "";
		if (returnCode.equalsIgnoreCase("400")) {
			returnCode = "ERR20";
		} else if (errorCode.equalsIgnoreCase("400")) {
			errorCode = "ERR20";
		}
		if (!returnMessage.equalsIgnoreCase("") || !returnCode.equalsIgnoreCase("")) {
			res = "{\"messageHeader\":{\"serviceId\":\"SPECTRUM_MOBILE\",\"requestType\":\"MNO\",\"referenceNumber\":\""
					+ referenceNumber + "\"},\"data\":{\"transactionId\":\"" + responseId
					+ "\",\"code\":\"400\",\"reason\":\"Bad Request\",\"message\":[{\"responseCode\":\"" + returnCode
					+ "\",\"description\":\"" + returnMessage + "\"}]}}";
		} else if (!errorCode.equalsIgnoreCase("") || !errorMessage.equalsIgnoreCase("")) {
			res = "{\"messageHeader\":{\"serviceId\":\"SPECTRUM_MOBILE\",\"requestType\":\"MNO\",\"referenceNumber\":\""
					+ referenceNumber + "\"},\"data\":{\"transactionId\":\"" + responseId
					+ "\",\"code\":\"400\",\"reason\":\"Bad Request\",\"message\":[{\"responseCode\":\"" + errorCode
					+ "\",\"description\":\"" + errorMessage + "\"}]}}";
		}

		if (operationName.equalsIgnoreCase("Activate Subscriber")
				|| operationName.equalsIgnoreCase("ESIM ActivateSubscriber")) {
			if (res.contains("data")) {
				res = res.replace("data", "activationData");
			}
		} else if (operationName.equalsIgnoreCase("Activate Subscriber Port-in")
				|| operationName.equalsIgnoreCase("ESIM Activate Subscriber Port-in")) {
			if (res.contains("data")) {
				res = res.replace("data", "portInData");
			}
		} else if (operationName.equalsIgnoreCase("Change MDN")) {
			if (res.contains("data")) {
				res = res.replace("data", "changeMdnData");
			}
		} else if (operationName.equalsIgnoreCase("Validate Device")
				|| operationName.equalsIgnoreCase(CommonConstants.LINE_INQUIRY)
				|| "CP-Reconnect Mdn".equalsIgnoreCase(operationName)) {
			log.info("Validate Device operationName::" + operationName + "::res::" +res);
			res = validateDeviceErrorCall(responseId, res);
			log.info("Validate Device Ends ::" + res);
		}
		if("CP-Reconnect Mdn".equalsIgnoreCase(operationName)) {
			try {
				String errorCode2 = "";
				String errorMsg2 = "";
				String rootOperationName=rcsDao.getRootTransName(responseId);
				log.debug("rootOperationName::"+rootOperationName);
				if("Change MDN".equalsIgnoreCase(rootOperationName)) {
				List<Map<String, Object>> result = rcsDao.transactionErrorMessage(responseId);
				if (result != null && result.size() == 2) {
					for (int i = 0; i < result.size(); i++) {
						errorCode2 = (String) result.get(i).get("ERROR_CODE");
						errorMsg2 = (String) result.get(i).get("ERROR_MSG");
						if(StringUtils.hasText(returnCode)) {
						if(!errorCode2.equalsIgnoreCase(returnCode)) {
							break;
						}}
						if(StringUtils.hasText(errorCode)) {
						if(!errorCode2.equalsIgnoreCase(errorCode)) {
							break;
						}
						}
					}
				} else if (result != null && result.size() > 0) {
					for (int i = 0; i < result.size(); i++) {
						errorCode2 = (String) result.get(i).get("ERROR_CODE");
						errorMsg2 = (String) result.get(i).get("ERROR_MSG");
						if(errorCode2.equalsIgnoreCase("ERR96")) {
							break;
						}
					}
				}
				
				
				log.debug("errorCode2::" + errorCode2 + "errorMsg2::" + errorMsg2 + "returnMessage::" +returnMessage + "returnCode::" +returnCode+ "errorCode::" +errorCode+ "errorMessage::" +errorMessage);
				if (StringUtils.hasText(errorCode2) && StringUtils.hasText(errorMsg2)&&((!returnMessage.equalsIgnoreCase("") || !returnCode.equalsIgnoreCase("")))||((!errorCode.equalsIgnoreCase("") || !errorMessage.equalsIgnoreCase("")))) {
					if(!errorCode2.equals("ERR96")) {
						errorCode2 = "ERR551";
					} else if (errorCode2.equals("ERR96")) {
						errorCode2 = "ERR556";
					}
					if (!returnMessage.equalsIgnoreCase("") || !returnCode.equalsIgnoreCase("")) {
						log.debug("returnCode::"+returnCode);
						returnMessage="Unable to reconnect MDN";
						returnCode="ERR55";
						res = "{\"messageHeader\":{\"serviceId\":\"SPECTRUM_MOBILE\",\"requestType\":\"MNO\",\"referenceNumber\":\""
								+ referenceNumber + "\"},\"data\":{\"transactionId\":\"" + responseId
								+ "\",\"code\":\"400\",\"reason\":\"Bad Request\",\"message\":[{\"responseCode\":\""
								+ errorCode2 + "\",\"description\":\"" +returnMessage+ " and "+ errorMsg2 + "\"}]}}";
						
						} else if (!errorCode.equalsIgnoreCase("") || !errorMessage.equalsIgnoreCase("")) {
						log.debug("errorCode::"+errorCode);
						errorMessage="Unable to reconnect MDN";
						errorCode="ERR55";
						res = "{\"messageHeader\":{\"serviceId\":\"SPECTRUM_MOBILE\",\"requestType\":\"MNO\",\"referenceNumber\":\""
								+ referenceNumber + "\"},\"data\":{\"transactionId\":\"" + responseId
								+ "\",\"code\":\"400\",\"reason\":\"Bad Request\",\"message\":[{\"responseCode\":\""
								+ errorCode2 + "\",\"description\":\"" +errorMessage+ " and " + errorMsg2 + "\"}]}}";
					}
				}
				else if(StringUtils.hasText(errorCode2) && StringUtils.hasText(errorMsg2)) {
					log.debug("errorCode2::"+errorCode2);
					res ="{\"messageHeader\":{\"serviceId\":\"SPECTRUM_MOBILE\",\"requestType\":\"MNO\",\"referenceNumber\":\""+referenceNumber+"\"},\"data\":{\"transactionId\":\""+responseId+"\",\"code\":\"400\",\"reason\":\"Bad Request\",\"message\":[{\"responseCode\":\""+errorCode2+"\",\"description\":\""+errorMsg2+"\"}]}}";
				}
					/*
					 * else { returnMessage="Unable to reconnect MDN"; returnCode="ERR55";
					 * if(!returnMessage.equalsIgnoreCase("") || !returnCode.equalsIgnoreCase("")) {
					 * res
					 * ="{\"messageHeader\":{\"serviceId\":\"SPECTRUM_MOBILE\",\"requestType\":\"MNO\",\"referenceNumber\":\""
					 * +referenceNumber+"\"},\"data\":{\"transactionId\":\""+
					 * responseId+"\",\"code\":\"400\",\"reason\":\"Bad Request\",\"message\":[{\"responseCode\":\""
					 * +returnCode+"\",\"description\":\""+returnMessage+"\"}]}}"; }else
					 * if(!errorCode.equalsIgnoreCase("") || !errorMessage.equalsIgnoreCase("")) {
					 * res
					 * ="{\"messageHeader\":{\"serviceId\":\"SPECTRUM_MOBILE\",\"requestType\":\"MNO\",\"referenceNumber\":\""
					 * +referenceNumber+"\"},\"data\":{\"transactionId\":\""+
					 * responseId+"\",\"code\":\"400\",\"reason\":\"Bad Request\",\"message\":[{\"responseCode\":\""
					 * +errorCode+"\",\"description\":\""+errorMessage+"\"}]}}"; } }
					 */
				
				}
			}
	    	catch(Exception e) {
	    		log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Error in getting failure transaction",e);
	    	}
	    }
		/*
		 * else if(operationName.equalsIgnoreCase("Validate Device")||operationName.
		 * equalsIgnoreCase("Line Inquiry")||operationName.
		 * equalsIgnoreCase("Deactivate Subscriber")||operationName.
		 * equalsIgnoreCase("TW Deactivate Subscriber")||operationName.
		 * equalsIgnoreCase("Activate Subscriber Port-in")||operationName.
		 * equalsIgnoreCase("Cancel Port-In")||operationName.
		 * equalsIgnoreCase("Change MDN Port-In")||operationName.
		 * equalsIgnoreCase("ESIM Activate Subscriber Port-in")) { res =
		 * validateDeviceErrorCall(responseId,res); }
		 */
		log.info("res :: " + res);
		return res;
	}
	
	public String validateDeviceErrorCall(String responseId, String res) {
		log.info("Enter validateDeviceErrorCall::");
		List<Map<String, Object>> result = rcsDao.getValidateDeviceDetails(responseId);
		String serviceName = "";
		String resquest_msg = "";
		try {
			if (result != null && result.size() > 0) {
				for (int i = 0; i < result.size(); i++) {
					serviceName = (String) result.get(i).get("SERVICENAME");
					resquest_msg = (String) result.get(i).get("REQUEST_MSG");
				}
			}
			log.info("serviceName validateDeviceErrorCall::" + serviceName);
			log.info("resquest_msg validateDeviceErrorCall::" + resquest_msg);
			log.info("res validateDeviceErrorCall::" + res);
			if (serviceName.equalsIgnoreCase("Activate-Subscriber") || serviceName.equalsIgnoreCase("addESimSubscriber")
					|| serviceName.equalsIgnoreCase("change-MDN")) {
				JSONObject jsonObj = new JSONObject(resquest_msg);
				JSONObject obj = new JSONObject();
				String transactionType = "";
				if (jsonObj.has("data")) {
					obj = jsonObj.getJSONObject("data");
					if (obj.has("transactionType")) {
						transactionType = obj.getString("transactionType");
						log.debug("transactionType validateDeviceErrorCall::" + transactionType);
					}
				}
				if (transactionType.equalsIgnoreCase("AS") || transactionType.equalsIgnoreCase("AE")) {
					if (res.contains("data")) {
						res = res.replace("data", "activationData");
					}
				} else if (transactionType.equalsIgnoreCase("SP") || transactionType.equalsIgnoreCase("PE")) {
					if (res.contains("data")) {
						res = res.replace("data", "portInData");
					}
				} else if (transactionType.equalsIgnoreCase("CP") || transactionType.equalsIgnoreCase("CE")) {
					if (res.contains("data")) {
						res = res.replace("data", "portInData");
					}
				}
			}
		} catch (Exception e) {
			log.error("ErrorCode : " + ErrorCodes.CECC0007 + " : Exception in validateDeviceErrorCall", e);
		}

		return res;
	}

	public String getResponseFromMboClient(String endUrl, String request, Map<String, String> dataMap,
			String operationName,RcsIntegrationServiceBean rcsServiceBean,OutboundRequest outReqBean,String responseId) throws Exception {
		log.info("Inside getResponseFromMboClient:: - request ::" + request + " - endUrl ::" + endUrl
				+ "- operationName::" + operationName + "- dataMap.get(initRequest)::" + dataMap.get("initRequest")
				+ "- dataMap ::" + dataMap);
		String server = properties.getServer();
		log.info("server:: " + server);
		String outputString = ApolloNEConstants.EMPTY;
		String authType = ApolloNEConstants.EMPTY;
		String header = ApolloNEConstants.EMPTY;
		String soapAction = ApolloNEConstants.EMPTY;
		String contextId = "";
		String tokenCode = "";
		List<Map<String, Object>> mboCredentialDetails = null;
		String mboOauthClientUrl = ApolloNEConstants.EMPTY;
		String mboOauthClientSecret = ApolloNEConstants.EMPTY;
		String mboOauthClientId = ApolloNEConstants.EMPTY;
		BufferedReader in = null;
		URL url;
		InputStreamReader isr = null;
		Boolean enableErrorQueue = apolloNEQueueProperties.getEnableQueueCheck();
		rcsServiceBean.setRequest(request);
		rcsServiceBean.setEndUrl(endUrl);
		rcsServiceBean.setHttpMethod(EHttpMethods.valueOf(CommonConstants.POST));
		if (enableErrorQueue) {
			log.info("Queue Enabled: " + enableErrorQueue);
			ExecutorService executor = null;
			try {
				executor = Executors.newSingleThreadExecutor();
				executor.execute(new Runnable() {
					@Override
					public void run() {
						sendRequestToExternalClient(rcsServiceBean,dataMap,outReqBean,operationName,responseId,true);
					}
				});

			} catch (Exception e) {
				log.error("ErrorCode : " + ErrorCodes.CECC0009 + " : Error ::", e);
			} finally {
				if (executor != null)
					executor.shutdown();
			}

		} else {
			try {
				contextId = consumerUtilityClass.getContextId(dataMap.get("initRequest"));
				log.info("Inside contextId:: " + contextId);
				if (contextId == null || "".equalsIgnoreCase(contextId)) {
					contextId = rcsDao.getContextFromReturnUrl(endUrl);
				}
				log.info("Inside contextId after if:: " + contextId);
				mboCredentialDetails = rcsDao.mboCredentialDetails(server, contextId);
				if (mboCredentialDetails != null) {
					log.info("Inside mboOauthClientUrl:: " + mboCredentialDetails.toString());
				}

				if (mboCredentialDetails != null && mboCredentialDetails.size() > 0) {
					for (int i = 0; i < mboCredentialDetails.size(); i++) {
						mboOauthClientUrl = (String) mboCredentialDetails.get(i).get("SERVICE_URL");
						mboOauthClientId = (String) mboCredentialDetails.get(i).get("service_request");
						mboOauthClientSecret = (String) mboCredentialDetails.get(i).get("service_description");
					}
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
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setUseCaches(false);
				OutputStream os = conn.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
				String token = consumerUtilityClass.getQuery(tokenRequest);
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
				while ((responseString = in.readLine()) != null) {
					outputString = outputString + responseString;
				}
				if (!"".equalsIgnoreCase(outputString)) {
					JSONObject obj = new JSONObject(outputString);
					tokenCode = obj.getString("access_token");
				}
				if (!"".equalsIgnoreCase(tokenCode)) {
					url = new URL(endUrl);
					HttpURLConnection serviceConn = (HttpURLConnection) url.openConnection();
					serviceConn.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON_VALUE);
					serviceConn.setRequestProperty("Accept", MediaType.APPLICATION_JSON_VALUE);
					serviceConn.setRequestProperty("SOAPAction", soapAction);
					serviceConn.setRequestProperty("Authorization", "Bearer " + tokenCode);
					serviceConn.setConnectTimeout(60000);
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
					outputString = IOUtils.toString(reader);
					log.info("response from ITMBO ::" + outputString);
					outputString = outputString + "~" + serviceConn.getResponseCode() + "~" + endUrl;
					log.info("final response ::" + outputString);
					//
				}
				return outputString;
			} catch (Exception e) {
				log.error("ErrorCode : " + ErrorCodes.CECC0006 + " : getTokenFromMboClient Exception{}", e);
			}
		}
		return null;
	}

	public void getManageRollbackClient(String requestJson, String transId, String responseId) {
		log.debug("Inside getManageRollbackClient:::");
		String request = null;
		String outputString = CommonConstants.EMPTYSTRING;
		String operationName = "ManageRollBackWF";
		String serviceName = "ManageAccountSubscriber";
		HttpURLConnection conn = null;
		try {
			String url = apolloNEServiceProperties.getRouterserviceurl();
			if (requestJson.contains("&")) {
				requestJson = requestJson.replaceAll("&", "u+00000026");
			}
			log.debug("getManageActionFromClient in RetrieveCatalogDetails - requestJson::" + requestJson
					+ "- transId ::" + transId + " - responseId::" + responseId);
			request = "json=" + requestJson + "&serviceName=" + serviceName + "&operationName=" + operationName
					+ "&transId=" + responseId + "&responseId=" + responseId + "&ruleParam=suspendSubscriber";
			URL serviceUrl = new URL(url);
			conn = (HttpURLConnection) serviceUrl.openConnection();
			byte[] requestData = request.getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("charset", "utf-8");
			conn.setRequestProperty("Content-Length", Integer.toString(requestData.length));
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("POST");
			conn.setUseCaches(false);
			OutputStream os = conn.getOutputStream();
			os.write(requestData);
			InputStreamReader isr = new InputStreamReader(conn.getInputStream());
			BufferedReader in = new BufferedReader(isr);
			String responseString = null;
			while ((responseString = in.readLine()) != null) {
				outputString = outputString + responseString;
			}
			isr.close();
			os.close();
			log.debug("outputString getManageRollbackClient::" + outputString);
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Exception in getManageRollbackClient::", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return;
	}

	private String promotionSufix(String startDate) {
		String result = "";
		if (startDate.contains("-")) {
			String bpStartDate[] = startDate.split("-");
			if (bpStartDate != null && !"".equals(bpStartDate)) {
				String bpYear = bpStartDate[0];
				log.debug("year::" + bpYear);
				String bpMonth = bpStartDate[1];
				log.debug("month::" + bpMonth);
				String beforeDate = bpStartDate[2];
				log.debug("beforeDate::" + beforeDate);
				String dateArray[] = beforeDate.split("T");
				String bpDate = dateArray[0];
				log.debug("date::" + bpDate);
				String beforeTime = dateArray[1];
				log.debug("beforeTime::" + beforeTime);
				String timeArray[] = beforeTime.split(":");
				String bpHour = timeArray[0];
				log.debug("bpHour::" + bpHour);
				String bpMinute = timeArray[1];
				log.debug("bpMinute::" + bpMinute);
				String bpSecond = timeArray[2];
				bpSecond = bpSecond.replaceAll("[Z]", "");
				log.debug("bpSecond::" + bpSecond);
				result = bpMonth + bpDate + bpYear + bpHour + bpMinute + bpSecond;
			}
		}

		return result;
	}

	public String getDataLimitValues(String wholesalePlanCode, String retailPlancode) {
		String dataLimitValues = "";
		if (!retailPlancode.equalsIgnoreCase("") && !wholesalePlanCode.equalsIgnoreCase("")) {
			List<Map<String, Object>> res = null;
			res = rcsDao.getThrottleDetails(retailPlancode, wholesalePlanCode);
			String Throttle_LTE = "";
			String Throttle_MHS = "";
			String data_LTE = "";
			String data_LTE_Units = "";
			String data_MHS = "";
			String data_MHS_Units = "";
			log.info("response res :: " + res);
			if (res != null && res.size() > 0) {
				for (int i = 0; i < res.size(); i++) {
					Throttle_LTE = (String) res.get(i).get("THROTTLE_LIMIT_LTE");
					Throttle_MHS = (String) res.get(i).get("THROTTLE_LIMIT_MHS");
				}
				StringBuffer alpha = new StringBuffer(), num = new StringBuffer();
				for (int j = 0; j < Throttle_LTE.length(); j++) {
					if (Character.isDigit(Throttle_LTE.charAt(j)))
						num.append(Throttle_LTE.charAt(j));
					else {
						alpha.append(Throttle_LTE.charAt(j));
					}
				}
				data_LTE = num.toString();
				data_LTE_Units = alpha.toString();
				StringBuffer alpha1 = new StringBuffer(), num1 = new StringBuffer();
				for (int j = 0; j < Throttle_MHS.length(); j++) {
					if (Character.isDigit(Throttle_MHS.charAt(j)))
						num1.append(Throttle_MHS.charAt(j));
					else
						alpha1.append(Throttle_MHS.charAt(j));
				}
				data_MHS = num1.toString();
				data_MHS_Units = alpha1.toString();
				log.info("data_LTE :: " + data_LTE + "data_MHS ::" + data_MHS);
				dataLimitValues = data_LTE + "~" + data_MHS;
			}
		}
		return dataLimitValues;
	}

	public String getNewDeviceId(String request) {
		log.info("getNewDeviceId request::" + request);
		String imei = "";
		try {
			if (request.startsWith("{")) {
				JSONObject initReq = new JSONObject(request);
				JSONArray suborderRetail = new JSONArray();
				if (initReq.has("data")) {
					JSONObject datanewObj = initReq.getJSONObject("data");
					if (datanewObj.has("transactionType")) {
						String transactionType = datanewObj.getString("transactionType");
						if (transactionType.equals("DE") || transactionType.equals("DS")) {
							if (datanewObj.has("subOrder")) {
								suborderRetail = datanewObj.getJSONArray("subOrder");
								for (int j = 0; j < suborderRetail.length(); j++) {
									datanewObj = suborderRetail.getJSONObject(j);
									if (datanewObj.has("deviceId")) {
										if (datanewObj.getJSONArray("deviceId").length() > 0) {
											JSONArray deviceIdArr = datanewObj.getJSONArray("deviceId");
											for (int i = 0; i < deviceIdArr.length(); i++) {
												JSONObject deviceidObj = deviceIdArr.getJSONObject(i);
												if (deviceidObj.has("type")) {
													String deviceidType = deviceidObj.getString("type");
													if (deviceidType.equals("newIMEI")) {
														imei = deviceidObj.getString("value");
														log.info("IMEI_VALUE::" + imei);
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0007+" : Exception{}", e);
		}
		return imei;
	}
	
	public String getActivationLineResponseFromClient(String requestJson, String transId, String responseId,
			String transactionName) {
		log.info(" Inside getActivationLineResponseFromClient transactionName:: " + transactionName+" transId:: " + transId +" responseId:: "+responseId);
		String request = null;
		String outputString = "";
		String operationName = "";
		String serviceName = "";
		HttpURLConnection conn = null;
		try {
			String url = apolloNEServiceProperties.getRouterserviceurl();
			if (transactionName.equals(CommonConstants.SUSPEND_SUBSCRIBER) || transactionName.equals(CommonConstants.HOTLINE_SUBSCRIBER) || transactionName.equals(CommonConstants.RESTORE_SERVICE)) {
				operationName = "UpdateSubscriberStatusWF";
				serviceName = "UpdateSubscriberStatus";
			} else if (transactionName.equals(CommonConstants.REMOVE_HOTLINE)) {
				operationName = "UpdateSubscriberStatusRestoreWF";
				serviceName = "UpdateSubscriberStatus";
			}
			if (requestJson.contains("&")) {
				requestJson = requestJson.replaceAll("&", "u+00000026");
			}

			log.debug("getActivationLineResponseFromClient in ReqDetails::" + requestJson+"url::" + url);
			request = "json=" + requestJson + "&serviceName=" + serviceName + "&operationName=" + operationName
					+ "&transId=" + responseId + "&responseId=" + responseId + "&ruleParam=connectionManagerValidation";
			log.info("request in getActivationLineResponseFromClient::" + request);
			URL serviceUrl = new URL(url);
			conn = (HttpURLConnection) serviceUrl.openConnection();
			byte[] requestData = request.getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("charset", "utf-8");
			conn.setRequestProperty("Content-Length", Integer.toString(requestData.length));
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("POST");
			conn.setUseCaches(false);
			OutputStream os = conn.getOutputStream();
			os.write(requestData);
			InputStreamReader isr = new InputStreamReader(conn.getInputStream());
			BufferedReader in = new BufferedReader(isr);
			String responseString = null;
			while ((responseString = in.readLine()) != null) {
				outputString = outputString + responseString;
			}
			isr.close();
			os.close();
			return outputString;
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Exception in getActivationLineResponseFromClient{}" + e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return null;
	}

	
	public String getChangeMdnCPClientCall(String requestJson, String transId, String responseId,String requestType) {
		String request = null;
		String outputString = "";
		String serviceName = "";
		String operationName = "";
		serviceName = "Change-MDN";
		operationName = "portInSyncFailureFlow_AP";
		JSONObject requestJsonObject = null;
		HttpURLConnection conn = null;
		try {
			String url = apolloNEServiceProperties.getRouterserviceurl();
			log.info("url::::" + url);
			log.debug(
					"getChangeMdnCPClientCall Inside " + requestJson + "transId" + transId + "responseId" + responseId);
			String nextAvailMdnZip = "";
			log.debug("formattedJson after replace reconnect::" + requestJson);
			if (requestJson.contains("&")) {
				requestJson = requestJson.replaceAll("&", "u+00000026");
			}
			request = "json=" + requestJson + "&serviceName=" + serviceName + "&operationName=" + operationName
					+ "&transId=" + responseId + "&responseId=" + responseId + "&ruleParam=validateDevice4Activation";
			log.debug("request::" + request + "url::" + url);
			URL serviceUrl = new URL(url);
			conn = (HttpURLConnection) serviceUrl.openConnection();
			byte[] requestData = request.getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("charset", "utf-8");
			conn.setRequestProperty("Content-Length", Integer.toString(requestData.length));
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("POST");
			conn.setUseCaches(false);
			OutputStream os = conn.getOutputStream();
			os.write(requestData);
			conn.connect();
			log.debug("code:: " + conn.getResponseCode() + "Inside http url connection url::" + url);
			InputStreamReader isr = new InputStreamReader(conn.getInputStream());
			BufferedReader in = new BufferedReader(isr);
			String responseString = null;
			while ((responseString = in.readLine()) != null) {
				outputString = outputString + responseString;
			}
			log.debug("outputString in getChangeMdnCPClientCall::" + outputString);
			isr.close();
			os.close();
			return outputString;
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Exception in getChangeMdnCPClientCall{}", e);

		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return null;
	}
	
	
	public String retrieveDeviceIdFromArray(String request) {
		JSONArray deviceArr = new JSONArray();
		JSONArray newDeviceArr = new JSONArray();
		JSONArray suborderArray = new JSONArray();
		JSONObject deviceObject = new JSONObject();

		try {
			if (request != null && !"".equalsIgnoreCase(request)) {
				if (request.startsWith("{")) {
					JSONObject ipObject = new JSONObject(request);
					if (ipObject.has("data")) {
						deviceObject = ipObject.getJSONObject("data");
						suborderArray = deviceObject.getJSONArray("subOrder");
						deviceArr = suborderArray.getJSONObject(0).getJSONArray("deviceId");
						for (int a = 0; a < deviceArr.length(); a++) {
							JSONObject newDeviceObject = new JSONObject();
							if (deviceArr.getJSONObject(a).has("type")) {
								if (deviceArr.getJSONObject(a).getString("type").equalsIgnoreCase("IMEI")) {
									newDeviceObject.put("type", deviceArr.getJSONObject(a).getString("type"));
									newDeviceObject.put("value", deviceArr.getJSONObject(a).getString("value"));
									newDeviceArr.put(newDeviceObject);
								}
							}
						}

						request = ipObject.toString();
						if (newDeviceArr.length() > 0) {
							request = request.replace(deviceArr.toString(), newDeviceArr.toString());
							log.info("request :::" + request);
						}
					}
				}
			}
		} catch (JSONException e) {
			log.error("retrieveDeviceId1 :::", e);
		}
		return request;
	}
	
	public void swapLineIdforTransferDevice(String transactionId){
		ResourceUpdateRequest resourceUpdateRequest = new ResourceUpdateRequest();
		String newLineId = "";
		String newMdn = "";
		String newsimId= "";
		String newdeviceId= "";
		String oldLineId = "";
		String oldMdn = "";
		String oldsimId= "";
		String olddeviceId= "";
		String channel="";
		String transactionTimeStamp= "";
		JsonArray subOrderArr =new JsonArray();
		JsonArray mdnArr = new JsonArray();
		JsonArray deviceArr = new JsonArray();
		JsonArray simIdArr = new JsonArray();
		JsonObject subOrderObj =new JsonObject();
		JsonObject deviceObj =new JsonObject();
		JsonObject simObj =new JsonObject();
		JsonObject mdnObj =new JsonObject();
		String syncTransaction ="";
		try{
			String inboundRequest = rcsDao.getRequest(transactionId);
			JsonObject inboundObj = new JsonParser().parse(inboundRequest).getAsJsonObject();
			JsonObject messageObj=  new JsonObject();			
			JsonObject reqObj = new JsonObject();
			JsonObject dataObj=  new JsonObject();
			if(inboundObj.has("messageHeader")){
				messageObj=inboundObj.get("messageHeader").getAsJsonObject();
				reqObj.add("messageHeader",messageObj);
			}
			if(inboundObj.has("data")){
				inboundObj=inboundObj.get("data").getAsJsonObject();
				if(inboundObj.has("subOrder")){
					JsonObject subOrderArrobj = inboundObj.getAsJsonArray("subOrder").get(0).getAsJsonObject();
					if(subOrderArrobj.has("newLineId")){
						JsonObject lineIdObj=subOrderArrobj.get("newLineId").getAsJsonObject();
						if(lineIdObj.has("mdn")){
							JsonObject mdnArrObj = lineIdObj.getAsJsonArray("mdn").get(0).getAsJsonObject();
							if(mdnArrObj.has("value")){
								newMdn=mdnArrObj.get("value").getAsString();
								resourceUpdateRequest.setNewMdn(newMdn);
							}
						}
						if(lineIdObj.has("lineId")){
							newLineId=lineIdObj.get("lineId").getAsString();
							resourceUpdateRequest.setNewLineId(newLineId);
						}
						if(lineIdObj.has("deviceId")){
							JsonObject deviceIdObj = lineIdObj.getAsJsonArray("deviceId").get(0).getAsJsonObject();
							if(deviceIdObj.has("value")){
								newdeviceId=deviceIdObj.get("value").getAsString();
							}
						}
						if(lineIdObj.has("simId")){
							JsonObject simIdObj = lineIdObj.getAsJsonArray("simId").get(0).getAsJsonObject();
							if(simIdObj.has("value")){
								newsimId=simIdObj.get("value").getAsString();
							}
						}
					}
					if(subOrderArrobj.has("oldLineId")){
						JsonObject lineIdObj=subOrderArrobj.get("oldLineId").getAsJsonObject();
						if(lineIdObj.has("mdn")){
							JsonObject mdnArrObj = lineIdObj.getAsJsonArray("mdn").get(0).getAsJsonObject();
							if(mdnArrObj.has("value")){
								oldMdn=mdnArrObj.get("value").getAsString();
								resourceUpdateRequest.setOldMdn(oldMdn);
							}
						}
						if(lineIdObj.has("lineId")){
							oldLineId=lineIdObj.get("lineId").getAsString();
							resourceUpdateRequest.setOldLineId(oldLineId);
						}
						if(lineIdObj.has("deviceId")){
							JsonObject deviceIdObj = lineIdObj.getAsJsonArray("deviceId").get(0).getAsJsonObject();
							if(deviceIdObj.has("value")){
								olddeviceId=deviceIdObj.get("value").getAsString();
							}
						}
						if(lineIdObj.has("simId")){
							JsonObject simIdObj = lineIdObj.getAsJsonArray("simId").get(0).getAsJsonObject();
							if(simIdObj.has("value")){
								oldsimId=simIdObj.get("value").getAsString();
							}
						}
					}
				}
			}
			apolloNEOutboundClientService.callSwapLineIdforTransferDevice(resourceUpdateRequest);
			inboundObj.remove("transactionType");
			inboundObj.addProperty("transactionType","MC");
			inboundObj.remove("subOrder");
			inboundObj.addProperty("id",newLineId);
			inboundObj.addProperty("lineId",newLineId);
			mdnObj.addProperty("type","oldMDN");
			mdnObj.addProperty("value",newMdn);
			mdnArr.add(mdnObj);
			deviceObj.addProperty("type","IMEI");
			deviceObj.addProperty("value",olddeviceId);
			deviceArr.add(deviceObj);
			simObj.addProperty("type","ICCID");
			simObj.addProperty("value",oldsimId);
			simIdArr.add(simObj);
			inboundObj.add("mdn",mdnArr);
			inboundObj.add("deviceId",deviceArr);
			inboundObj.add("simId",simIdArr);
			reqObj.add("data",inboundObj);
			String changeMdnUpdateRequest=reqObj.toString();
			if (inboundObj.has("syncTransaction")) {
				syncTransaction = inboundObj.get("syncTransaction").getAsString();
				log.info("syncTransaction:::" + syncTransaction);
				if (syncTransaction.equalsIgnoreCase("")) {
					syncTransaction = "F";
				}
			} else {
				syncTransaction = "F";
			}
			if (!syncTransaction.contains("F")) {
				log.info("Inside Transfer-Device NBOP::" + changeMdnUpdateRequest);
				final String requestJson = changeMdnUpdateRequest;
				final String transaction_Id = transactionId;
				final String ServiceName = "TransferDeviceChangeMDN";
				final String OperationName = "changemdnupdateworkflow";
				ExecutorService executor = Executors.newSingleThreadExecutor();
				executor.execute(new Runnable() {
					@Override
					public void run() {
						getSwapMdnFromClient(requestJson, transaction_Id,
								transaction_Id,"", OperationName,ServiceName);
					}
				});
				executor.shutdown();
			} else {
				log.info("Inside Transfer-Device ::" + changeMdnUpdateRequest);
				final String requestJson = changeMdnUpdateRequest;
				final String transaction_Id = transactionId;
				final String ServiceName = "TransferDeviceChangeMDN";
				final String OperationName = "SyncTransCMWF";
				ExecutorService executor = Executors.newSingleThreadExecutor();
				executor.execute(new Runnable() {
					@Override
					public void run() {
						getSwapMdnFromClient(requestJson, transaction_Id,
								transaction_Id,"", OperationName,ServiceName);
					}
				});
				executor.shutdown();
			}
		}catch (Exception e) {
			log.error("Exception::" + e.getMessage());
		}
	}
	
	public Message apolloNeSounthBoundCall(Message message) {
		log.debug("apolloNeSounthBoundCall::" + apolloNEQueueProperties.getApolloNeOutboundServiceQueue());
		Message result = null;
		try {
			result = initiateApolloNEOutbound(message);
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0004+" : Exception ::", e);
		}
		return result;
	}
}
