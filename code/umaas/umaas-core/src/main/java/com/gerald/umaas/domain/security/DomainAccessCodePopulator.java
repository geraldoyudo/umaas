package com.gerald.umaas.domain.security;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gerald.umaas.domain.entities.DomainAccessCode;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping.Priviledge;
import com.gerald.umaas.domain.repositories.DomainAccessCodeMappingRepository;
import com.gerald.umaas.domain.repositories.DomainAccessCodeRepository;

@Component
public class DomainAccessCodePopulator {
	@Autowired
	private DomainAccessCodeRepository codeRepository;
	@Autowired
	private DomainAccessCodeMappingRepository mappingRepository;
	
	@Value("${umaas.security.admin-code:0000}")
	private String adminCode;
	@Value("${umaas.security.admin-id:0000}")
	private String adminId;
	@PostConstruct
	public void initRepo(){
		System.out.println("post construct");
		System.out.println(adminCode);
		System.out.println(adminId);
		if(codeRepository.findOne(adminId) == null){
			DomainAccessCode code = new DomainAccessCode();
			code.setCode(adminCode);
			code.setId(adminId);
			codeRepository.save(code);
			DomainAccessCodeMapping mapping = new DomainAccessCodeMapping();
			mapping.setAccessCode(code);
			mapping.setPriviledge(Priviledge.ALL);
			mapping.setEntityId("ALL");
			mapping.setEntityType("ALL");
			mappingRepository.save(mapping);
		}
	}
}
