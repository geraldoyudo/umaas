package com.gerald.umaas.domain.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.gerald.umaas.domain.entities.Domain;
import com.gerald.umaas.domain.entities.DomainAccessCode;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping.Priviledge;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DomainAccessCodeMappingTest {
	@Autowired
	private DomainAccessCodeRepository accessCodeRepository;
	@Autowired
	private DomainAccessCodeMappingRepository codeMappingRepository;
	@Autowired
	private DomainRepository domainRepository;
	private Domain domain;
	public static final String DOMAIN_CODE = "1234";
	public static final String ACCESS_CODE = "4212";
	@Before
	public void cleanUp(){
		domainRepository.deleteAll();
		domain = new Domain();
		domain.setCode(DOMAIN_CODE);
		domain.setName("TEST");
		domainRepository.save(domain);
		accessCodeRepository.deleteAll();
		codeMappingRepository.deleteAll();
	}
	
	@Test
	public void testAccessCodeCreation(){
		DomainAccessCode code = creatAccessCode();
		assertNotNull(code.getId());
		assertThat(accessCodeRepository.count()).isEqualTo(1);
		code = accessCodeRepository.findOne(code.getId());
		assertNotNull(code);
		assertNotNull(code.getExpiryDate());
		System.out.println(code.getExpiryDate());
	}
	@Test
	public void testAccessCodeMappingCreation(){
		DomainAccessCodeMapping m = createMapping();
		assertNotNull(m.getId());
		
	}
	@Test
	public void testAccessCodeMappingFindQueries(){
		DomainAccessCodeMapping m = createMapping();
		
		assertNotNull(m.getId());
		assertThat(codeMappingRepository
				.findByAccessCodeAndEntityType(
						m.getAccessCode(),
						Domain.class.getSimpleName()).size()).isEqualTo(1);
		DomainAccessCode code = accessCodeRepository.findByCode(ACCESS_CODE);
		code = accessCodeRepository.findOne(code.getId());
		assertThat(code.getAccessRoles().size()).isEqualTo(1);
		System.out.println(code);
	}
	
	private DomainAccessCodeMapping createMapping() {
		DomainAccessCode code = creatAccessCode();
		DomainAccessCodeMapping m = new DomainAccessCodeMapping();
		m.setAccessCode(code);
		m.setEntityType(domain.getClass().getSimpleName());
		m.setEntityId(domain.getId());
		m.setPriviledge(Priviledge.ALL);
		m = codeMappingRepository.save(m);
		return m;
	}
	
	private DomainAccessCode creatAccessCode() {
		DomainAccessCode code = new DomainAccessCode();
		code.setCode(ACCESS_CODE);
		assertNull(code.getId());
		code.setExpiryDate(LocalDateTime.now().plusMinutes(2));
		code = accessCodeRepository.save(code);
		return code;
	}
	
	
}
