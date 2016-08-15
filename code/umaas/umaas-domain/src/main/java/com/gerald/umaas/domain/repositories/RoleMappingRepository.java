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

import com.gerald.umaas.domain.entities.Role;
import com.gerald.umaas.domain.entities.RoleMapping;
import com.gerald.umaas.domain.entities.RoleMapping.RoleMappingType;

/**
 *
 * @author Dev7
 */

public interface RoleMappingRepository extends  DomainResourceRepository<RoleMapping, String>{
    @Query("{'domain.id': '?0','key': ?1,'type': ?2 }")
    public List<RoleMapping> findByDomainAndKeyAndType(
            String domain, String key, RoleMapping.RoleMappingType type);

	public List<RoleMapping> findByKeyAndType(String id, RoleMappingType type);

	public List<RoleMapping> findByRole(Role role);
	public RoleMapping findByKeyAndRoleId(@Param("key") String key, @Param("roleId") String roleId);
	public Page<RoleMapping> findByDomainIdAndKey(@Param("domain") String domainId, @Param("key") String key, Pageable p);
}
