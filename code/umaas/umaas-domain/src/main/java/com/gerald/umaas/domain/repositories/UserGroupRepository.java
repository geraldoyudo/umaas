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
import org.springframework.data.rest.core.annotation.RestResource;

import com.gerald.umaas.domain.entities.AppUser;
import com.gerald.umaas.domain.entities.Group;
import com.gerald.umaas.domain.entities.UserGroup;

/**
 *
 * @author Dev7
 */
public interface UserGroupRepository extends DomainResourceRepository<UserGroup, String>{
   public UserGroup findByUserAndGroup(AppUser user, Group group);
   @RestResource(exported = false)
   public List<UserGroup> findByUser(AppUser user);
   public List<UserGroup> findByGroup(Group g);
   public Page<UserGroup> findByUserId(@Param("userId") String userId, Pageable p);
   public UserGroup findByUserIdAndGroupId(@Param("userId") String userId, @Param("groupId") String groupId);
}
