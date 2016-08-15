/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gerald.umaas.domain.repositories;

import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.gerald.umaas.domain.entities.Domain;

/**
 *
 * @author Dev7
 */

@RepositoryRestResource
public interface DomainRepository extends ResourceRepository<Domain, String>{
    public Domain findByCode(@Param("code") String code );
    public Domain findByName(@Param("name") String name);
}
