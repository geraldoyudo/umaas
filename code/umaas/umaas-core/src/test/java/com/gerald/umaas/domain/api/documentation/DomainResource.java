package com.gerald.umaas.domain.api.documentation;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import com.gerald.umaas.domain.entities.Domain;
import com.gerald.umaas.domain.entities.Domain;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping.Priviledge;
import com.gerald.umaas.domain.utils.MapBuilder;


public class DomainResource extends AbstractResource{

	//@Before
	public void initializeMapping(){
		createMapping(Domain.class.getSimpleName(),"ALL",Priviledge.UPDATE);
	}
    @Test
    public void getAllDomains() throws Exception {
    	initializeMapping();
    	initializeListOfDomains();
        this.mvc.perform(get("/domain/domains").accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isOk())
                .andDo(document("get-domains-example", collectionLinks));
    }
    
    @Test
    public void getDomainByCode() throws Exception {
    	initializeListOfDomains();
		createMapping(Domain.class.getSimpleName(),domain.getId(),Priviledge.VIEW); 
		this.mvc.perform(get("/domain/domains/search/findByCode").accept(APPLICATION_HAL)
        		.headers(headers)
        		.param("code", domain.getCode()))
                .andExpect(status().isOk())
                .andDo(document("get-domain-by-code-example",selfLink.and(
                		linkWithRel("domain").description("same as self"))));
    }
    
    @Test
    public void getDomainByName() throws Exception {
    	initializeListOfDomains();
		createMapping(Domain.class.getSimpleName(),domain.getId(),Priviledge.VIEW); 
		this.mvc.perform(get("/domain/domains/search/findByName").accept(APPLICATION_HAL)
        		.headers(headers)
        		.param("name", domain.getName()))
                .andExpect(status().isOk())
                .andDo(document("get-domain-by-name-example",selfLink.and(
                		linkWithRel("domain").description("same as self"))));
    }
    @Test
    public void getDomain() throws Exception {
		createMapping(Domain.class.getSimpleName(),domain.getId(),Priviledge.VIEW); 
		this.mvc.perform(RestDocumentationRequestBuilders.get("/domain/domains/{domainId}", domain.getId()).accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isOk())
                .andDo(document("get-domain-example",pathParameters(
                		parameterWithName("domainId").description("The domain id")) ));
    }
    
    @Test
    public void addDomain() throws Exception {
		createMapping(Domain.class.getSimpleName(),"ALL",Priviledge.ADD);
    	Map<String,Object> domainMap;
		domainMap = (new MapBuilder<String,Object>())
    			.put("name", "test.domain.com")
    			.put("code", "1111")
    			.put("properties", 
    					(new MapBuilder<String,Object>())
    					.put("url", "http://test.domain.com")
    					.build())
    			.build();
    			
    	String json = mapper.writeValueAsString(domainMap);  
    	this.mvc.perform( post("/domain/domains").accept(APPLICATION_HAL)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(json).headers(headers))
    	.andExpect(status().isCreated())
    	.andDo(document("add-domain-example", responseFields(
    			fieldWithPath("name").description("the domain's domainname"),
    			fieldWithPath("code").description("the domain code"),
    			fieldWithPath("properties").description("Additional properties of the domain"),
    			fieldWithPath("externalId").description("fully customizable id of the domain"),
    			fieldWithPath("meta").description("meta data of domain"),
    			fieldWithPath("_links").description("Resource links"))
    			, links(
    			linkWithRel("self").description("link to resource"),
    			linkWithRel("domain").description("same as self")	)));
    }
    
    @Test
    public void updateDomain() throws Exception {
		initializeListOfDomains();
		Domain d = domainRepository.findAll().get(0);
		createMapping(Domain.class.getSimpleName(),d.getId(),Priviledge.UPDATE); 
		Map<String,Object> domainMap = new HashMap<>();
    	Map<String,Object> properties = new HashMap<>();
    	properties.put("url", "http://sample.com");
    	domainMap.put("name", "domain.com");
    	domainMap.put("properties", properties);
		this.mvc.perform(RestDocumentationRequestBuilders.patch("/domain/domains/{domainId}", d.getId()).accept(APPLICATION_HAL)
        		.headers(headers)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(mapper.writeValueAsString(domainMap)))
                .andExpect(status().isOk())
                .andDo(document("patch-domain-example",pathParameters(
                		parameterWithName("domainId").description("The domain's id"))
                		
                		));
    }
    
    @Test
    public void updateDomainFull() throws Exception {
		initializeListOfDomains();
		Domain d = domainRepository.findAll().get(0);
		createMapping(Domain.class.getSimpleName(),d.getId(),Priviledge.UPDATE); 
		Map<String,Object> domain = new HashMap<>();
    	Map<String,Object> properties = new HashMap<>();
    	properties.put("url", "http://sample.test.com");
    	domain.put("code", "my-code-00");
    	domain.put("meta", (new MapBuilder<String,Object>())
    			.put("serial", 323).build());
    	domain.put("properties", properties);
		this.mvc.perform(RestDocumentationRequestBuilders.put("/domain/domains/{domainId}", d.getId()).accept(APPLICATION_HAL)
        		.headers(headers)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(mapper.writeValueAsString(domain)))
                .andExpect(status().isOk())
                .andDo(document("put-domain-example",pathParameters(
                		parameterWithName("domainId").description("The domain's id"))
                		
                		));
    }
    
    @Test
    public void deleteDomain() throws Exception {
		initializeListOfDomains();
		Domain d = domainRepository.findAll().get(0);
		createMapping(Domain.class.getSimpleName(),d.getId(),Priviledge.DELETE); 
		this.mvc.perform(RestDocumentationRequestBuilders.delete("/domain/domains/{domainId}", d.getId()).accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isNoContent())
                .andDo(document("delete-domain-example",pathParameters(
                		parameterWithName("domainId").description("The domain's id")) ));
    }
    private void initializeListOfDomains(){
    	for(int i=0; i<10; ++i){
    		createDomain();
    	}
    }
}
