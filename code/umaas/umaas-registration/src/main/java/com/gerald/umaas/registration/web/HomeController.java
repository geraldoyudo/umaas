package com.gerald.umaas.registration.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
	@Value("${umaas.accesscode.id:0000}")
	private String accessCodeId;
	@Value("${umaas.accesscode.value:0000}")
	private String accessCodeValue;
	@Value("${umaas.defaultDomain:domain-0}")
	private String defaultDomain;
	@Value("${umaas.core.contextPath:}")
	private String coreContextPath;
	
	@RequestMapping("/register")
	public String register( Model model, @RequestParam(name = "domain", required = false) String domainName){
		if(domainName == null){
			domainName = defaultDomain;
		}
		model.addAttribute("domainName", domainName);
		Map<String,String> accessCode = new HashMap<>();
		accessCode.put("id", accessCodeId);
		accessCode.put("code", accessCodeValue);
		model.addAttribute("accessCode", accessCode);
		model.addAttribute("coreUrl", "/umaas/core"  + coreContextPath);
		return "index";
	}
}
