/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gerald.umaas.domain.repositories.projection;

import org.springframework.data.rest.core.config.Projection;

import com.gerald.umaas.domain.entities.AppUser;
import com.gerald.umaas.domain.entities.UserGroup;

/**
 *
 * @author Dev7
 */
@Projection(name = "detailedUserUG", types = {UserGroup.class})
public interface UserGroupProjectionWithDetailedUser {
    public AppUser getUser();
}
