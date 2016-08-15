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

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import com.gerald.umaas.domain.entities.DomainAccessCode;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping.Priviledge;
import com.gerald.umaas.domain.utils.MapBuilder;


public class AccessCodeResource extends AbstractResource{

	//@Before
	public void initializeMapping(){
		createMapping(DomainAccessCode.class.getSimpleName(),"ALL",Priviledge.UPDATE);
	}
	
    @Test
    public void getAllAccessCodes() throws Exception {
    	initializeMapping();
    	initializeListOfAccessCodes();
        this.mvc.perform(get("/domain/domainAccessCodes").accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isOk())
                .andDo(document("get-access-codes-example", collectionLinks));
    }
    
    @Test
    public void getAccessCode() throws Exception {
		createMapping(DomainAccessCode.class.getSimpleName(),code.getId(),Priviledge.VIEW); 
		this.mvc.perform(RestDocumentationRequestBuilders.get("/domain/domainAccessCodes/{accessCodeId}", code.getId()).accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isOk())
                .andDo(document("get-access-code-example",pathParameters(
                		parameterWithName("accessCodeId").description("The access code id")) ));
    }
    
    @Test
    public void addAccessCode() throws Exception {
		createMapping(DomainAccessCode.class.getSimpleName(),"ALL",Priviledge.ADD);
    	Map<String,Object> domainMap;
		domainMap = (new MapBuilder<String,Object>())
    			.put("code", "33333")
    			.build();			
    	String json = mapper.writeValueAsString(domainMap);  
    	this.mvc.perform( post("/domain/domainAccessCodes").accept(APPLICATION_HAL)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(json).headers(headers))
    	.andExpect(status().isCreated())
    	.andDo(document("add-access-code-example"
    			, links(
    			linkWithRel("self").description("link to resource"),
    			linkWithRel("domainAccessCode").description("link to resource"))));
    }
    
    @Test
    public void updateAccessCode() throws Exception {
		initializeListOfAccessCodes();
		DomainAccessCode d = accessCodeRepository.findAll().get(0);
		createMapping(DomainAccessCode.class.getSimpleName(),d.getId(),Priviledge.UPDATE); 
		Map<String,Object> domainMap = new HashMap<>();
    	domainMap.put("code", "8323da38233");
		this.mvc.perform(RestDocumentationRequestBuilders.patch("/domain/domainAccessCodes/{accessCodeId}", d.getId()).accept(APPLICATION_HAL)
        		.headers(headers)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(mapper.writeValueAsString(domainMap)))
                .andExpect(status().isOk())
                .andDo(document("patch-access-code-example",pathParameters(
                		parameterWithName("accessCodeId").description("The access code id"))
                		
                		));
    }
    
    @Test
    public void updateDomainAccessCodeFull() throws Exception {
		initializeListOfAccessCodes();
		DomainAccessCode d = accessCodeRepository.findAll().get(0);
		createMapping(DomainAccessCode.class.getSimpleName(),d.getId(),Priviledge.UPDATE); 
		Map<String,Object> domain = new HashMap<>();
    	domain.put("code", "32233344");
    	domain.put("meta", (new MapBuilder<String,Object>())
    			.put("label", "executive").build());
    	
		this.mvc.perform(RestDocumentationRequestBuilders.put("/domain/domainAccessCodes/{accessCodeId}", d.getId()).accept(APPLICATION_HAL)
        		.headers(headers)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(mapper.writeValueAsString(domain)))
                .andExpect(status().isOk())
                .andDo(document("put-access-code-example",pathParameters(
                		parameterWithName("accessCodeId").description("The access code id"))
                		
                		));
    }
    
    @Test
    public void deleteAccessCodes() throws Exception {
		initializeListOfAccessCodes();
		DomainAccessCode d = accessCodeRepository.findAll().get(0);
		createMapping(DomainAccessCode.class.getSimpleName(),d.getId(),Priviledge.DELETE); 
		this.mvc.perform(RestDocumentationRequestBuilders.delete("/domain/domainAccessCodes/{accessCodeId}", d.getId()).accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isNoContent())
                .andDo(document("delete-access-code-example",pathParameters(
                		parameterWithName("accessCodeId").description("The access code id")) ));
    }
    private void initializeListOfAccessCodes(){
    	for(int i=0; i<10; ++i){
    		createRandomAccessCode();
    	}
    }
}
