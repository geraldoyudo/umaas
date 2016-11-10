/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gerald.umaas.domain.repositories;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.gerald.umaas.domain.entities.DomainResource;


/**
 *
 * @author Dev7
 * @param <T>
 * @param <V>
 */
@NoRepositoryBean
public interface DomainResourceRepository <T extends DomainResource,V extends Serializable> 
extends ResourceRepository<T,V>{
     
    @Query("{ 'domain.id': '?0' }")
    @RestResource(exported = false)
    public List<T> findByDomain( V domain);
    @Query("{'domain.id': '?0' }")
    public Page<T> findByDomain(@Param("domain") V domain, Pageable p );
     @Query("{ 'domain.id': '?0', 'externalId': '?1'}")
    public T findByDomainAndExternalId( @Param("domain") V domain, @Param("externalId") String externalid);
   
}
