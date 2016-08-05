/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gerald.umaas.domain.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.gerald.umaas.domain.entities.RoleMapping;
import com.gerald.umaas.domain.repositories.projection.RoleMappingProjection;

/**
 *
 * @author Dev7
 */
@RepositoryRestResource(excerptProjection = RoleMappingProjection.class)
public interface RoleMappingRepository extends  DomainResourceRepository<RoleMapping, String>{
    @Query("{'domain.id': '?0','key': ?1,'type': ?2 }")
    public RoleMapping findByDomainAndKeyAndType(
            String domain, String key, RoleMapping.RoleMappingType type);
}
