/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gerald.umaas.domain.repositories;


import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.format.annotation.DateTimeFormat;

import com.gerald.umaas.domain.entities.AppEvent;

/**
 *
 * @author Dev7
 */
public interface AppEventRepository extends DomainResourceRepository<AppEvent, String>{
	@RestResource(exported = false)
	public List<AppEvent> findByTypeAndDomainId(String type, String domainId);
	public Page<AppEvent> findByTypeAndDomainId(@Param("type") String type, 
			@Param("domain") String domainId, Pageable p);
	
	@RestResource(exported = false)
	public List<AppEvent> findByTypeAndDomainIdAndDateAfter(String type, String domainId, Date date);
	public Page<AppEvent> findByTypeAndDomainIdAndDateAfter(@Param("type") String type, @Param("domain") String domainId,
			@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @Param("date") Date d,  Pageable p);
	
	@RestResource(exported = false)
	public List<AppEvent> findByTypeAndDomainIdAndDateBefore(String type, String domainId, Date date);
	public Page<AppEvent> findByTypeAndDomainIdAndDateBefore(@Param("type") String type, @Param("domain") String domainId,
			@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @Param("date") Date d,  Pageable p);
	
	@RestResource(exported = false) 
	public List<AppEvent> findByTypeAndDomainIsNull(String type);
	public Page<AppEvent> findByTypeAndDomainIsNull(@Param("type") String type,  Pageable p);
	
	@RestResource(exported = false)
	public List<AppEvent> findByTypeAndDomainIsNullAndDateAfter(String type, Date date);
	public Page<AppEvent> findByTypeAndDomainIsNullAndDateAfter(@Param("type") String type,
			@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @Param("date") Date d,  Pageable p);
	
	@RestResource(exported = false)
	public List<AppEvent> findByTypeAndDomainIsNullAndDateBefore(String type, Date date);
	public Page<AppEvent> findByTypeAndDomainIsNullAndDateBefore(@Param("type") String type,
			@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @Param("date") Date d,  Pageable p);
}
