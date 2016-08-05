package com.gerald.umaas.domain.security;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.gerald.umaas.domain.entities.DomainAccessCodeMapping.Priviledge;

@Component
public class ApiSecurityChecker {
	
	@Autowired
	private PermissionManager permissionManager;
	@Value("${spring.data.rest.basePath}")
	private String basePath;
	
	public boolean check(Authentication auth, HttpServletRequest request){
		System.out.println("checking security");
		System.out.println(auth);
		System.out.println("Evaluating access");
	
		System.out.println(basePath);
		String path = request.getServletPath().replaceFirst(basePath + "/", "");
		System.out.println(path);
		String[] segments = path.split("/");
		String entityType = segments[0];
		String entityId = "collection";
		if(segments.length > 1){
			entityId = segments[1];
		}
		System.out.println(entityType);
		System.out.println(entityId);
		Priviledge priviledge = getPriviledge(request);
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
