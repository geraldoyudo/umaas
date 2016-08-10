package com.gerald.umaas.domain.rest;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Base64Utils;
import org.springframework.web.context.WebApplicationContext;

import com.gerald.umaas.domain.entities.AppUser;
import com.gerald.umaas.domain.entities.Domain;
import com.gerald.umaas.domain.entities.DomainAccessCode;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping;
import com.gerald.umaas.domain.entities.Field;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping.Priviledge;
import com.gerald.umaas.domain.repositories.DomainAccessCodeMappingRepository;
import com.gerald.umaas.domain.repositories.DomainAccessCodeRepository;
import com.gerald.umaas.domain.repositories.DomainRepository;
import com.gerald.umaas.domain.repositories.FieldRepository;
import com.gerald.umaas.domain.repositories.UserFieldRepository;
import com.gerald.umaas.domain.repositories.UserRepository;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNull;
@RunWith(SpringRunner.class)
@SpringBootTest
public class FilePropertyTest {
	@Autowired
	private WebApplicationContext context;
	@Autowired
	private MongoTemplate mongoTemplate;
	
	private MockMvc mvc;

	@Autowired
	private DomainRepository domainRepository;
	private Domain domain;
	public static final String DOMAIN_CODE = "1234";
	@Autowired
	private FieldRepository fieldRepository;
	@Autowired
	private UserFieldRepository userFieldRepository;
	@Autowired
	private UserRepository userRepository;
	private Field f;
	private AppUser user;
	public static final String ACCESS_CODE = "4212";
	private DomainAccessCode code;
	private HttpHeaders headers;
	@Autowired
	private DomainAccessCodeMappingRepository codeMappingRepository;
	 @Autowired
	 private DomainAccessCodeRepository accessCodeRepository;
	@Before
	public void setup() {
		mvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity()) 
				.build();
		cleanUp();
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
		createField();
		createUser();
		createMapping(AppUser.class.getSimpleName(), user.getId(), Priviledge.UPDATE);
	}
	
	private void cleanUp(){
		mongoTemplate.getDb().dropDatabase();
	}
	@Test
	public void testPropertyUpload() throws Exception {
		//fail("Not yet implemented");
		performPropertyUpload();
		// perform again to test existing routine
		performPropertyUpload();
	}

	private void performPropertyUpload() throws IOException, Exception {
		ClassPathResource resource = new ClassPathResource("/img/photo.png");
		System.out.println(resource.getFilename());
		MockMultipartFile fstmp = new MockMultipartFile("file", resource.getFilename(),
				"image/png",resource.getInputStream());
		String uri = String.format("/files/user/upload/%s/%s", user.getId(),f.getId());
		  mvc.perform(MockMvcRequestBuilders.fileUpload(uri)
                  .file(fstmp).headers(headers))
                  .andExpect( status().isOk());
	}
	
	@Test
	public void testUserPropertyView() throws Exception {
		performPropertyUpload();
		String uri = String.format("/files/user/view/%s/%s", user.getId(),f.getId());
		mvc.perform(MockMvcRequestBuilders
				.get(uri).headers(headers)).andExpect(status().isOk())
				.andExpect(header().string("content-disposition", isEmptyOrNullString()));
		
	}

	@Test
	public void testUserPropertyDownload() throws Exception {
		performPropertyUpload();
		String uri = String.format("/files/user/download/%s/%s", user.getId(),f.getId());
		mvc.perform(MockMvcRequestBuilders
				.get(uri).headers(headers)).andExpect(status().isOk())
				.andExpect(header().string("content-disposition", containsString("attachment")));
		
	}
	@Test
	public void testUserPropertyDownloadRestriction() throws Exception {
		performPropertyUpload();
		codeMappingRepository.deleteAll();
		String uri = String.format("/files/user/download/%s/%s", user.getId(),f.getId());
		mvc.perform(MockMvcRequestBuilders
				.get(uri).headers(headers)).andExpect(status().isForbidden());
				
		
	}
	
	private void createField() {
	    f = new Field();
		f.setDomain(domain);
		f.setType("file");
		f.setName("photo");
		f = fieldRepository.save(f);
		
	}

	private void createUser() {
		user = new AppUser();
		user.setEmail("sample@email.com");
		user.setPhoneNumber("+2348078229930");
		user.setPassword("2343");
		user.setUsername("test");
		user.setDomain(domain);
		user = userRepository.save(user);
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
}
