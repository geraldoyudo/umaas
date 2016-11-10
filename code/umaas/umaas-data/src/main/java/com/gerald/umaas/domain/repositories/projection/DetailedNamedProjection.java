/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gerald.umaas.domain.repositories.projection;

import java.util.Map;

import org.springframework.data.rest.core.config.Projection;

import com.gerald.umaas.domain.entities.Group;
import com.gerald.umaas.domain.entities.Role;

/**
 *
 * @author Dev7
 */
@Projection(name = "detailedNamed", types = {Group.class, Role.class})
public interface DetailedNamedProjection {
     public String getId();
    public String getName();
    public String getExternalId();
    public Map<String,Object> getMeta();
}
