package com.gerald.umaas.domain.api.documentation;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import com.gerald.umaas.domain.entities.AppUser;
import com.gerald.umaas.domain.entities.Domain;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping.Priviledge;
import com.gerald.umaas.domain.entities.Field;
import com.gerald.umaas.domain.entities.Group;
import com.gerald.umaas.domain.entities.Role;
import com.gerald.umaas.domain.utils.MapBuilder;


public class AccessCodeMappingResource extends AbstractResource{

	//@Before
	public void initializeMapping(){
		createMapping(DomainAccessCodeMapping.class.getSimpleName(),"ALL",Priviledge.UPDATE);
	}
	DomainAccessCodeMapping mapping;
	@Override
	protected void beforeResourceTest() {
		// TODO Auto-generated method stub
		createMapping(Group.class.getSimpleName()
				, "ALL", Priviledge.NONE);
		mapping = codeMappingRepository.findAll().get(0);
	}
	
    @Test
    public void getAllMappings() throws Exception {
    	initializeMapping();
    	initializeListOfAccessCodes();
        this.mvc.perform(get("/domain/domainAccessCodeMappings").accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isOk())
                .andDo(document("get-access-code-mappings-example", collectionLinks));
    }
    
    @Test
    public void getAccessCodeMapping() throws Exception {
		createMapping(DomainAccessCodeMapping.class.getSimpleName(),mapping.getId(),Priviledge.VIEW); 
		this.mvc.perform(RestDocumentationRequestBuilders.get("/domain/domainAccessCodeMappings/{accessCodeMappingId}", mapping.getId()).accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isOk())
                .andDo(document("get-access-code-mapping-example",pathParameters(
                		parameterWithName("accessCodeMappingId").description("The access code mapping id")) ));
    }
    
    @Test
    public void addAccessCodeMapping() throws Exception {
		createMapping(DomainAccessCodeMapping.class.getSimpleName(),"ALL",Priviledge.ADD);
    	Map<String,Object> domainMap;
		domainMap = (new MapBuilder<String,Object>())
    			.put("accessCode", "domains/domainAccessCodes/" + code.getId())
    			.put("entityType", AppUser.class.getName())
    			.put("entityId", "ALL")
    			.put("priviledge", Priviledge.ALL)
    			.put("enforced", true)
    			.build();			
    	String json = mapper.writeValueAsString(domainMap);  
    	this.mvc.perform( post("/domain/domainAccessCodeMappings").accept(APPLICATION_HAL)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(json).headers(headers))
    	.andExpect(status().isCreated())
    	.andDo(document("add-access-code-mapping-example"
    			, links(
    			linkWithRel("self").description("link to resource"),
    			linkWithRel("accessCode").description("link to the access code"),
    			linkWithRel("domainAccessCodeMapping").description("link to resource"))));
    }
    
    @Test
    public void updateAccessCode() throws Exception {
		initializeListOfAccessCodes();
		DomainAccessCodeMapping d = codeMappingRepository.findAll().get(0);
		createMapping(DomainAccessCodeMapping .class.getSimpleName(),d.getId(),Priviledge.UPDATE); 
		Map<String,Object> domainMap = new HashMap<>();
    	domainMap.put("entityId", "NONE");
		this.mvc.perform(RestDocumentationRequestBuilders.patch("/domain/domainAccessCodeMappings/{accessCodeMappingId}", d.getId()).accept(APPLICATION_HAL)
        		.headers(headers)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(mapper.writeValueAsString(domainMap)))
                .andExpect(status().isOk())
                .andDo(document("patch-access-code-mapping-example",pathParameters(
                		parameterWithName("accessCodeMappingId").description("The access code mapping id"))
                		
                		));
    }
    
    @Test
    public void updateDomainAccessCodeMappingFull() throws Exception {
		initializeListOfAccessCodes();
		DomainAccessCodeMapping d = codeMappingRepository.findAll().get(0);
		createMapping(DomainAccessCodeMapping.class.getSimpleName(),d.getId(),Priviledge.UPDATE); 
		Map<String,Object> domain = new HashMap<>();
    	domain.put("code", "32233344");
    	domain.put("accessCode", "domains/domainAccessCodes/" + code.getId());
		domain.put("entityType", AppUser.class.getName());
		domain.put("entityId", "ALL");
		domain.put("priviledge", Priviledge.ALL);
		domain.put("enforced", true);
    	domain.put("meta", (new MapBuilder<String,Object>())
    			.put("label", "executive").build());
    	
		this.mvc.perform(RestDocumentationRequestBuilders.put("/domain/domainAccessCodeMappings/{accessCodeMappingId}", d.getId()).accept(APPLICATION_HAL)
        		.headers(headers)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(mapper.writeValueAsString(domain)))
                .andExpect(status().isOk())
                .andDo(document("put-access-code-example",pathParameters(
                		parameterWithName("accessCodeMappingId").description("The access code mapping id"))
                		
                		));
    }
    
    @Test
    public void deleteAccessCodes() throws Exception {
		initializeListOfAccessCodes();
		DomainAccessCodeMapping d = codeMappingRepository.findAll().get(0);
		createMapping(DomainAccessCodeMapping.class.getSimpleName(),d.getId(),Priviledge.DELETE); 
		this.mvc.perform(RestDocumentationRequestBuilders.delete("/domain/domainAccessCodeMappings/{accessCodeMappingId}", d.getId()).accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isNoContent())
                .andDo(document("delete-access-code-example",pathParameters(
                		parameterWithName("accessCodeMappingId").description("The access code mapping id")) ));
    }
    private void initializeListOfAccessCodes(){
    	createMapping(Field.class.getSimpleName(), "ALL", Priviledge.ADD);
    	createMapping(Domain.class.getSimpleName(), "ALL", Priviledge.ADD);
    	createMapping(Role.class.getSimpleName(), "ALL", Priviledge.ADD);

    }
}
