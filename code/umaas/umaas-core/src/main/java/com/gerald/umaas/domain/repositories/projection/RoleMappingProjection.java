/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gerald.umaas.domain.repositories.projection;

import java.util.Set;

import org.springframework.data.rest.core.config.Projection;

import com.gerald.umaas.domain.entities.RoleMapping;
import com.gerald.umaas.domain.entities.RoleMapping.RoleMappingType;

/**
 *
 * @author Dev7
 */
@Projection(name = "roleMappingProjection", types = RoleMapping.class)
public interface RoleMappingProjection {
    public RoleMappingType getType();
    public String getKey();
    public Set<DetailedNamedProjection> getRoles();
       
}
