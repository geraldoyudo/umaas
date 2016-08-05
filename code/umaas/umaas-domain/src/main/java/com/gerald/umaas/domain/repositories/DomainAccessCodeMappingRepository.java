package com.gerald.umaas.domain.repositories;

import java.util.List;

import com.gerald.umaas.domain.entities.Domain;
import com.gerald.umaas.domain.entities.DomainAccessCode;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping;

public interface DomainAccessCodeMappingRepository extends ResourceRepository<DomainAccessCodeMapping, String> {
	public List<DomainAccessCodeMapping> findByAccessCodeAndEntityType(DomainAccessCode accessCode, String entityType);
	public DomainAccessCodeMapping findByAccessCodeAndEntityTypeAndEntityId(DomainAccessCode accesscode,
			String entityType, String entityId);
	
	
}
