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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import com.gerald.umaas.domain.entities.AppUser;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping.Priviledge;
import com.gerald.umaas.domain.entities.Field;
import com.gerald.umaas.domain.entities.UserField;
import com.gerald.umaas.domain.repositories.FieldRepository;
import com.gerald.umaas.domain.repositories.UserFieldRepository;
import com.gerald.umaas.domain.repositories.UserRepository;
import com.gerald.umaas.domain.utils.MapBuilder;


public class UserFieldResource extends AbstractResource{
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private FieldRepository fieldRepository;
	
	@Autowired
	private UserFieldRepository userFieldRepository;
	
	Field field1, field2;
	AppUser user;
	@Override
	protected void beforeResourceTest() {
		field1 = createField("nickname", "string");
		field2 = createField("age", "integer");
		user = createUser((new MapBuilder<String,Object>())
				.put("nickname", "jerry")
				.put("age", 11L).build());
	}
	
	@Override
	protected void afterResourceTest() {
		System.out.println("After User Field Resource Test");
	}
	@Test
	public void test(){
		
	}
   @Test
    public void getAllUserFields() throws Exception {
		createMapping(UserField.class.getSimpleName(),"ALL",Priviledge.VIEW); 
        this.mvc.perform(get("/domain/userFields").accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isOk())
                .andDo(document("get-userFields-example", collectionLinks));
    }
   
   @Test
   public void getUserFieldsByDomain() throws Exception {
		createMapping(AppUser.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		this.mvc.perform(get("/domain/userFields/search/findByDomain").accept(APPLICATION_HAL)
       		.headers(headers)
       		.param("domain", domain.getId()))
               .andExpect(status().isOk())
               .andDo(document("get-userFields-by-domain-example",selfLink));
   }
   
    
    @Test
    public void getUserFieldsByUser() throws Exception {
		createMapping(AppUser.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		this.mvc.perform(get("/domain/userFields/search/findByUserId").accept(APPLICATION_HAL)
        		.headers(headers)
        		.param("userId", user.getId()))
                .andExpect(status().isOk())
                .andDo(document("get-userFields-by-user-example",selfLink));
    }
    
    
    @Test
    public void getUserFieldByDomainAndExternalId() throws Exception {
		createMapping(AppUser.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		UserField uf = userFieldRepository.findByDomain(domain.getId()).get(0);
		this.mvc.perform(get("/domain/userFields/search/findByDomainAndExternalId").accept(APPLICATION_HAL)
        		.headers(headers)
        		.param("externalId", uf.getExternalId())
				.param("domain", domain.getId()))
                .andExpect(status().isOk())
                .andDo(document("get-userFields-by-domain-and-external-id-example",selfLink.and(
                		linkWithRel("user").description("Link to the user"),
                		linkWithRel("field").description("Link to the field"),
                		linkWithRel("domain").description("Link to the domain"),
                		linkWithRel("userField").description("Same as self"))));
    }
    @Test
    public void getUserField() throws Exception {
		createMapping(AppUser.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		UserField uf = userFieldRepository.findAll().get(0);
		this.mvc.perform(RestDocumentationRequestBuilders.get("/domain/userFields/{userFieldId}", uf.getId()).accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isOk())
                .andDo(document("get-userField-example",pathParameters(
                		parameterWithName("userFieldId").description("The user field id")) ));
    }
    
    @Test
    public void getUserFieldByUserAndField() throws Exception {
    	createMapping(AppUser.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		UserField uf = userFieldRepository.findByDomain(domain.getId()).get(0);
		this.mvc.perform(get("/domain/userFields/search/findByUserIdAndFieldId").accept(APPLICATION_HAL)
        		.headers(headers)
        		.param("userId",uf.getUser().getId())
				.param("fieldId", uf.getField().getId()))
                .andExpect(status().isOk())
                .andDo(document("get-userFields-by-user-and-field-example",selfLink.and(
                		linkWithRel("user").description("Link to the user"),
                		linkWithRel("field").description("Link to the field"),
                		linkWithRel("domain").description("Link to the domain"),
                		linkWithRel("userField").description("Same as self"))));
    }
    
    @Test
    public void addUserField() throws Exception {
		createMapping(AppUser.class.getSimpleName(),"ALL",Priviledge.ADD);
    	Field f = createField("sex", "select");
    	Map<String,Object> userField = new HashMap<>();
    	userField.put("field", "/domain/fields/" + f.getId());
    	userField.put("user", "/domain/users/" + user.getId());
    	userField.put("value", "male");
    	userField.put("domain", String.format("/domain/domains/%s", domain.getId()));
    	String json = mapper.writeValueAsString(userField);  
    	this.mvc.perform( post("/domain/userFields").accept(APPLICATION_HAL)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(json).headers(headers))
    	.andExpect(status().isCreated())
    	.andDo(document("add-userFields-example", responseFields(
    			fieldWithPath("value").description("the value of the field"),
    			fieldWithPath("meta").description("additional meta-data for the user"),
    			fieldWithPath("externalId").description("A fully customizable, unique field user id that can be used for search queries"),
    			fieldWithPath("_links").description("Resource links"))
    			, links(
    			linkWithRel("self").description("link to resource"),
    			linkWithRel("user").description("link to user"),
    			linkWithRel("field").description("link to field"),
    			linkWithRel("userField").description("same as self"),
    			linkWithRel("domain").description("link to the domain of the user field entry"))));
    }
    
    @Test
    public void updateUserField() throws Exception {
		createMapping(AppUser.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.UPDATE); 
		UserField uf = userFieldRepository.findAll().get(0);
		Map<String,Object> userField = new HashMap<>();
    	userField.put("value", "something");
		this.mvc.perform(RestDocumentationRequestBuilders.patch("/domain/userFields/{userFieldId}", uf.getId()).accept(APPLICATION_HAL)
        		.headers(headers)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(mapper.writeValueAsString(userField)))
                .andExpect(status().isOk())
                .andDo(document("patch-userField-example",pathParameters(
                		parameterWithName("userFieldId").description("The userField id"))
                		
                		));
    }
    
    @Test
    public void updateUserFieldFull() throws Exception {
    	createMapping(AppUser.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.UPDATE); 
		UserField uf = userFieldRepository.findAll().get(0);
		Map<String,Object> userField = new HashMap<>();
		userField.put("externalId", "uf-101");
		userField.put("meta", (new MapBuilder<String,Object>()).put("key", "random").build());
    	userField.put("value", "something");
		this.mvc.perform(RestDocumentationRequestBuilders.put("/domain/userFields/{userFieldId}", uf.getId()).accept(APPLICATION_HAL)
        		.headers(headers)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(mapper.writeValueAsString(userField)))
                .andExpect(status().isOk())
                .andDo(document("put-userField-example",pathParameters(
                		parameterWithName("userFieldId").description("The userField id"))
                		
                		));
    }
        
    @Test
    public void deleteUserField() throws Exception {
		createMapping(AppUser.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.DELETE); 
		UserField uf = userFieldRepository.findAll().get(0);
		this.mvc.perform(RestDocumentationRequestBuilders.delete("/domain/userFields/{userFieldId}", uf.getId()).accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isNoContent())
                .andDo(document("delete-userField-example",pathParameters(
                		parameterWithName("userFieldId").description("The userField id")) ));
    }
   
    
}
