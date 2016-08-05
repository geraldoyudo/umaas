package com.gerald.umaas.domain.entities.utils;

import java.util.ArrayList;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gerald.umaas.domain.entities.DomainAccessCode;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping;
import com.gerald.umaas.domain.repositories.DomainAccessCodeMappingRepository;

@Component
@Aspect
public class AccessRolesPopulator {
	@Autowired
	private DomainAccessCodeMappingRepository mappingRepository;
	public static final String DELIMITER = "|";
	private static final String ACCESS_ROLE_FORMAT = "%s"+ DELIMITER + "%s"+ DELIMITER + "%s";
	@Around("execution(*"
			+ " com.gerald.umaas.domain.repositories.DomainAccessCodeRepository.findOne(..))")
	public Object populateUser(ProceedingJoinPoint pjp) throws Throwable{
		
		Object retVal = pjp.proceed();
			if(retVal instanceof DomainAccessCode){
			 System.out.println(" User properties populating ");
			 DomainAccessCode accessCode = (DomainAccessCode) pjp.proceed();
			
			 populateRoles(accessCode);
			 return accessCode;
		}else{
			return retVal;
		}
		 
	}

	private void populateRoles(DomainAccessCode accessCode) {
		List<String> roles = new ArrayList<>();
		List<DomainAccessCodeMapping> mappings = mappingRepository.findByAccessCode(accessCode);	
		for(DomainAccessCodeMapping mapping: mappings){
			roles.add(String.format(ACCESS_ROLE_FORMAT,mapping.getEntityType(), mapping.getEntityId(),
					mapping.getPriviledge().toString()));
		}
		accessCode.setAccessRoles(roles);
	}
}
