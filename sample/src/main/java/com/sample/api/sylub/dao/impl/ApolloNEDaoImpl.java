package com.excelacom.century.apolloneoutbound.dao.impl;

import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Types;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.excelacom.century.apolloneoutbound.bean.Device;
import com.excelacom.century.apolloneoutbound.bean.ErrorCodes;
import com.excelacom.century.apolloneoutbound.bean.Line;
import com.excelacom.century.apolloneoutbound.bean.LineHistory;
import com.excelacom.century.apolloneoutbound.bean.OutboundRequest;
import com.excelacom.century.apolloneoutbound.bean.ProcessMetadata;
import com.excelacom.century.apolloneoutbound.bean.RequestBean;
import com.excelacom.century.apolloneoutbound.bean.ResourceUpdateRequest;
import com.excelacom.century.apolloneoutbound.bean.SendClientRequest;
import com.excelacom.century.apolloneoutbound.bean.TransactionHistory;
import com.excelacom.century.apolloneoutbound.dao.RcsDao;
import com.excelacom.century.apolloneoutbound.dao.respository.ServiceInfoRepository;
import com.excelacom.century.apolloneoutbound.dao.respository.TransactionRepository;
import com.excelacom.century.apolloneoutbound.entity.ServiceInfo;
import com.excelacom.century.apolloneoutbound.entity.TransactionDetails;
import com.excelacom.century.apolloneoutbound.service.ApolloNEOutboundClientService;
import com.excelacom.century.apolloneoutbound.utils.constants.ApolloNEConstants;
import com.excelacom.century.apolloneoutbound.utils.constants.CommonConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jayway.jsonpath.JsonPath;
import com.excelacom.century.apolloneoutbound.bean.StgPlanMigration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class ApolloNEDaoImpl implements RcsDao {

	@Autowired
	private ServiceInfoRepository serviceInfo;

	@Autowired
	private TransactionRepository transRepo;

	@Autowired
	private JdbcTemplate centuryCIFTemplate;

	
	@Autowired
	ApolloNEOutboundClientService apolloNEOutboundClientService;

	@Override
	public String getEndpointUrl(String serviceName, String server) {
		Optional<String> dbResp = serviceInfo.getEndpointUrl(serviceName, server);
		return dbResp.isPresent() ? dbResp.get() : "";
	}

	@Override
	public String getEndpointUrl2(String serviceName, String server) {
		Optional<String> dbResp = serviceInfo.getEndpointUrl2(serviceName, server);
		return dbResp.isPresent() ? dbResp.get() : "";
	}

	@Override
	public String getServiceUrl(String serviceName, String server) {
		Optional<String> dbResp = serviceInfo.getServiceUrl(serviceName, server);
		return dbResp.isPresent() ? dbResp.get() : "";
	}

	@Override
	// @Async(value = "threadPoolTaskExecutor")
	public void insertSouthBoundTransaction(OutboundRequest outReqBean) {
		log.info("Inside async method of insertrSBTransaction::" + outReqBean);
		String outboungReq="";
		try {
			if (outReqBean.getOperationName() != null
					&& (outReqBean.getOperationName().equalsIgnoreCase("Activate Subscriber Port-in")
							|| outReqBean.getOperationName().equalsIgnoreCase("ESIM Activate Subscriber Port-in")
							|| outReqBean.getOperationName().equalsIgnoreCase("Update Customer Information"))) {
				String formattedRequest = lnpInformationDetailsMask(outReqBean.getRequestJson(),
						outReqBean.getOperationName());
				outReqBean.setRequestJson(formattedRequest);
			}
			if (outReqBean.getOperationName() != null
					&& outReqBean.getOperationName().equalsIgnoreCase("ESIM Activate Subscriber Port-in")) {
				if (outReqBean.getResponseId() != null) {
					requestlnpInformationDetailsMask(outReqBean.getResponseId());
				}
			}if(outReqBean.getOperationName() != null && outReqBean.getOperationName().equalsIgnoreCase(CommonConstants.UPDATE_SHARED_NAME)) {
				outboungReq=maskingSharedName(outReqBean.getRequestJson(),CommonConstants.UPDATE_SHARED_NAME);
			}else {
				outboungReq=outReqBean.getRequestJson();
			}
			TransactionDetails td = TransactionDetails.builder().transactionName(outReqBean.getOperationName())
					.applicationName(outReqBean.getApplicationName()).transactionType(ApolloNEConstants.OUTBOUND)
					.status(ApolloNEConstants.INITIATED).reqSentDate(ZonedDateTime.now())
					.createdDate(ZonedDateTime.now()).createdBy(ApolloNEConstants.NSL)
					.requestMsg(outboungReq)
					.entityId(StringUtils.hasText(outReqBean.getEntityId()) ? outReqBean.getEntityId() : "0")
					.transactionUid(outReqBean.getTransUid()).tenantId(outReqBean.getTenantId())
					.rootTransactionId(Long.parseLong(outReqBean.getResponseId()))
					.sourceSystem(outReqBean.getSourceSystem()).relTransactionId(outReqBean.getResponseId())
					.transactionId(outReqBean.getTransId())
					.serviceName(outReqBean.getProcessPlanName())
					.workflowName(outReqBean.getWorkflowName()).build();
			transRepo.save(td);
			log.debug("Response from db::" + td.getTransactionId());

		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0008+" : Error in insertrSBTransaction{}", e);
		}
	}

	@Override
	//@Async(value = "threadPoolTaskExecutor")
	public void updateSouthBoundTransaction(String transId, String entityId, String groupId, String response,
			String statusCode, String target, String responseId, String queueStatus,String operationName, String clientEndUrl,SendClientRequest sendClientRequest) {
		log.info("Response from updateSouthBoundTransaction - transId::" + transId + " - entityId::" + entityId
				+ "-groupId::" + groupId + "-response::" + response + "-statusCode::" + statusCode + "-target::"
				+ target + "-responseId::" + responseId + "-queueStatus::" + queueStatus +"-operationName::" + operationName);
		log.debug("Inside updateSouthBoundTransaction::");
		log.debug("SendClientRequest bean::" + sendClientRequest);
		String iccid="";
		String transacId = "";
		String altZipCode = null;
		transacId = centuryCIFTemplate.queryForObject(ApolloNEConstants.GET_TRANSACTION_ID,
				new Object[] { transId }, String.class);
		
		try {
			
			if (operationName != null && (operationName.equalsIgnoreCase(CommonConstants.ACTIVATESUBSCRIBER)
					|| operationName.equalsIgnoreCase(CommonConstants.ADD_WEARABLE)
					|| operationName.equalsIgnoreCase(CommonConstants.CHANGE_MDN_CM)
					|| operationName.equalsIgnoreCase("Activate Subscriber PSIM")
					|| operationName.equalsIgnoreCase("Activate Subscriber ESIM")
					|| operationName.equalsIgnoreCase(CommonConstants.ESIM_ACTIVATESUBSCRIBER))) {
				if (sendClientRequest.getOutReqBean() != null) {
					log.debug("SendClientRequest getOutReqBean::" + sendClientRequest.getOutReqBean().toString());
					if (sendClientRequest.getOutReqBean().getRequestJson() != null) {
						String oBrequestJson = sendClientRequest.getOutReqBean().getRequestJson().toString();
						oBrequestJson = jsonFormatter(oBrequestJson);
						log.debug("SendClientRequest oBrequestJson::" + oBrequestJson);
						if (oBrequestJson.startsWith("[")) {
							JSONArray jsonarr = new JSONArray(oBrequestJson);
							JSONObject obJson = jsonarr.getJSONObject(0);
							log.debug("SendClientRequest obJson::" + obJson);
							if (obJson.has("zipCode")) {
								altZipCode = obJson.getString("zipCode");
							}
						}
						/*
						 * Gson respGson = new Gson(); RequestBean requestBean =
						 * respGson.fromJson(oBrequestJson, RequestBean.class);
						 * log.debug("SendClientRequest requestBean::" + requestBean); if
						 * (requestBean.getZipCode() != null) {
						 * log.debug("SendClientRequest altZipCode::" + requestBean.getZipCode());
						 * altZipCode = requestBean.getZipCode(); }
						 */
					}
				}
			}
			else if(operationName.equalsIgnoreCase(CommonConstants.CHANGE_SIM)) {
				
				
				String rootId=centuryCIFTemplate.queryForObject(ApolloNEConstants.GET_ROOOT_ID,
							  new Object[] { transId }, String.class);
				if("TEMP SIM Reserved".equalsIgnoreCase(getTransactionMileStoneFromMetadata(rootId)) && "Apollo-NE".equalsIgnoreCase(target)) {
					ProcessMetadata processMetadata=new ProcessMetadata();
					processMetadata.setRootTransactionId(rootId);
					ProcessMetadata[] processMetaData=apolloNEOutboundClientService.getProcessMetadata(processMetadata);
					iccid=new JSONObject(processMetaData[processMetaData.length-1].getProcessData()).getString("iccid"); 
				}
				
			}
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0008+" : Exception --{}", e);
		}
		
		
		String status = CommonConstants.FAILURE;
        if (!StringUtils.hasText(clientEndUrl)) {
			clientEndUrl = "";
        }
		//String httpCode = null;
		
		String responseCode = "S0000";
		

		try {
			if (StringUtils.hasText(response)) {

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

				log.debug("before status value::" + response);
				try {
					JsonObject responseJsonData = new JsonParser().parse(response).getAsJsonObject();
					log.debug("responseJsonData::" + responseJsonData);
					if (responseJsonData.has("data")) {
						if (responseJsonData.get("data").getAsJsonObject().has("message")) {
							responseJsonData = responseJsonData.get("data").getAsJsonObject().get("message")
									.getAsJsonArray().get(0).getAsJsonObject();
							log.debug("responseJsonData Message::" + responseJsonData);
							if (responseJsonData.has("responseCode")) {
								responseCode = responseJsonData.get("responseCode").getAsString();
							}
						}
					}
				} catch (Exception e) {
					log.error("ErrorCode : "+ErrorCodes.CECC0007+" : Exception --{}", e);
				}
			}
			log.debug("responseCode value::" + responseCode + "statusCode::" + statusCode + "queueStatus::"
					+ queueStatus);
			if (StringUtils.hasText(response) && StringUtils.hasText(operationName)
					&& operationName.equals(CommonConstants.VALIDATE_DEVICE)) {
				String rootId=centuryCIFTemplate.queryForObject(ApolloNEConstants.GET_ROOOT_ID,
						  new Object[] { transId }, String.class);
				log.info("Device Info rootId::" + rootId);
				String serviceName=centuryCIFTemplate.queryForObject(ApolloNEConstants.GET_SERVICE_NAME,
						  new Object[] { rootId }, String.class);
				log.info("Device Info serviceName::" + serviceName);
				if(serviceName.equalsIgnoreCase("Add-Wearable")
						|| serviceName.equalsIgnoreCase("ChangeESim")) {
					String ibRequest=centuryCIFTemplate.queryForObject(ApolloNEConstants.GET_REQUEST,
							  new Object[] { rootId }, String.class);
					String lineId = CommonConstants.EMPTYSTRING;
					String make = CommonConstants.EMPTYSTRING;
					String model = CommonConstants.EMPTYSTRING;
					String mode = CommonConstants.EMPTYSTRING;
					String os = CommonConstants.EMPTYSTRING;
					String cdmaLess = CommonConstants.EMPTYSTRING;
					String type = CommonConstants.EMPTYSTRING;
					Device device = new Device();
					if (ibRequest != null && !ibRequest.isEmpty()) {
						log.info("Device Info ibRequest::" + ibRequest);
						JSONObject jsonObj = new JSONObject(ibRequest);
						JSONObject dataObj = new JSONObject();
						JSONArray jsonarr = new JSONArray();
						if (jsonObj.has("data")) {
							dataObj = jsonObj.getJSONObject("data");
							if (dataObj.has("subOrder")) {
								jsonarr = dataObj.getJSONArray("subOrder");
								dataObj = jsonarr.getJSONObject(0);
								if (dataObj.has("lineId")) {
									lineId = dataObj.getString("lineId");
								}
							}
						}
					}
					log.info("Device Info lineId::" + lineId);
					JSONObject respObj = new JSONObject(response);
					log.info("Device Info respObj::" + respObj);
					JSONObject respDataObj = new JSONObject();
					JSONArray equipmentInfoArr = new JSONArray();
					JSONObject equipmentInfoObj = new JSONObject();
					if(respObj.has("data")) {
						respDataObj = respObj.getJSONObject("data");
						if(respDataObj.has("cdmaLess")) {
							cdmaLess = respDataObj.getString("cdmaLess");
						}
						if(respDataObj.has("equipmentInfo")) {
							equipmentInfoArr = respDataObj.getJSONArray("equipmentInfo");
							for(int i=0;i<equipmentInfoArr.length();i++) {
								equipmentInfoObj = equipmentInfoArr.getJSONObject(i);
								if(equipmentInfoObj.has("type")) {
									type = equipmentInfoObj.getString("type");
								}
								if(type.equalsIgnoreCase("make")) {
									make = equipmentInfoObj.getString("value");
								}
								if(type.equalsIgnoreCase("model")) {
									model = equipmentInfoObj.getString("value");
								}
								if(type.equalsIgnoreCase("mode")) {
									mode = equipmentInfoObj.getString("value");
								}
							}
							if(make!=null && !make.isEmpty()) {
								if(make.equalsIgnoreCase("Apple") || make.equalsIgnoreCase("APL")){
									os = "WATCHOS";
								}
							}
							log.info("Device Info make::" + make + " ::model::" + model + " ::mode::" + mode + " ::cdmaLess" + cdmaLess + " ::os" + os);
							device.setCdmaLess(cdmaLess);
							device.setMake(make);
							device.setModel(model);
							device.setOs(os);
							device.setModeValue(mode);
							device.seteLineId(lineId);
							apolloNEOutboundClientService.callUpdateDeviceInfoResource(device);
						}
					}
				}
			}
			int count=0;
			if (StringUtils.hasText(response) && StringUtils.hasText(operationName)
					&& operationName.equals(CommonConstants.QUERY_SHARED_NAME)) {
				log.debug("Inside QUERY_SHARED_NAME::" + response);
				response = maskingSharedName(response,operationName);
				log.debug("After QUERY_SHARED_NAME Masking::" + response);
			}			
			if (StringUtils.hasText(statusCode) && statusCode.startsWith("2") && responseCode != null
					&& !responseCode.startsWith("E") && !StringUtils.hasText(queueStatus) && !responseCode.equalsIgnoreCase("INVALID")
					&& !responseCode.equalsIgnoreCase("NOTFOUND")) {
				status = CommonConstants.SUCCESS;
				count=transRepo.updateTransactionDetails(status, ZonedDateTime.now(), response, statusCode, entityId,
					groupId, transId, target);
			} else if (responseCode!=null && (responseCode.equals("E2601") || responseCode.equals("E2302") || responseCode.equals("E2301")
					|| responseCode.equals("E1034")))  {
				status = CommonConstants.SUCCESS;
				count=transRepo.updateTransactionDetails(status, ZonedDateTime.now(), response, statusCode, entityId,
					groupId, transId, target);
			} else if (queueStatus.equalsIgnoreCase(CommonConstants.QUEUED)) {
			
				status = CommonConstants.QUEUED;
				count=transRepo.updateTransactionDetails(status, ZonedDateTime.now(), response, statusCode, entityId,
					groupId, transId, target);
			} else {
				count=transRepo.updateTransactionDetails(status, ZonedDateTime.now(), response, statusCode, entityId,
					groupId, transId, target);
				insertTransFailureLog(transId, response);
			}
			log.debug("transUId::" + transId + "status::" + status);
			
			/*if (operationName != null && (operationName.equalsIgnoreCase("Change Wholesale Rate Plan")||operationName.equals("Change Feature"))&& (status.equalsIgnoreCase(CommonConstants.SUCCESS)|| status.equalsIgnoreCase(CommonConstants.QUEUED))){
				String rootId=centuryCIFTemplate.queryForObject(ApolloNEConstants.GET_ROOOT_ID,
						  new Object[] { transId }, String.class);
				final String stgStatus = status;
				ExecutorService executor = Executors.newSingleThreadExecutor();
				executor.execute(new Runnable() {
					@Override
					public void run() {
						updateStgPlanMigration(rootId,stgStatus,"","");

					}
				});
			}else if (operationName != null && (operationName.equalsIgnoreCase(CommonConstants.SUSPEND_SUBSCRIBER)
							|| operationName.equalsIgnoreCase(CommonConstants.HOTLINE_SUBSCRIBER))){
					String rootId=centuryCIFTemplate.queryForObject(ApolloNEConstants.GET_ROOOT_ID,
						  new Object[] { transId }, String.class);			
					final String stgStatus = status;
					final String stgRootId =rootId;
					ExecutorService executor = Executors.newSingleThreadExecutor();
					executor.execute(new Runnable() {
						@Override
						public void run() {
							updateStgPlanMigrationRollBackStatus(stgRootId,stgStatus);

						}
					});
				}*/
			log.info("transacId ::" + transacId);
			// responseId=centuryCIFTemplate.queryForObject(ApolloNEConstants.GET_ROOT_TRANSACTION_ID,
			// new Object[] { transId },
			// String.class);
			insertTransactionMetadata(transacId, clientEndUrl, responseId, altZipCode,iccid);
			
			log.debug("Response from db::", count);
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0008+" : Error in updateSouthBoundTransaction{}", e);
		}
	}

	@Override
	public ServiceInfo getServiceInfoDetails(String serviceName, String server) {
		Optional<ServiceInfo> serviceInfoOpt = serviceInfo.findByServiceNameAndServer(serviceName, server);
		return serviceInfoOpt.isPresent() ? serviceInfoOpt.get() : null;
	}

	public void insertTransactionMetadata(String transId, String endpointURL, String responseId, String altZipCode, String iccid) {
		log.info("request inside insertTransactionMetadata:: - transId::" + transId + "-endpointURL::" + endpointURL
				+ "-responseId::" + responseId + "altZipCode:" +altZipCode+"ICCID::"+iccid);
		try {
			MapSqlParameterSource input = new MapSqlParameterSource();
			input.addValue("TRANSACTION_ID", transId);
			input.addValue("ROOT_TRANSACTION_ID", responseId);
			input.addValue("ALT_ZIPCODE", altZipCode);
			input.addValue("EXTERNAL_VALUE_4", iccid);
			if (endpointURL != null && !endpointURL.equalsIgnoreCase("")) {
				input.addValue("SERVICE_URL", endpointURL);
			} else {
				input.addValue("SERVICE_URL", null);
			}
			NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(
					centuryCIFTemplate.getDataSource());
			namedJdbcTemplate.update(CommonConstants.INSERT_REQUEST_DETAILS, input);
			log.info("After inserting Reference Data in insertTransactionMetadata::" + responseId);
		} catch (Exception e1) {
			log.error("ErrorCode : "+ErrorCodes.CECC0008+" : Exception - {}", e1);
		}
	}

	public void insertTransFailureLog(String transId, String responseMsg) {
		log.info("Inside insertTransFailureLog transId::" + transId + "responseMsg::" + responseMsg);
		String errorCode = "";
		String errorMessage = "";
		String referenceNumber = "";
		NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(
				centuryCIFTemplate.getDataSource());
		String rootId = (String) centuryCIFTemplate.queryForObject(ApolloNEConstants.GET_ROOOT_ID,
				new Object[] { transId }, String.class);
		try {
			if (responseMsg != null) {
				if (responseMsg.startsWith("{")) {
					JSONObject respObj = new JSONObject(responseMsg);
					log.info("respObjjjj:::" + respObj);
					if (respObj.has("errorCode")) {
						errorCode = respObj.getString("errorCode");
					} else if (respObj.has("returnCode")) {
						errorCode = respObj.getString("returnCode");
					} else if (respObj.has("responseCode")) {
						errorCode = respObj.getString("responseCode");
					} else if (respObj.has("errorcode")) {
						errorCode = respObj.getString("errorcode");
					}
					if (respObj.has("errorMessage")) {
						errorMessage = respObj.getString("errorMessage");
					} else if (respObj.has("errorDescription")) {
						errorMessage = respObj.getString("errorDescription");
					} else if (respObj.has("description")) {
						errorMessage = respObj.getString("description");
					}  else if (respObj.has("errordesc")) {
						errorMessage = respObj.getString("errordesc");
					}
					log.debug(
							"Inside insertTransFailureLog errorCode:: " + errorCode + "errorMessage:: " + errorMessage);
					if (respObj.has("data")) {
						respObj = respObj.getJSONObject("data");
						if (respObj.has("message") && respObj.get("message") instanceof JSONArray) {
							respObj = respObj.getJSONArray("message").getJSONObject(0);
						}
						if (respObj.has("errorCode")) {
							errorCode = respObj.getString("errorCode");
						} else if (respObj.has("returnCode")) {
							errorCode = respObj.getString("returnCode");
						} else if (respObj.has("responseCode")) {
							errorCode = respObj.getString("responseCode");
						}
						if (respObj.has("returnMessage")) {
							errorMessage = respObj.getString("returnMessage");
						} else if (respObj.has("errorDescription")) {
							errorMessage = respObj.getString("errorDescription");
						} else if (respObj.has("description")) {
							errorMessage = respObj.getString("description");
						}
					}
					log.debug(
							"Inside insertTransFailureLog errorCode::" + errorCode + "errorMessage:: " + errorMessage);
				}
				String transacId = (String) centuryCIFTemplate.queryForObject(ApolloNEConstants.GET_TRANSACTION_ID,
						new Object[] { transId }, String.class);
				log.debug("Inside insertTransFailureLog transacId::" + transacId);
				BigDecimal currTranscIdB;
				String currTranscId = "";
				Map<String, Object> currentTransaction = centuryCIFTemplate
						.queryForMap(ApolloNEConstants.GET_TRANSACTION_ID_OPERATION_NAME, new Object[] { transId });

				String operationName = "";
				if (currentTransaction != null && !currentTransaction.isEmpty()) {
					log.debug("Currrent Transaction" + currentTransaction.toString());
					currTranscIdB = (BigDecimal) currentTransaction.get("transaction_id");
					if (currTranscIdB != null) {
						currTranscId = currTranscIdB.toString();
					}
					operationName = (String) currentTransaction.get("TRANSACTION_NAME");
					log.debug("Currrent Transaction::transacId" + currTranscId + "operationName::" + operationName);
				}
				String serviceName = (String) centuryCIFTemplate.queryForObject(ApolloNEConstants.GET_SERVICE_NAME,
						new Object[] { transacId }, String.class);
				log.debug("serviceName::"+ serviceName);
				if (serviceName != null && serviceName.equalsIgnoreCase("Device-Detection")) {

				errorCode = "ERR05";
				errorMessage = "IMEI alignment failed";
				try {
					int deviceCount = 0;
					if (("Validate Device".equalsIgnoreCase(operationName))
							|| ("Change Feature".equalsIgnoreCase(operationName))
							|| ("Device Detection Change Rate Plan".equalsIgnoreCase(operationName))) {
						deviceCount = centuryCIFTemplate.queryForObject(ApolloNEConstants.GETDEVICEREQ_COUNT,
								new Object[] { rootId }, Integer.class);
						String deviceDetails = "";
						log.info("deviceId Count" + deviceCount);
						if (deviceCount != 0) {

							List<Map<String, Object>> deviceDetailsMap = new ArrayList<Map<String, Object>>();
							deviceDetailsMap = (List<Map<String, Object>>) centuryCIFTemplate
									.queryForList(ApolloNEConstants.GETDEVICEREQUEST, new Object[] { rootId });
							for (int i = 0; i < deviceDetailsMap.size(); i++) {
								log.info("deviceDetailsMap" + deviceDetailsMap);
								if (deviceDetailsMap.get(i).get(CommonConstants.TRANSACTION_NAME)
										.equals(CommonConstants.VALIDATE_DEVICE)) {
									deviceDetails = deviceDetailsMap.get(i).get(CommonConstants.S_RESPONSE_MSG).toString();
									break;
								}
							}
							log.debug("deviceDetails::" + deviceDetails);
							if (deviceDetails != null && (!deviceDetails.isEmpty())) {
								responseMsg = deviceDetails;
							}

						}
					}
					if (deviceCount != 0) {
						log.debug("responseMsg::" + responseMsg);
						if (responseMsg != null && responseMsg.startsWith("{")) {
							// JSONObject responseObj = new JSONObject(response);
							String requestMsg = "";
							Clob rootRequestMsg = centuryCIFTemplate.queryForObject(ApolloNEConstants.GET_ROOT_REQUEST_MSG,
									new Object[] { currTranscId }, Clob.class);
							if (rootRequestMsg != null) {
								InputStream in = rootRequestMsg.getAsciiStream();
								StringWriter w = new StringWriter();
								IOUtils.copy(in, w);
								requestMsg = w.toString();
								log.debug("root Transaction::requestMsg" + requestMsg);
							}

							log.info("serviceName::" + serviceName);

							if ("Device-Detection".equalsIgnoreCase(serviceName)) {
								JSONObject respObj = new JSONObject(requestMsg);
								String returnCode = "";
								String mdn = "";
								String imei = "";
								if (respObj.has("messageHeader")) {
									if (respObj.getJSONObject("messageHeader").has("referenceNumber")) {
										referenceNumber = respObj.getJSONObject("messageHeader")
												.getString("referenceNumber");
									}
								}
								/*if (respObj.has("simOtaDeviceChangeNotification")) {
									respObj = respObj.getJSONObject("simOtaDeviceChangeNotification");
									if (respObj.has("returnCode")) {
										returnCode = respObj.getString("returnCode");
									}
									if (respObj.has("deviceId")) {
										imei = respObj.getString("deviceId");
									}
									if (respObj.has("mdn")) {
										mdn = respObj.getString("mdn");
									}*/
									String formattedreq = jsonFormatter(requestMsg);
									JSONObject formattedJsonReq = new JSONArray(formattedreq).getJSONObject(0);
									if (formattedJsonReq.has("returnCode")) {
										returnCode = formattedJsonReq.getString("returnCode");
									}else if (formattedJsonReq.has("code")) {
										returnCode = formattedJsonReq.getString("code");
									}
									if (formattedJsonReq.has("imei")) {
										imei = formattedJsonReq.getString("imei");
									}
									if (formattedJsonReq.has("mdn")) {
										mdn = formattedJsonReq.getString("mdn");
									}
									boolean isEligilibilityCode = false;
									if (CommonConstants.RETURN_CODES_VALIDATE_DEVICE.contains(returnCode)) {
										isEligilibilityCode = true;
									}
									String transactionStatus = CommonConstants.FAILED;
									String notificationStatus = CommonConstants.FAILED;
									ResourceUpdateRequest resourceUpdateRequest = new ResourceUpdateRequest();
									resourceUpdateRequest.setTransactionName("ValidateDevice_DeviceDetection");
									log.info("responseId ::" + rootId);
									TransactionHistory transactionHistory = new TransactionHistory();
									transactionHistory.setTransactionId(rootId);
									transactionHistory.setTransactionStatus(transactionStatus);
									transactionHistory.setNotificationStatus(notificationStatus);
									Device device = new Device();
									String formattedrequest = jsonFormatter(responseMsg);
									JSONArray jsonArrayInner = new JSONArray(formattedrequest);
									log.info("map1111:: " + jsonArrayInner);
									respObj = jsonArrayInner.getJSONObject(0);
									Gson gson = new Gson();
									RequestBean deviceDetailsBean = gson.fromJson(respObj.toString(),
											RequestBean.class);
									if (CommonConstants.OS_APL.equalsIgnoreCase(deviceDetailsBean.getMake())) {
										device.setOs("IOS");
									} else {
										device.setOs("Android");
									}
									device.setImei(imei);
									if (deviceDetailsBean.getCdmaLess() != null) {
										device.setCdmaLess(deviceDetailsBean.getCdmaLess());
									} else {
										device.setCdmaLess("N");
									}
									if (deviceDetailsBean.getMode() != null) {
										String deviceType = deviceDetailsBean.getMode().toString();
										device.setDeviceType(deviceType.substring(0, 2));
									}
									device.setCreatedBy(CommonConstants.CREATED_BY);
									device.setCreatedDate(apolloNEOutboundClientService.getTimeStamp());
									// device.setDeviceName(deviceName);
									// device.setDeviceType(deviceType);
									// device.setEid(eid);
									if (isEligilibilityCode) {
										log.info("setting Eligibility Code::");
										device.setEligibilityCode(returnCode);
									} else {
										log.info("setting Eligibility Code::");
										device.setEligibilityCode(null);
									}
									// device.seteLineId(eLineId);
									device.setEndDate(apolloNEOutboundClientService.getTimeStamp());
									// device.setEquipmentType(equipmentType);
									// device.setEuiccCapable(euiccCapable);
									// device.setEuiccId(euiccId);
									device.setImei(imei);
									device.setIsByod(CommonConstants.YES);
									// device.setlId(lId);
									// device.setMacAddress(macAddress);
									device.setMake(deviceDetailsBean.getMake());
									device.setModel(deviceDetailsBean.getModel());
									device.setModeValue(deviceDetailsBean.getMode());
									device.setModifiedBy(CommonConstants.MODITY_BY);
									device.setModifiedDate(apolloNEOutboundClientService.getTimeStamp());
									device.setStartDate(apolloNEOutboundClientService.getTimeStamp());

									// lineHistory
									
									LineHistory lineHistory = new LineHistory();
									lineHistory.setTransactionId(rootId);
									lineHistory.setLineStatus(CommonConstants.ACTIVE);
									lineHistory.setMdn(mdn);
									lineHistory.setAcctStatus(CommonConstants.ACTIVE);
									lineHistory.setTransactionType(CommonConstants.DD);
									lineHistory.setStartDate(apolloNEOutboundClientService.getTimeStamp());
									lineHistory.setCreatedBy(CommonConstants.NSL);

									/*
									 * lineHistory.setFieldType("DEVICEID"); lineHistory.setNewValue(imei);
									 * lineHistory.setStartDate(mnoOutboundService.getTimeStamp());
									 * lineHistory.setCreatedBy(Constants.CREATED_BY);
									 * lineHistory.setModifiedBy(Constants.MODITY_BY);
									 * lineHistory.setModifiedDate(mnoOutboundService.getTimeStamp());
									 * lineHistory.setTransactionId(rootId);
									 */
									// lineHistory.setOrdType(Constants.ORDER_TYPE_BCD);

									Line line = new Line();
									line.setMdn(mdn);
									line.setReferenceNumber(referenceNumber);
									line.setModifiedDate(apolloNEOutboundClientService.getTimeStamp());
									line.setModifiedBy(CommonConstants.NSL);

									resourceUpdateRequest.setLineDetails(line);
									resourceUpdateRequest.setLineHistory(lineHistory);
									resourceUpdateRequest.setTransactionHistory(transactionHistory);
									resourceUpdateRequest.setDeviceDetails(device);
									log.debug("DeviceDetecton validate in Outbound::" + resourceUpdateRequest);
									apolloNEOutboundClientService.callResourceService(resourceUpdateRequest);
								//}
							}

						}
					}

				} catch (Exception e) {
					log.error("ErrorCode : "+ErrorCodes.CECC0004+" : error in device detection{}", e);
				}
			}
				
				if(serviceName != null && serviceName.equalsIgnoreCase("Manage-Blacklist")) {
					errorCode = "ERR117";
					errorMessage = "GSMA Check Failed";
				}
				if (serviceName.equalsIgnoreCase("Transfer-Device")) {
					if (("Change SIM".equalsIgnoreCase(operationName))
							|| ("Change Rate Plan".equalsIgnoreCase(operationName))) {
						errorCode= "ERR111";
						errorMessage = "Transfer Device – Change SIM and Device failed";
					}else if ("Deactivate Subscriber".equalsIgnoreCase(operationName)) {
						errorCode= "ERR110";
						errorMessage = "Transfer Device – Deactivation of Line 2 failed";
					}
				}

				MapSqlParameterSource input = new MapSqlParameterSource();
				input.addValue("TRANSACTION_ID", transacId);
				input.addValue("ROOT_TRANSACTION_ID", rootId);
				input.addValue("ERROR_CODE", errorCode);
				input.addValue("ERROR_MSG", errorMessage);
				input.addValue("CREATED_BY", "NSL");
				namedJdbcTemplate.update(ApolloNEConstants.INSERT_TRANS_FAILURE_LOG, input);
				if (operationName != null && (operationName.equalsIgnoreCase("Change Wholesale Rate Plan") || operationName.equals("Change Feature")|| operationName.equalsIgnoreCase("Remove Hotline") 
					|| operationName.equalsIgnoreCase("Restore Service"))){
					final String stgErrorCode= errorCode;	
					final String stgStatus = "FAILED";
					final String stgErrorMsg= errorMessage;
					final String stgRootId =rootId;
					ExecutorService executor = Executors.newSingleThreadExecutor();
					executor.execute(new Runnable() {
						@Override
						public void run() {
							updateStgPlanMigration(stgRootId,stgStatus,stgErrorCode,stgErrorMsg);

						}
					});
				}
				if (operationName != null && (operationName.equalsIgnoreCase(CommonConstants.SUSPEND_SUBSCRIBER)
							|| operationName.equalsIgnoreCase(CommonConstants.HOTLINE_SUBSCRIBER))){
					final String stgStatus = "Failure";
					final String stgRootId =rootId;
					ExecutorService executor = Executors.newSingleThreadExecutor();
					executor.execute(new Runnable() {
						@Override
						public void run() {
							updateStgPlanMigrationRollBackStatus(stgRootId,stgStatus);

						}
					});
				}
			}
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0008+" : Exception{}", e);
		}
	}

	@Override
	public String getNEFlag(String neFlag) {

		return serviceInfo.findByServiceName(neFlag);
	}

	@Override
	public Long getPrimaryKey() {
		return transRepo.getPrimaryKey();

	}

	@Override
	public String getRequestDetails(String responseId) {

		String request = centuryCIFTemplate.queryForObject(ApolloNEConstants.GET_REQUESTTRANSACTION_ID,
				new Object[] { responseId }, String.class);
		return request;
	}
	
	@Override
	public String getActivateSubsriberPortInRequestForCP (String responseId) {

		String request = centuryCIFTemplate.queryForObject(ApolloNEConstants.GET_ACTIVATE_SUBSCRIBER_PORTINREQUEST,
				new Object[] { responseId }, String.class);
		return request;
	}

	@Override
	public void updateQueueTransactionDetails(String status, String responseId) {
		transRepo.updateQueueTransactionDetails(status, responseId);
	}

	@Override
	public void updateRequestDetails(String request, Long responseId) {
		// Long transactionId= Long.parseLong(responseId);
		log.debug("transactionId:: " + responseId);
		transRepo.updateRequestDetails(request, responseId);
	}

	public String getRefNumber(Double trans_id) {
		log.info("trans_id::" + trans_id);
		String refNo = "";
		refNo = centuryCIFTemplate.queryForObject(ApolloNEConstants.GET_REF_NO, new Object[] { trans_id },
				String.class);
		// "select reference_number from rh_csr_request where root_transaction_id ='" +
		// trans_id + "'", String.class);
		return refNo;
	}

	public String getContextFromReturnUrl(String returnUrl) {
		String contextId = centuryCIFTemplate.queryForObject(ApolloNEConstants.GETCONTEXTFROMURL,
				new Object[] { returnUrl }, String.class);
		log.debug("getContextFromReturnUrl contextId::" + contextId);
		return contextId;
	}

	public List<Map<String, Object>> mboCredentialDetails(String server, String contextId) {
		List<Map<String, Object>> mboCredentialDetails = null;
		mboCredentialDetails = (List<Map<String, Object>>) centuryCIFTemplate.queryForList(
				"SELECT SERVICE_URL,service_request,service_description FROM SERVICE_INFO WHERE SERVICE_NAME='OAUTH_MBO' and server='"
						+ server + "' and issuer_name='" + contextId + "'");
		return mboCredentialDetails;
	}

	public int updateSouthBoundTransactionFailure(String transId, String entityId, String response, String groupId,
			String responseId, String targetSystem, String queueStatus) {
		log.info("updateSouthBoundTransactionFailure - transId ::" + transId + "-entityId::" + entityId
				+ "- response ::" + response + "- groupId :: " + groupId + "- responseId::" + responseId);
		try {
			String clientEndUrl = "";
			String httpCode = "";
			if (response == null) {
				response = ApolloNEConstants.NOT_RECEIVED_RESP;
			}
			if (response.contains("~")) {
				if (response.split("~").length == 3) {
					clientEndUrl = response.split("~")[2];
					httpCode = response.split("~")[1];
					response = response.split("~")[0];
				}
				if (response.split("~").length == 2) {
					httpCode = response.split("~")[1];
					response = response.split("~")[0];
				}
			}
			MapSqlParameterSource input = new MapSqlParameterSource();
			input.addValue("TRANSACTION_ID", transId);
			input.addValue("ENTITY_ID", entityId);
			if (StringUtils.hasText(queueStatus)) {
				input.addValue("status", queueStatus);
			} else {
				input.addValue("status", "FAILURE");
			}
			input.addValue("GROUPID", groupId);
			input.addValue("RESPONSE_MSG", new SqlLobValue(response), Types.CLOB);
			input.addValue("TARGETSYSTEM", targetSystem);
			NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(
					centuryCIFTemplate.getDataSource());
			int count = namedJdbcTemplate.update(ApolloNEConstants.UPDATE_SB_FAIL_TRANSACTION, input);
			/*
			 * String transacId =
			 * centuryCIFTemplate.queryForObject(ApolloNEConstants.GET_TRANSACTION_ID, new
			 * Object[] { transId }, String.class); input.addValue("TRANS_ID", transacId);
			 * input.addValue("SERVICE_URL", clientEndUrl);
			 * namedJdbcTemplate.update(ApolloNEConstants.UPDATE_TRANSACTION_METADATA,
			 * input);
			 */
			 String rootId=centuryCIFTemplate.queryForObject(ApolloNEConstants.GET_ROOOT_ID,
						  new Object[] { transId }, String.class);
			final String stgStatus = "FAILED";
			ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.execute(new Runnable() {
				@Override
				public void run() {
					updateStgPlanMigration(rootId,stgStatus,"","");

				}
			});
			return count;
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0008+" : Error in updateSouthBoundTransactionFailure{}", e);
		}
		return 0;
	}

	@Override
	public String getReferenceNumber(String ReferenceNumber) {
		String transactionId = centuryCIFTemplate.queryForObject(ApolloNEConstants.GET_REFERENCE_NO,
				new Object[] { ReferenceNumber }, String.class);
		return transactionId;
	}

	@Override
	public String gettransactionId(String referenceNumber) {
		String transId = (String) centuryCIFTemplate.queryForObject(ApolloNEConstants.GETTRANSACTIONID1,
				new Object[] { referenceNumber }, String.class);
		return transId;
	}
	
	@Override
	public String getRootTransactionId(String referenceNumber) {
		String roottransId = (String) centuryCIFTemplate.queryForObject(ApolloNEConstants.GETROOTTRANSACTIONID1,
				new Object[] { referenceNumber }, String.class);
		return roottransId;
	}

	public String lnpInformationDetailsMask(String request, String operationName) {
		try {
			String lnpInformation = "";
			if (operationName.equalsIgnoreCase("Update Customer Information")) {
				lnpInformation = ApolloNEConstants.LNPINFORMATIONAP;
			} else {
				lnpInformation = ApolloNEConstants.REQUESTLNPINFORMATION;
			}
			

			String[] lnpInformationList = lnpInformation.split(",");

			for (int i = 0; i < lnpInformationList.length; i++) {
				if (request.startsWith("[")) {
					JSONArray jsonTypeObject = new JSONArray(request);
					JSONObject jsonObject = jsonTypeObject.getJSONObject(0);
					lnpInformationList[i] = lnpInformationList[i].replace("$.data",
							"$.[0]." + jsonObject.names().getString(0) + ".data");
				}
				if (operationName.equalsIgnoreCase("Update Customer Information")) {
					if (request.contains("lnpUpdate")) {
						lnpInformationList[i] = lnpInformationList[i].replace("$.data.subOrder[0].lnp",
								"$.data.subOrder[0].lnpUpdate");
					}
				}
				log.info("lnpInformationDetaillnpInfo.." + lnpInformationList[i]);
				JsonNode updatedJson = JsonPath.using(ApolloNEConstants.configuration).parse(request)
						.set(lnpInformationList[i], "****").json();
				request = updatedJson.toString();
				// }
			}
			log.info("lnpInformationDetailsMask.." + request);

		} catch (Exception ex) {
			log.error("ErrorCode : "+ErrorCodes.CECC0007+" : Error in lnpInformationDetailsMask{}", ex);
		}
		return request;
	}

	public String requestlnpInformationDetailsMask(String transactionId) {
		String requestMSG = "";
		try {
			requestMSG = centuryCIFTemplate.queryForObject(ApolloNEConstants.GET_REQUESTTRANSACTION_ID,
					new Object[] { transactionId }, String.class);
			log.info("requestlnpInformationDetailsMask responseId::" + transactionId);
			String lnpInformation = ApolloNEConstants.REQUESTLNPINFORMATION;
			String[] lnpInformationList = lnpInformation.split(",");

			for (int i = 0; i < lnpInformationList.length; i++) {

				log.info("requestlnpInformationDetailsMask.." + lnpInformationList[i]);
				JsonNode updatedJson = JsonPath.using(ApolloNEConstants.configuration).parse(requestMSG)
						.set(lnpInformationList[i], "****").json();
				requestMSG = updatedJson.toString();
			}
			log.info("requestlnpInformationDetailsMask.." + requestMSG);
			MapSqlParameterSource input = new MapSqlParameterSource();
			input.addValue("TRANSACTION_ID", transactionId);
			input.addValue("REQUEST_MSG", new SqlLobValue(requestMSG), Types.CLOB);
			NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(
					centuryCIFTemplate.getDataSource());
			namedJdbcTemplate.update(ApolloNEConstants.UPDATE_REQUESTMSG_LNPINFORMATION, input);
		} catch (Exception ex) {
			log.error("ErrorCode : "+ErrorCodes.CECC0008+" : Error in requestlnpInformationDetailsMask{}", ex);
		}
		return requestMSG;
	}

	public List<Map<String, Object>> getThrottleDetails(String retailplancode_db, String planCode) {
		List<Map<String, Object>> res = null;
		res = (List<Map<String, Object>>) centuryCIFTemplate.queryForList(ApolloNEConstants.GET_THROTTLE_DETAILS,
				new Object[] { retailplancode_db, planCode });
		return res;
	}

	public String getServiceName(String responseId) {
		log.info("inside getServiceName::" + responseId);
		String serviceName = centuryCIFTemplate.queryForObject(ApolloNEConstants.GET_SERVICE_NAME,
				new Object[] { responseId }, String.class);
		log.info("API serviceName:: " + serviceName);
		return serviceName;
	}
	
	public String getRequest(String transactionId) {
		String resMsg = (String) centuryCIFTemplate.queryForObject(ApolloNEConstants.GET_REQUEST, new Object[] { transactionId },
				String.class);
		return resMsg;
	}
	
	public List<Map<String, Object>> getAdditionalData(String transID) {
		List<Map<String, Object>> result = (List<Map<String, Object>>) centuryCIFTemplate.queryForList(ApolloNEConstants.GET_ADDITIONAL_DATA, new Object[] { transID });
		return result;
	}
	
	public String getTransIdFromUid(String transId) {
		String transacId = centuryCIFTemplate.queryForObject(ApolloNEConstants.GET_TRANSACTION_ID, new Object[] { transId },
				String.class);
		return transacId;
	}
	
	public int getTenantIdFromTid(String transId) {
		int tenantId = (int) centuryCIFTemplate.queryForObject(ApolloNEConstants.GET_TENANT_ID,
				new Object[] { transId }, Integer.class);
		return tenantId;
	}
	
	public String jsonFormatter(String inputJson) {
		log.debug("inputJson::" + inputJson);
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
			log.error("ErrorCode : "+ErrorCodes.CECC0007+" : Error in jsonFormatter{}", e);
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
					}
				}
				if ("equipmentInfo".equalsIgnoreCase(keyStr)) {
					if (keyvalue instanceof JSONArray) {
						JSONArray array = (JSONArray) keyvalue;
						for (int i = 0; i < array.length(); i++) {
							JSONObject jsonObject1 = array.getJSONObject(i);
							String str = jsonObject1.toString();
							if (str.contains("\"type\":\"mode\"")) {
								str = str.replace("value", "mode");
								printJsonObject(new JSONObject(str), obj);
							} else if (str.contains("\"type\":\"model\"")) {
								str = str.replace("value", "model");
								printJsonObject(new JSONObject(str), obj);
							} else if (str.contains("\"type\":\"make\"")) {
								str = str.replace("value", "make");
								printJsonObject(new JSONObject(str), obj);
							}
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
				if (keyvalue instanceof JSONArray) {
					printJsonArray((JSONArray) keyvalue, obj);
				}
			}
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0007+" : Error in printJsonObject{}", e);
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
			log.error("ErrorCode : "+ErrorCodes.CECC0007+" : Error in printJsonArray{}", e);
		}
		return null;
	}

	public String maskingSharedName(String request, String operationName) {
		try {
			if (operationName.equalsIgnoreCase(CommonConstants.UPDATE_SHARED_NAME)) {
				JsonNode updatedJson = JsonPath.using(CommonConstants.configuration).parse(request)
						.set("$.data.subOrder[0].name[0].value", "****").json();
				request = updatedJson.toString();
			} else if (operationName.equalsIgnoreCase(CommonConstants.QUERY_SHARED_NAME)) {
				JsonObject formattedDataObj1 = new JsonParser().parse(request).getAsJsonObject();
				if (formattedDataObj1.has("data")) {
					JsonObject dataObject = formattedDataObj1.getAsJsonObject("data");
					log.debug("maskingSharedName dataObject::"+ dataObject);
					if (dataObject.has("name")) {
						JsonNode updatedJson = JsonPath.using(CommonConstants.configuration).parse(request)
								.set("$.data.name[0].value", "****").json();
						request = updatedJson.toString();
					}
				}
			}
			log.debug("maskingSharedName returning request::"+ request);
		} catch (Exception ex) {
			log.error("ErrorCode : "+ErrorCodes.CECC0007+" : Error in maskingSharedName{}", ex);
		}
		return request;
	}
	
	
    @Override
	public String getOutboundCallResponseMsg(String transactionId, String transacionName) {
		String responsemsg = transRepo.getOutboundResponseMsg(transactionId,transacionName);
		return responsemsg;
	}
    
    
    @Override
	public String getRootTransName(String responseId) {
		String resMsg = (String) centuryCIFTemplate.queryForObject(CommonConstants.GET_ROOT_OPERATION_NAME, new Object[] {responseId },
				String.class);
		return resMsg;
	}
    
    @Override
    public List<Map<String, Object>> transactionErrorMessage(String transactionId) {
		List<Map<String, Object>> res = null;
		log.info("transactionErrorMessage::");
		res = (List<Map<String, Object>>) centuryCIFTemplate.queryForList(CommonConstants.RESPONSEERROR,
				new Object[] { transactionId });
		log.info("transactionErrorMessageRES::" + res);
		return res;
	}

    @Override
    public List<Map<String, Object>> getValidateDeviceDetails(String responseId) {
    	log.info("getValidateDeviceDetails::");
		List<Map<String, Object>> result = (List<Map<String, Object>>) centuryCIFTemplate
				.queryForList(CommonConstants.GET_VALIDATE_DEVICE_DETAILS, new Object[] { responseId });
		return result;
	}

	@Override
	public String getTransactionMileStoneFromMetadata(String responseId) {
		String transacId = centuryCIFTemplate.queryForObject(ApolloNEConstants.GET_TRANSACTIONMILESTONE, new Object[] { responseId },
				String.class);
		return transacId;
	}

	@Override
	public void updateTransactionMileStoneFromMetadata(String responseId, String transMileStone) {
		
		centuryCIFTemplate.update(
                ApolloNEConstants.UPDATE_TRANSACTIONMILESTONE, 
                transMileStone, responseId);
	}
	
	public String getUpdatePortInTransactionId(String responseId) {
		String transactionId = "";
		try {
			transactionId = centuryCIFTemplate.queryForObject(ApolloNEConstants.GET_UPDATE_PORT_IN_TRANSID,
					new Object[] { responseId }, String.class);
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0008+" : Error in getUpdatePortInTransactionId{}", e);
		}
		return transactionId;
	}

	public String getUpdatePortInActivateTransactionId(String updatePortInTransId) {
		String rootTransactionId = "";
		try {
			rootTransactionId = centuryCIFTemplate.queryForObject(ApolloNEConstants.GET_UPDATE_PORT_IN_ACTIVATE_TRANSACTION_ID,
					new Object[] { updatePortInTransId }, String.class);
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0008+" : Error in getUpdatePortInActivateTransactionId{}", e);
		}
		return rootTransactionId;
	}
	
	public void updateStgPlanMigration(String transId,String status,String errorCode,String errorMessage){
		StgPlanMigration stgPlanMigration = new StgPlanMigration();
		try {
			String requestMsg = centuryCIFTemplate.queryForObject(ApolloNEConstants.GET_REQUEST, new Object[] { transId },
			String.class);
			log.info("updateStgPlanMigration :: transId :: "+transId +" errorCode:: "+errorCode+" errorMessage:: "+errorMessage);
			if(requestMsg.contains("isPlanMigration")){
				JsonObject obj = new JsonParser().parse(requestMsg).getAsJsonObject();
				if (obj.get("data").getAsJsonObject().get("subOrder").getAsJsonArray().get(0)
					.getAsJsonObject().has("lineId")) {
					String eLineId = obj.get("data").getAsJsonObject().get("subOrder")
					.getAsJsonArray().get(0).getAsJsonObject().get("lineId").getAsString();
					stgPlanMigration.setELineId(eLineId);
				}
				stgPlanMigration.setTransactionId(transId);
				stgPlanMigration.setStatus(status);
				if(errorCode!=null && ! errorCode.equalsIgnoreCase("")){
					stgPlanMigration.setErrorCode(errorCode);
				}
				stgPlanMigration.setProcessedEndDate(apolloNEOutboundClientService.getTimeStamp());
				if(errorMessage!=null && ! errorMessage.equalsIgnoreCase("")){
					stgPlanMigration.setErrorMessage(errorMessage);
				}
				apolloNEOutboundClientService.updateStgPlanMigration(stgPlanMigration);
			}
		}
		catch (Exception e) {
			log.error("Exception updateStgPlanMigration", e);
		}
	}
	
	public void updateStgPlanMigrationRollBackStatus(String transId,String status){
		StgPlanMigration stgPlanMigration = new StgPlanMigration();
		try {
			String requestMsg = centuryCIFTemplate.queryForObject(ApolloNEConstants.GET_REQUEST, new Object[] { transId },
			String.class);
			if(requestMsg.contains("isPlanMigration")){
				JsonObject obj = new JsonParser().parse(requestMsg).getAsJsonObject();
				if (obj.get("data").getAsJsonObject().get("subOrder").getAsJsonArray().get(0)
					.getAsJsonObject().has("lineId")) {
					String eLineId = obj.get("data").getAsJsonObject().get("subOrder")
					.getAsJsonArray().get(0).getAsJsonObject().get("lineId").getAsString();
					stgPlanMigration.setELineId(eLineId);
				}
				stgPlanMigration.setRollBackStatus(status);
				apolloNEOutboundClientService.updateStgPlanMigration(stgPlanMigration);
			}
		}
		catch (Exception e) {
			log.error("Exception updateStgPlanMigration", e);
		}
	}
	
}
