package com.excelacom.century.apolloneoutbound.dao.respository;

import java.time.ZonedDateTime;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.excelacom.century.apolloneoutbound.entity.TransactionDetails;

@Repository
public interface TransactionRepository extends CrudRepository<TransactionDetails, Long> {

	@Transactional
	@Modifying
	@Query(value = "update TransactionDetails td set td.status = :status, td.respReceivedDate = :respDate, td.responseMsg = :respMsg, td.httpResponse = :respCode, td.entityId = :entityId, td.groupId = :grpId, td.targetSystem = :target where td.transactionUid = :transUid")
	public int updateTransactionDetails(@Param(value = "status") String status,
			@Param(value = "respDate") ZonedDateTime respDate, @Param(value = "respMsg") String respMsg,
			@Param(value = "respCode") String respCode, @Param(value = "entityId") String entityId,
			@Param(value = "grpId") String grpId, @Param(value = "transUid") String transUid,
			@Param(value = "target") String target);

	@Query(value = "select seq_transaction_id.nextval from dual", nativeQuery = true)
	public Long getPrimaryKey();
	
	@Transactional
	@Modifying
	@Query(value = "update TransactionDetails td set td.status = :status where td.transactionUid = :transUid")
	public int updateQueueTransactionDetails(@Param(value = "status") String status,@Param(value = "transUid") String transUid);
	
	@Transactional
	@Modifying
	@Query(value = "update TransactionDetails td set td.requestMsg = :requestMsg where td.transactionId = :transactionId")
	public int updateRequestDetails(@Param(value = "requestMsg") String requestMsg,@Param(value = "transactionId") Long transactionId);
	
	@Query(value = "Select td.responseMsg from TransactionDetails  td where td.relTransactionId = :relTransactionId and td.transactionName = :transactionName")
	public String getOutboundResponseMsg(@Param(value = "relTransactionId") String relTransactionId,@Param(value = "transactionName") String transactionName);
	
}

