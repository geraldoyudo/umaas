package com.gerald.umaas.domain_admin.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gerald.umaas.domain_admin.security.AccessCode;
import com.gerald.umaas.domain_admin.service.ServiceUIProxy;

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

	@Autowired
	private ServiceUIProxy serviceUIProxy;
	
	@RequestMapping("/admin")
	public String home( Model model,
			@RequestParam(name = "domain", required = true) String domainName
			, @AuthenticationPrincipal AccessCode code){
		if(domainName == null){
			domainName = defaultDomain;
		}
		if(coreContextPath == null)
			coreContextPath = "";
		model.addAttribute("domainName", domainName);
		Map<String,String> accessCode = new HashMap<>();
		accessCode.put("id",code.getId());
		accessCode.put("code",code.getCode());
		model.addAttribute("coreUrl", "/umaas/core"  + coreContextPath);
		model.addAttribute("accessCode", accessCode);
		Map<String,String> serviceUINames = new HashMap<>();
		Map<String,String> serviceUIDescriptions = new HashMap<>();
		for(ServiceUIProxy.Service service : serviceUIProxy.getServices()){
			serviceUINames.put(service.getId(), service.getName());
			serviceUIDescriptions.put(service.getId(), serviceUIProxy.getDescription(service.getId()));
		}
		model.addAttribute("serviceUINames", serviceUINames);
		model.addAttribute("serviceUIDescriptions", serviceUIDescriptions);
		return "index";
	}
}
