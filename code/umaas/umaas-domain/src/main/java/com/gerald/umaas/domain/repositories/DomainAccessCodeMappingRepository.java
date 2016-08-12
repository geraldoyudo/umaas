package com.gerald.umaas.domain.repositories;

import java.util.List;

import com.gerald.umaas.domain.entities.DomainAccessCode;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping.Priviledge;

public interface DomainAccessCodeMappingRepository extends ResourceRepository<DomainAccessCodeMapping, String> {
	public List<DomainAccessCodeMapping> findByAccessCodeAndEntityType(DomainAccessCode accessCode, String entityType);
	public List<DomainAccessCodeMapping> findByAccessCodeAndEntityTypeAndEntityId(DomainAccessCode accesscode,
			String entityType, String entityId);
	public DomainAccessCodeMapping findByAccessCodeAndEntityTypeAndEntityIdAndPriviledge(DomainAccessCode accesscode,
			String entityType, String entityId, Priviledge priviledge);
	public long countByAccessCodeAndEntityTypeAndEntityIdAndPriviledge(DomainAccessCode accesscode,
			String entityType, String entityId, Priviledge priviledge);
	public List<DomainAccessCodeMapping> findByAccessCode(DomainAccessCode accessCode);
	
	
}
