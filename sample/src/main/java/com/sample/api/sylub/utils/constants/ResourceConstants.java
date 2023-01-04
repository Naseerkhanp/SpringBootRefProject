package com.excelacom.century.apolloneoutbound.utils.constants;

public interface ResourceConstants {

	enum resourceUpdateServiceUrl {

		GETREFERENCEVALUEDESC("/getReferenceValueDesc"), GETACCOUNTSUMMARY("/getAccountSummary"),
		GETLINEPLANBYLINEID("/getlineplanbylineid"), GETMALINEDETAILSBYACCOUNTNO("/getMALineDetailsByAccountNo"),
		GETLINEHISTORYDETAILSBYLINEID("/getlinehistorydetailsbylineid"),
		GETACTIVELINEDETAILSBYELINEID("/getActiveLinedetailsbyELineId"),
		GETLINEDETAILSBYLINEID("/getlinedetailsbylineid"),
		GETACTIVELINEDETAILSBYACCNUM("/getActiveLineDetailsByAccNum"),
		GETLINEDETAILSBYACCTNUMBER("/getlinedetailsbyacctnumber"), GETACCDETAILS("/getAccDetails"),
		GETCALLLINEACCOUNTRESOURCESERVICE("/getCallLineAccountResourceService"),
		GETPROMOTIONDETAIL("/getPromotionDetail"), GETFEATUREDETAILSBYLINEID("/getfeaturedetailsbylineid"),
		ACCOUNTDETAILSRESOURCESERVICEURL("/AccountDetailsResourceServiceURL"),
		UPDATEIMSIBYLINEID("/updateImsibylineid"), UPDATEINFLIGHTTRANSSTATUS("/updateInflightTransStatus"),
		UPDATETRANSACTIONHISTORYFORNS("/updateTransactionHistoryForNS"),
		LINEDETAILSLINEIDRESOURCESERVICEURL("/lineDetailsLineIdResourceServiceURL"),
		SIMDETAILSBYICCIDFORSEARCHENVIRONMENTLINEID("/simDetailsByIccIdForSearchEnvironmentLineId"),
		SIMDETAILSBYICCIDFORSEARCHENVIRONMENT("/simDetailsByIccIdForSearchEnvironment"),
		GETSIMDETAILSBYLINEID("/getsimdetailsbylineid"), GETDEVICEDETAILSBYLINEID("/getdevicedetailsbylineid"),
		GETLINEHISTORYBYQUERY("/getlinehistorybyquery"), GETREFERENCEVALUE("/getReferenceValue"),
		TRANSACTIONHISTORYBYMDN("/transactionHistoryByMdn"), TRANSACTIONHISTORYBYLINEID("/transactionhistorybyLineid"),
		GETLINEDETAILSBYHOSTMDN("/getLineDetailsByHostMdn"),
		TRANSACTIONHISTORYBYLINEIDANDDATE("/transactionhistorybyLineidDate"),
		GETLINEDETAILSBYMDN("/getlinedetailsbymdn"), GETTRANSACTIONHISTORYBYTRANSID("/getTransactionHistoryByTransId"),
		GETLINESUMMARY("/getLineSummary"), GETTRANSACTIONHISTORYFORUPDATEIMSI("/getTransactionHistoryForUpdateIMSI"),
		GETFIELDTYPEBLINEHISTORYBYLINEID("/getFieldTypeLineHistoryByLineId"),ManageAccount("/resourceUpdateManageAccountService")
		,DEFAULT("/resourceUpdatesService"),GETNPANXXDETAILSBYZIPCODE("/getNpaNxxbyZipCode"),GET_REF_ERROR_RULES("/getRefErrorRulesUsingDetails")
		,UpdateSubscriberStatus("/resourceUpdatesService"), SuspendSubscriber("/resourceUpdateSuspendService"),
		DeactivateSubscriber("/resourceUpdateDeactivateService"),HotlineSubscriber("/resourceUpdateHotlineService"), ChangeBCD("/resourceUpdateChangeBCDService"),
		RestoreService("/resourceUpdateRestoreSuspendService"), ReconnectService("/resourceUpdateReconnectService"),
		RemoveHotline("/resourceUpdateRemoveHotlineService"),FALLOUT_SAVE("/saveFalloutTransactionDetail"),
		ValidateDevice_DeviceDetection("/resourceUpdatesService"),GETERRORDETAILS("/getErrorDetails"),
		UPDATEALTZIPCODE("/updateAltZipcode"),GETRESOURCEINFO("/getResourceInfo"),GETPROCESSMETA("/getProcessMetadata"),INSERTPROCESSMETADATA("/saveProcessMetadata")
		,UPDATERESOURCEINFO("/updateResourceInfo"),GSMAHISTORYUSINGIMEI("/getGsmaHistoryListUsingImeiAndStatus"),SAVEDEVICEGSMA("/saveDeviceGsmaHistory"),SWAPLINEIDFORTRANSFERDEVICE("/swapLineIdForTransferDevice"),
		UPDATEDEVICEINFO("/updateDeviceInfoForSmartWatch"),UPDATESTGPLANMIGRATIONLIST("/updateStgPlanMigrationList");

		private String serviceUrl;

		public String getServiceUrl() {
			return serviceUrl;
		}

		private void setServiceUrl(String serviceUrl) {
			this.serviceUrl = serviceUrl;
		}

		resourceUpdateServiceUrl(String serviceUrl) {
			this.serviceUrl = serviceUrl;
		}
	}

}
