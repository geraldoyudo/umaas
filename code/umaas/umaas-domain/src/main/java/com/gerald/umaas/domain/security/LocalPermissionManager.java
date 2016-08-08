package com.gerald.umaas.domain.security;

import java.util.List;

import javax.swing.text.html.parser.Entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.gerald.umaas.domain.entities.AppUser;
import com.gerald.umaas.domain.entities.Domain;
import com.gerald.umaas.domain.entities.DomainAccessCode;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping.Priviledge;
import com.gerald.umaas.domain.entities.DomainResource;
import com.gerald.umaas.domain.entities.Resource;
import com.gerald.umaas.domain.repositories.DomainAccessCodeMappingRepository;
import com.gerald.umaas.domain.repositories.DomainAccessCodeRepository;
import com.gerald.umaas.domain.services.GeneralResourceManager;

@Component
public class LocalPermissionManager implements PermissionManager{
	private static final String META_DOMAINS = "domains";
	private static final String DOMAIN_ITEMS = "domain";
	private static final String ALL_ITEMS = "ALL";
	@Autowired
	private DomainAccessCodeMappingRepository codeMappingRepository;
	@Autowired
	private DomainAccessCodeRepository accessCodeRepository;
	private static final String ENTITY_PACKAGE = AppUser.class.getPackage().getName();
	@Autowired
	private GeneralResourceManager resourceManager;
	@Override
	public boolean hasPermission(String entityType, String entityId, Priviledge priviledge) {
		Class<?> entityClass = findEntityClassByName(entityType);
		System.out.println("has permission called");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());
		DomainAccessCode accessCode = accessCodeRepository.findOne(auth.getName());
		if(accessCode == null){
			System.out.println("Access code not found");
			return false;
		}
		System.out.println("Domain access code retrieved");
		if(entityClass == null){
			System.out.println("Entity Type not a class");
		}else if(entityId.equals(ALL_ITEMS)){
			if(checkEntry(accessCode, entityType, ALL_ITEMS, priviledge)) return true;
			if(DomainResource.class.isAssignableFrom(entityClass)){
				if(checkEntry(accessCode, Domain.class.getSimpleName(), ALL_ITEMS, priviledge)) return true;
			}
				
			if(checkEntry(accessCode, ALL_ITEMS, ALL_ITEMS, priviledge)) return true;
			return false;
		}
		else if(DomainResource.class.isAssignableFrom(entityClass)){
			System.out.println("Evaluating domain resource");
			if(checkEntry(accessCode, entityType, entityId,priviledge)) return true;
			// get domain information
			DomainResource domainResource = (DomainResource) resourceManager.getObjectById(entityId,entityType );
			Domain d = domainResource.getDomain();
			if(checkForDomainEntry(d, accessCode, entityType, entityId, priviledge)) return true;
			if(checkEntry(accessCode, entityType, ALL_ITEMS,priviledge)) return true;
			if(checkEntry(accessCode, Domain.class.getSimpleName(), d.getId(), priviledge)) return true;
			if(checkEntry(accessCode, Domain.class.getSimpleName(),ALL_ITEMS, priviledge)) return true;
			if(checkEntry(accessCode, ALL_ITEMS, ALL_ITEMS, priviledge)) return true;
			return false;
			
		}else if(Resource.class.isAssignableFrom(Entity.class)){
			System.out.println("Evaluating resource");
			if(checkEntry(accessCode, entityType, entityId, priviledge)) return true;
			if(checkEntry(accessCode, entityType, ALL_ITEMS, priviledge)) return true;
			if(checkEntry(accessCode, ALL_ITEMS, ALL_ITEMS, priviledge)) return true;
			return false;
		}else{
			System.out.println("Entity Type not a resource");
		}
	
		return false;
	}
	
	private boolean checkEntry(DomainAccessCode accessCode, 
			String entityType, String entityId, Priviledge priviledge){
		DomainAccessCodeMapping mapping = 
				codeMappingRepository.findByAccessCodeAndEntityTypeAndEntityId(accessCode, 
						entityType, entityId);
		if(mapping == null){
			return false;
		}
		if(mapping.getPriviledge().equals(priviledge)){
			return true;
		}
		if(mapping.getPriviledge().equals(Priviledge.ALL)){
			return true;
		}
		if(mapping.getPriviledge().equals(Priviledge.UPDATE) && priviledge.equals(Priviledge.VIEW)){
			return true;
		}
		return false;
	}
	private boolean checkForDomainEntry(Domain d, DomainAccessCode accessCode, 
			String entityType, String entityId, Priviledge priviledge){
		DomainAccessCodeMapping mapping;
		
		mapping = codeMappingRepository.findByAccessCodeAndEntityTypeAndEntityId(accessCode, 
						entityType, DOMAIN_ITEMS);
		if(mapping == null){
			return false;
		}
		Object domainList = mapping.meta(META_DOMAINS);
		if(domainList == null)
			return false;
		if(! (domainList instanceof List)){
			return false;
		}
		List<String> domains = (List<String>) domainList;
		
		if(!domains.contains(d.getId())){
			return false;
		}
		
		if(mapping.getPriviledge().equals(priviledge)){
			return true;
		}
		if(mapping.getPriviledge().equals(Priviledge.ALL)){
			return true;
		}
		if(mapping.getPriviledge().equals(Priviledge.UPDATE) && priviledge.equals(Priviledge.VIEW)){
			return true;
		}
		return false;
	}
	
	public Class<?> findEntityClassByName(String name) {
		System.out.println(name);
		System.out.println(ENTITY_PACKAGE);
	      try{
	        return Class.forName(ENTITY_PACKAGE + "." + name);
	      } catch (ClassNotFoundException e){
	        //do nothing
	      }
	    
	    //nothing found: return null or throw ClassNotFoundException
	    return null;
	  }

}
