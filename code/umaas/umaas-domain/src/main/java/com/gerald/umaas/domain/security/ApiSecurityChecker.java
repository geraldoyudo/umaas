package com.gerald.umaas.domain.security;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping.Priviledge;
import com.gerald.umaas.domain.web.utils.PostDataPersisterFilter;

@Component
public class ApiSecurityChecker {
	
	@Autowired
	private PermissionManager permissionManager;
	@Value("${spring.data.rest.basePath}")
	private String basePath;
	@Autowired
	private ObjectMapper objectMapper;
	
	public boolean check(Authentication auth, HttpServletRequest request){
		System.out.println("checking security");
		System.out.println(auth);
		System.out.println("Evaluating access");
	
		System.out.println(basePath);
		String path = request.getServletPath().replaceFirst(basePath + "/", "");
		System.out.println(path);
		String[] segments = path.split("/");
		String entityType = ResourceEntityMapper.getEntityName( segments[0]);
		if(entityType == null){
			System.out.println("Collection cannot be mapped to an entity name");
			return false;
		}
		String entityId = "ALL";
		if(segments.length > 1){
			entityId = segments[1];
		}
		System.out.println(entityType);
		System.out.println(entityId);
		Priviledge priviledge = getPriviledge(request);
		if(priviledge.equals(Priviledge.ADD) && entityId.equals("ALL")){
			try{
			String body = (String) request.getAttribute(PostDataPersisterFilter.POST_DATA);
			
			HashMap<String,Object> content = objectMapper.readValue(body, HashMap.class);
			String domainUrl = (String) content.get("domain");
			if(domainUrl == null){
				System.out.println("No domain specified");
			}
			String[] domainPaths = domainUrl.split("/");
			String domain = domainPaths[domainPaths.length -1];
			System.out.println("Evaluating for domain " + domain);
			return permissionManager.hasDomainCollectionPermission(domain, entityType, priviledge);
			}catch( IOException ex){
				System.out.println("Cannot read data");
				System.out.println(ex.getMessage());
				return true;
			}
		}
		System.out.println(priviledge);
		return permissionManager.hasPermission(entityType, entityId, priviledge);
	}
	
	private Priviledge getPriviledge(HttpServletRequest request) {
		System.out.println("Request Method " + request.getMethod());
		switch(request.getMethod().toLowerCase()){
			case "get": return Priviledge.VIEW;
			case "post": return Priviledge.ADD;
			case "patch": return Priviledge.UPDATE;
			case "delete": return Priviledge.DELETE;
			default: return Priviledge.VIEW;
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
