package com.excelacom.century.apolloneoutbound.dao.respository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.excelacom.century.apolloneoutbound.entity.ServiceInfo;

@Repository
public interface ServiceInfoRepository extends CrudRepository<ServiceInfo, Long> {

	@Query(value = "SELECT si.endPointUrl FROM ServiceInfo si where serviceName = :serviceName and si.server = :server")
	public Optional<String> getEndpointUrl(@Param("serviceName") String serviceName, @Param("server") String server);

	public Optional<ServiceInfo> findByServiceNameAndServer(String serviceName, String server);
	
	@Query(value = "SELECT si.serviceUrl FROM ServiceInfo si where serviceName = :serviceName")
	public String findByServiceName(@Param("serviceName") String serviceName);
	
	@Query(value = "SELECT si.endPointUrl2 FROM ServiceInfo si where serviceName = :serviceName and si.server = :server")
	public Optional<String> getEndpointUrl2(@Param("serviceName") String serviceName, @Param("server") String server);
	
	@Query(value = "SELECT si.serviceUrl FROM ServiceInfo si where serviceName = :serviceName and si.server = :server")
	public Optional<String> getServiceUrl(@Param("serviceName") String serviceName, @Param("server") String server);
}
