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

import com.gerald.umaas.domain.entities.AppUser;
import com.gerald.umaas.domain.entities.Field;
import com.gerald.umaas.domain.entities.UserField;

/**
 *
 * @author Dev7
 */
public interface UserFieldRepository extends DomainResourceRepository<UserField, String>{
   public UserField findByUserAndField(AppUser user, Field field);
   @RestResource(exported = false)
   public List<UserField> findByUser(AppUser user);
   public Page<UserField> findByUserId(@Param("userId") String userId, Pageable p);
   public List<UserField> findByField(Field f);
   public UserField findByUserIdAndFieldId(@Param("userId") String userId, @Param("fieldId") String fieldId);

}
