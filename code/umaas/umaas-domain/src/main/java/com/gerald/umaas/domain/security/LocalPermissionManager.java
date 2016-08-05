package com.gerald.umaas.domain.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.gerald.umaas.domain.entities.DomainAccessCode;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping.Priviledge;
import com.gerald.umaas.domain.repositories.DomainAccessCodeMappingRepository;

@Component
public class LocalPermissionManager implements PermissionManager{
	@Autowired
	private DomainAccessCodeMappingRepository codeMappingRepository;
	
	@Override
	public boolean hasPermission(String entityType, String entityId, Priviledge priviledge) {
		System.out.println("has permission called");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(!(auth.getPrincipal() instanceof DomainAccessCode))
			return false;
		DomainAccessCode accessCode = (DomainAccessCode) auth.getPrincipal();
		DomainAccessCodeMapping mapping = codeMappingRepository.findByAccessCodeAndEntityTypeAndEntityId(accessCode, entityType, entityId);
		if(mapping == null) return false;
		if(mapping.getPriviledge().equals(Priviledge.ALL)){
			return true;
		}else{
			return mapping.getPriviledge().equals(priviledge);
		}
	}

}
