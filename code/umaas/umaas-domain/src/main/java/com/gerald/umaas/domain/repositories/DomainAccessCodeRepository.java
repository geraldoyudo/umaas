package com.gerald.umaas.domain.repositories;

import com.gerald.umaas.domain.entities.Domain;
import com.gerald.umaas.domain.entities.DomainAccessCode;
import java.lang.String;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

public interface DomainAccessCodeRepository extends ResourceRepository<DomainAccessCode, String>{

	public DomainAccessCode findByCode(String code);
}
