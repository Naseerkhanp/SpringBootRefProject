package com.excelacom.century.apolloneoutbound.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TRANSACTION_DETAILS")
@ToString
@EqualsAndHashCode
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3245598052741951577L;

	@Id
	//@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_id_gen")
	//@SequenceGenerator(name = "transaction_id_gen", sequenceName = "seq_transaction_id", allocationSize = 1)
	@Column(name = "TRANSACTION_ID")
	private Long transactionId;

	@Column(name = "REL_TRANSACTION_ID")
	private String relTransactionId;

	@Column(name = "APPLICATION_NAME")
	private String applicationName;

	@Column(name = "TRANSACTION_TYPE")
	private String transactionType;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "REQ_SENT_DATE")
	private ZonedDateTime reqSentDate;

	@Column(name = "RESP_RECEIVED_DATE")
	private ZonedDateTime respReceivedDate;

	@Column(name = "CREATED_DATE")
	private ZonedDateTime createdDate;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_DATE")
	private ZonedDateTime modifiedDate;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "EXTERNAL_ID1")
	private String externalId1;

	@Column(name = "NOTIFI_ENTITY_ID")
	private Long notifiEntityId;

	@Column(name = "NOTIFI_REL_ENTITY_ID")
	private Long notifiRelEntityId;

	@Column(name = "NOTIFI_ENTITY_TYPE")
	private String notifiEntityType;

	@Column(name = "NOTIFI_RETRY_COUNTS")
	private Long notifiRetryCounts;

	@Column(name = "NOTIFI_ENTITY_STATUS")
	private String notifiEntityStatus;

	@Column(name = "RESPONSE_MSG")
	@Lob
	private String responseMsg;

	@Column(name = "REQUEST_MSG")
	@Lob
	private String requestMsg;

	@Column(name = "ENTITY_ID")
	private String entityId;

	@Column(name = "ICC_VAL")
	private String iccVal;

	@Column(name = "TRANSACTION_UID")
	private String transactionUid;

	@Column(name = "GROUP_ID")
	private String groupId;

	@Column(name = "TRANS_GROUP_ID")
	private Long transGroupId;

	@Column(name = "SERVICENAME")
	private String serviceName;

	@Column(name = "TRANSACTION_NAME")
	private String transactionName;

	@Column(name = "HTTP_REQUEST")
	private String httpRequest;

	@Column(name = "HTTP_RESPONSE")
	private String httpResponse;

	@Column(name = "NOTES")
	private String notes;

	@Column(name = "SOURCE_SYSTEM")
	private String sourceSystem;

	@Column(name = "ORDERTIMESTAMP")
	private ZonedDateTime orderTimeStamp;

	@Column(name = "TENANT_ID")
	private Integer tenantId;

	@Column(name = "TRANSACTION_STATUS")
	private String transactionStatus;

	@Column(name = "ROOT_TRANSACTION_ID")
	private Long rootTransactionId;

	@Column(name = "TARGET_SYSTEM")
	private String targetSystem;

	@Column(name = "EXT_TRANSACTION_ID")
	private String extTransactionId;

	@Column(name = "CHANNEL")
	private String channel;

	@Column(name = "AGENT_ID")
	private String agentId;

	@Column(name = "USER_LOGIN_ID")
	private String userLoginId;

	@Column(name = "USER_LOGIN_REF")
	private String userLoginRef;
	
	@Column(name = "WORKFLOW_NAME")
	private String workflowName;

}
