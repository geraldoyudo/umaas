package com.gerald.umaas.domain_admin.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
	@Value("${umaas.registration.accesscode.id:0000}")
	private String accessCodeId;
	@Value("${umaas.registration.accesscode.value:0000}")
	private String accessCodeValue;
	@Value("${umaas.registration.defaultDomain:domain-0}")
	private String defaultDomain;
	
	@RequestMapping("/admin")
	public String home( Model model, @RequestParam(name = "domain", required = false) String domainName){
		if(domainName == null){
			domainName = defaultDomain;
		}
		model.addAttribute("domainName", domainName);
		Map<String,String> accessCode = new HashMap<>();
		accessCode.put("id", accessCodeId);
		accessCode.put("code", accessCodeValue);
		model.addAttribute("accessCode", accessCode);
		return "index";
	}
}
