/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gerald.umaas.domain.repositories;

import java.util.List;

import com.gerald.umaas.domain.entities.AppUser;
import com.gerald.umaas.domain.entities.Group;
import com.gerald.umaas.domain.entities.UserGroup;

/**
 *
 * @author Dev7
 */
public interface UserGroupRepository extends ResourceRepository<UserGroup, String>{
   public UserGroup findByUserAndGroup(AppUser user, Group group);
   public List<UserGroup> findByUser(AppUser user);
   public List<UserGroup> findByGroup(Group g);
}
