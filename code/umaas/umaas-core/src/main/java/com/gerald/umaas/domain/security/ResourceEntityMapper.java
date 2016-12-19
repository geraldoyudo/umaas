package com.gerald.umaas.domain.security;

import java.util.HashMap;
import java.util.Map;

import com.gerald.umaas.domain.entities.AppUser;
import com.gerald.umaas.domain.entities.Domain;
import com.gerald.umaas.domain.entities.DomainAccessCode;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping;
import com.gerald.umaas.domain.entities.Field;
import com.gerald.umaas.domain.entities.Group;
import com.gerald.umaas.domain.entities.Role;
import com.gerald.umaas.domain.entities.RoleMapping;
import com.gerald.umaas.domain.entities.UserField;
import com.gerald.umaas.domain.entities.UserGroup;

public class ResourceEntityMapper {
	private static Map<String,String> uriMap = new HashMap<>();
	
	static{
		uriMap.put("appUsers", AppUser.class.getSimpleName());
		uriMap.put("fields", Field.class.getSimpleName());
		uriMap.put("userFields", UserField.class.getSimpleName());
		uriMap.put("groups", Group.class.getSimpleName());
		uriMap.put("userGroups", UserGroup.class.getSimpleName());
		uriMap.put("roles", Role.class.getSimpleName());
		uriMap.put("roleMappings", RoleMapping.class.getSimpleName());
		uriMap.put("domains", Domain.class.getSimpleName());
		uriMap.put("domainAccessCodes", DomainAccessCode.class.getSimpleName());
		uriMap.put("domainAccessCodeMappings", DomainAccessCodeMapping.class.getSimpleName());
		uriMap.put("auth", "auth");
		uriMap.put("system", "admin");
		uriMap.put("endpoint", "domainAdmin");
		uriMap.put("domainAdmin", "domainAdmin");
	}
	public static String getEntityName(String collectionName){
		return uriMap.get(collectionName);
	}
}
