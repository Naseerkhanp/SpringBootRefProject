package com.excelacom.century.apolloneoutbound.utils.constants;

import org.springframework.http.MediaType;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

public interface ApolloNEConstants {

	public static final String ID = "ID";

	public static final String COLON_SPACE = " : ";

	public static final String METHOD_INITIATECALL = "initiateCall";
	
   public static final String GET_THROTTLE_DETAILS="Select * From ref_throttle_details where RETAILPLANCODE=? and WHOLESALEPLANCODE=?";
	
	public static final String GET_ROOT_TRANSACTION_ID= "SELECT root_transaction_id from transaction_Details where transaction_id=?";

	public static final String EMPTY = "";

	public static final String AMP = "&";

	public static final String QUESTION = "?";

	public static final String EQUAL = "=";

	public static final String REQUEST_PARAM = "requestParam";

	public static final String DATA = "data";

	public static final String ENDPOINT_HTTP_METHOD = "EndPointHttpMethod";

	public static final String COMMA = ",";

	public static final String PATH_PARAM = "Path";

	public static final String STRING_ZERO = "0";

	public static final String CONTENTTYPE = "Content-Type";

	public static final String APPLICATION_JSON = "application/json";

	public static final String STATUS = "status";

	public static final String HTTP_NO_CONTENT = "NO_CONTENT";

	public static final String NEWLINE = "\n";

	public static final String CHARSET = "UTF-8";

	public static final String MESSAGEHEADER = "messageHeader";

	public static final String EXCEPTION = "exception";

	public static final String ERRORS = "errors";

	public static final String SLASH = "/";

	public static final String MESSAGE = "message";

	public static final String STATUSCODE = "status_code";

	public static final String CODE_204 = "204";

	public static final String CREATED = "Created";

	public static final String NOCONTENT = "No Content";

	public static final String ACCEPTED = "Accepted";

	public static final String SUCCESS = "Success";

	public static final String OK = "OK";

	public static final String STRING_NULL = "null";

	public static final String RCS = "RCS";
	
	public static final String HMNO="HMNO";
	
	public static final String CBRS_NE="CBRS-NE";
	
	public static final String SYNIVERSE = "SYNIVERSE";

	public static final String BODY_PARAM = "bodyParam";

	public static final String OUTBOUND = "OUTBOUND";

	public static final String INITIATED = "INITIATED";
	
	public static final String HTTPS = "https";

	public static final String NSL = "NSL";

	public static final Object AUTH_TYPE = "AuthenticationType";
	
	public static final String HEADER = "header";
	
	public static final String GET_ROOOT_ID = "SELECT ROOT_TRANSACTION_ID FROM TRANSACTION_DETAILS WHERE TRANSACTION_UID=?";
	
	public static final String GET_TRANSACTION_ID = "SELECT transaction_id from transaction_Details where transaction_uid=?";
	
	public static final String GET_TENANT_ID = "SELECT tenant_id from transaction_Details where transaction_id=?";
	
	public static final String GET_SERVICE_NAME = "SELECT servicename from transaction_Details where transaction_id=(select root_transaction_id from transaction_details where transaction_id=?)";
	
	public static final String INSERT_TRANS_FAILURE_LOG = "INSERT " 
            +"INTO trans_failure_log " 
            +"  ( " 
            +"    TRANSACTION_ID , " 
            +"    ROOT_TRANSACTION_ID , "
			+" 	  ERROR_CODE ,"
			+"    ERROR_MSG ,"
            +"    CREATED_BY,"
            +"    CREATED_DATE"
            +"  ) " 
            +"  VALUES " 
            +"  ( " 
            +"    :TRANSACTION_ID , " 
            +"    :ROOT_TRANSACTION_ID , "
			+"    :ERROR_CODE ,"
			+"    :ERROR_MSG ,"
            +"    :CREATED_BY,"
            +"    systimestamp " 
            +"  )";
	
	public static final String NOT_RECEIVED_RESP = "[{\"status\":\"Response not received\"}]";
	   
	public static final String UNAUTH_RESP = "1012116 - Invalid token.~401";
	
	public static final String INTERNAL_ERROR_CODE = "500";
	
    public static final String ERR_RESPONSE_CODE="\"responseCode\":\"E";
	
	public static final String MESSAGE_HEADER="messageHeader";
	
	public static final String REF_NUM="referenceNumber";

	public static final String GET_REQUESTTRANSACTION_ID = "select request_msg from TRANSACTION_DETAILS where TRANSACTION_ID=?";
	
	public static final String GET_ACTIVATE_SUBSCRIBER_PORTINREQUEST = "select request_msg from TRANSACTION_DETAILS where ROOT_TRANSACTION_ID=? and transaction_name='Activate Subscriber Port-in'";

	public static final String APOLLONE = "Apollo-NE";

	public static final String FAILED = "Failed";
	
	public static final String GET_REF_NO = "SELECT EXTERNAL_TRANSACTION_ID FROM TRANSACTION_METADATA WHERE TRANSACTION_ID=?";
	
	public static final String GETCONTEXTFROMURL="SELECT issuer_name FROM SERVICE_INFO WHERE SERVICE_url=? and service_name='COMMON_MBO_DNS'";
	
	public static final String UPDATE_SB_FAIL_TRANSACTION = "update transaction_details set status = :status,RESP_RECEIVED_DATE=systimestamp,ENTITY_ID = :ENTITY_ID ,  GROUP_ID = :GROUPID, TARGET_SYSTEM = :TARGETSYSTEM  where transaction_uid = :TRANSACTION_ID";

	public static final String GETROOTTRANSACTIONID1 = "select root_transaction_id from transaction_metadata where external_transaction_id=?";
	
	public static final String GET_REFERENCE_NO = "select EXTERNAL_TRANSACTION_ID from TRANSACTION_METADATA where TRANSACTION_ID =(select ROOT_TRANSACTION_ID from transaction_details where transaction_id=?)";

	public static final String GETTRANSACTIONID1 = "select transaction_id from transaction_metadata where external_transaction_id=?";
	
	public static final Configuration configuration = Configuration.builder()
			    .jsonProvider(new JacksonJsonNodeJsonProvider())
			    .mappingProvider(new JacksonMappingProvider())
			    .build();
				
	public static final String LNPINFORMATION="$.data.lnp.ospAccountNo,$.data.lnp.pin,$.data.lnp.address.zip,$.data.lnp.address.city,$.data.lnp.address.state,$.data.lnp.address.addressLine1,$.data.lnp.address.addressLine2,$.data.lnp.lnpName.business,$.data.lnp.lnpName.name.first,$.data.lnp.lnpName.name.last";
	
	public static final String REQUESTLNPINFORMATION="$.data.subOrder[0].lnp.ospAccountNo,$.data.subOrder[0].lnp.pin,$.data.subOrder[0].lnp.lnpAddress.zipCode,$.data.subOrder[0].lnp.lnpAddress.city,$.data.subOrder[0].lnp.lnpAddress.state,$.data.subOrder[0].lnp.lnpAddress.addressLine1,$.data.subOrder[0].lnp.lnpAddress.addressLine2,$.data.subOrder[0].lnp.lnpName.businessName,$.data.subOrder[0].lnp.lnpName.name.firstName,$.data.subOrder[0].lnp.lnpName.name.lastName";
	
	public static final String LNPINFORMATIONAP="$.data.subOrder[0].lnp.ospAccountNo,$.data.subOrder[0].lnp.pin,$.data.subOrder[0].lnp.lnpAddress.zipCode,$.data.subOrder[0].lnp.lnpAddress.city,$.data.subOrder[0].lnp.lnpAddress.state,$.data.subOrder[0].lnp.lnpAddress.addressLine1,$.data.subOrder[0].lnp.lnpAddress.addressLine2,$.data.subOrder[0].lnp.lnpName.business,$.data.subOrder[0].lnp.lnpName.name.firstName,$.data.subOrder[0].lnp.lnpName.name.lastName";
	
	public static final String UPDATE_REQUESTMSG_LNPINFORMATION="update transaction_details set REQUEST_MSG = :REQUEST_MSG where transaction_id = :TRANSACTION_ID";

	public static final String GET_ADDITIONAL_DATA = "select * from ref_additional_data where transactionid=?";
	
	public static final String SER_ENV_INTERNAL_ERROR="{\"messageHeader\":{\"serviceId\":\"SPECTRUM_MOBILE\",\"requestType\":\"MNO\",\"referenceNumber\":\"921914343343434343\"},\"data\":{\"code\":\"500\",\"reason\":\"Internal Server Error\",\"message\":[{\"responseCode\":\"ERR18\",\"description\":\"Request Timed Out. Try again later\"}]}}";
	
	public static final String  GETDEVICEREQ_COUNT = "select count(*) from (SELECT response_msg,transaction_name FROM transaction_details WHERE root_transaction_id =? and transaction_name='Validate Device')";
	
	public static final String  GETDEVICEREQUEST = "select * from (SELECT response_msg,transaction_name FROM transaction_details WHERE root_transaction_id =? and transaction_name='Validate Device')";	  
	
	public static final String GET_ROOT_REQUEST_MSG =  "SELECT REQUEST_MSG from transaction_Details where transaction_id=(select root_transaction_id from transaction_details where transaction_id=?)"; 
	
	public static final String GET_TRANSACTION_ID_OPERATION_NAME= "SELECT transaction_id,TRANSACTION_NAME from transaction_Details where transaction_uid=?";
	
	public static final String GET_OUTBOUND_CALL_COUNT= "SELECT count(*) from TRANSACTION_DETAILS where rel_transaction_id=? and transaction_name=?";
	
	public static final String GET_OUTBOUND_CALL_RESPONSE_MSG= "SELECT response_msg from TRANSACTION_DETAILS where rel_transaction_id=? and transaction_name=?";

	public static final String GET_REQUEST = "SELECT REQUEST_MSG from transaction_details where transaction_id=?";

	public static final String GET_TRANSACTIONMILESTONE = "SELECT TRANSACTION_MILESTONE from transaction_metadata where TRANSACTION_ID=?";

	public static final String UPDATE_TRANSACTIONMILESTONE = "update transaction_metadata set TRANSACTION_MILESTONE=? where  ROOT_TRANSACTION_ID=?";
	
	public static final String GET_UPDATE_PORT_IN_TRANSID = "select root_transaction_id from transaction_details where transaction_id=?";
	
	public static final String GET_UPDATE_PORT_IN_ACTIVATE_TRANSACTION_ID = "select min(transaction_id) from transaction_details where transaction_name in ('Activate Subscriber Port-in','ESIM Activate Subscriber Port-in') and root_transaction_id=?";
	
	
}
