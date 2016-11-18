package com.gerald.umaas.domain.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.gerald.umaas.domain.entities.DomainAccessCode;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping.Priviledge;

public interface DomainAccessCodeMappingRepository extends ResourceRepository<DomainAccessCodeMapping, String> {
	@RestResource(exported = false)
	public List<DomainAccessCodeMapping> findByAccessCodeAndEntityType(DomainAccessCode accessCode, String entityType);
	public Page<DomainAccessCodeMapping> findByAccessCodeIdAndEntityType(
			@Param("codeId") String accessCodeId, @Param("entityType") String entityType, Pageable p);

	@RestResource(exported = false)
	public List<DomainAccessCodeMapping> findByAccessCodeAndEntityTypeAndEntityId(DomainAccessCode accesscode,
			String entityType, String entityId);
	
	public Page<DomainAccessCodeMapping> findByAccessCodeIdAndEntityTypeAndEntityId
	(@Param("codeId") String accesscodeId,
			@Param("entityType") String entityType, @Param("entityId") String entityId, Pageable p);
	
	@RestResource(exported = false)
	public DomainAccessCodeMapping findByAccessCodeAndEntityTypeAndEntityIdAndPriviledge(DomainAccessCode accesscode,
			String entityType, String entityId, Priviledge priviledge);
	
	public DomainAccessCodeMapping findByAccessCodeIdAndEntityTypeAndEntityIdAndPriviledge
	(@Param("codeId") String accessCodeId,
			@Param("entityType") String entityType,@Param("entityId") String entityId, 
			@Param("priviledge") Priviledge priviledge);
	
	@RestResource(exported = false)
	public long countByAccessCodeAndEntityTypeAndEntityIdAndPriviledge(DomainAccessCode accesscode,
			String entityType, String entityId, Priviledge priviledge);
	@RestResource(exported = false)
	public List<DomainAccessCodeMapping> findByAccessCode(DomainAccessCode accessCode);
	
	
}
