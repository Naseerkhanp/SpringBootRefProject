package com.excelacom.century.apolloneoutbound.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.excelacom.century.apolloneoutbound.bean.Account;
import com.excelacom.century.apolloneoutbound.bean.Device;
import com.excelacom.century.apolloneoutbound.bean.DeviceGsmaHistory;
import com.excelacom.century.apolloneoutbound.bean.ErrorCodes;
import com.excelacom.century.apolloneoutbound.bean.ErrorDetails;
import com.excelacom.century.apolloneoutbound.bean.Feature;
import com.excelacom.century.apolloneoutbound.bean.Line;
import com.excelacom.century.apolloneoutbound.bean.LineHistory;
import com.excelacom.century.apolloneoutbound.bean.LinePlan;
import com.excelacom.century.apolloneoutbound.bean.NpaNxx;
import com.excelacom.century.apolloneoutbound.bean.ProcessMetadata;
import com.excelacom.century.apolloneoutbound.bean.RefErrorRules;
import com.excelacom.century.apolloneoutbound.bean.RefPilotPrg;
import com.excelacom.century.apolloneoutbound.bean.ReferenceValue;
import com.excelacom.century.apolloneoutbound.bean.RequestBean;
import com.excelacom.century.apolloneoutbound.bean.ResourceInfo;
import com.excelacom.century.apolloneoutbound.bean.ResourceUpdateRequest;
import com.excelacom.century.apolloneoutbound.bean.SearchEnvironmentResultDto;
import com.excelacom.century.apolloneoutbound.bean.Sim;
import com.excelacom.century.apolloneoutbound.bean.TransactionHistory;
import com.excelacom.century.apolloneoutbound.properties.ApolloNEServiceProperties;
import com.excelacom.century.apolloneoutbound.utils.constants.CommonConstants;
import com.excelacom.century.apolloneoutbound.utils.constants.ResourceConstants;
import com.google.gson.Gson;
import com.excelacom.century.apolloneoutbound.bean.StgPlanMigration;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class ApolloNEOutboundClientService {

	@Autowired
	ApolloNEServiceProperties apolloNEProperties;
	
	@Autowired
	private JdbcTemplate centuryCIFTemplate;
	
	public Line getLineDetailsWithELineId(String lineId) {
		log.info("getLineDetails :: " + lineId);
		Line line = new Line();
		line.setLineId(lineId);
		return callLineResourceServiceByLineId(lineId);
	}

	public Line callLineResourceServiceFromMdn(Line lineBean) {
		log.debug("Inside callLineResourceService::" + lineBean.toString());
		String outputString = "";
		HttpURLConnection conn = null;
		Gson requestGson = new Gson();
		String url = "";
		Line line = new Line();
		try {
			url = apolloNEProperties.getGetlinedetailsbymdn() + "?mdn=" + lineBean.getMdn();
			log.debug("callLineResourceService URL::" + url);
			URL serviceUrl = new URL(url);
			conn = (HttpURLConnection) serviceUrl.openConnection();
			byte[] requestData = requestGson.toJson(lineBean).getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/json");
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
			String responseString = "";
			while ((responseString = in.readLine()) != null) {
				outputString = outputString + responseString;
				line = requestGson.fromJson(outputString, Line.class);
				log.debug("Request Bean in callLineResourceService::" + line.toString());
			}
			isr.close();
			os.close();
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Exception -{}", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		return line;
	}
	
	public RefPilotPrg callRefPilotTableFromImei(RefPilotPrg refPilotPrg) {
		log.debug("Inside callRefPilotTableFromImei::" + refPilotPrg);
		String outputString = "";
		HttpURLConnection conn = null;
		Gson requestGson = new Gson();
		String url = "";
		RefPilotPrg refPilot = new RefPilotPrg();
		try {
			url = apolloNEProperties.getGetRefPilotbyImei() + "?imei=" + refPilotPrg.getImei();
			log.debug("callRefPilotTableFromImei URL::" + url);
			URL serviceUrl = new URL(url);
			conn = (HttpURLConnection) serviceUrl.openConnection();
			byte[] requestData = requestGson.toJson(refPilotPrg).getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/json");
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
			String responseString = "";
			while ((responseString = in.readLine()) != null) {
				outputString = outputString + responseString;
				refPilot = requestGson.fromJson(outputString, RefPilotPrg.class);
				log.debug("Request Bean in callRefPilotTableFromImei::" + refPilot.toString());
			}
			isr.close();
			os.close();
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0006+" : callRefPilotTableFromImei Exception -{}", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		return refPilot;
	}

	
	public LinePlan callLinePlanResourceService(LinePlan linePlan) {
		String outputString = "";
		HttpURLConnection conn = null;
		String url="";
		Gson requestGson = new Gson();
		try {
			//URL serviceUrl = new URL(apolloNEProperties.getResourceServiceURL()+ResourceConstants.resourceUpdateServiceUrl.valueOf("UPDATETRANSACTIONHISTORYFORNS").getServiceUrl());
			url=apolloNEProperties.getResourceServiceURL()+ResourceConstants.resourceUpdateServiceUrl.valueOf("GETLINEPLANBYLINEID").getServiceUrl()+"?eLineId="+linePlan.getELineId();
			//url=sbNEServiceProperties.getLinePlanDetailsByELineId()+"?eLineId="+linePlan.geteLineId();
			URL serviceUrl = new URL(url);
			conn = (HttpURLConnection) serviceUrl.openConnection();
			byte[] requestData = linePlan.toString().getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/json");
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
				linePlan = requestGson.fromJson(outputString, LinePlan.class);
			}
			isr.close();
			os.close();
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Error in callLinePlanResourceService{}", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		log.info("callRestAPI :: outputString :: " + outputString);
		return linePlan;
	}
	
	public String updateTransactionHistoryForNSUrl(TransactionHistory transactionHistory) {			
		log.info("updateTransactionHistoryForNSUrl in MNOOutbound :: "+transactionHistory);
				String outputString = CommonConstants.EMPTYSTRING;
				HttpURLConnection conn = null;
				Gson requestGson = new Gson();
				try {
					URL serviceUrl = new URL(apolloNEProperties.getResourceServiceURL()+ResourceConstants.resourceUpdateServiceUrl.valueOf("UPDATETRANSACTIONHISTORYFORNS").getServiceUrl());
					//URL serviceUrl = new URL(sbNEServiceProperties.getUpdateTransactionHistoryForNSUrl());
					conn = (HttpURLConnection) serviceUrl.openConnection();
					log.info("req::"+requestGson.toJson(transactionHistory));
					byte[] requestData = requestGson.toJson(transactionHistory).getBytes(StandardCharsets.UTF_8);
					conn.setRequestProperty("Content-Type", "application/json");
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
				} catch (Exception e) {
					log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Error in updateTransactionHistoryForNSUrl{}", e);
				} finally {
					if (conn != null) {
						conn.disconnect();
						conn = null;
					}
				}
				log.info("updateTransactionHistoryUrl :MNOOutbound: outputString :: " + outputString);
				return outputString;
			}
	public String updateInflightTransStatus(Line line) {
		log.info("updateInflightTransStatus in MNOOutbound :: " + line);
		String outputString = CommonConstants.EMPTYSTRING;
		HttpURLConnection conn = null;
		Gson requestGson = new Gson();
		try {
			
			URL serviceUrl = new URL(apolloNEProperties.getResourceServiceURL()+ResourceConstants.resourceUpdateServiceUrl.valueOf("UPDATEINFLIGHTTRANSSTATUS").getServiceUrl());

			//URL serviceUrl = new URL(sbNEServiceProperties.getUpdateInflightTransStatus());
			conn = (HttpURLConnection) serviceUrl.openConnection();
			log.info("req::" + requestGson.toJson(line));
			byte[] requestData = requestGson.toJson(line).getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/json");
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
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Error in updateInflightTransStatus{}", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		log.info("updateInflightTransStatus :MNOOutbound: outputString :: " + outputString);
		return outputString;
	}
	public Account callAccountResourceService(String accountNumber) {
		// log.info("callAccountResourceService :: " + accountNumber);
		String outputString = "";
		HttpURLConnection conn = null;
		Gson requestGson = new Gson();
		Account account = null;
		try {
			URL serviceUrl = new URL(apolloNEProperties.getAcctDetails() + "?accountNumber=" + accountNumber);
			conn = (HttpURLConnection) serviceUrl.openConnection();
			byte[] requestData = accountNumber.getBytes(StandardCharsets.UTF_8);
			// byte[] requestData =
			// requestGson.toJson(line).getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/json");
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
				account = requestGson.fromJson(outputString, Account.class);
			}
			isr.close();
			os.close();
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Exception -{}", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		return account;
	}

	public Device callDeviceResourceService(Device deviceBean) {
		Device device = null;
		log.debug("Inside callDeviceResourceService::" + deviceBean);
		String outputString = "";
		HttpURLConnection conn = null;
		Gson requestGson = new Gson();
		try {
			String url = apolloNEProperties.getGetDeviceDetailsByLineId() + "?eLineId=" + deviceBean.geteLineId();
			// log.info("callDeviceResourceService URL::::" + url);
			URL serviceUrl = new URL(url);
			conn = (HttpURLConnection) serviceUrl.openConnection();
			byte[] requestData = requestGson.toJson(deviceBean).getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/json");
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
				outputString = responseString;
				device = requestGson.fromJson(outputString, Device.class);
				log.info("Request Bean in callDeviceResourceService::" + device.toString());
			}
			isr.close();
			os.close();
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Exception -{}" , e);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		return device;
	}
	public Line getMALineDetails(String accountNumber) {
		log.info("getMALineDetails :: " + accountNumber);
		Line response = null;
		Line line = new Line();
		line.setAccountNumber(accountNumber);
		response=callMALineDetailsResourceService(line);
		log.info("getMALineDetails response:: " + response.toString());
		return response;
	}
	
	public Line callMALineDetailsResourceService(Line line) {
		log.info("Inside callMALineDetailsResourceService::" + line.toString());
		String outputString = null;
		HttpURLConnection conn = null;
		Gson requestGson = new Gson();
		try {
			String url = apolloNEProperties.getResourceServiceURL()+ResourceConstants.resourceUpdateServiceUrl.valueOf("GETMALINEDETAILSBYACCOUNTNO").getServiceUrl() + "?accountNumber=" + line.getAccountNumber();

			//String url = sbNEServiceProperties.getMALineDetailsByLineId() + "?accountNumber=" + line.getAccountNumber();
			log.info("callMALineDetailsResourceService URL::" + url);
			URL serviceUrl = new URL(url);
			conn = (HttpURLConnection) serviceUrl.openConnection();
			byte[] requestData = requestGson.toJson(line).getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/json");
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
				outputString = responseString;
				line = requestGson.fromJson(outputString, Line.class);
				log.info("Request Bean in callLSLineHistoryResourceService::" + line.toString());
			}
			isr.close();
			os.close();
		} catch (Exception e) {
			log.error("Exception in callMALineDetailsResourceService{}", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		log.info("callMALineDetailsResourceService :: outputString :: " + outputString);
		return line;
	}
	
	public void updateManageAccountDetails(RequestBean reqBean) {
		log.info("request inside NEW.........updateManageAccountDetails::" + reqBean.toString());
		String transactionName = "Manage Account";
		Line line=new Line();
		LineHistory lineHistory=new LineHistory();
		TransactionHistory transactionHistory = new TransactionHistory();
		ResourceUpdateRequest resourceUpdateRequest=new ResourceUpdateRequest();
		line.seteLineId(reqBean.getLineId());
		line.setAccountNumber(reqBean.getAccountNumber());
		line.setBcd(reqBean.getBillCycleResetDay());
		line.setMdn(reqBean.getMdn());
		lineHistory.setStartDate(getTimeStamp());
		//lineHistory.setCreatedDate(getTimeStamp());
		transactionHistory.setModifiedDate(getTimeStamp());
		transactionHistory.setTransactionEndDate(getTimeStamp());
		transactionHistory.setTransactionId(reqBean.getTransId());
		transactionHistory.setAccountNumber(reqBean.getAccountNumber());
		resourceUpdateRequest.setTransactionHistory(transactionHistory);
		resourceUpdateRequest.setTransactionName(transactionName);
		resourceUpdateRequest.setTransationId(reqBean.getTransId());
		resourceUpdateRequest.setLineDetails(line);
		resourceUpdateRequest.setLineHistory(lineHistory);
		callResourceService(resourceUpdateRequest);
	}
	public String getTimeStamp() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		Calendar cal = Calendar.getInstance();
		log.debug("Current Date: " + sdf.format(cal.getTime()));
		String date=sdf.format(cal.getTime());
		return date;
	}

	public String callResourceService(ResourceUpdateRequest resourceUpdateRequest) {
		log.info("callResourceService in INBOUND::" + resourceUpdateRequest.toString());
			String outputString = "";
			HttpURLConnection conn = null;
			Gson requestGson = new Gson();
			URL serviceUrl=null;
			String enumUrl="";
			try {
				String transactionName = resourceUpdateRequest.getTransactionName();
				log.info("transactionName callResourceService::" + transactionName);
				if (transactionName != null && (!transactionName.isEmpty())) {
					try {
						if (transactionName.contains(" ")) {
							transactionName = transactionName.replaceAll("\\s", "");
						}
						if (transactionName.contains("-")) {
							transactionName = transactionName.replaceAll("-", "_");
						}
						log.info("transactionName callResourceService::" + transactionName);
						enumUrl = ResourceConstants.resourceUpdateServiceUrl.valueOf(transactionName).getServiceUrl();
						serviceUrl = new URL(apolloNEProperties.getResourceServiceURL() + enumUrl);
					} catch (Exception e) {
						enumUrl = ResourceConstants.resourceUpdateServiceUrl.valueOf("DEFAULT").getServiceUrl();
						serviceUrl = new URL(apolloNEProperties.getResourceServiceURL() + enumUrl);
						log.error("ErrorCode : "+ErrorCodes.CECC0020+" : callResourceService enum{} ", e);
					}

				} else {
					enumUrl = ResourceConstants.resourceUpdateServiceUrl.valueOf("DEFAULT").getServiceUrl();
					serviceUrl = new URL(apolloNEProperties.getResourceServiceURL() + enumUrl);
				}
				log.info("URL ::" + serviceUrl);
				//URL serviceUrl = new URL(sbNEServiceProperties.getResourceServiceURL());
				conn = (HttpURLConnection) serviceUrl.openConnection();
				log.info("req::"+requestGson.toJson(resourceUpdateRequest));
				byte[] requestData = requestGson.toJson(resourceUpdateRequest).getBytes(StandardCharsets.UTF_8);
				//byte[] requestData = request.toString().getBytes(StandardCharsets.UTF_8);
				//conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				conn.setRequestProperty("Content-Type", "application/json");
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
					outputString = responseString;
				}
				isr.close();
				os.close();
			} catch (Exception e) {
				log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Error in callResourceService{}", e);
			} finally {
				if (conn != null) {
					conn.disconnect();
					conn = null;
				}
			}
			log.info("callResourceService :: outputString :: " + outputString);
			return outputString;
		}


	
	public Line callLineResourceServiceByLineId(String eLineId) {
		String outputString = "";
		HttpURLConnection conn = null;
		Gson requestGson = new Gson();
		Line line = null;
		try {
			URL serviceUrl = new URL(apolloNEProperties.getResourceServiceURL()+ResourceConstants.resourceUpdateServiceUrl.valueOf("GETLINEDETAILSBYLINEID").getServiceUrl()+"?eLineId="+eLineId);
			conn = (HttpURLConnection) serviceUrl.openConnection();
			byte[] requestData = eLineId.getBytes(StandardCharsets.UTF_8);
			//byte[] requestData = requestGson.toJson(line).getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/json");
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
				line = requestGson.fromJson(outputString, Line.class);
			}
			isr.close();
			os.close();
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Error in callLineResourceServiceByLineId{}", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		log.info("callLineResourceService :: line :: " + line);
		return line;
	}
	
	public List<NpaNxx> getNpaNxxDetails(String zipCode) {
		log.info("getNpaNxxDetails :: " + zipCode);
		Gson requestGson = new Gson();
		List<NpaNxx> npanxxList = null;
		String response = null;
		NpaNxx npanxx = new NpaNxx();
		npanxx.setZipCode(zipCode);
		response = callNpaNxxResourceService(npanxx);
		log.info("getNpaNxxDetails response:: " + response);
		if (Objects.nonNull(response)) {
			Type userListType = new com.google.gson.reflect.TypeToken<List<NpaNxx>>(){}.getType();
			npanxxList  = requestGson.fromJson(response, userListType);  
		}
		return npanxxList;
	}
	
	public String callNpaNxxResourceService(NpaNxx npanxx) {
		log.info("Inside callNpaNxxResourceService::::" + npanxx.toString());
		String outputString = "";
		HttpURLConnection conn = null;
		Gson requestGson = new Gson();
		try {
			// String url = inboundProperties.getFeatureDetailResourceService() +
			// "?zipCode=" + npanxx.getZipCode();
			String url = apolloNEProperties.getResourceServiceURL() + ResourceConstants.resourceUpdateServiceUrl
					.valueOf("GETNPANXXDETAILSBYZIPCODE").getServiceUrl() + "?zipCode=" + npanxx.getZipCode();
			log.info("callFeatureResourceService URL::::" + url);
			URL serviceUrl = new URL(url);
			conn = (HttpURLConnection) serviceUrl.openConnection();
			byte[] requestData = requestGson.toJson(npanxx).getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/json");
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
				outputString = responseString;
				// sim = requestGson.fromJson(outputString, Sim.class);

			}
			isr.close();
			os.close();
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Exception - "+e.getMessage(), e);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		log.info(" callNpaNxxResourceService:: outputString :: " + outputString);
		return outputString;
	}

	public RefErrorRules[] getErrorRulesDetails(RefErrorRules refErrorRules) {
		String outputString = "";
		HttpURLConnection conn = null;
		Gson requestGson = new Gson();
		RefErrorRules[] line = null;
		try {
			String url = apolloNEProperties.getResourceServiceURL() +ResourceConstants.resourceUpdateServiceUrl.valueOf("GET_REF_ERROR_RULES").getServiceUrl();
			log.debug("url getErrorRulesDetails:: " + url);
			URL serviceUrl = new URL(url);
			conn = (HttpURLConnection) serviceUrl.openConnection();
			byte[] requestData = requestGson.toJson(refErrorRules).getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/json");
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
				outputString = responseString;
				line = requestGson.fromJson(outputString, RefErrorRules[].class);
			}
			isr.close();
			os.close();
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Exception in getErrorRulesDetails{}",e);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		log.debug("getErrorRulesDetails :: " + line);
		return line;
	}

	
	public String   getLineDetails(String mdn) {
		log.info("getLineDetails :: " + mdn);
		String response = null;
		Line line = new Line();
		line.setMdn(mdn);
		response = callLineResourceService(line);
		return response;
	}

			
	public String callLineResourceService(Line line) {
		log.info("Inside callLineResourceService::" + line.toString());
		String outputString = null;
		HttpURLConnection conn = null;
		Gson requestGson = new Gson();
		String url="";
		try {
			if(line.getMdn()!=null ) {
			url=apolloNEProperties.getResourceServiceURL()+ResourceConstants.resourceUpdateServiceUrl.valueOf("GETLINEDETAILSBYMDN").getServiceUrl()+"?mdn="+line.getMdn();
			 //url=sbNEServiceProperties.getLineDetailsMDNResourceServiceURL()+"?mdn="+line.getMdn();
			}
			else if(line.getHostMdn()!=null) {
			 url=apolloNEProperties.getResourceServiceURL()+ResourceConstants.resourceUpdateServiceUrl.valueOf("GETLINEDETAILSBYHOSTMDN").getServiceUrl()+"?hostMdn="+line.getHostMdn();	
			 //url=sbNEServiceProperties.getLineDetailsUsingHostMDNResourceServiceURL()+"?hostMdn="+line.getHostMdn();
			}
			log.info("callLineResourceService URL::" + url);
			URL serviceUrl = new URL(url);
			conn = (HttpURLConnection) serviceUrl.openConnection();
			//byte[] requestData = line.toString().getBytes(StandardCharsets.UTF_8);
			byte[] requestData =requestGson.toJson(line).getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/json");
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
				outputString = responseString;
				/*line = requestGson.fromJson(outputString, Line.class);
				log.info("callLineResourceService response::" + line.toString());
				resourceUpdateRequest.setLine(line);
				log.info("callLineResourceService resourceUpdateRequest::" + resourceUpdateRequest.toString());*/
			}
			isr.close();
			os.close();
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Error in callLineResourceService{}", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		log.info("callLineResourceService :: outputString :: " + outputString);
		return outputString;
	}
	
	public String callTransactionHistoryForUpdateIMSI(String imsi,String rootTransId,String eLineId) {
		String outputString = CommonConstants.EMPTYSTRING;
		HttpURLConnection conn = null;
		Gson requestGson = new Gson();
		try {
			URL serviceUrl = new URL(apolloNEProperties.getResourceServiceURL()+ResourceConstants.resourceUpdateServiceUrl.valueOf("GETTRANSACTIONHISTORYFORUPDATEIMSI").getServiceUrl()+"?imsi="+imsi+"&rootTransId="+rootTransId+"&eLineId="+eLineId);

			//URL serviceUrl = new URL(sbNEServiceProperties.getGetTransactionHistoryForUpdateIMSI()+"?imsi="+imsi+"&rootTransId="+rootTransId+"&eLineId="+eLineId);
			conn = (HttpURLConnection) serviceUrl.openConnection();							
			byte[] requestData = eLineId.getBytes(StandardCharsets.UTF_8);
			//byte[] requestData = requestGson.toJson(line).getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/json");
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
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Error in callTransactionHistoryForUpdateIMSI{}", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		log.info("callTransactionHistoryForUpdateIMSI :: line :: " + outputString);
		return outputString;
	}
	
	public String updateImsibylineid(Sim sim) {
		log.info("updateImsibylineid in MNOOutbound :: " + sim);
		String outputString = CommonConstants.EMPTYSTRING;
		HttpURLConnection conn = null;
		Gson requestGson = new Gson();
		try {
			URL serviceUrl = new URL(apolloNEProperties.getResourceServiceURL()+ResourceConstants.resourceUpdateServiceUrl.valueOf("UPDATEIMSIBYLINEID").getServiceUrl());
			//URL serviceUrl = new URL(sbNEServiceProperties.getUpdateImsibylineid());
			conn = (HttpURLConnection) serviceUrl.openConnection();
			log.info("req::" + requestGson.toJson(sim));
			byte[] requestData = requestGson.toJson(sim).getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/json");
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
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Error in updateImsibylineid{}", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		log.info("updateImsibylineid :MNOOutbound: outputString :: " + outputString);
		return outputString;
	}
	public void insertMNODomainDetails(RequestBean asyncResponseBean) {
		log.info("request inside NEW.........insertMNODomainDetails in mnoservice::" + asyncResponseBean.toString());
		com.excelacom.century.apolloneoutbound.bean.Promotion promotion=new com.excelacom.century.apolloneoutbound.bean.Promotion();
		Account account=new Account();
		Line line=new Line();
		Sim sim=new Sim();
		LinePlan linePlan=new LinePlan();
		ResourceUpdateRequest resourceUpdateRequest=new ResourceUpdateRequest();
		LineHistory lineHistory=new LineHistory();
		TransactionHistory transactionHistory=new TransactionHistory();
		com.excelacom.century.apolloneoutbound.bean.Feature feature=new com.excelacom.century.apolloneoutbound.bean.Feature();
		Device device=new Device();
		com.excelacom.century.apolloneoutbound.bean.ReferenceValue referenceValue = new com.excelacom.century.apolloneoutbound.bean.ReferenceValue();
		String transId = "";
		String transactionName = "";
		String inboundRequest = "";
		String deviceDetails = "";
		int deviceCount = 0;
		String lineId = "";
		String accountId = "";
		String verifyAccount = "";
		String verifyLine = "";
		String formattedrequest = "";
		RequestBean obRequestBean = null;
		RequestBean secondObReqBean = null;
		RequestBean ibRequestBean = null;
		RequestBean deviceDetailsBean = null;
		JSONObject requestobj = new JSONObject();
		JSONArray featureArray = new JSONArray();
		JSONObject featureObj = new JSONObject();
		Map<String, String> featMap = new HashMap<String, String>();
		List<Map<String, Object>> outboundRequest = null;
		String action = "";
		Map<String, Object> inboundReq=new HashMap<String, Object>();
		String transName="";
		String orderType = "";
		String referenceNumberNew="";
		String syncTransaction="";

		try {
			if (asyncResponseBean != null && asyncResponseBean.getReferenceNumber() != null) {
				log.info("insertMNODomainDetails in mnoservice getReferenceNumber::" + asyncResponseBean.getReferenceNumber());
				if(asyncResponseBean.getReferenceNumber().endsWith("SUS")|| asyncResponseBean.getReferenceNumber().endsWith("DEA")||asyncResponseBean.getReferenceNumber().endsWith("HOT")) {
					referenceNumberNew = asyncResponseBean.getReferenceNumber().substring(0, (asyncResponseBean.getReferenceNumber().length()-3));
					log.info("referenceNumberNew::"+referenceNumberNew);
					asyncResponseBean.setReferenceNumber(referenceNumberNew);
				}if(asyncResponseBean.getReferenceNumber().endsWith("Wearable")) {
					referenceNumberNew = asyncResponseBean.getReferenceNumber().substring(0, (asyncResponseBean.getReferenceNumber().length()-8));
					log.info("referenceNumberNew::"+referenceNumberNew);
					asyncResponseBean.setReferenceNumber(referenceNumberNew);
				}
				transId = centuryCIFTemplate.queryForObject(CommonConstants.GETNBTRANSID, new
			    Object[] { asyncResponseBean.getReferenceNumber() }, String.class);
			    log.info("request inside NEW..transId.......::" + transId);
				 
				outboundRequest = (List<Map<String, Object>>) centuryCIFTemplate.queryForList(CommonConstants.GETOBREQUEST1,
						new Object[] { transId });
				log.info("outboundRequest::" + outboundRequest);
				int counter = 0;
				if (outboundRequest.size() > 0) {
					for (int i = 0; i < outboundRequest.size(); i++) {
						log.info("formattedrequest:: " + outboundRequest.get(i).toString());

						for (Map<String, Object> map : outboundRequest) {
							log.info("formattedrequest map:: " + map.get("TRANSACTION_NAME"));
							if (!(map.get("TRANSACTION_NAME").toString().equalsIgnoreCase("AsyncService"))
									&& !(map.get("TRANSACTION_NAME").toString().equalsIgnoreCase("GateWayService"))
									&& !(map.get("TRANSACTION_NAME").toString().equalsIgnoreCase("ConnectionManagerLD"))
									&& !(map.get("TRANSACTION_NAME").toString().equalsIgnoreCase("UpdateLineActivation"))
									&& !(map.get("TRANSACTION_NAME").toString().equalsIgnoreCase("hmnoasyncservice"))) {
								transactionName = map.get("TRANSACTION_NAME").toString();
								if (map.get("REQUEST_MSG").toString().startsWith("[")
										|| map.get("REQUEST_MSG").toString().startsWith("{")) {
									counter++;
									formattedrequest = jsonFormatter(map.get("REQUEST_MSG").toString());
									JSONArray jsonArrayInner = new JSONArray(formattedrequest);
									log.info("map1111:: " + jsonArrayInner);
									requestobj = jsonArrayInner.getJSONObject(0);
									Gson gson = new Gson();
									if (counter == 1) {
										log.info("Inside else if :: ");
										obRequestBean = gson.fromJson(requestobj.toString(), RequestBean.class);
									} else {
										log.info("Inside if obRequestBean:: ");
										secondObReqBean = gson.fromJson(requestobj.toString(), RequestBean.class);
										log.info("Inside if secondObReqBean:: " + secondObReqBean.toString());
									}
									log.info("requestobj:: " + requestobj.toString());
									log.info("request after bean conversion in insertMNODomainDetails111::"
											+ obRequestBean.toString() + "\n---------------"
											+ asyncResponseBean.toString());
								}
							}
							if (map.get("TRANSACTION_NAME").toString().equalsIgnoreCase("Activate Subscriber")
									|| map.get("TRANSACTION_NAME").toString()
											.equalsIgnoreCase("Activate Subscriber Port-In")
									|| map.get("TRANSACTION_NAME").toString().equalsIgnoreCase("Change Feature")
									|| map.get("TRANSACTION_NAME").toString().equalsIgnoreCase("Change Rate Plan")
									|| map.get("TRANSACTION_NAME").toString().equalsIgnoreCase("Change Wholesale Rate Plan")
									|| map.get("TRANSACTION_NAME").toString()
											.equalsIgnoreCase("Device Detection Change Rate Plan")) {
								if (map.get("REQUEST_MSG").toString() != "") {
									if (map.get("REQUEST_MSG").toString().startsWith("{")) {
										JSONObject newObj = new JSONObject(map.get("REQUEST_MSG").toString());
										newObj = newObj.getJSONObject("data");
										if (newObj.has("feature") && newObj.getJSONArray("feature").length() != 0) {
											featureArray = newObj.getJSONArray("feature");
										}
										if (newObj.has("newRatePlan")) {
											JSONObject newRatePlanObj = new JSONObject();
											newRatePlanObj = newObj.getJSONObject("newRatePlan");
											if (newRatePlanObj.has("planCode")) {
												String newWholeSaleplanCode = newRatePlanObj.getString("planCode");
												obRequestBean.setNewRatePlan(newWholeSaleplanCode);
												log.info(
														"obj new rate whole sale plan Code::" + newWholeSaleplanCode);
											}
											if (newRatePlanObj.has("feature")
															&& newRatePlanObj.getJSONArray("feature").length() != 0) {
														featureArray = newRatePlanObj.getJSONArray("feature");
														log.info("array of faetures inside new rate plan::" + featureArray.toString());
											}

										}
										if (newObj.has("oldRatePlan")) {
											JSONObject oldRatePlanObj = new JSONObject();
											oldRatePlanObj = newObj.getJSONObject("oldRatePlan");
											if (oldRatePlanObj.has("planCode")) {
												String oldWholeSaleplanCode = oldRatePlanObj.getString("planCode");
												obRequestBean.setOldRatePlan(oldWholeSaleplanCode);
												log.info(
														"obj old rate whole sale plan Code::" + oldWholeSaleplanCode);
											}
										}

									} else {
										if (map.get("REQUEST_MSG").toString().startsWith("[")) {
											JSONArray requestArray = new JSONArray(map.get("REQUEST_MSG").toString());
											JSONObject obj = new JSONObject();
											obj = requestArray.getJSONObject(0);
											if (obj.has("resellerOrder")) {
												obj = obj.getJSONObject("resellerOrder");
												obj = obj.getJSONObject("data");
												if (obj.has("feature") && obj.getJSONArray("feature").length() != 0) {
													featureArray = obj.getJSONArray("feature");
												}
											} else if (obj.has("MNOChangeRatePlan")) {
												obj = obj.getJSONObject("MNOChangeRatePlan");
												obj = obj.getJSONObject("data");
												log.info("obj::" + obj.toString());
												if (obj.has("oldRatePlan")) {
													JSONObject oldRatePlanObj = new JSONObject();
													oldRatePlanObj = obj.getJSONObject("oldRatePlan");
													if (oldRatePlanObj.has("planCode")) {
														String oldWholeSaleplanCode = oldRatePlanObj
																.getString("planCode");
														obRequestBean.setOldRatePlan(oldWholeSaleplanCode);
														log.info("obj old rate whole sale plan Code::"
																+ oldWholeSaleplanCode);
													}
												}
												if (obj.has("feature") && obj.getJSONArray("feature").length() != 0) {
													featureArray = obj.getJSONArray("feature");
												} else if (obj.has("newRatePlan")) {
													obj = obj.getJSONObject("newRatePlan");
													if (obj.has("feature")
															&& obj.getJSONArray("feature").length() != 0) {
														featureArray = obj.getJSONArray("feature");
														log.info("obj new rate plan::" + featureArray.toString());
													}
													if (obj.has("planCode")) {
														String newWholeSaleplanCode = obj.getString("planCode");
														obRequestBean.setNewRatePlan(newWholeSaleplanCode);
														log.info("obj new rate whole sale plan Code::"
																+ newWholeSaleplanCode);
													}

												}
											}
										}
									}
								}
							}
							if (featureArray.length() > 0) {
								for (int j = 0; j < featureArray.length(); j++) {
									String featureCode = "";
									String subscribe = "";
									featureObj = featureArray.getJSONObject(j);
									Iterator a = featureObj.keys();
									while (a.hasNext()) {
										String keyStr = a.next().toString();
										if (keyStr.equalsIgnoreCase("subscribe")) {
											subscribe = featureObj.getString(keyStr);
											if (subscribe.equalsIgnoreCase("A")) {
												subscribe = "Y";
											} else {
												subscribe = "N";
											}
										}
										if (keyStr.equalsIgnoreCase("featureCode")) {
											featureCode = featureObj.getString(keyStr);
										}
										if (subscribe != "" && featureCode != "") {
											featMap.put(featureCode, subscribe);
										}
									}

								}
							}
						}
					}
				}
				inboundReq = centuryCIFTemplate.queryForMap(CommonConstants.GETIBREQUEST, new Object[] { transId });
				inboundRequest=inboundReq.get(CommonConstants.REQ_MSG).toString();
				transName=inboundReq.get(CommonConstants.TRANS_NAME).toString();
				log.info("inboundRequest::" + inboundRequest);

				if (!"".equalsIgnoreCase(inboundRequest)) {
					formattedrequest = jsonFormatter(inboundRequest);
					JSONArray jsonArrayInner = new JSONArray(formattedrequest);
					log.info("map1111:: " + jsonArrayInner);
					requestobj = jsonArrayInner.getJSONObject(0);
					Gson gson = new Gson();
					ibRequestBean = gson.fromJson(requestobj.toString(), RequestBean.class);
				}
				/*
				 * if (!("Deactivate Subscriber".equalsIgnoreCase(transactionName) ||
				 * "Suspend Subscriber".equalsIgnoreCase(transactionName) ||
				 * "Restore Service".equalsIgnoreCase(transactionName) ||
				 * "Reconnect Service".equalsIgnoreCase(transactionName))) { deviceCount =
				 * centuryCIFTemplate.queryForObject(Constants.GETDEVICEREQ_COUNT, new Object[]
				 * { transId, transId }, Integer.class); if (deviceCount != 0) {
				 * List<Map<String, Object>> deviceDetailsMap = new ArrayList<Map<String,
				 * Object>>(); deviceDetailsMap = (List<Map<String, Object>>) centuryCIFTemplate
				 * .queryForList(Constants.GETDEVICEREQUEST, new Object[] { transId, transId });
				 * for (int i = 0; i < deviceDetailsMap.size(); i++) { if
				 * (deviceDetailsMap.get(i).get(Constants.TRANSACTION_NAME)
				 * .equals(Constants.VALIDATE_DEVICE)) { deviceDetails =
				 * deviceDetailsMap.get(i).get(Constants.RESPONSE_MSG).toString(); break; } else
				 * if (deviceDetailsMap.get(i).get(Constants.TRANSACTION_NAME)
				 * .equals(Constants.ACTIVATE_SUB)) { deviceDetails = getDeviceDetailsValues(
				 * deviceDetailsMap.get(i).get(Constants.REQUEST_MSG).toString()); break; }else
				 * if (deviceDetailsMap.get(i).get(Constants.TRANSACTION_NAME)
				 * .equals(Constants.TN_CHANGE_RATE_PLAN)) { deviceDetails =
				 * getDeviceDetailsValues(
				 * deviceDetailsMap.get(i).get(Constants.REQUEST_MSG).toString()); break; } else
				 * if (deviceDetailsMap.get(i).get(Constants.TRANSACTION_NAME)
				 * .equals(Constants.TRANSACTION_NAME_CS)) { deviceDetails =
				 * getDeviceDetailsValues(
				 * deviceDetailsMap.get(i).get(Constants.REQUEST_MSG).toString()); break; } }
				 * log.info("deviceDetails::" + deviceDetails); } if
				 * (!"".equalsIgnoreCase(deviceDetails)) { formattedrequest =
				 * jsonFormatter(deviceDetails); JSONArray jsonArrayInner = new
				 * JSONArray(formattedrequest); log.info("map1111:: " + jsonArrayInner);
				 * requestobj = jsonArrayInner.getJSONObject(0); Gson gson = new Gson();
				 * deviceDetailsBean = gson.fromJson(requestobj.toString(), RequestBean.class);
				 * } }
				 */
			}
			MapSqlParameterSource input = new MapSqlParameterSource();
			NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(
					centuryCIFTemplate.getDataSource());
			log.info("Inside insertMNORequestDetails Input :: IF " + transName);
			log.info("Inside insertMNORequestDetails Input :: obRequestBean " + obRequestBean+" asyncResponseBean"+asyncResponseBean);
		
			if (obRequestBean != null && asyncResponseBean != null) {
				if (obRequestBean.getSubscriberGroupCd() != null) {
					input.addValue("SUBGROUPCD", obRequestBean.getSubscriberGroupCd());
					account.setSubgroupcd(obRequestBean.getSubscriberGroupCd());
				} else if (asyncResponseBean.getSubscriberGroupCd() != null) {
					input.addValue("SUBGROUPCD", asyncResponseBean.getSubscriberGroupCd());
					account.setSubgroupcd(asyncResponseBean.getSubscriberGroupCd());
				} else {
					input.addValue("SUBGROUPCD", null);
				}
				if (ibRequestBean.getAccountNumber() != null) {
					input.addValue("ACCOUNT_NUMBER", ibRequestBean.getAccountNumber());
					line.setAccountNumber(ibRequestBean.getAccountNumber());
					account.setAccountNumber(ibRequestBean.getAccountNumber());
					lineHistory.setAccountNumber(ibRequestBean.getAccountNumber());
					accountId = ibRequestBean.getAccountNumber();
					//verifyAccount = verifyAccount(accountId);
				} else {
					input.addValue("ACCOUNT_NUMBER", null);
				}
				if (ibRequestBean.getAction() != null) {
					action = ibRequestBean.getAction();
				}
				if (obRequestBean.getContextId() != null) {
					input.addValue("CONTEXT_ID", obRequestBean.getContextId());
					account.setContextId(obRequestBean.getContextId());
				} else if (ibRequestBean.getContextId() != null) {
					input.addValue("CONTEXT_ID", ibRequestBean.getContextId());
					account.setContextId(ibRequestBean.getContextId());
				} else {
					input.addValue("CONTEXT_ID", null);
				}
				if (obRequestBean.getType() != null) {
					input.addValue("ACCT_TYPE", obRequestBean.getType());
					account.setAccountType(obRequestBean.getType());
				} else if (ibRequestBean.getType() != null) {
					input.addValue("ACCT_TYPE", ibRequestBean.getType());
					account.setAccountType(ibRequestBean.getType());
				} else {
					input.addValue("ACCT_TYPE", null);
				}
				if (obRequestBean.getPin() != null) {
					input.addValue("PIN", obRequestBean.getPin());
					log.info("obRequestBean.getPin()::"+obRequestBean.getPin());
					account.setPin(obRequestBean.getPin());
				} else if (ibRequestBean.getPin() != null) {
					input.addValue("PIN", ibRequestBean.getPin());
					log.info("ibRequestBean.getPin()::"+ibRequestBean.getPin());
					account.setPin(ibRequestBean.getPin());
				} else {
					input.addValue("PIN", null);
				}
				if (obRequestBean.getImsi() != null) {
					input.addValue("IMSI", obRequestBean.getImsi());
					sim.setImsi(obRequestBean.getImsi());
					transactionHistory.setImsi(obRequestBean.getImsi());
				} else if (asyncResponseBean.getImsi() != null) {
					input.addValue("IMSI", asyncResponseBean.getImsi());
					sim.setImsi(asyncResponseBean.getImsi());
					transactionHistory.setImsi(asyncResponseBean.getImsi());
					
				} else {
					input.addValue("IMSI", null);
				}
				if (ibRequestBean.getReferenceNumber() != null) {
					input.addValue("REFERENCENUMBER", obRequestBean.getReferenceNumber());
					line.setReferenceNumber(obRequestBean.getReferenceNumber());
				} else if (asyncResponseBean.getReferenceNumber() != null) {
					input.addValue("REFERENCENUMBER", asyncResponseBean.getReferenceNumber());
					line.setReferenceNumber(asyncResponseBean.getReferenceNumber());
				} else {
					input.addValue("REFERENCENUMBER", null);
				}
				if (obRequestBean.getMdn() != null) {
					input.addValue("MDN", obRequestBean.getMdn());
					line.setMdn(obRequestBean.getMdn());
					lineHistory.setMdn(obRequestBean.getMdn());
				} else if (asyncResponseBean.getMdn() != null) {
					input.addValue("MDN", asyncResponseBean.getMdn());
					line.setMdn(asyncResponseBean.getMdn());
					lineHistory.setMdn(asyncResponseBean.getMdn());
				} else {
					input.addValue("MDN", null);
					input.addValue("HOSTMDN", null);
				}
				if (obRequestBean.getHostMDN() != null) {
					input.addValue("HOSTMDN", obRequestBean.getHostMDN());
					line.setHostMdn(obRequestBean.getHostMDN());
				} else if (asyncResponseBean.getHostMDN() != null) {
					input.addValue("HOSTMDN", asyncResponseBean.getHostMDN());
					line.setHostMdn(asyncResponseBean.getHostMDN());
				} else {
					input.addValue("HOSTMDN", null);
				}
				if (asyncResponseBean.getMin() != null) {
					input.addValue("MIN", asyncResponseBean.getMin());
					line.setMin(asyncResponseBean.getMin());
					transactionHistory.setMin(asyncResponseBean.getMin());
				} else {
					input.addValue("MIN", null);
				}
				if (ibRequestBean.getLineId() != null) {
					input.addValue("LINEID", ibRequestBean.getLineId());
					lineId = ibRequestBean.getLineId();
					//verifyLine = verifyLine(lineId);
					device.seteLineId(ibRequestBean.getLineId());
					line.seteLineId(ibRequestBean.getLineId());
					sim.seteLineId(ibRequestBean.getLineId());
					linePlan.setELineId(ibRequestBean.getLineId());
					lineHistory.seteLineId(ibRequestBean.getLineId());
					feature.setELineId(ibRequestBean.getLineId());
					promotion.seteLineId(ibRequestBean.getLineId());
				} else {
					input.addValue("LINEID", null);
				}
				if (obRequestBean.getBillCycleResetDay() != null) {
					input.addValue("BILLCYCLEDAY", obRequestBean.getBillCycleResetDay());
					line.setBcd(obRequestBean.getBillCycleResetDay());
				} else if (ibRequestBean.getBillCycleResetDay() != null) {
					input.addValue("BILLCYCLEDAY", ibRequestBean.getBillCycleResetDay());
					line.setBcd(ibRequestBean.getBillCycleResetDay());
				} else {
					input.addValue("BILLCYCLEDAY", null);
				}
				if (obRequestBean.getImei() != null) {
					input.addValue("IMEI", obRequestBean.getImei());
					device.setImei(obRequestBean.getImei());
				}else if (ibRequestBean.getImei() != null) {
					input.addValue("IMEI", ibRequestBean.getImei());
					device.setImei(ibRequestBean.getImei());
				}  else if (asyncResponseBean.getImei() != null) {
					input.addValue("IMEI", asyncResponseBean.getImei());
					device.setImei(asyncResponseBean.getImei());
				} else {
					input.addValue("IMEI", null);
				}
				if (obRequestBean.getOldDeviceId() != null) {
					resourceUpdateRequest.setOldIMEI(obRequestBean.getOldDeviceId());
				}else if (ibRequestBean.getOldDeviceId() != null) {
					resourceUpdateRequest.setOldIMEI(ibRequestBean.getOldDeviceId());
				}
				if (obRequestBean.getNewDeviceId() != null) {
					//resourceUpdateRequest.setNewMEI(obRequestBean.getNewDeviceId());
					log.info("obRequestBean.getNewDeviceId()::"+obRequestBean.getNewDeviceId());
					device.setImei(obRequestBean.getNewDeviceId());
					//device.setDeviceId(obRequestBean.getNewDeviceId());
				}else if (ibRequestBean.getNewDeviceId() != null) {
					//resourceUpdateRequest.setIMEI(ibRequestBean.getNewDeviceId());
					log.info("ibRequestBean.getNewDeviceId()::"+ibRequestBean.getNewDeviceId());
					device.setImei(ibRequestBean.getNewDeviceId());
					//device.setDeviceId(ibRequestBean.getNewDeviceId());
				}
				if (obRequestBean.getOldIccid() != null) {
					resourceUpdateRequest.setOldICCID(obRequestBean.getOldIccid());
				}else if (ibRequestBean.getOldIccid() != null) {
					resourceUpdateRequest.setOldICCID(ibRequestBean.getOldIccid());
				}
				
				if (asyncResponseBean.getIccid() != null) {
					input.addValue("ICCID", asyncResponseBean.getIccid());
					sim.setIccid(asyncResponseBean.getIccid());
				} else if (obRequestBean.getIccid() != null && !obRequestBean.getIccid().isEmpty()) {
					input.addValue("ICCID", obRequestBean.getIccid());
					sim.setIccid(obRequestBean.getIccid());
				} else {
					input.addValue("ICCID", null);
				}
				if (ibRequestBean.getPlanCode() != null) {
					input.addValue("RETAIL_PLANCODE", ibRequestBean.getPlanCode());
					linePlan.setRetailPlan(ibRequestBean.getPlanCode());
				} else {
					input.addValue("RETAIL_PLANCODE", null);
				}
				if (obRequestBean.getPlanCode() != null) {
					input.addValue("WHOLESALE_PLAN", obRequestBean.getPlanCode());
					linePlan.setWhsPlan(obRequestBean.getPlanCode());
				} else {
					input.addValue("WHOLESALE_PLAN", null);
				}
				// Device details update
				if (deviceDetailsBean != null) {
					if (deviceDetailsBean.getMake() != null) {
						input.addValue("MAKE", deviceDetailsBean.getMake());
						device.setMake(deviceDetailsBean.getMake());
						if(deviceDetailsBean.getMake().equalsIgnoreCase(CommonConstants.OS_APL)) {
							device.setOs("IOS");
						}else {
							device.setOs("Android");
						}
					} else {
						input.addValue("MAKE", null);
					}
					if (deviceDetailsBean.getDeviceId() != null) {
						input.addValue("IMEI", deviceDetailsBean.getDeviceId());
						device.setImei(deviceDetailsBean.getDeviceId());
						//device.setDeviceId(deviceDetailsBean.getDeviceId());
						
					} else {
						input.addValue("IMEI", null);
					}
					if (deviceDetailsBean.getNewDeviceId() != null) {
						log.info("deviceDetailsBean.getNewDeviceId()::"+deviceDetailsBean.getNewDeviceId());
						input.addValue("IMEI", deviceDetailsBean.getNewDeviceId());
						device.setImei(deviceDetailsBean.getNewDeviceId());
						//device.setDeviceId(deviceDetailsBean.getNewDeviceId());
					} else {
						input.addValue("IMEI", null);
					}
					if (deviceDetailsBean.getModel() != null) {
						input.addValue("MODEL", deviceDetailsBean.getModel());
						device.setModel(deviceDetailsBean.getModel());
					} else {
						input.addValue("MODEL", null);
					}
					if (deviceDetailsBean.getMode() != null) {
						input.addValue("MODE", deviceDetailsBean.getMode());
						device.setModeValue(deviceDetailsBean.getMode());
						// DEVICE_TYPE
						String deviceType = deviceDetailsBean.getMode().toString();
						input.addValue("DEVICE_TYPE", deviceType.substring(0, 2));
						device.setDeviceType(deviceType.substring(0, 2));
						sim.setFirstActivatedNetwork(deviceType.substring(0, 2));
					} else {
						input.addValue("MODE", null);
					}
					if (deviceDetailsBean.getCdmaLess() != null) {
						input.addValue("CDMALESS", deviceDetailsBean.getCdmaLess());
						device.setCdmaLess(deviceDetailsBean.getCdmaLess());
					} else {
						input.addValue("CDMALESS", "N");
						device.setCdmaLess("N");
					}

					if (deviceDetailsBean.getDeviceCategory() != null) {
						input.addValue("DEVICE_CATEGORY", deviceDetailsBean.getDeviceCategory());
					} else {
						input.addValue("DEVICE_CATEGORY", null);
					}
					if (deviceDetailsBean.getProdType() != null) {
						input.addValue("PRODTYPE", deviceDetailsBean.getProdType());
					} else {
						input.addValue("PRODTYPE", null);
					}
				}
				if (secondObReqBean != null) {
					log.info("secondObReqBea## " + secondObReqBean.toString());
				}
				
				line.setIsMigrated(CommonConstants.NO);
				//Added for sync Transaction in MWTGNSLNI-270 & 320 By Geetha Perumal
				if(ibRequestBean.getSyncTransaction() != null) {
					syncTransaction = ibRequestBean.getSyncTransaction();
				}		
				log.info("SyncTransaction:: " + syncTransaction);
				if (syncTransaction.equalsIgnoreCase("T")) {
					syncTransaction = CommonConstants.YES;
				} else {
					syncTransaction = CommonConstants.NO;
				}
				line.setSyncTransaction(syncTransaction);
				lineHistory.setTransactionId(transId);
				account.setIsMigrated(CommonConstants.NO);
				resourceUpdateRequest.setTransactionName(transactionName);
				resourceUpdateRequest.setTransationId(transId);
				resourceUpdateRequest.setFeatureMap(featMap);
				log.info("device## device" + device.toString());
				log.info("resourceUpdateRequest## resourceUpdateRequest" + resourceUpdateRequest.toString());
				log.info("line## resourceUpdateRequest" + line.toString());
				// Added ReferenceValue Bean for EventType MWTGNSL-957 by Geetha Perumal
				log.info("transactionName::" + transactionName);
				log.info("asyncResponseBean::" + asyncResponseBean+ "obRequestBean::" +obRequestBean+ "ibRequestBean::" +ibRequestBean+ "secondObReqBean::" +secondObReqBean);
				setResourceDetails(asyncResponseBean,obRequestBean, ibRequestBean, account, line,device, sim, linePlan, lineHistory,
						resourceUpdateRequest,inboundRequest,feature,secondObReqBean,deviceDetailsBean,transactionHistory,promotion,referenceValue);
			}else {
				log.info("transactionName  " + transName);
				if(transName.equalsIgnoreCase("Update Account Change")) {
					log.info("ibRequestBean Update Account Change " + ibRequestBean);
					if (ibRequestBean.getAccountNumber() != null) {
						account.setAccountNumber(ibRequestBean.getAccountNumber());
					} else {
						input.addValue("ACCOUNT_NUMBER", null);
					}
					resourceUpdateRequest.setTransactionName(transName);
					resourceUpdateRequest.setTransationId(transId);
					setResourceDetails(asyncResponseBean,obRequestBean, ibRequestBean, account, line,device, sim, linePlan, lineHistory,
							resourceUpdateRequest,inboundRequest,feature,secondObReqBean,deviceDetailsBean,transactionHistory,promotion,referenceValue);
				}
			}
		} catch (JSONException e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0007+" : Exception{}", e);
		}
	}
	
	public SearchEnvironmentResultDto callSimResourceServiceForSearchEnvironment(Sim simBean) {
		log.info("Inside callSimResourceService::");
		String outputString = CommonConstants.EMPTYSTRING;
		HttpURLConnection conn = null;
		Gson requestGson = new Gson();
		Boolean serviceMigFlag = true;
		SearchEnvironmentResultDto searchEnvironmentResultDto = null;
		try {
			String url = apolloNEProperties.getResourceServiceURL()+ResourceConstants.resourceUpdateServiceUrl.valueOf("SIMDETAILSBYICCIDFORSEARCHENVIRONMENT").getServiceUrl()+"?iccid="+simBean.getIccid() + "&serviceMigFlag="+serviceMigFlag;
			log.info("callSimResourceServiceForSearchEnvironment URL::" + url);
			log.info("callSimResourceServiceForSearchEnvironment URL::" + url);
			URL serviceUrl = new URL(url);
			conn = (HttpURLConnection) serviceUrl.openConnection();
			byte[] requestData = requestGson.toJson(simBean).getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/json");
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
				outputString =  responseString;
				if (StringUtils.hasText(outputString))
					searchEnvironmentResultDto = requestGson.fromJson(outputString, SearchEnvironmentResultDto.class);
				//log.info("Request Bean in callSimResourceServiceForSearchEnvironment::" + sim.toString());
			}
			isr.close();
			os.close();
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Error in callSimResourceServiceForSearchEnvironment{}", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		log.info("callSimResourceServiceForSearchEnvironment :: outputString :: " + outputString);
		return searchEnvironmentResultDto;
	}
	
	public String jsonFormatter(String inputJson) {
		log.info("inputJson::" + inputJson);
		String response = null;
		JSONObject finalObj = new JSONObject();
		try {
			if (inputJson.startsWith("[")) {
				JSONArray jsonarr = new JSONArray(inputJson);
				for (int i = 0; i < jsonarr.length(); i++) {
					JSONObject obj = jsonarr.getJSONObject(i);
					response = "[" + printJsonObject(obj, finalObj) + "]";
					log.info("JsonArray Formatter Final Response::" + response);
				}
			} else {
				JSONObject object = new JSONObject(inputJson);
				response = "[" + printJsonObject(object, finalObj) + "]";
				log.info("JsonObject Formatter Final Response::" + response);
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
							String key = jsonObject1.get("type").toString();

							String str = jsonObject1.toString();

							if (key.equalsIgnoreCase("hostMDN")) {
								str = str.replace("value", "hostMDN");
							} else {
								str = str.replace("value", "mdn");
							}
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
	
	public void setResourceDetails(RequestBean asyncResponseBean,RequestBean obRequestBean, RequestBean ibRequestBean, Account account, Line line,
			Device device,Sim sim, LinePlan linePlan, LineHistory lineHistory, ResourceUpdateRequest resourceUpdateRequest,String inboundRequest,Feature feature,
			RequestBean secondObReqBean,RequestBean deviceDetailsBean,TransactionHistory transactionHistory,com.excelacom.century.apolloneoutbound.bean.Promotion promotion, ReferenceValue referenceValue) {
		
		switch (resourceUpdateRequest.getTransactionName()) {		
		case CommonConstants.TRANSACTION_NAME_SS:
			resourceUpdateRequest = suspendResourceDetails(account, line, sim, linePlan, lineHistory,
					resourceUpdateRequest,referenceValue,asyncResponseBean);
			break;
		case CommonConstants.TRANSACTION_NAME_SH:
			resourceUpdateRequest=hotLineResourceDetails(account, line, sim,lineHistory, resourceUpdateRequest, obRequestBean,referenceValue,asyncResponseBean);
			break;		
		case CommonConstants.TRANSACTION_NAME_RS:
			resourceUpdateRequest=restoreResourceDetails(account, line, sim, linePlan, resourceUpdateRequest,lineHistory,referenceValue,asyncResponseBean);
			break;		
		case CommonConstants.TRANSACTION_NAME_RH:
			resourceUpdateRequest=removeHotLineResourceDetails(account, line, sim, lineHistory, resourceUpdateRequest, obRequestBean,referenceValue,asyncResponseBean);
			break;	
		case CommonConstants.TRANSACTION_NAME_SD:
			resourceUpdateRequest = deactivateResourceDetails(account, line, sim, linePlan, lineHistory,
					resourceUpdateRequest,device,inboundRequest,referenceValue);
			break;
		}
		//resourceUpdateRequest=insertIntoTransactionHistory(inboundRequest,resourceUpdateRequest.getTransationId(),resourceUpdateRequest.getTransactionName(),transactionHistory,resourceUpdateRequest);
		// Update TransactionHistory
		log.info("update transaction History TransactionId" + resourceUpdateRequest.getTransationId());
		transactionHistory.setTransactionEndDate(getTimeStamp());
		transactionHistory.setModifiedDate(getTimeStamp());
		transactionHistory.setTransactionId(resourceUpdateRequest.getTransationId());
		resourceUpdateRequest.setTransactionHistory(transactionHistory);
		log.info("Calling callResourceService after transactionHistory......"+resourceUpdateRequest.toString());
		log.info("Calling callResourceService resourceUpdateRequest.getTransactionName()......"+resourceUpdateRequest.getTransactionName());
		if(!("Deactivate Transfer Wearable".equalsIgnoreCase(resourceUpdateRequest.getTransactionName()))) {
			callResourceService(resourceUpdateRequest);
		}		
	}
	
	public ResourceUpdateRequest suspendResourceDetails(Account account, Line line, Sim sim, LinePlan linePlan,
			LineHistory lineHistory, ResourceUpdateRequest resourceUpdateRequest,ReferenceValue referenceValue,RequestBean asyncResponseBean) {
		try {
			Line smartWatchLine = new Line();
			String lineId = "";
			log.info("Inside suspendResourceDetails asyncResponseBean::" + asyncResponseBean.toString());
			
			/* account details */
			//account.setAccountStatus(CommonConstants.ACC_STATUS_SS);
			account.setModifiedBy(CommonConstants.MODITY_BY);
			account.setModifiedDate(getTimeStamp());

			/* Line details */
			
			log.info("line DETAILS..." + line.toString());			
			line.setMdn(asyncResponseBean.getMdn());
			log.info("line DETAILS MDN VALUE..." + line.getMdn());
			smartWatchLine = callLineDetailsByMDN(line.getMdn());
			log.info("smart Watch mdn line...:" + smartWatchLine.toString());
			if(smartWatchLine!=null && smartWatchLine.getLineType()!=null && smartWatchLine.getLineType().equalsIgnoreCase("SMARTWATCH")) {
				lineId = smartWatchLine.geteLineId();
				line.seteLineId(lineId);
				lineHistory.seteLineId(lineId);
			}
			line.setLineStatus(CommonConstants.LINE_STATUS_SS);
			line.setModifiedDate(getTimeStamp());
			line.setModifiedBy(CommonConstants.MODITY_BY);
			log.info("line Bean after setting values suspendResourceDetails::" + line.toString());

			/* sim details */

			sim.setSimStatus(CommonConstants.SIM_STATUS_SS);
			//sim.setActivationDate(getTimeStamp());
			sim.setModifiedBy(CommonConstants.MODITY_BY);
			sim.setModifiedDate(getTimeStamp());

			/* lineHistory details */

			lineHistory.setOrdType(CommonConstants.ORDER_TYPE_SS);
			lineHistory.setTransactionType(CommonConstants.TRANSACTION_TYPE_SS);
			lineHistory.setLineStatus(CommonConstants.LINE_STATUS_SS);
			lineHistory.setOldValue(CommonConstants.OLD_VALUE_SS);
			lineHistory.setNewValue(CommonConstants.NEW_VALUE_SS);
			lineHistory.setFieldType(CommonConstants.FIELD_TYPE_SS);
			lineHistory.setCreatedBy(CommonConstants.CREATED_BY);
			//lineHistory.setCreatedDate(getTimeStamp());
			lineHistory.setStartDate(getTimeStamp());

			/* LinePlan details */
			//linePlan.setEndDate(getTimeStamp());
			linePlan.setModifiedBy(CommonConstants.MODITY_BY);
			//linePlan.setModifiedDate(getTimeStamp());


			/* resource details */
			resourceUpdateRequest.setAccount(account);
			resourceUpdateRequest.setLineDetails(line);
			resourceUpdateRequest.setSimDetails(sim);
			resourceUpdateRequest.setLinePlan(linePlan);
			resourceUpdateRequest.setLineHistory(lineHistory);
			// Added ReferenceValue Bean for EventType MWTGNSL-957 by Geetha Perumal
			resourceUpdateRequest.setReferenceValue(referenceValue);
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0019+" : Exception in suspendResourceDetails{}",e);
		}
		return resourceUpdateRequest;
	}
	
	public ResourceUpdateRequest hotLineResourceDetails(Account account, Line line, Sim sim, LineHistory lineHistory,
			ResourceUpdateRequest resourceUpdateRequest, RequestBean obRequestBean,ReferenceValue referenceValue,RequestBean asyncResponseBean) {
		try {
			log.info("Inside hotLineResourceDetails asyncResponseBean::" + asyncResponseBean.toString());
			//account.setAccountStatus(CommonConstants.ACC_STATUS_SH);
			account.setModifiedBy(CommonConstants.MODITY_BY);
			account.setModifiedDate(getTimeStamp());
			
			/* Line details */
			
			Line smartWatchLine = new Line();
			String lineId = "";	
			line.setMdn(asyncResponseBean.getMdn());			
			smartWatchLine = callLineDetailsByMDN(line.getMdn());
			log.info("smart Watch mdn line...:" + smartWatchLine.toString());
			log.info("smartWatchLine.getLineType()...:" + smartWatchLine.getLineType());
			if(smartWatchLine!=null && smartWatchLine.getLineType()!=null && smartWatchLine.getLineType().equalsIgnoreCase("SMARTWATCH")) {
				log.info("Insdie smart watch condition for hotline...:" + smartWatchLine.getLineType());
				lineId = smartWatchLine.geteLineId();
				line.seteLineId(lineId);				
				lineHistory.seteLineId(lineId);
				
			}
			line.setLineStatus(CommonConstants.LINE_STATUS_SH);
			line.setModifiedDate(getTimeStamp());
			line.setHotlineType(obRequestBean.getHotlineType());
			line.setModifiedBy(CommonConstants.MODITY_BY);
			
			/* sim details */
			sim.setSimStatus(CommonConstants.SIM_STATUS_SH);
			sim.setModifiedBy(CommonConstants.MODITY_BY);
			sim.setModifiedDate(getTimeStamp());

			/* lineHistory details */

			lineHistory.setOrdType(CommonConstants.ORDER_TYPE_SH);
			lineHistory.setTransactionType(CommonConstants.TRANSACTION_TYPE_SH);
			//lineHistory.setFieldType(CommonConstants.FIELD_TYPE_SH_1);
			lineHistory.setCreatedBy(CommonConstants.CREATED_BY);
			//lineHistory.setNewValue(lineHistory.getMdn());
			lineHistory.setNewValue(CommonConstants.ORDER_TYPE_SH);
			//lineHistory.setCreatedDate(getTimeStamp());
			lineHistory.setStartDate(getTimeStamp());
			lineHistory.setLineStatus(CommonConstants.ORDER_TYPE_SH);
			//lineHistory.setModifiedBy(CommonConstants.MODITY_BY);
			//lineHistory.setModifiedDate(getTimeStamp());
			lineHistory.setFieldType(CommonConstants.FIELD_TYPE_SH_2);
			log.info("hotLineResourceDetails  lineHistory Table...:" + lineHistory.toString());
			
			/* resource details */
			resourceUpdateRequest.setAccount(account);
			resourceUpdateRequest.setSimDetails(sim);
			resourceUpdateRequest.setLineDetails(line);
			resourceUpdateRequest.setLineHistory(lineHistory);
			// Added ReferenceValue Bean for EventType MWTGNSL-957 by Geetha Perumal
			resourceUpdateRequest.setReferenceValue(referenceValue);
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0019+" : Exception in hotLineResourceDetails{}",e);
		}
		return resourceUpdateRequest;
	}

	public ResourceUpdateRequest restoreResourceDetails(Account account, Line line, Sim sim, LinePlan linePlan,
			ResourceUpdateRequest resourceUpdateRequest,LineHistory lineHistory,ReferenceValue referenceValue,RequestBean asyncResponseBean) {
		try {
			log.info("Inside restoreResourceDetails asyncResponseBean::" + asyncResponseBean.toString());
			
			/* account details */
			account.setAccountStatus(CommonConstants.ACC_STATUS);
			account.setModifiedBy(CommonConstants.MODITY_BY);
			account.setModifiedDate(getTimeStamp());

			/* Line details */
			
			Line smartWatchLine = new Line();
			String lineId = "";
			line.setMdn(asyncResponseBean.getMdn());
			smartWatchLine = callLineDetailsByMDN(line.getMdn());
			log.info("smart Watch mdn line...:" + smartWatchLine.toString());
			if(smartWatchLine!=null && smartWatchLine.getLineType()!=null && smartWatchLine.getLineType().equalsIgnoreCase("SMARTWATCH")) {
				lineId = smartWatchLine.geteLineId();
				line.seteLineId(lineId);
				lineHistory.seteLineId(lineId);
			}
			line.setLineStatus(CommonConstants.LINE_STATUS_RS);
			line.setModifiedDate(getTimeStamp());
			line.setModifiedBy(CommonConstants.MODITY_BY);

			/* sim details */

			sim.setSimStatus(CommonConstants.SIM_STATUS_RS);
			sim.setModifiedBy(CommonConstants.MODITY_BY);
			sim.setModifiedDate(getTimeStamp());
			
			
			/* LinePlan details */
		//	linePlan.setEndDate(getTimeStamp());
			linePlan.setModifiedBy(CommonConstants.MODITY_BY);
			//linePlan.setModifiedDate(getTimeStamp());
			
			/* Line History details */
			lineHistory.setOrdType(CommonConstants.ORDER_TYPE_RS);
			lineHistory.setTransactionType(CommonConstants.TRANSACTION_TYPE_RS);
			lineHistory.setLineStatus(CommonConstants.LINE_STATUS_RS);
			lineHistory.setOldValue(CommonConstants.NEW_VALUE_SS);
			lineHistory.setNewValue(CommonConstants.OLD_VALUE_SS);
			lineHistory.setFieldType(CommonConstants.FIELD_TYPE_SS);
			lineHistory.setCreatedBy(CommonConstants.CREATED_BY);
			//lineHistory.setCreatedDate(getTimeStamp());
			lineHistory.setStartDate(getTimeStamp());

			/* resource details */
			resourceUpdateRequest.setAccount(account);
			resourceUpdateRequest.setSimDetails(sim);
			resourceUpdateRequest.setLineDetails(line);
			resourceUpdateRequest.setLinePlan(linePlan);
			// Added ReferenceValue Bean for EventType MWTGNSL-957 by Geetha Perumal
			resourceUpdateRequest.setReferenceValue(referenceValue);
			resourceUpdateRequest.setLineHistory(lineHistory);
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0019+" : Exception in restoreResourceDetails{}",e);
		}
		return resourceUpdateRequest;
	}
	
	public ResourceUpdateRequest removeHotLineResourceDetails(Account account, Line line, Sim sim, LineHistory lineHistory,
			ResourceUpdateRequest resourceUpdateRequest, RequestBean obRequestBean,ReferenceValue referenceValue,RequestBean asyncResponseBean) {
		try {
			log.info("Inside removeHotLineResourceDetails asyncResponseBean::" + asyncResponseBean.toString());
			account.setAccountStatus(CommonConstants.ACC_STATUS);
			account.setModifiedBy(CommonConstants.MODITY_BY);
			account.setModifiedDate(getTimeStamp());

			/* Line details */
			
			Line smartWatchLine = new Line();
			String lineId = "";
			line.setMdn(asyncResponseBean.getMdn());			
			smartWatchLine = callLineDetailsByMDN(line.getMdn());
			log.info("smart Watch mdn line...:" + smartWatchLine.toString());
			if(smartWatchLine!=null && smartWatchLine.getLineType()!=null && smartWatchLine.getLineType().equalsIgnoreCase("SMARTWATCH")) {
				log.info("Insdie smart watch condition for hotline...:" + smartWatchLine.getLineType());
				lineId = smartWatchLine.geteLineId();
				line.seteLineId(lineId);				
				lineHistory.seteLineId(lineId);
			}
			line.setLineStatus(CommonConstants.LINE_STATUS);
			line.setModifiedDate(getTimeStamp());
			line.setHotlineType(obRequestBean.getHotlineType());
			line.setModifiedBy(CommonConstants.MODITY_BY);

			sim.setSimStatus(CommonConstants.SIM_STATUS);
			sim.setModifiedBy(CommonConstants.MODITY_BY);
			sim.setModifiedDate(getTimeStamp());
			
			/* lineHistory details */
			
			lineHistory.setAcctStatus(CommonConstants.ACC_STATUS);
			lineHistory.setOrdType(CommonConstants.ORDER_TYPE_RH);
			lineHistory.setTransactionType(CommonConstants.TRANSACTION_TYPE_RH);
			//lineHistory.setNewValue(lineHistory.getMdn());
			//lineHistory.setFieldType(CommonConstants.FIELD_TYPE_RH);
			lineHistory.setFieldType(CommonConstants.FIELD_TYPE_SH_2);
			lineHistory.setCreatedBy(CommonConstants.CREATED_BY);
			//lineHistory.setCreatedDate(getTimeStamp());
			lineHistory.setStartDate(getTimeStamp());
			lineHistory.setLineStatus(CommonConstants.LINE_STATUS);

			/* resource details */
			resourceUpdateRequest.setAccount(account);
			resourceUpdateRequest.setSimDetails(sim);
			resourceUpdateRequest.setLineDetails(line);
			resourceUpdateRequest.setLineHistory(lineHistory);
			// Added ReferenceValue Bean for EventType MWTGNSL-957 by Geetha Perumal
			resourceUpdateRequest.setReferenceValue(referenceValue);
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0019+" : Exception in activateResourceDetails{}",e);
		}
		return resourceUpdateRequest;
	}
	
	public ResourceUpdateRequest deactivateResourceDetails(Account account, Line line, Sim sim, LinePlan linePlan,
			LineHistory lineHistory, ResourceUpdateRequest resourceUpdateRequest,Device device,String inboundRequest,ReferenceValue referenceValue) {
		try {
			String transactionType="";
			if (inboundRequest.contains("transactionType")) {
				JSONObject requestJsonObject = new JSONObject(inboundRequest);
				if (requestJsonObject.has(CommonConstants.DATA)) {
					JSONObject requestJsonObjectData = requestJsonObject.getJSONObject(CommonConstants.DATA);
					if (requestJsonObjectData.has("transactionType")) {
						transactionType = requestJsonObjectData.getString("transactionType");
						log.info("Inside deactivateResourceDetails method::transactionType::" + transactionType);
					}
				}
			}
			
			/* account details */
			//account.setAccountStatus(CommonConstants.ACC_STATUS_SD);
			account.setModifiedBy(CommonConstants.MODITY_BY);
			account.setModifiedDate(getTimeStamp());
			
			
			/* Line details */
			line.setLineStatus(CommonConstants.LINE_STATUS_SD);
			//line.setDeactivationDate(getTimeStamp());
			line.setModifiedDate(getTimeStamp());
			line.setModifiedBy(CommonConstants.MODITY_BY);

			/* sim details */
			sim.setSimStatus(CommonConstants.SIM_STATUS_SD);
			sim.setDeactivationDate(getTimeStamp());
			sim.setModifiedDate(getTimeStamp());
			sim.setModifiedBy(CommonConstants.MODITY_BY);
			/* device details */
			device.setModifiedDate(getTimeStamp());
			device.setModifiedBy(CommonConstants.MODITY_BY);
			device.setEndDate(getTimeStamp());

			/* LinePlan details */
			//linePlan.setEndDate(getTimeStamp());
			linePlan.setModifiedBy(CommonConstants.MODITY_BY);
			//linePlan.setModifiedDate(getTimeStamp());
			
			/* Line History details */
			lineHistory.setOrdType(CommonConstants.ORDER_TYPE_SD);
			lineHistory.setTransactionType(CommonConstants.TRANSACTION_TYPE_SD);
			//lineHistory.setCreatedDate(getTimeStamp());
			lineHistory.setCreatedBy(CommonConstants.CREATED_BY);
			lineHistory.setAcctStatus(CommonConstants.ACC_STATUS_SD);
			lineHistory.setLineStatus(CommonConstants.LINE_STATUS_SD);
			lineHistory.setOldValue(CommonConstants.OLD_VALUE_SS);
			lineHistory.setNewValue(CommonConstants.LINE_STATUS_SD);
			lineHistory.setStartDate(getTimeStamp());
			lineHistory.setFieldType(CommonConstants.FIELD_TYPE_SS);
			
			/* resource details */
			if(transactionType.equalsIgnoreCase("TW")) {
				resourceUpdateRequest.setTransactionName("Deactivate Transfer Wearable");
			}
			resourceUpdateRequest.setAccount(account);
			resourceUpdateRequest.setLineDetails(line);
			resourceUpdateRequest.setSimDetails(sim);
			resourceUpdateRequest.setLinePlan(linePlan);
			resourceUpdateRequest.setDeviceDetails(device);
			resourceUpdateRequest.setLineHistory(lineHistory);
			// Added ReferenceValue Bean for EventType MWTGNSL-957 by Geetha Perumal
			resourceUpdateRequest.setReferenceValue(referenceValue);
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0019+" : Exception in deactivateResourceDetails{}",e);
		}
		return resourceUpdateRequest;
	}
	
	
	public Line callLineDetailsByMDN(String mdn) {
		log.info("Inside callLineDetailsByMDN::" + mdn);
		String outputString = CommonConstants.EMPTYSTRING;
		//String outputString = null;
		HttpURLConnection conn = null;
		Gson requestGson = new Gson();
		String url="";
		Line line = null;
		try {
			url=apolloNEProperties.getResourceServiceURL()+ResourceConstants.resourceUpdateServiceUrl.valueOf("GETLINEDETAILSBYMDN").getServiceUrl()+"?mdn="+mdn;
			log.info("getLinedetailsbymdn URL::" + url);
			URL serviceUrl = new URL(url);
			conn = (HttpURLConnection) serviceUrl.openConnection();
			byte[] requestData = mdn.getBytes(StandardCharsets.UTF_8);
			//byte[] requestData =requestGson.toJson(mdn).getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/json");
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
				log.info("responseString::" + responseString);
				line = requestGson.fromJson(responseString, Line.class);
				log.info("Request Bean in callLineResourceService::" + line.toString());
			}
			isr.close();
			os.close();
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Error in callLineDetailsByMDN{}", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		log.info("callLineMdnResourceService :: outputString :: " + outputString);
		return line;
	}

	public void updateAltZipcode(TransactionHistory transactionHistory) {
		String outputString = CommonConstants.EMPTYSTRING;
		HttpURLConnection conn = null;
		Gson requestGson = new Gson();
		String url = "";
		try {
			url = apolloNEProperties.getResourceServiceURL()
					+ ResourceConstants.resourceUpdateServiceUrl.valueOf("UPDATEALTZIPCODE").getServiceUrl();
			URL serviceUrl = new URL(url);
			conn = (HttpURLConnection) serviceUrl.openConnection();
			log.info("updateAltZipcode URL::" + url + "req::" + requestGson.toJson(transactionHistory));
			byte[] requestData = requestGson.toJson(transactionHistory).getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("charset", "utf-8");
			conn.setRequestProperty("Content-Length", Integer.toString(requestData.length));
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("PUT");
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
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Error in updateAltZipcode{}", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
	}
	
	public ErrorDetails callErrorResponse(String code) {
		log.info("Inside callErrorResponse::" + code);
		String outputString = CommonConstants.EMPTYSTRING;
		//String outputString = null;
		HttpURLConnection conn = null;
		Gson requestGson = new Gson();
		String url="";
		ErrorDetails msg = null;
		try {
			url=apolloNEProperties.getResourceServiceURL()+ResourceConstants.resourceUpdateServiceUrl.GETERRORDETAILS.getServiceUrl()+"?httpStatusCode="+code;
			log.info("callErrorResponse URL::" + url);
			URL serviceUrl = new URL(url);
			conn = (HttpURLConnection) serviceUrl.openConnection();
			byte[] requestData = code.getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/json");
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
				log.info("responseString callErrorDetails::" + responseString);
				msg = requestGson.fromJson(responseString, ErrorDetails.class);
				log.info("Request Bean in callErrorDetails::" + msg.toString());
			}
			isr.close();
			os.close();
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Error in callErrorDetails-{}", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		log.info("callErrorDetails outputString:: " + outputString);
		return msg;
	}
	
	
	public Sim callSimResourceService(Sim simBean) {
		log.info("Inside callSimResourceService::");
		String outputString = "";
		HttpURLConnection conn = null;
		Gson requestGson = new Gson();
		Sim sim = null;
		try {
			String url = apolloNEProperties.getResourceServiceURL()+ResourceConstants.resourceUpdateServiceUrl.valueOf("GETSIMDETAILSBYLINEID").getServiceUrl() + "?eLineId=" + simBean.geteLineId();
			//String url = sbNEServiceProperties.getSimDetailsByLineId() + "?eLineId=" + simBean.geteLineId();
			log.info("callSimResourceService URL::" + url);
			URL serviceUrl = new URL(url);
			conn = (HttpURLConnection) serviceUrl.openConnection();
			byte[] requestData = requestGson.toJson(simBean).getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/json");
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
			String responseString ="";
			while ((responseString = in.readLine()) != null) {
				outputString =  responseString;
				sim = requestGson.fromJson(outputString, Sim.class);
				//log.info("Request Bean in callSimResourceService::" + sim.toString());
			}
			isr.close();
			os.close();
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Error in callSimResourceService{}", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		log.info("callSimResourceService :: outputString :: " + outputString);
		return sim;
	}
	
	public ResourceInfo getResourceInfoService(ResourceInfo resourceInfo) {
		log.info("getResourceInfoService in INBOUND::" + resourceInfo.getAvailability());
		String outputString = "";
		HttpURLConnection conn = null;
		Gson requestGson = new Gson();
		ResourceInfo resourceInfodetails=null;
		try {
			URL serviceUrl = new URL(apolloNEProperties.getResourceServiceURL()	+ResourceConstants.resourceUpdateServiceUrl.valueOf("GETRESOURCEINFO").getServiceUrl());
			//URL serviceUrl = new URL(sbNEServiceProperties.getReferenceValueUrl());
			conn = (HttpURLConnection) serviceUrl.openConnection();
			log.info("req::"+requestGson.toJson(resourceInfo));
			byte[] requestData = requestGson.toJson(resourceInfo).getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/json");
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
				outputString = responseString;
				resourceInfodetails = requestGson.fromJson(outputString, ResourceInfo.class);
			}
			isr.close();
			os.close();
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Error in ResourceInfo{}", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		log.info("ResourceInfo :: ResourceInfo :: " + outputString);
		return resourceInfodetails;
	}
	
	public ProcessMetadata[] getProcessMetadata(ProcessMetadata processMetadata) {
		log.debug("getProcessMetadata :: " + processMetadata);
		String outputString = "";
		HttpURLConnection conn = null;
		Gson requestGson = new Gson();
		ProcessMetadata[] processMetadatalst = null;
		try {
			//URL serviceUrl = new URL(inboundProperties.getProcessSpecDetails());
			URL serviceUrl = new URL(apolloNEProperties.getResourceServiceURL()+ResourceConstants.resourceUpdateServiceUrl.valueOf("GETPROCESSMETA").getServiceUrl());
			conn = (HttpURLConnection) serviceUrl.openConnection();
			// log.debug("req:::" + requestGson.toJson(transactionHistory));
			byte[] requestData = requestGson.toJson(processMetadata).getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/json");
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
				outputString = responseString;
				processMetadatalst = requestGson.fromJson(outputString, ProcessMetadata[].class);
				log.debug("outputString" + outputString);
			}
			isr.close();
			os.close();
		} catch (Exception e) {
			log.error("processMetadatalst - {}", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		return processMetadatalst;
	}
	
	public void updateResourceInfo(ResourceInfo resourceInfo) {

		log.debug("getResourceInfoService in INBOUND::" + resourceInfo.getAvailability());
		String outputString = "";
		HttpURLConnection conn = null;
		Gson requestGson = new Gson();
		try {
			URL serviceUrl = new URL(apolloNEProperties.getResourceServiceURL()+ResourceConstants.resourceUpdateServiceUrl.valueOf("UPDATERESOURCEINFO").getServiceUrl());
			//URL serviceUrl = new URL(sbNEServiceProperties.getReferenceValueUrl());
			conn = (HttpURLConnection) serviceUrl.openConnection();
			log.debug("req::"+requestGson.toJson(resourceInfo));
			byte[] requestData = requestGson.toJson(resourceInfo).getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/json");
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
				outputString = responseString;
			}
			isr.close();
			os.close();
		} catch (Exception e) {
			log.error("Error in ResourceInfo{}", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		log.debug("ResourceInfo :: ResourceInfo :: " + outputString);
		
	
		
	}
	
	public ProcessMetadata insertProcessMetadata(ProcessMetadata processMetadata) {
		
		String outputString = "";
		HttpURLConnection conn = null;
		Gson requestGson = new Gson();
		try {
			URL serviceUrl = new URL(apolloNEProperties.getResourceServiceURL()+ResourceConstants.resourceUpdateServiceUrl.valueOf("INSERTPROCESSMETADATA").getServiceUrl());
			//URL serviceUrl = new URL(sbNEServiceProperties.getReferenceValueUrl());
			conn = (HttpURLConnection) serviceUrl.openConnection();
			log.debug("req::"+requestGson.toJson(processMetadata));
			byte[] requestData = requestGson.toJson(processMetadata).getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/json");
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
				outputString = responseString;
				processMetadata = requestGson.fromJson(outputString, ProcessMetadata.class);
			}
			isr.close();
			os.close();
		} catch (Exception e) {
			log.error("Error in processMetadata{}", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		log.debug("processMetadata :: processMetadata :: " + outputString);
		return processMetadata;
	}
	
	public DeviceGsmaHistory[] getDeviceHistUsingImeis(String string, String status) {

		//log.debug("getResourceInfoService in INBOUND::" + resourceInfo.getAvailability());
		String outputString = "";
		HttpURLConnection conn = null;
		Gson requestGson = new Gson();
		DeviceGsmaHistory[] resourceArr=null;
		try {
			String resourceURl=apolloNEProperties.getResourceServiceURL()	+ResourceConstants.resourceUpdateServiceUrl.valueOf("GSMAHISTORYUSINGIMEI").getServiceUrl()+"?imeis="+string+"&"+"status="+status;
			URL serviceUrl = new URL(resourceURl);
			//URL serviceUrl = new URL(sbNEServiceProperties.getReferenceValueUrl());
			conn = (HttpURLConnection) serviceUrl.openConnection();
			
			byte[] requestData = null;
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("charset", "utf-8");
			//conn.setRequestProperty("Content-Length", Integer.toString(requestData.length));
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("POST");
			conn.setUseCaches(false);
		/*
		 * OutputStream os = conn.getOutputStream(); os.write(requestData);
		 */
			InputStreamReader isr = new InputStreamReader(conn.getInputStream());
			BufferedReader in = new BufferedReader(isr);
			String responseString = null;
			while ((responseString = in.readLine()) != null) {
				outputString = responseString;
				resourceArr = requestGson.fromJson(outputString, DeviceGsmaHistory[].class);
			}
			isr.close();
			
		} catch (Exception e) {
			log.error("Error in DeviceGsmaHistory{}", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		log.debug("DeviceGsmaHistory :: DeviceGsmaHistory :: " + outputString);
		return resourceArr;
	
		
	}
	
	public DeviceGsmaHistory[] saveDevicegsmaHistlst(List<DeviceGsmaHistory> resourceInfolst) {

		//log.debug("getResourceInfoService in INBOUND::" + resourceInfo.getAvailability());
		String outputString = "";
		HttpURLConnection conn = null;
		Gson requestGson = new Gson();
		DeviceGsmaHistory[] resourceArr=null;
		try {
			
			URL serviceUrl = new URL(apolloNEProperties.getResourceServiceURL()	+ResourceConstants.resourceUpdateServiceUrl.valueOf("SAVEDEVICEGSMA").getServiceUrl());
			//URL serviceUrl = new URL(sbNEServiceProperties.getReferenceValueUrl());
			conn = (HttpURLConnection) serviceUrl.openConnection();
			log.debug("req::"+requestGson.toJson(resourceInfolst));
			byte[] requestData = requestGson.toJson(resourceInfolst).getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/json");
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
				outputString = responseString;
				resourceArr = requestGson.fromJson(outputString, DeviceGsmaHistory[].class);
			}
			isr.close();
			os.close();
		} catch (Exception e) {
			log.error("Error in saveDevicegsmaHistlst{}", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		log.debug("saveDevicegsmaHistlst :: saveDevicegsmaHistlst :: " + outputString);
		return resourceArr;
	
		
	}
	
	public String callSwapLineIdforTransferDevice(ResourceUpdateRequest resourceUpdateRequest) {
		log.info("callSwapLineIdforTransferDevice in INBOUND:::" + resourceUpdateRequest.toString());
		String outputString = "";
		HttpURLConnection conn = null;
		Gson requestGson = new Gson();
		try {
			URL serviceUrl = new URL(apolloNEProperties.getResourceServiceURL()+ResourceConstants.resourceUpdateServiceUrl.valueOf("SWAPLINEIDFORTRANSFERDEVICE").getServiceUrl());
			conn = (HttpURLConnection) serviceUrl.openConnection();
			log.info("req:::"+requestGson.toJson(resourceUpdateRequest));
			byte[] requestData = requestGson.toJson(resourceUpdateRequest).getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/json");
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
				outputString = responseString;
			}
			isr.close();
			os.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		log.info("callSwapLineIdforTransferDevice :: outputString :: " + outputString);
		return outputString;
	}	
	
	public String callUpdateDeviceInfoResource(Device deviceBean) {
		log.debug("Inside callUpdateDeviceInfoResource::" + deviceBean.toString());
		String outputString = "";
		HttpURLConnection conn = null;
		Gson requestGson = new Gson();		
		try {
			String url="";
			url = apolloNEProperties.getResourceServiceURL() +ResourceConstants.resourceUpdateServiceUrl.valueOf("UPDATEDEVICEINFO").getServiceUrl();
			URL serviceUrl = new URL(url);
			conn = (HttpURLConnection) serviceUrl.openConnection();
			byte[] requestData = requestGson.toJson(deviceBean).getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/json");
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
			String responseString = CommonConstants.EMPTYSTRING;
			while ((responseString = in.readLine()) != null) {
				outputString = outputString + responseString;
			}
			isr.close();
			os.close();
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0006+" :Exception in callUpdateDeviceInfoResource - {}", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		log.info("callUpdateDeviceInfoResource outputString :: " + outputString);
		return outputString;
	}
	
	public String updateStgPlanMigration(StgPlanMigration stgPlanMigration) {
		String outputString = "";
		HttpURLConnection conn = null;
		Gson requestGson = new Gson();
		String url ="";
		try {
			url = apolloNEProperties.getResourceServiceURL()+ResourceConstants.resourceUpdateServiceUrl.valueOf("UPDATESTGPLANMIGRATIONLIST").getServiceUrl();
			log.info("callLineResourceService URL::" + url);
			URL serviceUrl = new URL(url);
			conn = (HttpURLConnection) serviceUrl.openConnection();
			byte[] requestData = requestGson.toJson(stgPlanMigration).getBytes(StandardCharsets.UTF_8);
			conn.setRequestProperty("Content-Type", "application/json");
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
			String responseString = "";
			while ((responseString = in.readLine()) != null) {
				outputString = outputString + responseString;
			}
			isr.close();
			os.close();
		} catch (Exception e) {
			log.error("Exception - {}", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		log.info("callLineResourceService :: outputString :: " + outputString);
		return outputString;
	}	
}
