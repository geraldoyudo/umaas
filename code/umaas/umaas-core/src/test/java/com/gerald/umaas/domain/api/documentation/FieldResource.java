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

import org.assertj.core.util.Maps;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import com.gerald.umaas.domain.entities.DomainAccessCodeMapping.Priviledge;
import com.gerald.umaas.domain.entities.Field;
import com.gerald.umaas.domain.repositories.FieldRepository;


public class FieldResource extends AbstractResource{
	@Autowired
	private FieldRepository fieldRepository;

	//@Before
	public void initializeMapping(){
		createMapping(Field.class.getSimpleName(),"ALL",Priviledge.VIEW);
	}
    @Test
    public void getAllFields() throws Exception {
    	initializeMapping();
        this.mvc.perform(get("/domain/fields").accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isOk())
                .andDo(document("get-fields-example", collectionLinks));
    }
    
    @Test
    public void getFieldsByDomain() throws Exception {
    	initializeListOfFields();
		createMapping(Field.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		this.mvc.perform(get("/domain/fields/search/findByDomain").accept(APPLICATION_HAL)
        		.headers(headers)
        		.param("domain", domain.getId()))
                .andExpect(status().isOk())
                .andDo(document("get-fields-by-domain-example",selfLink
                		));
    }
    
    @Test
    public void getFieldsByDomainAndExternalId() throws Exception {
    	initializeListOfFields();
		createMapping(Field.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		Field g = fieldRepository.findAll().get(0);
		this.mvc.perform(get("/domain/fields/search/findByDomainAndExternalId").accept(APPLICATION_HAL)
        		.headers(headers)
        		.param("domain", domain.getId())
        		.param("externalId", g.getExternalId()))
                .andExpect(status().isOk())
                .andDo(document("get-fields-by-domain-and-external-id-example",selfLink.and(
                		linkWithRel("field").description("Link to the field"),
                		linkWithRel("domain").description("Link to the field's domain"))));
    }
    @Test
    public void getfield() throws Exception {
		createMapping(Field.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		initializeListOfFields();
		Field g = fieldRepository.findAll().get(0);
		this.mvc.perform(RestDocumentationRequestBuilders.get("/domain/fields/{fieldId}", g.getId()).accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isOk())
                .andDo(document("get-field-example",pathParameters(
                		parameterWithName("fieldId").description("The field id")) ));
    }
    
    @Test
    public void addfield() throws Exception {
		createMapping(Field.class.getSimpleName(),"ALL",Priviledge.ADD);
    	
    	Map<String,Object> field = new HashMap<>(); 	
    	field.put("name", "nickname");
    	field.put("type", "string");
    	field.put("domain", String.format("/domain/domains/%s", domain.getId()));
    	String json = mapper.writeValueAsString(field);  
    	this.mvc.perform( post("/domain/fields").accept(APPLICATION_HAL)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(json).headers(headers))
    	.andExpect(status().isCreated())
    	.andDo(document("add-field-example", responseFields(
    			fieldWithPath("name").description("the field name"),
    			fieldWithPath("meta").description("additional meta-data for the field"),
    			fieldWithPath("externalId").description("A fully customizable, unique field field id that can be used for search queries"),
    			fieldWithPath("type").description("The type of field"),
    			fieldWithPath("mandatory").description("Wheter the field should be compulsory (true/false)"),
    			fieldWithPath("registrationItem").description("Whether the field should appear during registration"),
    			fieldWithPath("properties").description("Other properties relevant to the field type"),
    			fieldWithPath("_links").description("Resource links"))
    			, links(
    			linkWithRel("self").description("link to resource"),
    			linkWithRel("field").description("same as self"),
    			linkWithRel("domain").description("link to the field's domain"))));
    }
    
    @Test
    public void updateField() throws Exception {
		createMapping(Field.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.UPDATE); 
		initializeListOfFields();
		Field u = fieldRepository.findAll().get(0);
		Map<String,Object> field = new HashMap<>(); 	
    	field.put("name", "playname");
    	field.put("domain", String.format("/domain/domains/%s", domain.getId()));
		this.mvc.perform(RestDocumentationRequestBuilders.patch("/domain/fields/{fieldId}", u.getId()).accept(APPLICATION_HAL)
        		.headers(headers)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(mapper.writeValueAsString(field)))
                .andExpect(status().isOk())
                .andDo(document("patch-field-example",pathParameters(
                		parameterWithName("fieldId").description("The field id"))
                		
                		));
    }
    
    @Test
    public void updateFieldFull() throws Exception {
		createMapping(Field.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.UPDATE); 
		initializeListOfFields();
		Field u = fieldRepository.findAll().get(0);
		Map<String,Object> field = new HashMap<>(); 	
    	field.put("name", "nickname");
    	field.put("type", "string");
    	field.put("externalId", "field-01");
    	field.put("meta", Maps.newHashMap("serial", 3));
    	field.put("domain", String.format("/domain/domains/%s", domain.getId()));
		this.mvc.perform(RestDocumentationRequestBuilders.put("/domain/fields/{fieldId}", u.getId()).accept(APPLICATION_HAL)
        		.headers(headers)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(mapper.writeValueAsString(field)))
                .andExpect(status().isOk())
                .andDo(document("put-field-example",pathParameters(
                		parameterWithName("fieldId").description("The field id"))
                		
                		));
    }
    
    @Test
    public void deleteField() throws Exception {
		createMapping(Field.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.DELETE); 
		initializeListOfFields();
		Field u = fieldRepository.findAll().get(0);
		this.mvc.perform(RestDocumentationRequestBuilders.delete("/domain/fields/{fieldId}", u.getId()).accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isNoContent())
                .andDo(document("delete-field-example",pathParameters(
                		parameterWithName("fieldId").description("The field id")) ));
    }
    private Field makeField(){
    	long value = resourceCount++;
    	Field g = new Field();
    	g.setName("field " + value);
    	g.setType("string");
    	g.setDomain(domain);
    	return fieldRepository.save(g);
    }
    
    private void initializeListOfFields(){
    	for(int i=0; i<10; ++i){
    		makeField();
    	}
    }
}
