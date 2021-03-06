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

import com.gerald.umaas.domain.entities.Role;

/**
 *
 * @author Dev7
 */
public interface RoleRepository extends DomainResourceRepository<Role, String>{
     @RestResource(exported = false)
     public List<Role> findByName(String name);
     public Role findByDomainIdAndName(@Param("domain")String domain, @Param("name")String name);
     public Page<Role> findByName(@Param("name") String name, Pageable p );
    
 
   
}
