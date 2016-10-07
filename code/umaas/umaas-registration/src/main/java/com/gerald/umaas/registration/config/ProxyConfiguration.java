package com.gerald.umaas.registration.config;

import javax.annotation.PostConstruct;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProxyConfiguration extends RouteBuilder {
	Logger log = LoggerFactory.getLogger(ProxyConfiguration.class.getName());
	@Value("${umaas.core}")
	private String coreUrl;
	@Value("${umaas.registration.contextPath:}")
	private String contextPath;
	@PostConstruct
	public void init(){
		coreUrl = coreUrl.replace("http", "http4")
				.replace("https", "https4")
				.replace("//", "");
		log.info(coreUrl);
	}
	@Override
	public void configure() throws Exception {
		if(contextPath == null)
			contextPath = "";
		String forwardPrefix = contextPath + "/umaas/core";
		 from("servlet://core?matchOnUriPrefix=true")
		 .log("${headers}")
		.setHeader("X-Forwarded-Host", simple("${headers.host}"))
		.setHeader("X-Forwarded-Prefix", simple(forwardPrefix))
		 .to(coreUrl + "?bridgeEndpoint=true&throwExceptionOnFailure=false");
		 
		 from("servlet:///test?httpMethodRestrict=GET")
		 .log("request received")
		 .transform().constant("Hello world!");
		
	}

}
