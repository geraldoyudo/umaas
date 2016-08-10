package com.gerald.umaas.domain.api.documentation;

import static org.junit.Assert.assertNull;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.hypermedia.LinksSnippet;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Base64Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerald.umaas.domain.entities.Domain;
import com.gerald.umaas.domain.entities.DomainAccessCode;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping.Priviledge;
import com.gerald.umaas.domain.entities.Field;
import com.gerald.umaas.domain.repositories.DomainAccessCodeMappingRepository;
import com.gerald.umaas.domain.repositories.DomainAccessCodeRepository;
import com.gerald.umaas.domain.repositories.DomainRepository;
import com.gerald.umaas.domain.repositories.FieldRepository;

@RunWith(SpringRunner.class)
@AutoConfigureRestDocs("target/generated-snippets")
@SpringBootTest
@AutoConfigureMockMvc(secure = true)
public abstract class AbstractResource {
	public static final String APPLICATION_HAL = "application/hal+json";
	@Autowired
    protected MockMvc mvc;
	@Autowired
	protected DomainAccessCodeRepository accessCodeRepository;
	@Autowired
	protected DomainAccessCodeMappingRepository codeMappingRepository;
	protected DomainAccessCode code;
	public static final String ACCESS_CODE = "4212";
	public static final String DOMAIN_CODE = "1234";
	@Autowired
	protected MongoTemplate mongoTemplate;
	protected Domain domain;
	protected HttpHeaders headers;
	@Autowired
	protected DomainRepository domainRepository;
	@Autowired
	protected FieldRepository fieldRepository;
	protected ObjectMapper mapper = new ObjectMapper();
	protected long resourceCount = 0;
	
	protected final LinksSnippet collectionLinks = links(
			linkWithRel("self").description("Link to the resource"),
    		linkWithRel("profile").description("Link to access resource profile"),
    		linkWithRel("search").description("Link to access resource search queries"));
	protected final LinksSnippet selfLink = links(
			linkWithRel("self").description("Link to the resource")
    		);
	
	@Before
	public void setUp(){
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
	
    protected DomainAccessCodeMapping createMapping(String entityType, 
			String entityId, Priviledge priviledge) {
		DomainAccessCodeMapping m = new DomainAccessCodeMapping();
		m.setAccessCode(code);
		m.setEntityType(entityType);
		m.setEntityId(entityId);
		m.setPriviledge(priviledge);
		m = codeMappingRepository.save(m);
		return m;
	}
    protected DomainAccessCodeMapping createMapping(String entityType, 
			List<String> domains, Priviledge priviledge) {
    	if(domains == null){
    		domains = new  ArrayList<>();
    	}
		DomainAccessCodeMapping m = new DomainAccessCodeMapping();
		m.setAccessCode(code);
		m.setEntityType(entityType);
		m.setEntityId("domain");
		m.setPriviledge(priviledge);
		m.meta("domains", domains);
		m = codeMappingRepository.save(m);
		return m;
	}
	
	protected DomainAccessCode createAccessCode() {
	    code = new DomainAccessCode();
		code.setCode(ACCESS_CODE);
		assertNull(code.getId());
		code = accessCodeRepository.save(code);
		return code;
	}
	 
	protected Field createField(String name, String type) {
		   Field  f = new Field();
			f.setDomain(domain);
			f.setType(type);
			f.setName(name);
			return fieldRepository.save(f);
			
	}
}
