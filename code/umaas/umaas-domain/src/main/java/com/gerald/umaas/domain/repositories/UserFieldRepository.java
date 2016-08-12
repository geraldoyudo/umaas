/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gerald.umaas.domain.repositories;

import java.util.List;

import com.gerald.umaas.domain.entities.AppUser;
import com.gerald.umaas.domain.entities.Field;
import com.gerald.umaas.domain.entities.UserField;

/**
 *
 * @author Dev7
 */
public interface UserFieldRepository extends ResourceRepository<UserField, String>{
   public UserField findByUserAndField(AppUser user, Field field);
   public List<UserField> findByUser(AppUser user);
   public List<UserField> findByField(Field f);
}
