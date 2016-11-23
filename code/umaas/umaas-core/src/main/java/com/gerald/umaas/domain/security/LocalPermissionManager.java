package com.gerald.umaas.domain.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.gerald.umaas.domain.entities.Group;
import com.gerald.umaas.domain.entities.Resource;
import com.gerald.umaas.domain.entities.UserField;
import com.gerald.umaas.domain.repositories.DomainAccessCodeMappingRepository;
import com.gerald.umaas.domain.repositories.DomainAccessCodeRepository;
import com.gerald.umaas.domain.repositories.DomainRepository;
import com.gerald.umaas.domain.services.DomainResourceManager;
import com.gerald.umaas.domain.services.GeneralResourceManager;

@Component
public class LocalPermissionManager implements PermissionManager{
	private static final Logger log = LoggerFactory
			.getLogger(LocalPermissionManager.class);
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
	@Autowired
	private DomainResourceManager domainResourceManager;
	
	@Override
	public boolean hasPermission(String entityType, String entityId, Priviledge priviledge) {
		Class<?> entityClass = findEntityClassByName(entityType);
		log.info("has permission called");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.info(auth.getName());
		DomainAccessCode accessCode = accessCodeRepository.findOne(auth.getName());
		if(accessCode == null){
			log.info("Access code not found");
			return false;
		}
		log.info("Domain access code retrieved");
		try{
		if(entityClass == null){
			log.info("Evaluating non-class resource");
			if(checkEntry(accessCode, entityType, entityId, priviledge)) return true;
			if(checkEntry(accessCode, entityType, ALL_ITEMS, priviledge)) return true;
			if(checkEntry(accessCode, ALL_ITEMS, ALL_ITEMS, priviledge)) return true;
			return false;
		}else if(entityId.equals(ALL_ITEMS)){
			if(checkEntry(accessCode, entityType, ALL_ITEMS, priviledge)) return true;
			if(DomainResource.class.isAssignableFrom(entityClass)){
				if(checkEntry(accessCode, Domain.class.getSimpleName(), ALL_ITEMS, priviledge)) return true;
			}
				
			if(checkEntry(accessCode, ALL_ITEMS, ALL_ITEMS, priviledge)){
				return true;
			}
			return false;
		}else if(DomainResource.class.isAssignableFrom(entityClass)){
			log.info("Evaluating domain resource");
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
			
		}else if(Resource.class.isAssignableFrom(entityClass)){
			log.info("Evaluating resource");
			if(checkEntry(accessCode, entityType, entityId, priviledge)) return true;
			if(checkEntry(accessCode, entityType, ALL_ITEMS, priviledge)) return true;
			if(checkEntry(accessCode, ALL_ITEMS, ALL_ITEMS, priviledge)) return true;
			return false;
		}else{
			log.info("Entity Type not a resource");
		}
		}catch(PriviledgeDeniedException ex){
			//do nothing
		}
	
		return false;
	}
	
	private boolean checkEntry(DomainAccessCode accessCode, 
			String entityType, String entityId, Priviledge priviledge) throws PriviledgeDeniedException{
		List<DomainAccessCodeMapping> mapping = 
				codeMappingRepository.findByAccessCodeAndEntityTypeAndEntityId(accessCode, 
						entityType, entityId);
		if(mapping == null || mapping.isEmpty()){
			return false;
		}
		if(containsPriviledge(mapping,Priviledge.NONE)){
			throw new PriviledgeDeniedException();
		}
		if(containsPriviledge(mapping, priviledge)){
			return true;
		}
		if(containsPriviledge(mapping,Priviledge.ALL)){
			return true;
		}
		if(containsPriviledge(mapping,Priviledge.UPDATE) && priviledge.equals(Priviledge.VIEW)){
			return true;
		}
		return false;
	}
	private boolean checkForDomainEntry(Domain d, DomainAccessCode accessCode, 
			String entityType, Priviledge priviledge) throws PriviledgeDeniedException{
		List<DomainAccessCodeMapping> mapping;
		
		mapping = codeMappingRepository.findByAccessCodeAndEntityTypeAndEntityId(accessCode, 
						entityType, DOMAIN_ITEMS);
		if(mapping == null || mapping.isEmpty()){
			mapping = codeMappingRepository.findByAccessCodeAndEntityTypeAndEntityId(accessCode, 
					ALL_ITEMS, DOMAIN_ITEMS);
			if(mapping == null || mapping.isEmpty()){
				return false;
			}
		}
		if(containsPriviledge(mapping,Priviledge.NONE)){
			throw new PriviledgeDeniedException();
		}
		if(priviledge.equals(Priviledge.VIEW)){
			if(containsPriviledgeForDomain(mapping, Priviledge.UPDATE, d.getId())) return true;
		}
		if(containsPriviledgeForDomain(mapping, priviledge, d.getId())){
			return true;
		}
		log.info("No priviledge for domain set");
		if(containsPriviledge(mapping, Priviledge.ALL)){
			return true;
		}
		return false;
	}
	private boolean checkForDomainEntry(String domainId, DomainAccessCode accessCode, 
			String entityType,  Priviledge priviledge) throws PriviledgeDeniedException{
		Domain d = domainRepository.findOne(domainId);
		if(d == null) {
			log.info("No domain found");
			return false;
		}
			
		return checkForDomainEntry(d, accessCode, entityType, priviledge);
	}
	
	public Class<?> findEntityClassByName(String name) {
		log.info(name);
		log.info(ENTITY_PACKAGE);
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
		log.info("has domain collection permission called");
		if(entityType.equals(UserField.class.getSimpleName())){
			if(hasDomainCollectionPermission(domainId,AppUser.class.getSimpleName(),priviledge))
				return true;
			
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.info(auth.getName());
		DomainAccessCode accessCode = accessCodeRepository.findOne(auth.getName());
		if(accessCode == null){
			log.info("Access code not found");
			return false;
		}
		log.info("Domain access code retrieved");
		try{
		 if( checkForDomainEntry(domainId, accessCode, entityType, priviledge)) return true;
		 if(checkEntry(accessCode, entityType, ALL_ITEMS, priviledge)) return true;
		 if(checkEntry(accessCode, ALL_ITEMS, ALL_ITEMS, priviledge)) return true;
		} catch(PriviledgeDeniedException ex){
			//do nothing
		}
		return false;
	}
	private boolean containsPriviledge(List<DomainAccessCodeMapping> mappings, Priviledge priviledge ){
		if(mappings == null) return false;
		for(DomainAccessCodeMapping mapping: mappings){
			try{
				if(mapping.getPriviledge().equals(priviledge)){
					return true;
				}
			}catch(NullPointerException ex){
				continue;
			}
		}
		return false;
	}
	private boolean containsPriviledgeForDomain(List<DomainAccessCodeMapping> mappings, 
			Priviledge priviledge, String domainId){
		if(mappings == null) return false;
		List<String> domains;
		for(DomainAccessCodeMapping mapping: mappings){
			if(!mapping.getEntityId().equals(DOMAIN_ITEMS))
				continue;
			if(mapping.getPriviledge().equals(priviledge)){
				try{
				domains = (List<String> ) mapping.getMeta().get(META_DOMAINS);
				if(domains.contains(domainId)){
					return true;
				}
				}catch(NullPointerException | ClassCastException ex) {
					continue;
				}
			}
		}
		return false;
	}

	@Override
	public boolean hasPermissionWithExternalId(String domainId, String entityType, String entityExternalId, Priviledge priviledge) {
		log.info("has permission with external id");
		log.info(domainId);
		log.info(entityExternalId);
		if( domainId == null){
			return hasPermission(entityType, ALL_ITEMS,priviledge);
		}
		Domain d = domainRepository.findOne(domainId);
		log.info(d.toString());
		if( d == null) return false;
		String id = domainResourceManager.getIdFromExternalId(d, entityExternalId, entityType);
		log.info("ID = " + id);
		if(id == null ) return false;
		
		return hasPermission(entityType, id, priviledge);
	}

	@Override
	public boolean hasUserDomainPermission(String entityType, String userId, Priviledge priviledge) {
		log.info("has user domain permission");
		log.info(entityType);
		DomainResource user = (DomainResource) domainResourceManager.getObjectById(userId, AppUser.class.getSimpleName());
		if(user == null ) return false;
		log.info(user.toString());
		return hasDomainCollectionPermission(user.getDomain().getId(), entityType, priviledge);
	}

	@Override
	public boolean hasAffiliateDomainPermission(String entityType, String key, Priviledge priviledge) {
		log.info("has affiliate domain permission");
		log.info(entityType);
		DomainResource affiliate = (DomainResource) domainResourceManager.getObjectById(key, AppUser.class.getSimpleName());
		if(affiliate == null ){
			affiliate = (DomainResource) domainResourceManager.getObjectById(key, Group.class.getSimpleName());
			if(affiliate == null) return false;
		}
		log.info(affiliate.toString());
		return hasDomainCollectionPermission(affiliate.getDomain().getId(), entityType, priviledge);
	}
}
