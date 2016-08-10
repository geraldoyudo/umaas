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
import com.gerald.umaas.domain.entities.UserField;
import com.gerald.umaas.domain.repositories.DomainAccessCodeMappingRepository;
import com.gerald.umaas.domain.repositories.DomainAccessCodeRepository;
import com.gerald.umaas.domain.repositories.DomainRepository;
import com.gerald.umaas.domain.services.GeneralResourceManager;

@Component
public class LocalPermissionManager implements PermissionManager{
	public static final String META_DOMAINS = "domains";
	public static final String DOMAIN_ITEMS = "domain";
	public static final String ALL_ITEMS = "ALL";
	@Autowired
	private DomainAccessCodeMappingRepository codeMappingRepository;
	@Autowired
	private DomainAccessCodeRepository accessCodeRepository;
	@Autowired
	private DomainRepository domainRepository;
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
		try{
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
			if(entityType.equals(UserField.class.getSimpleName())){
				String userEntityType = AppUser.class.getSimpleName();
				String userEntityId = entityId;
				if(!userEntityId.equals("domain")){
					UserField uf = (UserField) resourceManager.getObjectById(entityId, UserField.class.getSimpleName());
					if(uf == null) return true;
					userEntityId = uf.getUser().getId();
					if(hasPermission(userEntityType, userEntityId, priviledge)) return true;
				}
				
			}
			if(checkEntry(accessCode, entityType, entityId,priviledge)) return true;
			// get domain information
			DomainResource domainResource = (DomainResource) resourceManager.getObjectById(entityId,entityType );
			Domain d = domainResource.getDomain();
			if(checkForDomainEntry(d, accessCode, entityType,priviledge)) return true;
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
		}catch(PriviledgeDeniedException ex){
			//do nothing
		}
	
		return false;
	}
	
	private boolean checkEntry(DomainAccessCode accessCode, 
			String entityType, String entityId, Priviledge priviledge) throws PriviledgeDeniedException{
		DomainAccessCodeMapping mapping = 
				codeMappingRepository.findByAccessCodeAndEntityTypeAndEntityId(accessCode, 
						entityType, entityId);
		if(mapping == null){
			return false;
		}
		if(mapping.getPriviledge().equals(Priviledge.NONE)){
			throw new PriviledgeDeniedException();
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
			String entityType, Priviledge priviledge) throws PriviledgeDeniedException{
		DomainAccessCodeMapping mapping;
		
		mapping = codeMappingRepository.findByAccessCodeAndEntityTypeAndEntityId(accessCode, 
						entityType, DOMAIN_ITEMS);
		if(mapping == null){
			mapping = codeMappingRepository.findByAccessCodeAndEntityTypeAndEntityId(accessCode, 
					ALL_ITEMS, DOMAIN_ITEMS);
			if(mapping == null){
				return false;
			}
		}
		if(mapping.getPriviledge().equals(Priviledge.NONE)){
			throw new PriviledgeDeniedException();
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
	private boolean checkForDomainEntry(String domainId, DomainAccessCode accessCode, 
			String entityType,  Priviledge priviledge) throws PriviledgeDeniedException{
		Domain d = domainRepository.findOne(domainId);
		if(d == null) {
			System.out.println("No domain found");
			return false;
		}
			
		return checkForDomainEntry(d, accessCode, entityType, priviledge);
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

	@Override
	public boolean hasDomainCollectionPermission(String domainId, String entityType, Priviledge priviledge) {
		System.out.println("has permission called");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());
		DomainAccessCode accessCode = accessCodeRepository.findOne(auth.getName());
		if(accessCode == null){
			System.out.println("Access code not found");
			return false;
		}
		System.out.println("Domain access code retrieved");
		try{
		 if( checkForDomainEntry(domainId, accessCode, entityType, priviledge)) return true;
		 if(checkEntry(accessCode, entityType, "ALL", priviledge)) return true;
		} catch(PriviledgeDeniedException ex){
			//do nothing
		}
		return false;
	}

}
