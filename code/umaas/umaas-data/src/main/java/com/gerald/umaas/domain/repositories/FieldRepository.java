/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gerald.umaas.domain.repositories;

import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.gerald.umaas.domain.entities.Domain;
import com.gerald.umaas.domain.entities.Field;

/**
 *
 * @author Dev7
 */
public interface FieldRepository extends DomainResourceRepository<Field, String>{
	public Field findByDomainIdAndName(@Param("domain")String domain, @Param("name") String name);
    @RestResource(exported = false)
	public Field findByDomainAndName(Domain d, String groupName);
}
