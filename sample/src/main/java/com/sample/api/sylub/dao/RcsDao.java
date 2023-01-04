package com.excelacom.century.apolloneoutbound.dao;

import java.util.List;
import java.util.Map;

import com.excelacom.century.apolloneoutbound.bean.OutboundRequest;
import com.excelacom.century.apolloneoutbound.bean.SendClientRequest;
import com.excelacom.century.apolloneoutbound.entity.ServiceInfo;

public interface RcsDao {

	String getEndpointUrl(String serviceName, String server);

	String getEndpointUrl2(String serviceName, String server);
	
	String getServiceUrl(String serviceName, String server);
	
	void insertSouthBoundTransaction(OutboundRequest outReqBean);

	void updateSouthBoundTransaction(String transId, String entityId, String groupId, String response,
			String statusCode,String target,String responseId,String queueStatus,String operationName, String clientEndUrl,SendClientRequest sendClientRequest);
	
	ServiceInfo getServiceInfoDetails(String serviceName, String server);
	
	String getNEFlag(String neFlag);

	Long getPrimaryKey();
	
	public List<Map<String, Object>> getThrottleDetails(String retailplancode_db, String planCode);


	String getRequestDetails(String responseId);
	
	void updateQueueTransactionDetails(String status,String responseId);
	
	void updateRequestDetails(String request,Long responseId);
	
	String getRefNumber(Double trans_id);
	
	String getContextFromReturnUrl(String returnUrl);
	
	List<Map<String, Object>> mboCredentialDetails(String server, String contextId);
	
	int updateSouthBoundTransactionFailure(String transId, String entityId, String response, String groupId,String responseId, String targetSystem, String queueStatus);

	String getReferenceNumber(String referenceNumber);

	String gettransactionId(String refNumber);
	
	String getRootTransactionId(String refNumber);
	
	public String getServiceName(String responseId);
	
	public String requestlnpInformationDetailsMask(String transactionId);
	
	public List<Map<String, Object>> getAdditionalData(String responseId);
	
	public String maskingSharedName(String response,String operationName);

	public String getOutboundCallResponseMsg(String transactionId, String transacionName); 
	
	public String getRequest(String transactionResp);

	String getRootTransName(String responseId);

	List<Map<String, Object>> transactionErrorMessage(String transactionId);

	List<Map<String, Object>> getValidateDeviceDetails(String responseId);
	
	public String getTransIdFromUid(String transId);
	
	public int getTenantIdFromTid(String transId);

	String getTransactionMileStoneFromMetadata(String responseId);

	void updateTransactionMileStoneFromMetadata(String responseId, String transMileStone);
	
	public String getUpdatePortInTransactionId(String responseId);

	public String getUpdatePortInActivateTransactionId(String updatePortInTransId);

	String getActivateSubsriberPortInRequestForCP(String responseId);

	
	
	
}
