/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gerald.umaas.domain.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.gerald.umaas.domain.entities.Domain;

/**
 *
 * @author Dev7
 */

public interface DomainRepository extends ResourceRepository<Domain, String>{
    @RestResource(exported = false)
    public List<Domain> getDomainByCode(String code);
    public Page<Domain> getDomainByCode(@Param("code") String code, Pageable p );
    @RestResource(exported = false)
    public List<Domain> getDomainByName(String domain);
    public Page<Domain> getDomainByName(@Param("name") String domain, Pageable p);
}
