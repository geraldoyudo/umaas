package com.gerald.umaas.domain.api.documentation;

import static org.junit.Assert.assertNull;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
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
import com.gerald.umaas.domain.entities.Affiliate;
import com.gerald.umaas.domain.entities.AppUser;
import com.gerald.umaas.domain.entities.Domain;
import com.gerald.umaas.domain.entities.DomainAccessCode;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping.Priviledge;
import com.gerald.umaas.domain.entities.Field;
import com.gerald.umaas.domain.entities.Group;
import com.gerald.umaas.domain.entities.Role;
import com.gerald.umaas.domain.entities.RoleMapping;
import com.gerald.umaas.domain.entities.UserField;
import com.gerald.umaas.domain.entities.UserGroup;
import com.gerald.umaas.domain.repositories.DomainAccessCodeMappingRepository;
import com.gerald.umaas.domain.repositories.DomainAccessCodeRepository;
import com.gerald.umaas.domain.repositories.DomainRepository;
import com.gerald.umaas.domain.repositories.FieldRepository;
import com.gerald.umaas.domain.repositories.GroupRepository;
import com.gerald.umaas.domain.repositories.RoleMappingRepository;
import com.gerald.umaas.domain.repositories.RoleRepository;
import com.gerald.umaas.domain.repositories.UserRepository;

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
	@Autowired
	protected UserRepository userRepository;
	@Autowired
	protected GroupRepository groupRepository;
	@Autowired
	protected RoleRepository roleRepository;
	@Autowired
	protected RoleMappingRepository roleMappingRepository;
	
	@Rule 
	public ResourceRule resourceRule = new ResourceRule();
	
	protected final LinksSnippet collectionLinks = links(
			linkWithRel("self").description("Link to the resource"),
    		linkWithRel("profile").description("Link to access resource profile"),
    		linkWithRel("search").description("Link to access resource search queries"));
	protected final LinksSnippet selfLink = links(
			linkWithRel("self").description("Link to the resource")
    		);
	
	public void setUp(){
		System.out.println( "Setting up");
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
	protected DomainAccessCode createRandomAccessCode() {
		long value = resourceCount ++;
	    DomainAccessCode code =  new DomainAccessCode();
		code.setCode("1234" + value);
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
	
	protected Group createGroup(String name) {
		   Group  g = new Group();
			g.setDomain(domain);
			g.setName(name);
			return groupRepository.save(g);
			
	}
	protected Role createRole(String name) {
		   Role  r = new Role();
			r.setDomain(domain);
			r.setName(name);
			return roleRepository.save(r);
			
	}
	
	protected AppUser createUser(){
		long value = resourceCount ++;
		AppUser u = new AppUser();
		u.setDomain(domain);
		u.setEmail(String.format("sample%s@email.com", value));
		u.setUsername(String.format("sample%s", value));
		u.setPassword(String.format("password%s", value));
		u.setPhoneNumber(String.format("+234808323232%s", value));
		u.setEmailVerified(true);
		u.setPhoneNumberVerified(true);
		return userRepository.save(u);
	}
	
	protected AppUser createUser(Map<String,Object> properties){
		AppUser u = createUser();
		if(properties != null){
			u.setProperties(properties);
		}
		return userRepository.save(u);
	}
	
	protected Domain createDomain(){
		long count = resourceCount ++;
		Domain d = new Domain();
		d.setCode("1234" + count);
		d.setName("domain" + count);
		return domainRepository.save(d);
	}
	
	protected RoleMapping createRoleMapping(Affiliate affiliate, Role r){
		RoleMapping rm = new RoleMapping();
		rm.setDomain(domain);
		rm.setKey(affiliate.key());
		rm.setRole(r);
		return roleMappingRepository.save(rm);
	}
	 public class ResourceRule implements TestRule{
			@Override
			public Statement apply(Statement base, Description description) {
				return new ResourceStatement(base);
			}
	    	
	    }
	    
	    public class ResourceStatement extends Statement{
	    	
	    	private Statement base;
	    	
	    	public ResourceStatement(Statement base) {
				this.base = base;
			}
			@Override
			public void evaluate() throws Throwable {
				try{
					AbstractResource.this.setUp();
					AbstractResource.this.beforeResourceTest();
					base.evaluate();
				}catch(Exception ex ){
					AbstractResource.this.afterResourceTest();
					throw ex;
				}
				AbstractResource.this.afterResourceTest();
			}
	    	
	    }
	    
	    protected void beforeResourceTest(){
	    	System.out.println( "Before Resource Test");
	    }
	    protected void afterResourceTest(){
	    	System.out.println( "After Resource Test");
	    }
}
