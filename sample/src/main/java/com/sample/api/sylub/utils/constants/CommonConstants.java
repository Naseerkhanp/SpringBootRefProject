package com.excelacom.century.apolloneoutbound.utils.constants;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

public interface CommonConstants {

	public static final String ENDURL = "/nsl/outbound/cbrs/v1/cbrs-outbound";

	/** The Constant AMP. */
	public static final String AMP = "&";

	/** The Constant SOAPACTION. */
	public static final String SOAPACTION = "soapAction=";

	/** The Constant EQUALTO. */
	public static final String EQUALTO = "=";

	
	public static final String INSERT_REQUEST_DETAILS = "INSERT " + "INTO TRANSACTION_METADATA" + "  ( "
			+ " TRANSACTION_ID , " + "    ROOT_TRANSACTION_ID , " + " CREATED_DATE , " + "    CREATED_BY , "
			+ "    MODIFIED_DATE ," 		+ "    MODIFIED_BY , " + "    SERVICE_URL, " + " ALT_ZIPCODE " + ", EXTERNAL_VALUE_4"+"  ) " + "  VALUES " 
			+ "  ( " + "    :TRANSACTION_ID , " + "   :ROOT_TRANSACTION_ID , " + "    systimestamp, " 
			+ "    'NSL' , " + "    NULL, "	+ "    NULL , " + "  :SERVICE_URL, " + " :ALT_ZIPCODE ," + ":EXTERNAL_VALUE_4)";
	
	/** The Constant JSON. */
	public static final String JSON = "json=";

	/** The Constant DESTNAME. */
	public static final String DESTNAME = "destName=";

	/** The Constant ENTITYID. */
	public static final String ENTITYID = "entityId=";

	/** The Constant DESTINATION. */
	public static final String DESTINATION = "destination=";

	/** The Constant ENDPOINTURLEQ. */
	public static final String ENDPOINTURLEQ = "EndpointURL=";

	/** The Constant ENDPOINTOPERATION. */
	public static final String ENDPOINTOPERATION = "EndpointOperation=";

	/** The Constant REQUESTJSON. */
	public static final String REQUESTJSON = "requestJson=";

	/** The Constant RESPONSEID. */
	public static final String RESPONSEID = "responseId=";

	/** The Constant EMPTYSTRING. */
	public static final String EMPTYSTRING = "";

	/** The Constant NULL. */
	public static final String NULL = "null";

	/** The Constant ENDPOINTSERVICETYPE. */
	public static final String ENDPOINTSERVICETYPE = "endpointServiceType";

	/** The Constant TRANSACTION_ID. */
	public static final String TRANSACTION_ID = "TRANSACTION_ID";

	/** The Constant ENTITY_ID. */
	public static final String ENTITY_ID = "ENTITY_ID";

	/** The Constant STATUS. */
	public static final String STATUS = "status";

	public static final String FAILURE = "FAILURE";

	public static final String GROUPID = "GROUPID";

	/** The Constant STATUS. */
	public static final String RESPONSE_MSG = "RESPONSE_MSG";

	/** The Constant TRANSGROUPID. */
	public static final String TRANSGROUPID = "TRANSGROUPID";

	/** The Constant APPLICATION_NAME. */
	public static final String APPLICATION_NAME = "APPLICATION_NAME";

	/** The Constant TRANSACTION_TYPE. */
	public static final String TRANSACTION_TYPE = "TRANSACTION_TYPE";

	/** The Constant TRANSACTION_UID. */
	public static final String TRANSACTION_UID = "TRANSACTION_UID";

	public static final String GROUP_ID = "GROUP_ID";

	public static final String TRANS_GROUP_ID = "TRANS_GROUP_ID";

	public static final String TENANT_ID = "TENANT_ID";

	public static final String SUCCESS = "SUCCESS";
	
	/** The Constant EMPTYSTRING. */
	public static final String TARGET_SYSTEM = "CBRS-NE";
	
	public static final String TARGET_SEARCH_SYSTEM = "NSL";
	
	public static final String CBRS_ADD_SUB="Add Subscriber";
	
	public static final String CBRS_RECONNECT_SUB="Reconnect Subscriber";
	
	public static final String UPDATE_PORT_OUT="Update Port-Out";
	
	public static final String  GET="GET";
	
	public static final String  POST="POST";
	
	public static final String  NSL="NSL";
	
	public static final String NE_FLAG="NE_FLAG";
	
	public static final String  AP_NE="AP_NE";
	
	public static final String  NO="NO";
	
	public static final String REQ_JSON="json";
	
	public static final String CBRS_SUSPEND_SUB="Suspend Subscriber";
	
	
	public static final String DATA = "data";

	public static final String MESSAGEHEADER = "messageHeader";
	
	public static final String CBRS_CR="CBRS Change Rate Plan";
	
	public static final String RECONNECT_SERVICE="Reconnect Service";
	
	public static final String ACTIVATESUBSCRIBER = "Activate Subscriber";
	
	public static final String ActivateRETURNURL = "/nsl/provisioning/mno/callback/v1/activateService/newMDN";

	 public static final String ActivateASYNCERRORURL = "/nsl/provisioning/mno/callback/v1/async-service/asyncError";

	public static final String RETURNURL = "/nsl/provisioning/cbrs/callback/v1/async-service";

	 public static final String ASYNCERRORURL = "/nsl/provisioning/cbrs/callback/v1/async-err-response";

	public static final String APOLLOTARGET_SYSTEM = "Apollo-NE";
	
	public static final String TIME_STAMP="yyyy-MM-dd'T'HH:mm:ss'Z'";
	
	public static final String QUEUED = "QUEUED";
	
	public static final String IMSI = "imsi";
	
	public static final String DEVICEID = "deviceId";
	
	public static final String VALIDATE_DEVICE = "Validate Device";
	
	public static final String IMSI_INQUIRY = "Imsi Inquiry";
	
	public static final String ChangeMdnRETURNURL = "/nsl/provisioning/mno/callback/v1/async-service/changeMDN";
	
	public static final String ChangeFeatureRETURNURL = "/nsl/provisioning/mno/callback/v1/async-callback";
	
	public static final String ChangeFeatureAsyncErrorurl = "/nsl/provisioning/mno/callback/v1/async-service/asyncError";
	
    public static final String ReconnectServiceRETURNURL = "/nsl/provisioning/mno/callback/v1/callbackService";
	
	public static final String ReconnectServiceAsyncErrorurl = "/nsl/provisioning/mno/callback/v1/async-service/asyncError";
	
	public static final String VALIDATE_MDN_PORTABILITY="Validate MDN Portability";
	
	public static final String UPDATE_SUBSCRIBER_GROUP="UpdateSubscriber Group";
	
	public static final String VALIDATE_MDN_PORTABILITY_RETURNURL = "/nsl/provisioning/mno/callback/v1/async-service";
	
	public static final String ACTIVATEPORTIN_RETURNURL = "/nsl/provisioning/mno/callback/v1/activateService/portIn";
	
	public static final String TRANSFERWATCH_RETURNURL = "/nsl/provisioning/wearable/v1/callback/transferWatch";
	
	 public static final String ACTIVATEPORTIN_ASYNCERRORURL = "/nsl/provisioning/mno/callback/v1/async-service/asyncError";
	 
	 public static final String RETRIEVEDEVICE_RETURNURL = "/nsl/provisioning/mno/callback/v1/async-service/retrieveDevice";
	 
	 public static final String RETRIEVEDEVICE_ASYNCERRORURL = "/nsl/provisioning/mno/callback/v1/async-service/asyncError";

	 public static final String VALIDATE_MDN_PORTABILITY_ASYNCERRORURL = "/nsl/provisioning/mno/callback/v1/async-service/asyncError";

	public static final String CHANGE_MDN = "Change MDN";

	public static final String FAILED = "Failed";
	
	public static final String ACTIVATESUBSCRIBER_PORTIN = "Activate Subscriber Port-in";

	public static final String TRANSACTION_SUCESS = "COMPLETED";

	public static final String TRANSACTION_CANCELLED = "CANCELLED";
	
	public static final String NOTIFICATION_SUCCESS = "SUCCESS";

	public static final String PROMOTION_INQUIRY = "Promotion Inquiry";
	
	public static final String MDN = "mdn";
	 
	public static final String mhsCustomPDL= "mhsCustomPDL";
	
	public static final String MHSCustomPDL= "MHSCustomPDL";
	
	public static final String VALIDATEBYOD = "Validate BYOD";

	public static final String ESIM_ACTIVATESUBSCRIBER = "ESIM ActivateSubscriber";
	
	public static final String ESIM_ACTIVATESUBSCRIBER_PORTIN = "ESIM Activate Subscriber Port-in";
	
	public static final String VALIDATE_DEVICE_INQUIRY = "validatedeviceinquiry";

	public static final String IMEI = "deviceId";
	
	public static final String VALIDATEBYOD_RETURNURL = "/nsl/provisioning/mno/callback/v1/async-service/validateDevice";
	
	public static final String VALIDATEBYOD_ASYNCERRORURL = "/nsl/provisioning/mno/callback/v1/async-service/asyncError";
	
	public static final String MANAGE_PROMOTION_RETURNURL = "/nsl/provisioning/mno/callback/v1/async-service";
	
	public static final String MANAGE_PROMOTION_ASYNCERRORURL = "/nsl/provisioning/mno/callback/v1/async-service/asyncError";
	 
	public static final String CANCEL_PORTIN="Cancel Port-In";
	
	
	public static final String CHANGE_BCD = "Change BCD";
	
	public static final String RESET_FEATURE = "Reset Feature";
	
	public static final String CHANGE_SIM = "Change SIM";

	public static final String MANAGE_PROMOTION = "managepromotion";
	
	public static final String SUBSCRIBERGROUP_INQUIRY = "Subscribergroup Inquiry";
	
	public static final String CBRSUSAGENOTIFICATION = "CBRS Usage Notification";
	
	public static final String UPDATE_WIFI_ADDRESS = "Update Wifi Address";
	
	public static final String VALIDATE_WIFI_ADDRESS = "Validate Wifi Address";
	
	public static final String GET_WIFI_ADDRESS = "Get Wifi Address";
	
	public static final String PORTIN_INQUIRY = "Portin Inquiry";
	
	public static final String VALIDATE_SIM = "Validate SIM";
	
	public static final String ICCID = "iccid";

	public static final String CHANGE_PDL = "Change-PDL";
	
	public static final String LINE_INQUIRY="IMSI LineInquiry";
	
	public static final String LINE_INQ="Line Inquiry";
	
    public static final String SUSPEND_SUBSCRIBER = "Suspend Subscriber";
	
	public static final String HOTLINE_SUBSCRIBER = "Hotline Subscriber";
	
	public static final String DELETE_SUBSCRIBER = "Deactivate Subscriber";
	
	public static final String TW_DEACTIVATE_SUBSCRIBER = "TW Deactivate Subscriber";
	
	public static final String S_RESPONSE_MSG="response_msg";
	   
	public static final String TRANSACTION_NAME="transaction_name";
	
	public static final String RETURN_CODES_VALIDATE_DEVICE = "S0001,S0002,S0003,S0004";
	
	public static final String OS_APL = "APL";

	public static final String MODITY_BY = "NSL";

	public static final String CREATED_BY = "NSL";

	public static final String YES = "YES";
	
	public static final String ACTIVE="ACTIVE";
	
	public static final String DD="DD";
	
		/** The Constant PROCESS_PLAN_NAME. */
	public static final String PROCESS_PLAN_NAME = "processPlanName";
	
	/** The Constant WORKFLOW_NAME. */
	public static final String WORKFLOW_NAME = "workflowName";

	public static final String ADDWEARABLE = "AddWearableWF";
	
	public static final String ADD_WEARABLE = "Add Wearable";

	public static final String RECONNETWEREABLEMDN ="TW-Reconnect MDN";
	


	public static final String RESTORE_SERVICE = "Restore Service";

	public static final String REMOVE_HOTLINE = "Remove Hotline";

	
	public static final String ESim_CHANGESIM = "Change eSIM";
	
	public static final String CHANGE_MDN_CM = "Change MDN";
	
	public static final String VALIDATE_MDN = "Validate MDN Portability";
	
	public static final String RETRIEVE_DEVICE = "Retrieve Device";
	
	public static final String CHANGE_MDN_CP_RECONNECT_SERVICE = "CP-Reconnect Mdn";
	
	
	
	public static final String DEACTIVE_SUBSCRIBER = "Deactivate Subscriber";
	
	public static final String TW_DEACTIVE_SUBSCRIBER= "TW Deactivate Subscriber";
	
	
	
	
	public static final String VALIDATE_BYOD = "Validate BYOD";
	
	public static final String CHANGE_DEVICE = "Change of Device";
	
	
	
	public static final String CHANGE_SIM_DEVICE = "Change of SIM and Device";
	
	public static final String CHANGE_RATE_PLAN = "Change Rate Plan";
	
	public static final String DD_CHANGE_RATE_PLAN="Device Detection Change Rate Plan";
	
	
	
	public static final String UPDATE_DUE_DATE = "Update Due-Date";
	
	public static final String UPDATE_CUSTOMER_INFORMATION = "Update Customer Information";
	
	public static final String CANCEL_PORTIIN = "Cancel Port-In";
	
	public static final String CHANGE_FEATURE = "Change Feature";
	
	public static final String CHANGE_WHOLESALE_RATE_PLAN = "Change Wholesale Rate Plan";
	
	public static final String MANAGEACCOUNT = "UpdateSubscriber Group";
	
	
	
	
	
	


		
		 /** The Constant INBOUND. */
		public static final String INBOUND = "inbound";
		 
		/** The Constant ENDPOINTURL. */
		public static final String ENDPOINTURL = "EndpointURL";

	
		/** The Constant USERNAME. */
		public static final String USERNAME ="UserName";
		

		
		public static final String INSERT_ASYNC_STATUS = "insertAsyncStatus";

		public static final String INSERT_MNO_RESPONSE_DETAILS = "insertMNOResponseDetails";

		public static final String UPDATE_SOUTHBOUND_TRANSACTION_FAILURE = "updateSouthBoundTransactionFailure";

		public static final String UPDATE_SOUTHBOUND_TRANSACTION = "updateSouthBoundTransaction";

		public static final String INSERT_SBOUND_TRANSACTION = "insertSBoundTransaction";
		
		public static final String MAPPINGURL = "/nsl/outbound/provisioning/sb-ne-vzw/sb-ne-services";

		public static final String INPROGRESS = "INPROGRESS";
		

		
		 /** The Constant OUTBOUND. */
		public static final String OUTBOUND = "outbound";
		
		 public static final String REL_TRANSACTION_ID ="REL_TRANSACTION_ID";
		 
		 
		 public static final String GETTRANSACTIONID = "select SEQ_TRANSACTION_ID.nextval from dual";
		 
		 public static final String GETLINEBYICCID = "select E_LINE_ID from SIM where ICCID=?";
		 
			public static final String INSERT_SOUTH_TRANSACTION = "INSERT " 
		            +"INTO transaction_details " 
		            +"  ( " 
		            +"    TRANSACTION_ID , " 
		            +"    REL_TRANSACTION_ID , "
		            +"    ROOT_TRANSACTION_ID , "
		            +"    TRANSACTION_NAME , " 
		            +"	  APPLICATION_NAME,"
		            +"    EXT_TRANSACTION_ID , " 
		            +"    TRANSACTION_TYPE , " 
		            +"    STATUS , " 
		            +"    REQ_SENT_DATE , " 
		            +"    RESP_RECEIVED_DATE , " 
		            +"    CREATED_DATE , " 
		            +"    CREATED_BY , " 
		            +"    MODIFIED_DATE , " 
		            +"    MODIFIED_BY , " 
		            +"      ICC_VAL,"
		            +"    REQUEST_MSG ," 
		            +"    ENTITY_ID ,"
		            +"    TRANSACTION_UID,"
					+" 	  GROUP_ID ,"
					+"    TRANS_GROUP_ID ,"
		            +"    TENANT_ID,"
		            +"    TRANSACTION_STATUS,"
		            +"    WORKFLOW_NAME,"
		            +"    SERVICENAME"
		            +"  ) " 
		            +"  VALUES " 
		            +"  ( " 
		            +"    :TRANSACTION_ID , " 
		            +"    :REL_TRANSACTION_ID , " 
		            +"    :ROOT_TRANSACTION_ID , "
		            +"    :APPLICATION_NAME , " 
		            +"	  :APPLICATIONNAME,"
		            +"    NULL, " 
		            +"    :TRANSACTION_TYPE , " 
		            +"    'INITIATED', " 
		            +"    systimestamp , " 
		            +"    NULL , " 
		            +"    systimestamp , " 
		            +"    'NSL' , " 
		            +"    NULL , " 
		            +"    NULL , " 
		            +"      NULL,"
		            +"    :REQUEST_MSG ,"
		            +"    :ENTITY_ID ,"
		            +"    :TRANSACTION_UID,"
					+"    :GROUP_ID ,"
					+"    :TRANS_GROUP_ID ,"
		            +"    :TENANT_ID,"
		            +"    :TRANSACTION_STATUS,"
		            +"    :WORKFLOW_NAME,"
		            +"    :PROCESS_PLAN_NAME"
		            +"  )";
		 
		 public static final String INSERT_SOUTH_TRANSACTION1 = "INSERT " 
		            +"INTO transaction_details " 
		            +"  ( " 
		            +"    TRANSACTION_ID , " 
		            +"    REL_TRANSACTION_ID , " 
		            +"    TRANSACTION_NAME , " 
		            +"	  APPLICATION_NAME,"
		            +"    EXT_TRANSACTION_ID , " 
		            +"    TRANSACTION_TYPE , " 
		            +"    STATUS , " 
		            +"    REQ_SENT_DATE , " 
		            +"    RESP_RECEIVED_DATE , " 
		            +"    CREATED_DATE , " 
		            +"    CREATED_BY , " 
		            +"    MODIFIED_DATE , " 
		            +"    MODIFIED_BY , " 
		            +"      ICC_VAL,"
		            +"    REQUEST_MSG ," 
		            +"    ENTITY_ID ,"
		            +"    TRANSACTION_UID,"
					+" 	  GROUP_ID ,"
					+"    TRANS_GROUP_ID ,"
		            +"    TENANT_ID"
		            +"  ) " 
		            +"  VALUES " 
		            +"  ( " 
		            +"   nextval('SEQ_TRANSACTION_ID'), "
		            +"    ? , " 
		            +"    ? , " 
		            +"	  ? ,"
		            +"    NULL, " 
		            +"    ? , " 
		            +"    'INITIATED', " 
		            +"    systimestamp , " 
		            +"    NULL , " 
		            +"    systimestamp , " 
		            +"    'NSL' , " 
		            +"    NULL , " 
		            +"    NULL , " 
		            +"      NULL,"
		            +"    ? ,"
		            +"    ? ,"
		            +"    ?,"
					+"    ? ,"
					+"    ? ,"
		            +"    ? "
		            +"  )";
		 
		 
		public static final String UPDATE_SB_FAIL_TRANSACTION = "update transaction_details set status = :status,RESPONSE_MSG = :RESPONSE_MSG,RESP_RECEIVED_DATE=systimestamp,ENTITY_ID = :ENTITY_ID ,SOURCE_SYSTEM = :SOURCE_SYSTEM , TARGET_SYSTEM = :TARGET_SYSTEM , GROUP_ID = :GROUPID where transaction_uid = :TRANSACTION_ID";
			
		/*
		 * public static final String INSERT_MNO_RESPONSE ="INSERT "
		 * +"INTO rh_csr_response" +"  ( " +"    TRANSACTION_ID , "
		 * +"    CUSTOMER_ID , " +"    CUSTOMER_REQUEST_ID , " +"    ICCID1 ,"
		 * +"    EID ," +"	  MDN," +"	  IMEI," +"    SMDP_STATUS,"
		 * +"    CBRS_STATUS, " +"    MNO_STATUS, " +"    CREATED_DATE , "
		 * +"    CREATED_BY , " +"    MODIFIED_DATE ," +"    MATCHING_ID ,"
		 * +"	  WHOLESALEPLANCODE," +"    MODIFIED_BY, " +"    MODEL,"
		 * +"    MANUFACTURER," +"    OS_VERSION," +"    DEVICE_MODE,"
		 * +"    CDMALESS_FLAG" +"  ) " +"  VALUES " +"  ( " +"    :TRANSACTION_ID , "
		 * +"    :CUSTOMER_ID , " +"    :CUSTOMER_REQUEST_ID , " +"    :ICCID, "
		 * +"    :EID, " +"	  :MDN," +"	  :IMEI," +"    NULL , " +"    NULL , "
		 * +"    :MNO_STATUS , " +"    systimestamp, " +"    'NSL' , " +"    NULL, "
		 * +"    :MATCHING_ID, " +"	  :WHOLESALEPLANCODE," +"    NULL, " +"	  :MODEL,"
		 * +"	  :MANUFACTURER," +"    NULL," +"    :DEVICE_MODE,"
		 * +"    :CDMALESS_FLAG" +"  )";
		 */

		public static final String UPDATE_SB_SUCCESS_TRANSACTION = "update transaction_details set status = :status , RESPONSE_MSG = :RESPONSE_MSG,HTTP_RESPONSE=:httpCode,NOTES = '' ,RESP_RECEIVED_DATE=systimestamp ,ENTITY_ID = :ENTITY_ID, SOURCE_SYSTEM = :SOURCE_SYSTEM , TARGET_SYSTEM = :TARGET_SYSTEM, SERVICENAME = :SERVICENAME where transaction_uid = :TRANSACTION_ID";
		
		/*public static final String TRANS_ID_COUNT="SELECT COUNT(TRANSACTION_ID) FROM RH_CSR_REQUEST WHERE REFERENCE_NUMBER=:REFERENCE_NUMBER";
		
		public static final String  TRANS_ID="SELECT TRANSACTION_ID " 
				+"FROM rh_csr_request " 
				+"WHERE created_date = " 
				+"  (SELECT MAX(created_date) " 
				+"  FROM rh_csr_request " 
				+"  WHERE REFERENCE_NUMBER=? " 
				+"  ) " 
				+"AND REFERENCE_NUMBER=?";*/
				
		public static final String TRANS_ID_COUNT="SELECT COUNT(TRANSACTION_ID) FROM TRANSACTION_METADATA WHERE EXTERNAL_TRANSACTION_ID=:REFERENCE_NUMBER";
		
		public static final String GET_REF_NO="SELECT EXTERNAL_TRANSACTION_ID FROM TRANSACTION_METADATA WHERE TRANSACTION_ID=?";
		
		public static final String  TRANS_ID="SELECT TRANSACTION_ID " 
				+"FROM TRANSACTION_METADATA " 
				+"WHERE created_date = " 
				+"  (SELECT MAX(created_date) " 
				+"  FROM TRANSACTION_METADATA " 
				+"  WHERE EXTERNAL_TRANSACTION_ID=? " 
				+"  ) " 
				+"AND EXTERNAL_TRANSACTION_ID=?";
		
		public static final String DELTA ="~";
		
		
		
		public static final String MNO ="MNO";
		
		public static final String OPERATION_NAME = "EndpointOperation";
		
		public static final String ENDPOINT_URL = "EndpointURL"; 

		
		
		public static final String JSON_FORMATTER = "jsonFormatter";
		
		public static final String DYNAMICCPF_NOTIFY="DynamicPFONotification";
		
		public static final String HMNOASYNC ="HMNOAsyncService";
		
		public static final String VALIDATEMDN = "ValidateMdnPortabilityAsync";
		
		public static final String MNOACTIVESYNC="MNOActivateAsync";
		
		public static final String MNOACTIVATEPORT="MNOActivatePortAsync";

		
		public static final String ESIMACTIVATESUBSCRIBER = "ESIM ActivateSubscriber";

		public static final String RETURN_URL = "returnURL";

		public static final String ASYNCERROR_URL = "asyncErrorURL";

		

		
		
	
		public static final String FIND_TRANSACTION_STATUS_COUNT = "findTransactionStatusCount";

		public static final String JWT_URL = "SELECT SERVICE_URL FROM SERVICE_INFO WHERE SERVICE_NAME='JWT_TOKEN' and server=?";

		public static final String END_URL = "SELECT SERVICE_URL FROM SERVICE_INFO WHERE SERVICE_NAME=? and server=?";

		public static final String END_POINT_URL = "SELECT END_POINT_URL FROM SERVICE_INFO WHERE SERVICE_NAME=? and server=?";

		public static final String GET_TRANSACTION_COUNT = "SELECT count(*) FROM transaction_details where REL_TRANSACTION_ID=? and TRANSACTION_NAME=?";

		/** The Constant GET_TRANSACTION_COUNT BASED ON RELATIONAL_TRANSACTION_ID. */
		public static final String UPDATE_TRANSACTION_STATUS = "UPDATE  TRANSACTION_DETAILS  SET TRANSACTION_STATUS='INACTIVE' WHERE REL_TRANSACTION_ID= :RELTRANSACTIONID AND TRANSACTION_NAME= :TRANSACTIONNAME";
		
		  public static final String ROOT_TRANSACTION_ID ="ROOT_TRANSACTION_ID";
			
			public static final String TRANSACTION_STATUS ="TRANSACTION_STATUS";
			
			public static final String ACTIVATESUBSCRIBERPORTIN="Activate Subscriber Port-in";
			
			public static final String ACTIVATESUBSCRIBERMDN="Activate Subscriber";
			
			public static final String DEACTIVATESUBSCRIBER="Deactivate Subscriber";
			
			public static final String SUSPENDSUBSCRIBER="Suspend Subscriber";
			
			public static final String HOTLINESUBSCRIBER="Hotline Subscriber";
			
			public static final String REMOVEHOTLINE="Remove Hotline";
			
			public static final String RESTORESERVICE="Restore Service";
			
			public static final String RECONNECTSERVICE="Reconnect Service";
			
			public static final String UPDATEPORTOUT="Update Port-Out";
			
			public static final String CANCELPORTIN="Cancel Port-In";
			
			public static final String UPDATEDUEDATE="Update Due-Date";
			
			public static final String UPDATECUSTOMER_INFO="Update Customer Information";
			
			public static final String RETRIEVE_PSCODE = "RETRIEVE_PSCODE";

			
			
			public static final String TRANSACTION_TIMESTAMP = "TRANSACTION_TIMESTAMP";
			
			public static final String INSERT_LINE_HISTORY_DETAILS ="INSERT " 
				    +"INTO line_history" 
				    +"  ( " 
				    +"    TRANSACTIONID , " 
				    +"    CUSTOMERID , " 
				    +"    MDN , " 
				    +"    IMEI , " 
				    +"    ICCID ,"
					+"    USERID,"	
				    +"    TRANSACTIONDATE , " 
				    +"    ORIGDATA , " 
				    +"    NEWDATA ,"
					+"    ORDERTYPE, "
					+"    REFERENCENUMBER "
				    +"  ) " 
				    +"  VALUES " 
				    +"  ( " 
				    +"    :TRANSACTIONID , " 
				    +"    :CUSTOMERID , " 
				    +"    :MDN, " 
				    +"    :IMEI , " 
				    +"    :ICCID, " 
					+"    :USERID , " 
					+"    systimestamp, " 
				    +"    :MDN , " 
				    +"    :NEWDATA , " 
					+"    :ORDERTYPE,  " 
					+"    :REFERENCENUMBER  "
				    +"  )";
			
			public static final String GET_NE_FLAG = "SELECT SERVICE_URL FROM SERVICE_INFO WHERE SERVICE_NAME='NE_FLAG'";

			public static final String REQUEST_PARAM = "requestParam";
			
			//public static final String UPDATE_FEATURECODES = "update rh_csr_request set featurecodes = :FEATURECODES where TRANSACTION_ID = :REL_TRANSACTION_ID";
		


			public static final String LINE_HISTORY = "Line History";
			
			public static final String UPDATEWIFIADDRESS = "Update Wifi Address";
			
			 public static final String GET_OPERATION_NAME="SELECT transaction_name " 
						+"FROM transaction_Details " 
						+"WHERE transaction_id = " 
						+"  (SELECT MAX(transaction_id) " 
						+"  FROM transaction_Details " 
						+"  WHERE rel_transaction_id=? AND transaction_type= 'OUTBOUND'" 
						+"  ) ";
			 
			
			 
			/*public static final String GET_TRANS_ID_COUNT="select count(*) from RH_CSR_REQUEST where TRANSACTION_ID in (select TRANSACTION_ID from RH_CSR_RESPONSE where TRANSACTION_ID in (select TRANSACTION_ID from RH_CSR_REQUEST where REFERENCE_NUMBER=?))";
				
			public static final String GET_TRANS_ID="select TRANSACTION_ID from RH_CSR_REQUEST where TRANSACTION_ID in (select TRANSACTION_ID from RH_CSR_RESPONSE where TRANSACTION_ID in (select TRANSACTION_ID from RH_CSR_REQUEST where REFERENCE_NUMBER=?))";
			 */
			public static final String GET_TRANS_ID_COUNT="select count(*) from transaction_details where TRANSACTION_ID in (select TRANSACTION_ID from transaction_metadata where external_transaction_id=?)and status='SUCCESS'";
				
			public static final String GET_TRANS_ID="select TRANSACTION_ID from transaction_details where TRANSACTION_ID in (select TRANSACTION_ID from transaction_metadata where external_transaction_id=?)and status='SUCCESS'";
		
			public static final String GET_VALIDATE_DEVICE_DETAILS="select * from TRANSACTION_DETAILS where TRANSACTION_ID=?";

			
			
			/*public static final String IMEI_COUNT = "SELECT count(*) FROM rh_csr_response where mdn=? and imei is not null";
			
			public static final String GET_IMEI="SELECT IMEI FROM rh_csr_response WHERE created_date = (SELECT MAX(created_date) FROM rh_csr_response WHERE mdn=? and imei is not null)";*/
			
			public static final String UPDATE_DEVICE_DETAILS = "update DEVICE_DETAILS set make = :make,model = :model,modes = :mode,CDMALESS = :cdmaLess where LINE_ID =(select LINE_ID from LINE_DETAILS where REFERENCE_NUMBER = :REFERENCE_NUMBER)";
			
			public static final String UPDATE_LINE_PLAN_ASSOC = "update LINE_PLAN_ASSOC set WHS_RATE_PLAN = :planCode where LINE_ID =(select LINE_ID from LINE_DETAILS where REFERENCE_NUMBER = :REFERENCE_NUMBER)";
			
			public static final String UPDATE_ACCOUNT_DETAILS = "update ACCOUNT set SUBGROUP_CD = :subscriberGroupCd where ACCOUNT_NUMBER =(select ACCOUNT_NUMBER from LINE_DETAILS where REFERENCE_NUMBER = :REFERENCE_NUMBER)";
			
			public static final String GET_LINE_HISTORY_DETAILS_MDN = "select * from line_history where (mdn=? and created_date >= CURRENT_TIMESTAMP-30)";
			
			public static final String GET_LINE_INQUIRY_DETAILS_MDN = "select * from line where mdn=?";
			
			public static final String GET_LINE_HISTORY_DETAILS_STARTDATE_ENDDATE = "select * from line_history where (mdn =? and trunc(start_date)=? and trunc(end_date)=?)";
			
			public static final String GET_LINE_HISTORY_DETAILS_ORDERTYPE = "select * from line_history where (mdn =? and ord_type=?)";
			
			public static final String GET_LINE_HISTORY_DETAILS_STARTDATE_ENDDATE_ORDERTYPE = "select * from line_history where mdn =? and TRUNC(start_date)=? and TRUNC(end_date)=? and ord_type=?";
			
			public static final String GET_CURRENT_DATE_TIME_STAMP = "select systimestamp from dual";
			
			public static final String GET_LINE_INQUIRY_DATE_TIME = "select last_updated from line where mdn =?";
			
			public static final String GET_LINE_INQUIRY_DATA_FROM_SIM_DETAILS = "select * from sim_details where line_id IN( select line_id from line_details where mdn = ?)";
			
			public static final String GET_LINE_INQUIRY_DATA_FROM_DEVICE_DETAILS ="select * from device_details where line_id IN( select line_id from line_details where mdn = ?)";
			
			public static final String GET_APOLLO_NE_FLAG = "SELECT service_url FROM SERVICE_INFO WHERE SERVICE_NAME='OAUTH' and server ='AP_NE'";

			public static final String END_POINT_URL_2 = "SELECT end_point_url2 FROM SERVICE_INFO WHERE SERVICE_NAME=? and server=?";
			
			public static final String GET_SIM_DETAILS_FROM_LINE_ID = "select * from sim where line_id =?";
			
			public static final String GET_DEVICE_DETAILS_FROM_LINE_ID = "select * from device where line_id =?";
			
			public static final String GET_DEVICE_DETAILS_FROM_LINE_ID_NEW = "select IMEI from device where line_id =?";
			
			public static final String GET_LINE_INQUIRY_DETAILS_FROM_LINE_ID = "select feature_codes,is_active from line_plan where line_id =?";
			
			public static final String GET_APOLLO_OAUTHREQUEST = "SELECT service_request FROM SERVICE_INFO WHERE SERVICE_NAME='OAUTH' and server ='AP_NE'";
			
			public static final String GET_DATE_MONTH_FORMAT = "select  TO_CHAR(TO_DATE(?, 'DD-MM-YY'), 'DD-MON-YY') from dual";
			
			public static final String ACCOUNTSUMMARY = "AccountSummary";
			
			public static final String GET_ACCOUNT_SUMMARY_DETAILS = "select context_id,acct_type,ext_acct_status,ext_acct_id from account where account_Number=?";
			
			public static final String GET_LINE_DETAILS = "select id,line_id,mdn,line_status from line where account_Number=?";
			
			public static final String GET_LINE_DETAILS_FOR_ACTIVE = "select id,line_id,mdn,line_status from line where line_status = 'ACTIVE' and account_Number=?";
			
			public static final String GET_RETAIL_PLAN_FROM_LINE_ID = "SELECT Retail_Plan FROM line_plan WHERE start_date = (SELECT MAX(start_date) FROM line_plan WHERE line_id=?)";
					
	        //public static final String IMEI_MANU_COUNT = "select count(*) from rh_csr_Response where imei=? and manufacturer is not null";
				
			public static final String GET_LINE_SUMMARY_ACC_NO = "SELECT ACCOUNT_NUMBER FROM LINE WHERE LINE_ID =?";

			public static final String GET_LINE_SUMMARY_DETAILS_FROM_ACCOUNT = "select * from account where ACCOUNT_NUMBER=?";
			
			public static final String GET_LINE_SUMMARY_DETAILS_LINEID = "select * from line where LINE_ID=?";
			
			public static final String GET_LINE_SUMMARY_DETAILS_FROM_DEVICE = "select * from DEVICE where LINE_ID=?";

			public static final String GET_LINE_SUMMARY_DETAILS_FROM_LINEHISTORY = "select * from LINE_HISTORY where LINE_ID=?";

			public static final String GET_LINE_SUMMARY_DETAILS_FROM_SIM = "select * from SIM where LINE_ID=?";

			public static final String GET_LINE_SUMMARY_FROM_LINE_PLAN = "select * from LINE_PLAN where LINE_ID=?";

			public static final String GET_LINEID_COUNT = "SELECT count(LINE_ID) FROM LINE WHERE LINE_ID=?";

			public static final String GET_LINE_SUMMARY_DETAILS_LINEID_ACTIVE = "select * from line where LINE_ID=? AND LINE_STATUS ='ACTIVE'";

			public static final String GET_LSUMMARY_LINEID_PLAN = "select FEATURE_CODES,RETAIL_PLAN,WHS_PLAN,IS_ACTIVE from LINE_PLAN where LINE_ID=?";
			
			public static final String GET_PROMO_DETAILS_USING_LINEID = "select * from promotion where LINE_ID=?";
			
			public static final String GET_PROMO_DETAILS_USING_MDN = "select * from promotion where LINE_ID=(select line_id from line where created_date=(SELECT MAX(created_date) FROM line WHERE mdn=?))";
			
			public static final String GET_LINE_ID_FOR_SEARCH_ENV = "select line_id from sim where created_date=(SELECT MAX(created_date) from sim where iccid=?)";
			
			public static final String GET_ICCID_FOR_SEARCH_ENV = "SELECT iccid FROM SIM WHERE iccid = ?";
			
			public static final String GETTRANSACTIONNAME = "select * from (select transaction_name from transaction_details where rel_transaction_id=? and transaction_name<>'AsyncService' order by transaction_id desc) where ROWNUM=1";
			
			//public static final String GETTRANSACTIONID1 = "select transaction_id from rh_csr_request where reference_number=?";
			
			public static final String GETTRANSACTIONID1 = "select transaction_id from transaction_metadata where external_transaction_id=?";
			
			public static final String GET_PARENT_SERVICE = "select count(servicename) from transaction_details where transaction_id=? and servicename='Transfer-Wearable'";
			
			public static final String GET_WATCH_ID = "select mdn from line where E_LINE_ID = ? and host_mdn is not null";
			
			public static final String GET_SUBGROUPCD_FOR_SEARCH_ENV = "select subgroupcd from line where e_line_id=?";
		    
		    public static final String GET_SEARCH_ENV_SERVICE_NAME = "SELECT servicename from transaction_details where transaction_id =(select max(transaction_id) from transaction_metadata where external_transaction_id=?)";
		    
		    public static final String OLDACCOUNTVALUE ="select ACCOUNT_NUMBER from line where line_id=?";
		    
		    public static final String UPDATE_ACC = "update line set ACCOUNT_NUMBER=:ACCOUNT_NUMBER,SUBGROUPCD=:SUBGROUPCD,LAST_UPDATED=systimestamp where LINE_ID=:LINE_ID and MDN=:MDN";
			
			public static final String DELETE_ACC = "update line set ACCOUNT_NUMBER=null,SUBGROUPCD=null,LAST_UPDATED=systimestamp where LINE_ID=:LINE_ID and MDN=:MDN";
			
			public static final String UPDATE_LINE_HIS ="update line_history set OLD_VALUE=:OLDACCT,NEW_VALUE=:ACCOUNT_NUMBER where LINE_ID=:LINE_ID and MDN=:MDN";
			
			public static final String CHANNEL= "CHANNEL";
			
			public static final String AGENT_ID="AGENT_ID";
			
			public static final String ACCOUNT_NUMBER ="ACCOUNT_NUMBER";
			
			public static final String LINE_ID= "LINE_ID";
			
			
			public static final String AGENT_PHONE_NUMBER = "AGENT_PHONE_NUMBER";
			
			public static final String AGENT_FIRST_NAME = "AGENT_FIRST_NAME";
			
			public static final String AGENT_LAST_NAME = "AGENT_LAST_NAME";
			
			public static final String AGENT_EMAIL_ID = "AGENT_EMAIL_ID";
			
			public static final String AGENT_ADDRESSLINE1 = "AGENT_ADDRESSLINE1";
			
			public static final String AGENT_ADDRESSLINE2 = "AGENT_ADDRESSLINE2";
			
			public static final String AGENT_CITY = "AGENT_CITY";
			
			public static final String AGENT_STATE = "AGENT_STATE";
			
			public static final String AGENT_ZIPCODE = "AGENT_ZIPCODE";
			
			
			
			public static final String REFERENCENUMBER1 = "REFERENCENUMBER";
			public static final String TRANSACTION_NAME_CS="Change SIM";
			
			
			public static final String TN_CHANGE_RATE_PLAN="Change Rate Plan";
			
			public static final String ACTIVATE_SUBSCRIBER="Activate-Subscriber";
			
			public static final String INSERT_TRANSACTION_LINE_ASSOC ="INSERT " 
				    +"INTO TRANSACTION_LINE_ASSOC " 
				    +"  ( " 
				     +"    TRANSACTION_ID , " 
				     +"    LINE_ID , " 
				     +"    ACCOUNT_NUMBER , " 
				     +"    MDN , " 
				     +"    AGENT_ID , " 
				     +"    AGENT_FIRST_NAME , " 
				     +"    AGENT_LAST_NAME , " 
				     +"    AGENT_EMAIL_ID , " 
				     +"    AGENT_ADDRESSLINE1 , " 
				     +"    AGENT_ADDRESSLINE2 , " 
				     +"    AGENT_CITY , " 
				     +"    AGENT_STATE , " 
				     +"    AGENT_ZIPCODE , " 
				     +"    CHANNEL , " 
				     +"    AGENT_PHONE_NUMBER , " 
				     +"    REFERENCENUMBER , " 
				     +"    CREATED_DATE , " 
				     +"    CREATED_BY , " 
				     +"    MODIFIED_DATE , "
				     +"    MODIFIED_BY  " 
				    +"  ) " 
				    +"  VALUES " 
				    +"  ( " 
				    +"    :TRANSACTION_ID , " 
				    +"    :LINE_ID , "
				    +"    :ACCOUNT_NUMBER , " 
				    +"	  :MDN,"
				    +"    :AGENT_ID , " 
				    +"    :AGENT_FIRST_NAME , " 
				    +"    :AGENT_LAST_NAME , " 
				    +"    :AGENT_EMAIL_ID , " 
				    +"    :AGENT_ADDRESSLINE1 , " 
				    +"    :AGENT_ADDRESSLINE2 , " 
				    +"    :AGENT_CITY , " 
				    +"    :AGENT_STATE,"
				    +"    :AGENT_ZIPCODE ,"
				    +"    :CHANNEL ,"
				    +"    :AGENT_PHONE_NUMBER , " 
				    +"    :REFERENCENUMBER , " 
				    +"    systimestamp , "
				    +"    'NSL' ,"   			
				    +"    NULL , " 
				    +"    NULL  "			
				    +"  )";
		    
		   
		    public static final String GETIBREQUEST = "select request_msg,transaction_name from transaction_details where transaction_id=?";
		    
		    public static final String GETNBTRANSID = "SELECT ROOT_TRANSACTION_ID FROM transaction_details  WHERE TRANSACTION_ID =?";
		    
		    public static final String GETOBREQUEST = "select * from (select request_msg from transaction_details where rel_transaction_id=? and transaction_name<>'AsyncService' order by transaction_id desc) where ROWNUM=1";
		    
		    public static final String GETOBREQUEST1 = "select request_msg,transaction_name from transaction_details where rel_transaction_id=? and transaction_name not in ('AsyncService','Validate BYOD') order by transaction_id asc";
		    
		    public static final String TRANS_NAME = "transaction_name";
		    
			public static final String REQ_MSG ="request_msg";
			
			public static final String SUBORDER="subOrder";
			
			public static final String ADDITIONAL_DATA="additionalData";
			
			public static final String ACCOUNT="account";
			
			public static final String GET_SEARCH_ENV_STATUS = "SELECT servicename from transaction_details where transaction_id =(select max(transaction_id) from transaction_metadata where external_transaction_id=?)";

			public static final String NAME = "name";
			
			public static final String VALUE = "value";
			
			
		    
			public static final String GET_ORIGINAL_REFNUMBER = "SELECT request_msg from transaction_Details where rel_transaction_id=? and transaction_name='GateWayService' and servicename='MnoValidateService'";
			
			public static final String GET_ROOT_ID = "SELECT root_transaction_id from transaction_Details where transaction_uid=?";
			
			public static final String GET_TRANSACTION_ID = "SELECT transaction_id from transaction_Details where transaction_uid=?";
			
			public static final String GET_TRANSACTION_ID_OPERATION_NAME= "SELECT transaction_id,TRANSACTION_NAME from transaction_Details where transaction_uid=?";
			
			
			public static final String GET_SERVICE_NAME = "SELECT servicename from transaction_Details where transaction_id=(select root_transaction_id from transaction_details where transaction_id=?)";
			
			public static final String GET_SERVICE_NAME_SE = "SELECT servicename from transaction_Details where transaction_id=?";
			
			public static final String GET_ROOT_REQUEST_MSG =  "SELECT REQUEST_MSG from transaction_Details where transaction_id=(select root_transaction_id from transaction_details where transaction_id=?)"; 
			
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

		
			   
			public static final String RESPONSECODE = "200";
			
			public static final String REQUEST_TRANSACTION_ID = "select transaction_id from rh_csr_Request where reference_number=?";
			
			/*public static final String GET_REFERENCE_NO = "select REFERENCE_NUMBER from rh_csr_request where TRANSACTION_ID =(select ROOT_TRANSACTION_ID from transaction_details where transaction_id=?)";*/
			
			public static final String GET_REFERENCE_NO = "select EXTERNAL_TRANSACTION_ID from TRANSACTION_METADATA where TRANSACTION_ID =(select ROOT_TRANSACTION_ID from transaction_details where transaction_id=?)";
			
			public static final String GET_REQUEST = "SELECT REQUEST_MSG from transaction_details where transaction_id=?";
			
			public static final String GET_PARENT_CHANGESIM_SERVICE = "select count(servicename) from transaction_details where transaction_id=? and servicename='Change-SIM'";
			public static final String ACTIVATESUBSCRIBER_AP = "ActivateSubscriberAP";
			
			public static final String CONTENTTYPE = "Content-Type";
			
			public static final String APPLICATION_JSON = "application/json";
			
			enum CategoryName{
				MUNLP("Unlimited"),MUNL("Unlimited"),MBTG("Basic"),MSWAP("4GNUMSHARE")
				,TUNL("Unlimited"),TUNLP("Unlimited"),TBTG("Basic");
				private String category;

				public String getCategory() {
					return category;
				}
				
				private CategoryName(String category) {
					this.category=category;
				}
				
			}
			

			
			
			public static final String REF_NUM="referenceNumber";
			
			public static final String MESSAGE_HEADER="messageHeader";
			
			public static final String ERR_RESPONSE_CODE="\"responseCode\":\"E";
			
			public static final String NOT_RECEIVED_RESP = "[{\"status\":\"Response not received\"}]";
		   
			public static final String UNAUTH_RESP = "1012116 - Invalid token.~401";

			public static final String RESPONSEERROR = "select ERROR_CODE,ERROR_MSG from trans_failure_log where ROOT_TRANSACTION_ID=? and ERROR_CODE is not null and ERROR_MSG is not null";

		

			public static final String GET_SERVICE_NAME_AND_REQUEST_MSG = "SELECT servicename,REQUEST_MSG from transaction_Details where transaction_id=(select root_transaction_id from transaction_details where transaction_id=?)";

		

			public static final String GET_TRANSACTION_NAME = "SELECT transaction_name from transaction_Details where transaction_id=(select root_transaction_id from transaction_details where transaction_id=?)";

			public static final String GET_INELIGIBLE_DEVICE_DESC = "select VALUE_DESC from REFERENCE_VALUES where value=?";


			enum PlanDesc {
				CHART_DPFO20_5G_4GFB("CHARTER DPFO 2_0 5G_4G FALLBACK PLAN"),
				CH_DPFO20_5GTAB_4GFB("CHARTER 5G DPFO 2.0 TABLET PLAN WITH 4G FALLBACK"),
				CH_4GHD_DYNCAL_AQCI("CHARTER 4GHD DYNAMIC CAL AUTO QCI DPFO 2.0"),
				_4G_CH_4G_TAB_DYNCL_AQCI("CHARTER 4GHD TABLET DYNAMIC CAL AUTO QCI DPFO 2.0"),
				CHARTER_WEARABLE_AW("CHARTER HD WEARABLE APPLE WATCH"), SM_5G_CBRS_01("SPECTRUM MOBILE 5G CBRS PLAN"),
				CH_4G_TAB_DYNCL_AQCI("CHARTER 4GHD TABLET DYNAMIC CAL AUTO QCI DPFO 2.0"),
				CH_5G_CUSTOM_DCAL_3("CHARTER 5G CUSTOM BASE PLAN DYNAMIC CALENDAR 3.0"),
				CH_5G_CUST_DCAL_UNL3("CHARTER 5G CUSTOM BASE PLAN DYNAMIC CALENDAR UNLIMITED 3.0"),
				CH_5G_RB_CUST_DCAL_3("CHARTER 5G RADIO BLOCK CUSTOM BASE DYNAMIC CALENDAR 3.0"),
				CH_4G_TAB_CUST_DCAL3("CHARTER 4G TABLET CUSTOM BASE DYNAMIC CALENDAR 3.0"),
				CH_5G_TAB_CUST_DCAL3("CHARTER 5G TABLET CUSTOM BASE PLAN DYNAMIC CALENDAR 3.0"),
				CH_5GTAB_CSTDYN_UNL3("CHARTER 5G TABLET CUSTOM BASE PLAN DYNAMIC CALENDAR UNLIMITED 3.0"),
				CH_4G_CUSTOM_DCAL_3("CHARTER 4G CUSTOM BASE DYNAMIC CALENDAR 3.0");

				public String getDescription() {
					return description;
				}

				private void setDescription(String description) {
					this.description = description;
				}

				private String description;

				private PlanDesc(String PlanDesc) {
					this.description = PlanDesc;
				}

			}
			
			enum RetailPlanName{
				
				MUNL("Unlimited"),MUNLP("Unlimited Plus"),MBTG("By The Gig"),MSWAP("Smartwatch Access")
				,TUNL("Unlimited"),TUNLP("Unlimited Plus"),TBTG("By The Gig");
			

				private String retailPlanName ;

				public String getRetailPlanName() {
					return retailPlanName;
				}

				private void setRetailPlanName(String retailPlanName) {
					this.retailPlanName = retailPlanName;
				}

				private RetailPlanName(String PlanDesc ) {
					this.retailPlanName=PlanDesc;
				}
				
				
			}
			
			public static final String GET_UPDATE_PORT_IN_TRANSID = "select root_transaction_id from transaction_details where transaction_id=?";
			
			public static final String GET_UPDATE_PORT_IN_ACTIVATE_TRANSACTION_ID = "select min(transaction_id) from transaction_details where transaction_name in ('Activate Subscriber Port-in','ESIM Activate Subscriber Port-in') and root_transaction_id=?";
			
			public static final String GET_UPDATE_PORT_IN_REQUEST_MSG = "SELECT request_msg from transaction_Details where rel_transaction_id=? and transaction_name='GateWayService' and transaction_type='INBOUND'";
			
		    public static final String GET_UPDATE_PORT_IN_REQUEST_NUMBER = "SELECT JSON_VALUE(td.request_msg, '$.gatewayResponse.requestNo' RETURNING VARCHAR2) AS requestno FROM transaction_details td WHERE td.transaction_name LIKE 'GateWayService' AND td.transaction_type = 'INBOUND' AND td.root_transaction_id = ?";
		    
		    public static final String  GETDEVICEREQ_COUNT = "select count(*) from (SELECT response_msg,transaction_name FROM transaction_details WHERE root_transaction_id =? and transaction_name='Validate Device')";
		    
		    public static final String  GETDEVICEREQUEST = "select * from (SELECT response_msg,transaction_name FROM transaction_details WHERE root_transaction_id =? and transaction_name='Validate Device')";	    
	
			
			public static final String REQUEST_MSG="response_msg";
			
			public static final String ACTIVATE_SUB="Activate Subscriber";
			
			public static final String TRANSACTION_NAME_SS = "Suspend Subscriber";
		    
		    public static final String TRANSACTION_NAME_SH = "Hotline Subscriber";
		    
		    public static final String TRANSACTION_NAME_RS="Restore Service";
		    
		    public static final String TRANSACTION_NAME_RH = "Remove Hotline";

			public static final String TRANSACTION_TYPE_RH = "RH";
			
			public static final String LINE_STATUS_SS = "SUSPEND";
			
			public static final String SIM_STATUS_SS = "SUSPENDED";
			
			public static final String ORDER_TYPE_SS = "SUSPEND";
			
			public static final String TRANSACTION_TYPE_SS = "SS";
			
			public static final String OLD_VALUE_SS = "ACTIVE";

			public static final String NEW_VALUE_SS = "SUSPEND";
			
			public static final String FIELD_TYPE_SS = "LINE_STATUS";
			
			public static final String LINE_STATUS_SH= "HOTLINE";
			
			public static final String SIM_STATUS_SH= "HOTLINE";
			
			public static final String FIELD_TYPE_RH = "MDN";

			public static final String ORDER_TYPE_RH ="REM_HOT";
			
			public static final String ORDER_TYPE_SH = "HOTLINE";
			
			public static final String TRANSACTION_TYPE_SH = "SH";
			
			public static final String FIELD_TYPE_SH_2 = "LINE_STATUS";
			
			public static final String LINE_STATUS_RS = "ACTIVE";

			public static final String SIM_STATUS_RS = "ACTIVE";
			
			public String ORDER_TYPE_RS = "RESTORE";
			
			public static final String TRANSACTION_TYPE_RS = "RS";
			
			public static final String ACC_STATUS= "ACTIVE";
			
			public static final String LINE_STATUS = "ACTIVE";
			
			public static final String SIM_STATUS = "ACTIVE";
			
			public static final String TRANSACTION_NAME_SD = "Deactivate Subscriber";
			
			public static final String LINE_STATUS_SD = "DEACTIVE";
			
			public static final String ACC_STATUS_SD= "DEACTIVE";

			public static final String SIM_STATUS_SD = "DEACTIVATED";
			
			public static final String ORDER_TYPE_SD = "DEACTIVATE";
			
			public static final String TRANSACTION_TYPE_SD = "SD";
			
			public static final String ERR_CODE_500="\"code\":\"500\"";
			
			public static final String ERR_CODE_504="\"returnCode\":\"504\"";
			   
	        public static final String SUCC_500="SUCC_500";
			
			public static final String SUCCESS_500="500";
				   
		    public static final String CODE = "code";

			public static final String GET_ADDITIONAL_DATA ="select * from ref_additional_data where transactionid=?";
			
			
			
			public static final String SUBSCRIBERGROUPINQUIRY = "Subscribergroup Inquiry";
	
			
			public static final String SER_ENV_INTERNAL_ERROR="{\"messageHeader\":{\"serviceId\":\"SPECTRUM_MOBILE\",\"requestType\":\"MNO\",\"referenceNumber\":\"921914343343434343\"},\"data\":{\"code\":\"500\",\"reason\":\"Internal Server Error\",\"message\":[{\"responseCode\":\"ERR18\",\"description\":\"Request Timed Out. Try again later\"}]}}";

			public static final String NE = "NE";
			
			enum resourceUpdateServiceUrl {

				UpdateSubscriberStatus("/resourceUpdatesService"), SuspendSubscriber("/resourceUpdateSuspendService"),
				DeactivateSubscriber("/resourceUpdateDeactivateService"),HotlineSubscriber("/resourceUpdateHotlineService"), ChangeBCD("/resourceUpdateChangeBCDService"),
				RestoreService("/resourceUpdateRestoreSuspendService"), ReconnectService("/resourceUpdateReconnectService"),
				RemoveHotline("/resourceUpdateRemoveHotlineService"),

				ManageAccount("/resourceUpdateManageAccountService"),
				ValidateDevice_DeviceDetection("/resourceUpdatesService"), DEFAULT("/resourceUpdatesService");

				private String serviceUrl;

				public String getServiceUrl() {
					return serviceUrl;
				}

				private void setServiceUrl(String serviceUrl) {
					this.serviceUrl = serviceUrl;
				}

				private resourceUpdateServiceUrl(String serviceUrl) {
					this.serviceUrl = serviceUrl;
				}
			}
			
			public static final String CBRS = "CBRS";

			
			
			public static final String CHANGE_BCD_ROLLBACK = "Change BillCycleDay";
			
		
					
			public static final String LNPINFORMATION="$.data.lnp.ospAccountNo,$.data.lnp.pin,$.data.lnp.address.zip,$.data.lnp.address.city,$.data.lnp.address.state,$.data.lnp.address.addressLine1,$.data.lnp.address.addressLine2,$.data.lnp.lnpName.business,$.data.lnp.lnpName.name.first,$.data.lnp.lnpName.name.last";
			
			public static final String REQUESTLNPINFORMATION="$.data.subOrder[0].lnp.ospAccountNo,$.data.subOrder[0].lnp.pin,$.data.subOrder[0].lnp.lnpAddress.zipCode,$.data.subOrder[0].lnp.lnpAddress.city,$.data.subOrder[0].lnp.lnpAddress.state,$.data.subOrder[0].lnp.lnpAddress.addressLine1,$.data.subOrder[0].lnp.lnpAddress.addressLine2,$.data.subOrder[0].lnp.lnpName.businessName,$.data.subOrder[0].lnp.lnpName.name.firstName,$.data.subOrder[0].lnp.lnpName.name.lastName";
			
			public static final String UPDATE_REQUESTMSG_LNPINFORMATION="update transaction_details set REQUEST_MSG = :REQUEST_MSG where transaction_id = :TRANSACTION_ID";

			public static final String GETLINE_HISTORY_TRANS_ID_AND_STATUS = "/getLineHistoryByTransIdandFieldType";
			
			public static final String GETCONTEXTFROMURL="SELECT issuer_name FROM SERVICE_INFO WHERE SERVICE_url=? and service_name='COMMON_MBO_DNS'";
			
			
			
			public static final String UPDATE_TRANSACTION_METADATA = "UPDATE TRANSACTION_METADATA SET SERVICE_URL = :SERVICE_URL where TRANSACTION_ID = :TRANS_ID";

			public static final String UPDATE_SHARED_NAME = "Update Shared Name";
			
			public static final String QUERY_SHARED_NAME = "Query Shared Name";
			
			public static final Configuration configuration = Configuration.builder()
				    .jsonProvider(new JacksonJsonNodeJsonProvider())
				    .mappingProvider(new JacksonMappingProvider())
				    .build();

			public static final String GET_ROOT_OPERATION_NAME = "select transaction_name from transaction_details where transaction_id=?";
		
			public static final String ERROR_RESPONSE = "{\"code\":\"code\",\"reason\":\"reason\",\"message\":[{\"responseCode\":\"ERR18\",\"description\":\"description\"}]}";
			
			public static final String DEFAULT_DESCRIPTION ="Unable to process the request";
			
			public static final String RESPONSE_CODE ="ERR18";
			
			String SWAPMDN_WF = "swapMDNAPWF";

			String SWAPMDN_PP = "swapMDN";
			
			public static final String DEVICE_CHECK_GSMA = "Device Check GSMA";
}
