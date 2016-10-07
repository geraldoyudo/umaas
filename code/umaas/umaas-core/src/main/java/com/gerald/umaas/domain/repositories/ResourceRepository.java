/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gerald.umaas.domain.repositories;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.gerald.umaas.domain.entities.Resource;

/**
 *
 * @author Dev7
 * @param <T>
 * @param <V>
 */
@NoRepositoryBean
public interface ResourceRepository<T extends Resource,V extends Serializable> extends MongoRepository<T, V>{
    @RestResource(exported = false)
    public List<T> findByExternalId(V externalId);
    public Page<T> findByExternalId(@Param("externalId")V externalId, Pageable p);
}
