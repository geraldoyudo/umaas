package com.gerald.umaas.domain.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;

import com.gerald.umaas.domain.entities.Domain;
import com.gerald.umaas.domain.entities.DomainAccessCode;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping.Priviledge;
import com.gerald.umaas.domain.repositories.DomainAccessCodeMappingRepository;
import com.gerald.umaas.domain.repositories.DomainAccessCodeRepository;
import com.gerald.umaas.domain.repositories.DomainRepository;
import com.gerald.umaas.domain.repositories.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RestApiAccessTest {
	 @Autowired
	 private TestRestTemplate restTemplate;
	 @Autowired
	 private UserRepository userRepository;
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
		createMapping();
	}

    @Test
    public void listUsers() throws Exception {
    	DomainAccessCode code = accessCodeRepository.findByCode(ACCESS_CODE);
    	
    	HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);  
		String authorization = "Basic " + Base64Utils.encodeToString
				(String.format("%s:%s", code.getId(),code.getCode()).getBytes());
		
		headers.set("Authorization", authorization);
 		HttpEntity<Map<String, String>> request = new HttpEntity<>(headers);
    	 ResponseEntity<String> response =
    			 restTemplate.exchange("/domain/appUsers", HttpMethod.GET, request, String.class);
    	assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                
    }
	//@Test
	public void test() {
		fail("Not yet implemented");
	}
	
	private DomainAccessCodeMapping createMapping() {
		DomainAccessCode code = createAccessCode();
		DomainAccessCodeMapping m = new DomainAccessCodeMapping();
		m.setAccessCode(code);
		m.setEntityType("appUsers");
		m.setEntityId("collection");
		m.setPriviledge(Priviledge.VIEW);
		m = codeMappingRepository.save(m);
		return m;
	}
	
	private DomainAccessCode createAccessCode() {
		DomainAccessCode code = new DomainAccessCode();
		code.setCode(ACCESS_CODE);
		assertNull(code.getId());
		code = accessCodeRepository.save(code);
		return code;
	}

}
