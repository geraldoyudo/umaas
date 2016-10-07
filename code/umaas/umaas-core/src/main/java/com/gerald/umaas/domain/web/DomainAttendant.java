package com.gerald.umaas.domain.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gerald.umaas.domain.repositories.DomainRepository;
import com.gerald.utils.general.CodeGenerator;

@RestController
@Order(10)
public class DomainAttendant {
	@Autowired
	@Qualifier("domainCode")
	private CodeGenerator<String> domainCodeGenerator;
	@Autowired
	private DomainRepository domainRepository;
	
	@RequestMapping(path = "/domain/domainAdmin/generateCode")
	public Map<String, String> generateCode(){
		Map<String,String> ret = new HashMap<>();
	   String code = domainCodeGenerator.generateCode();
        while(domainRepository.findByCode(code) != null){
            code = domainCodeGenerator.generateCode();
        }
        ret.put("code", code);
        return ret;
	}
}
