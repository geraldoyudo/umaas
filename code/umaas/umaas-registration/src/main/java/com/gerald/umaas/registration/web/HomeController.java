package com.gerald.umaas.registration.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
	@Value("${umaas.registration.accesscode.id:0000}")
	private String accessCodeId;
	@Value("${umaas.registration.accesscode.value:0000}")
	private String accessCodeValue;
	@Value("${umaas.registration.defaultDomain:domain-0}")
	private String defaultDomain;
	
	@RequestMapping("/register")
	public String home(Model model){
		model.addAttribute("domainName", defaultDomain);
		Map<String,String> accessCode = new HashMap<>();
		accessCode.put("id", accessCodeId);
		accessCode.put("code", accessCodeValue);
		model.addAttribute("accessCode", accessCode);
		return "index";
	}
}
