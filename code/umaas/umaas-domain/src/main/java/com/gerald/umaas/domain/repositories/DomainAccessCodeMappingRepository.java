package com.gerald.umaas.domain.repositories;

import java.util.List;

import com.gerald.umaas.domain.entities.Domain;
import com.gerald.umaas.domain.entities.DomainAccessCode;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping;

public interface DomainAccessCodeMappingRepository extends ResourceRepository<DomainAccessCodeMapping, String> {
	List<DomainAccessCodeMapping> findByAccessCodeAndDomain(DomainAccessCode accessCode, Domain domain);
	List<DomainAccessCodeMapping> findByAccessCodeAndDomainAndAspect(DomainAccessCode accesscode,
			Domain domain, DomainAccessCodeMapping.Aspect aspect);
	
	
}
