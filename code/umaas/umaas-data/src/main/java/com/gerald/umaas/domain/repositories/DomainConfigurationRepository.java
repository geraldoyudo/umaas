package com.gerald.umaas.domain.repositories;

import com.gerald.umaas.domain.entities.DomainConfiguration;

public interface DomainConfigurationRepository extends ResourceRepository<DomainConfiguration, String> {
	public DomainConfiguration findByDomainId(String domainId);
}
