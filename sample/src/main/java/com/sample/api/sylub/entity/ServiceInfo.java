package com.excelacom.century.apolloneoutbound.entity;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "SERVICE_INFO")
@ToString
@EqualsAndHashCode
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInfo implements Serializable {

	private static final long serialVersionUID = -3987578320423487077L;

	@Id
	@Column(name = "SERVICE_ID")
	private Long seviceId;

	@Column(name = "SERVICE_NAME")
	private String serviceName;

	@Column(name = "SERVICE_URL")
	private String serviceUrl;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "VERSION")
	private String version;

	@Column(name = "ISSUER_NAME")
	private String issuerName;

	@Column(name = "SERVICE_TYPE")
	private String serviceType;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	@Column(name = "SERVICE_PORT")
	private Long servicePort;

	@Column(name = "SERVICE_REQUEST")
	@Lob
	private String serviceRequest;

	@Column(name = "TYPE")
	private String type;

	@Column(name = "ACTION")
	private String action;

	@Column(name = "RETRYLIMIT")
	private String retryLimit;

	@Column(name = "TIMEDELAY")
	private Integer timeDelay;

	@Column(name = "SERVER")
	private String server;

	@Column(name = "HEALTH_STATUS")
	private String healthStatus;

	@Column(name = "RESPONSE_TIME")
	private Date responseTime;

	@Column(name = "LAST_RESPOND_TIME")
	private Date lastRespondTime;

	@Column(name = "SERVICE_GROUP")
	private String serviceGroup;

	@Column(name = "SERVICE_DESCRIPTION")
	private String serviceDescription;

	@Column(name = "SERVICE_GROUP_ID")
	private Integer serviceGroupId;

	@Column(name = "METHOD")
	private String method;

	@Column(name = "TENANT_ID")
	private Integer tenantId;

	@Column(name = "GRAPH_META_JSON")
	private Blob graphMetaJson;

	@Column(name = "PROCESSPLAN")
	private String processPlan;

	@Column(name = "REQUEST_PARAM_TYPE")
	private String requestParamType;

	@Column(name = "AUTH_TYPE")
	private String authType;

	@Column(name = "END_POINT_URL")
	private String endPointUrl;

	@Column(name = "END_POINT_URL2")
	private String endPointUrl2;

}
