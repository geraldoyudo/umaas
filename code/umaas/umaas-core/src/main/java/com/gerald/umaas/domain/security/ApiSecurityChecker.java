package com.gerald.umaas.domain.security;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerald.umaas.domain.entities.AppUser;
import com.gerald.umaas.domain.entities.Domain;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping.Priviledge;
import com.gerald.umaas.domain.entities.Group;
import com.gerald.umaas.domain.entities.UserField;
import com.gerald.umaas.domain.repositories.DomainRepository;
import com.gerald.umaas.domain.web.utils.PostDataPersisterFilter;

@Component
public class ApiSecurityChecker {
	private static final Logger log = LoggerFactory
			.getLogger(ApiSecurityChecker.class);
	@Autowired
	private PermissionManager permissionManager;
	@Value("${spring.data.rest.basePath}")
	private String basePath;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private DomainRepository domainRepository;
	
	public boolean check(Authentication auth, HttpServletRequest request){
		try{
		log.info("checking security");
		log.info(auth.toString());
		log.info("Evaluating access");
	
		log.info(basePath);
		String path = request.getRequestURI().split(basePath + "/")[1];
		log.info(path);
		String[] segments = path.split("/");
		String entityType = ResourceEntityMapper.getEntityName( segments[0]);
		if(entityType == null){
			log.info("Collection cannot be mapped to an entity name");
			return true;
		}
		String entityId = "ALL";
		if(segments.length > 1){
			entityId = segments[1];
		}
		log.info(entityType);
		log.info(entityId);
		Priviledge priviledge = getPriviledge(request);
		if(entityType.equals("auth")){
			String domain = request.getParameter("domain");
			if(domain != null){
				return permissionManager.hasDomainCollectionPermission(domain, entityType, priviledge);
			}
		}
		if(entityType.equals("domainAdmin")){
			String domainId = request.getParameter("domain");
			if(domainId == null) return false;
			return permissionManager.hasPermission(Domain.class.getSimpleName(), domainId, priviledge);
		}
		if(entityId.equals("search")){
			if(entityType.equals(Domain.class.getSimpleName())){
				String searchCommand = segments[2];
				log.info("Search command " + searchCommand );
				String domainId = null;
				if(searchCommand.equals("findByCode")){
					String code = request.getParameter("code");
					if(code != null){
						domainId = domainRepository.findByCode(code).getId();		
					}
				}else if(searchCommand.equals("findByName")){
					String name = request.getParameter("name");
					if( name!= null){
						domainId = domainRepository.findByName(name).getId();		
					}
				}
				if(domainId == null)
					return true;
				else{
					
					if( permissionManager.hasPermission(Domain.class.getSimpleName(), domainId, priviledge) == true)
						return true;					
				}
			}
			String domainId = request.getParameter("domain");
			if(domainId == null){
				domainId = request.getParameter("domainId");
			}
			String externalId = request.getParameter("externalId");
			if (externalId != null){
				return permissionManager.hasPermissionWithExternalId(domainId, entityType, externalId, priviledge);
			}
			if(domainId != null){
				return permissionManager.hasDomainCollectionPermission(domainId, entityType, priviledge);
			}
			String userId = request.getParameter("user");
			if(userId == null){
				userId = request.getParameter("userId");
			}
			if(userId != null){
				if(entityType.equals(UserField.class.getSimpleName()))
					return permissionManager.hasPermission(AppUser.class.getSimpleName(), userId, priviledge);
				else{
					return permissionManager.hasUserDomainPermission(entityType, userId, priviledge);
				}
			}
			String groupId = request.getParameter("groupId");
			if(groupId != null){
				return permissionManager.hasPermission(Group.class.getSimpleName(), entityId, priviledge);
			}
			String key = request.getParameter("key");
			if(key != null){
				return permissionManager.hasAffiliateDomainPermission(entityType, key, priviledge);
			}
			return permissionManager.hasPermission(entityType, "ALL", priviledge);
			
		}
		if(priviledge.equals(Priviledge.ADD) && entityId.equals("ALL")){
			try{
			String body = (String) request.getAttribute(PostDataPersisterFilter.POST_DATA);
			
			HashMap<String,Object> content = objectMapper.readValue(body, HashMap.class);
			String domainUrl = (String) content.get("domain");
			if(domainUrl == null){
				log.info("No domain specified");
			}
			String[] domainPaths = domainUrl.split("/");
			String domain = domainPaths[domainPaths.length -1];
			log.info("Evaluating for domain " + domain);
			return permissionManager.hasDomainCollectionPermission(domain, entityType, priviledge);
			}catch( IOException ex){
				log.info("Cannot read data");
				log.info(ex.getMessage());
				return true;
			}
		}
		log.info(priviledge.name());
		return permissionManager.hasPermission(entityType, entityId, priviledge);
		}catch(ArrayIndexOutOfBoundsException | NullPointerException | RequestNotSupportedException ex){
			return true;
		}
	}
	
	public boolean checkNonDomain(Authentication auth, HttpServletRequest request){
		String path = request.getRequestURI().replaceFirst(request.getContextPath(), "");
		System.out.println(path);
		String[] segments = path.split("/");
		String entityType = ResourceEntityMapper.getEntityName( segments[1]);
		String entityId = segments[2];
		System.out.println(entityType);
		System.out.println(entityId);
		Priviledge priviledge = getPriviledge(request);
		if(entityType.equals("domainAdmin")){
			String domainId = segments[3];
			System.out.println(domainId);
			if(domainId == null) return false;
			return permissionManager.hasPermission(Domain.class.getSimpleName(), domainId, priviledge);
		}
		return permissionManager.hasPermission(entityType, entityId, priviledge);
	}
	public boolean checkFilePropertyAccess(Authentication auth, HttpServletRequest request){
		log.info("Checking file property access");
		try{
			String path = request.getRequestURI().split("/files/")[1];
			log.info("Path = " + path);
			String[] segments = path.split("/");
			String userId = segments[2];
			String mode = segments[1];
			Priviledge priviledge = Priviledge.VIEW;
			if(mode.equals("upload")){
				priviledge = Priviledge.UPDATE;
			}
			String entityType = AppUser.class.getSimpleName();
			return permissionManager.hasPermission(entityType, userId, priviledge);
		} catch(ArrayIndexOutOfBoundsException ex){
			return true;
		}
	}
	private Priviledge getPriviledge(HttpServletRequest request) {
		log.info("Request Method " + request.getMethod());
		switch(request.getMethod().toLowerCase()){
			case "get": return Priviledge.VIEW;
			case "post": return Priviledge.ADD;
			case "patch": return Priviledge.UPDATE;
			case "delete": return Priviledge.DELETE;
			case "put": return Priviledge.UPDATE;
			default: throw new RequestNotSupportedException();
		}
	}
	private HttpServletRequest getAsHttpRequest(ServletRequest request)
	{
		if (!(request instanceof HttpServletRequest)) {
			throw new RuntimeException("Expecting an HTTP request");
		}

		return (HttpServletRequest) request;
	}

}
