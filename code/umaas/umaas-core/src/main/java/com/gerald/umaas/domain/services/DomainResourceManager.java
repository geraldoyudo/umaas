/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gerald.umaas.domain.services;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;
import org.springframework.stereotype.Component;

import com.gerald.umaas.domain.entities.Domain;
import com.gerald.umaas.domain.entities.Resource;
import com.gerald.umaas.domain.repositories.DomainResourceRepository;

/**
 *
 * @author Dev7
 */
@Component
public class DomainResourceManager {
    private static final Logger logger = LoggerFactory
			.getLogger(DomainResourceManager.class);
    @Autowired
    List<DomainResourceRepository<?,?>>repositories;
    
    public Object getObjectById(String id, Class<Resource> clazz){
        for(DomainResourceRepository r : repositories){
            if( ((ParameterizedType)r.getClass().getGenericSuperclass())
                    .getActualTypeArguments()[0].getClass().equals(clazz)){
                return r.findOne(id);
            }
        }
        return null;
    }
    public Object getObjectById(String id, String type){
        DefaultRepositoryMetadata repositoryData;
   
        for(DomainResourceRepository r : repositories){
          
            repositoryData = new DefaultRepositoryMetadata(r.getClass().getInterfaces()[0]);
            logger.debug(repositoryData.getDomainType().getSimpleName());
            if(repositoryData.getDomainType().getSimpleName().equals(type) ){
                return r.findOne(id);
            }
        }
        return null;
    }
    
    public Object getObjectByDomainAndExternalId(Domain d, String externalId, Class<Resource> clazz){
        for(DomainResourceRepository r : repositories){
            if( ((ParameterizedType)r.getClass().getGenericSuperclass())
                    .getActualTypeArguments()[0].getClass().equals(clazz)){
                return r.findByDomainAndExternalId(d.getId(), externalId);
            }
        }
        return null;
    }
    public Object getObjectByDomainAndExternalId(Domain d, String externalId, String type){
        DefaultRepositoryMetadata repositoryData;
   
        for(DomainResourceRepository r : repositories){
          
            repositoryData = new DefaultRepositoryMetadata(r.getClass().getInterfaces()[0]);
            logger.debug(repositoryData.getDomainType().getSimpleName());
            if(repositoryData.getDomainType().getSimpleName().equals(type) ){
            	 return r.findByDomainAndExternalId(d.getId(), externalId);
            }
        }
        return null;
    }
    
    public String getIdFromExternalId(Domain d, String externalId, String type){
    	Resource r = (Resource) getObjectByDomainAndExternalId(d, externalId, type);
    	if(r == null) return null;
    	return r.getId();
    }
}
