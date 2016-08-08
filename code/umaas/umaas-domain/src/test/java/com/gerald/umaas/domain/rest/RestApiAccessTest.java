package com.gerald.umaas.domain.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;

import com.gerald.umaas.domain.entities.AppUser;
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
	@Autowired
	private MongoTemplate mongoTemplate;
	private DomainAccessCode code;
	private HttpHeaders headers;
	@Before
	public void cleanUp(){
		mongoTemplate.getDb().dropDatabase();
		domain = new Domain();
		domain.setCode(DOMAIN_CODE);
		domain.setName("TEST");
		domainRepository.save(domain);
		createAccessCode();
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);  
		String authorization = "Basic " + Base64Utils.encodeToString
				(String.format("%s:%s", code.getId(),code.getCode()).getBytes());
		
		headers.set("Authorization", authorization);
	}

   // @Test
    public void listUsers() throws Exception {
    	AppUser user = createUser();
 		HttpEntity<Map<String, String>> request = new HttpEntity<>(headers);
    	 ResponseEntity<String> response =
    			 restTemplate.exchange("/domain/appUsers/" + user.getId(), HttpMethod.GET, request, String.class);
    	assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    	createMapping(AppUser.class.getSimpleName(), user.getId(),Priviledge.UPDATE);
    	response =
    			 restTemplate.exchange("/domain/appUsers/" + user.getId(), HttpMethod.GET, request, String.class);
    	assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    	response =
   			 restTemplate.exchange("/domain/appUsers", HttpMethod.GET, request, String.class);
    	assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    	createMapping(AppUser.class.getSimpleName(), "ALL",Priviledge.UPDATE);
    	response =
      			 restTemplate.exchange("/domain/appUsers", HttpMethod.GET, request, String.class);
       	assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    @Test
    public void testAddUsers() throws Exception {
    	Map<String,String> newUser = new HashMap<>();
    	newUser.put("email", "something@email.com");
    	newUser.put("password", "1234");
    	newUser.put("username", "my_user");
    	newUser.put("phoneNumber", "08032323233");
    	newUser.put("domain", "/domain/domains/" + domain.getId());
 		HttpEntity<Map<String, String>> request = new HttpEntity<>(newUser, headers);
    	 ResponseEntity<String> response =
    			 restTemplate.exchange("/domain/appUsers/", HttpMethod.POST, request, String.class);
    	assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    	DomainAccessCodeMapping mapping = createMapping(AppUser.class.getSimpleName(), "domain",Priviledge.ADD);
    	mapping.meta("domains", Arrays.asList(domain.getId()));
    	codeMappingRepository.save(mapping);
    	response =
   			 restTemplate.exchange("/domain/appUsers/", HttpMethod.POST, request, String.class);
    	assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    	
    	
    }
	//@Test
	public void test() {
		fail("Not yet implemented");
	}
	
	private DomainAccessCodeMapping createMapping(String entityType, 
			String entityId, Priviledge priviledge) {
		DomainAccessCodeMapping m = new DomainAccessCodeMapping();
		m.setAccessCode(code);
		m.setEntityType(entityType);
		m.setEntityId(entityId);
		m.setPriviledge(priviledge);
		m = codeMappingRepository.save(m);
		return m;
	}
	
	private DomainAccessCode createAccessCode() {
	    code = new DomainAccessCode();
		code.setCode(ACCESS_CODE);
		assertNull(code.getId());
		code = accessCodeRepository.save(code);
		return code;
	}
	
	private AppUser createUser() {
		AppUser user = new AppUser();
		user.setEmail("sample@email.com");
		user.setPhoneNumber("+2348078229930");
		user.setPassword("2343");
		user.setUsername("test");
		user.setDomain(domain);
		user = userRepository.save(user);
		return user;
	}
}
