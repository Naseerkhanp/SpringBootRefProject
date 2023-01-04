/**
 * 
 */
package com.excelacom.century.apolloneoutbound.consumer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.ServiceUnavailableException;

import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpServerErrorException.GatewayTimeout;
import org.springframework.web.client.HttpServerErrorException.ServiceUnavailable;
import org.springframework.web.client.ResourceAccessException;

import com.excelacom.century.apolloneoutbound.bean.EHttpMethods;
import com.excelacom.century.apolloneoutbound.bean.ErrorCodes;
import com.excelacom.century.apolloneoutbound.bean.ErrorDetails;
import com.excelacom.century.apolloneoutbound.bean.ErrorMessage;
import com.excelacom.century.apolloneoutbound.bean.ErrorResponse;
import com.excelacom.century.apolloneoutbound.bean.SendClientRequest;
import com.excelacom.century.apolloneoutbound.dao.RcsDao;
import com.excelacom.century.apolloneoutbound.exception.OutboundRetryException;
import com.excelacom.century.apolloneoutbound.logger.UtilityService;
import com.excelacom.century.apolloneoutbound.properties.ApolloNEQueueProperties;
import com.excelacom.century.apolloneoutbound.service.ApolloNEOutboundClientService;
import com.excelacom.century.apolloneoutbound.service.ApolloNEOutboundService;
import com.excelacom.century.apolloneoutbound.utils.constants.ApolloNEConstants;
import com.excelacom.century.apolloneoutbound.utils.constants.CommonConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;

import lombok.extern.log4j.Log4j2;

/**
 * @author Dhayanand.B
 *
 */
@Log4j2
@Component
public class ApolloNEOutbountClientSender {

	@Autowired
	private ApolloNEOutboundService apolloNEService;

	@Autowired
	private ApolloNEQueueProperties apolloNEQueueProperties;

	@Autowired
	private RcsDao rcsDao;

	@Autowired
	private RabbitTemplate customRabbitTemplate;
	
	@Autowired
	private ApolloNEOutboundClientService apolloNEOutboundClientService;
	
	@Autowired
	private UtilityService utilityService;
	
	@Autowired
	private ApolloNEQueueProperties properties;

	@Autowired
	private ConsumerUtilityClass consumerUtilityClass;

	@RabbitListener(queues = "#{apolloNEQueueProperties.getApolloNeSendClientQueue()}", containerFactory = "apolloNeSendClientContainer")
	public Message sendMessagetoExternal(Message message, Channel channel) {
		log.debug("Inside sendMessagetoExternal");
		String response = ApolloNEConstants.EMPTY;
		long deliverytag = 0;
		SendClientRequest sendClientRequest = null;
		String target = CommonConstants.EMPTYSTRING;

		
		ObjectMapper mapper = new ObjectMapper();
		String statusCode = "500";
		String queueStatus = "";

		try {
			int num = channel.getChannelNumber();
			MessageProperties properties = message.getMessageProperties();
			deliverytag = properties.getDeliveryTag();
			String messageId = properties.getMessageId();
			log.debug("messageId:" + messageId + " num :" + num);
			log.debug("message: \n" + message);
			byte[] body = message.getBody();
			String request = new String(body);
			// Boolean isFailed = Boolean.FALSE;
			if (request != null) {
				Gson gson = new Gson();
				sendClientRequest = gson.fromJson(request, SendClientRequest.class);
				sendClientRequest = consumerUtilityClass.sendMessageToClient(sendClientRequest);
				statusCode = sendClientRequest.getStatusCode();
				/*
				 * log.debug("Inside sendMessagetoExternal sendClientRequest" +
				 * sendClientRequest + "::statusCode1::" + statusCode);
				 */
				mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
				response = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(sendClientRequest);
				if (sendClientRequest.getStatusCode() != null
						&& (sendClientRequest.getStatusCode().equalsIgnoreCase("401")
								|| sendClientRequest.getStatusCode().equalsIgnoreCase("404")
								|| sendClientRequest.getStatusCode().equalsIgnoreCase("503")
								|| sendClientRequest.getStatusCode().equalsIgnoreCase("502"))) {
					throw new OutboundRetryException(getResponseObject(sendClientRequest.getResponse()));
				}
			}
		} catch (IOException | GatewayTimeout | URISyntaxException | ServiceUnavailable | ServiceUnavailableException
				| ResourceAccessException | OutboundRetryException e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0004+" : Connection Exception", e);
			
			retryTransationUpdate(sendClientRequest);
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			try {
				response = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(sendClientRequest);
			} catch (JsonProcessingException e1) {
				log.error("ErrorCode : "+ErrorCodes.CECC0007+" : Connection Exception", e1);
			}

			Boolean ismaxTryreached = sendNack(message, channel, deliverytag, true, sendClientRequest);
			if (!ismaxTryreached) {
				throw new OutboundRetryException(e.getMessage(), e);
			} else {
				sendClientRequest = constructErrorResponse(e, sendClientRequest);
			}
			queueStatus = CommonConstants.QUEUED;
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0004+" : sendClientRequest Exception", e);
			if (sendClientRequest!=null && !StringUtils.hasText(sendClientRequest.getStatusCode())) {
				sendClientRequest.setStatusCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
			}
			if (sendClientRequest != null) {
				sendClientRequest = constructErrorResponse(e, sendClientRequest);
			}
			queueStatus = CommonConstants.QUEUED;
		}

		try {
			channel.basicAck(deliverytag, false);

		} catch (IOException e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0010+" : IOException Exception", e);
			sendClientRequest = constructErrorResponse(e, sendClientRequest);
		}
		if (sendClientRequest != null) {
			if (sendClientRequest.isInvokeitmbo()) {
				apolloNEService.updateMboTransactionDetails(sendClientRequest.getOutReqBean(),
						sendClientRequest.getResponse(), queueStatus, sendClientRequest);
			} else {
				apolloNEService.updateTransactionDetails(sendClientRequest.getRcsServiceBean(),
						sendClientRequest.getOutReqBean(), sendClientRequest.getOperationName(),
						sendClientRequest.getResponseId(), target, sendClientRequest.getResponse(), statusCode,
						queueStatus, sendClientRequest);
			}
		}
		return MessageBuilder.withBody(response.getBytes()).setContentType(MediaType.APPLICATION_JSON_VALUE).build();
	}

	private String getResponseObject(String response) {
		try {
			if (StringUtils.hasText(response)) {
				// String clientEndUrl = null;
				// String httpCode = null;
				if (response.contains("~")) {
					if (response.split("~").length == 3) {
						// clientEndUrl = response.split("~")[2];
						// httpCode = response.split("~")[1];
						response = response.split("~")[0];
					}
					if (response.split("~").length == 2) {
						// httpCode = response.split("~")[1];
						response = response.split("~")[0];
					}

					if (response != null && response.startsWith("{")) {
						try {
							JSONObject responseObj = new JSONObject(response);
							if (responseObj != null && responseObj.has("errorMessage")) {
								String errorMessage = responseObj.getString("errorMessage");
								if (StringUtils.hasText(errorMessage)) {
									response = errorMessage;
								}
							}
						} catch (JSONException e) {
							log.error("ErrorCode : "+ErrorCodes.CECC0007+" : Unable parse JSON:" + e.getMessage());
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0004+" : Unable parse response:" + e.getMessage());
		}
		log.debug("Response after parsing::" + response);
		return response;
	}

	private SendClientRequest constructErrorResponse (Exception e, SendClientRequest sendClientRequest) {
		try {
			StringWriter sw = new StringWriter();
			e.getCause().printStackTrace(new PrintWriter(sw));
			sendClientRequest.setStackTrace(sw.toString());
		} catch (Exception er) {
			log.error("ErrorCode : "+ErrorCodes.CECC0004+" : Unable to get Stack trace:" + er.getMessage());
		}
		try {
			String errorMessage = e.getMessage();
			log.debug("Inside ismaxTryreached sendClientRequest::" + sendClientRequest);

			if (!StringUtils.hasText(sendClientRequest.getStatusCode())) {
				int httpstatusCode = 0;
				if (e instanceof GatewayTimeout) {
					httpstatusCode = ((GatewayTimeout) e).getRawStatusCode();
				} else if (e instanceof ServiceUnavailable) {
					httpstatusCode = ((ServiceUnavailable) e).getRawStatusCode();
				}
				if (httpstatusCode != 0) {
					sendClientRequest.setStatusCode(String.valueOf(httpstatusCode));
				}
			}
			String response = this.errorResponse(sendClientRequest.getStatusCode(), errorMessage);
			if (response != null)
				sendClientRequest.setResponse(response);
			utilityService.callFalloutResourceService(sendClientRequest);
		} catch (Exception er) {
			log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Unable to construct error response:" + er.getMessage());
		}
		return sendClientRequest;
	}

	private String errorResponse(String httpCode, String des) {
		log.info("inside errorResponse httpCode::" + httpCode + "::Description::" + des);
		ErrorMessage msg = new ErrorMessage();
		ErrorResponse errResp = new ErrorResponse();
		ErrorDetails errorMessageBean = new ErrorDetails();
		ArrayList<ErrorMessage> responseMessage = new ArrayList<ErrorMessage>();
		String response = "";
		String httpMessage = "";
		int httpStatusCode = HttpStatus.SERVICE_UNAVAILABLE.value();
		try {
			if (!StringUtils.hasText(httpCode)) {
				httpCode = String.valueOf(httpStatusCode);
				errResp.setCode(httpStatusCode);
			}
			errResp.setCode(httpStatusCode);
			errorMessageBean = apolloNEOutboundClientService.callErrorResponse(httpCode);
			if (errorMessageBean != null) {
				httpMessage = errorMessageBean.getStatus();
			}
			httpStatusCode = Integer.valueOf(httpCode);

			errResp.setCode(httpStatusCode);
			errResp.setReason(httpMessage);
			msg.setResponseCode(CommonConstants.RESPONSE_CODE);
			if (StringUtils.hasText(des)) {
				msg.setDescription(des);
			} else {
				msg.setDescription(CommonConstants.DEFAULT_DESCRIPTION);
			}
			responseMessage.add(msg);
			errResp.setMessage(responseMessage);
			log.info("errorResponse Messages::" + errResp.getMessage());
			response = new Gson().toJson(errResp);
			log.info("inside errorResponse Resp ::" + response);
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0006+" : Error inside errorResponse - {}", e);
		}
		return response;
	}

	private Boolean sendNack(Message message, Channel channel, long deliverytag, Boolean moveToErrorQueue, SendClientRequest sendClientRequest) {
		Boolean ismaxTryreached = Boolean.FALSE;
		String queueName="";
		try {
			MessageProperties props = message.getMessageProperties();
			List<Map<String, ?>> headers = props.getXDeathHeader();// getHeaders();
			log.debug("sendNack headers::" + headers);
			if (headers != null)
				for (Map<String, ?> m : headers) {
					log.debug(m);
					if (((String) m.get("queue"))
							.equalsIgnoreCase(apolloNEQueueProperties.getApolloNeSendClientQueue())) {
						queueName=(String) m.get("queue");
						log.debug("queueName"+queueName);
						sendClientRequest.setQueueName(queueName);
						long c = ((Long) m.get("count")).longValue();
						int maxRetryCount=apolloNEQueueProperties.getMaxretrycount();
						if( maxRetryCount==0) {
							maxRetryCount=3;
						}
						log.debug("maxRetryCount"+maxRetryCount+"c::"+c);
						if (c >= maxRetryCount)
							ismaxTryreached = Boolean.TRUE;
					}
				}
			if (moveToErrorQueue && ismaxTryreached) {
				CorrelationData correlationData = new CorrelationData("error");

				customRabbitTemplate.send(apolloNEQueueProperties.getApolloNeErrorExchange(),
						apolloNEQueueProperties.getApolloNeErrorQueue(), message, correlationData);

			} else {
				channel.basicNack(deliverytag, false, false);
			}
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0005+" : Exception : {}", e);
		}
		return ismaxTryreached;
	}

	private SendClientRequest retryTransationUpdate(SendClientRequest sendClientRequest) {
		sendClientRequest.setIsRetryTransaction(Boolean.TRUE);
		sendClientRequest.setTransactionStatus(ApolloNEConstants.FAILED);
		if (sendClientRequest.getRetryCount() == null) {
			sendClientRequest.setRetryCount(0);
		}
		Integer retryCount = sendClientRequest.getRetryCount();
		sendClientRequest.setRetryCount(retryCount++);
		return sendClientRequest;
	}

}
