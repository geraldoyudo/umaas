package com.gerald.umaas.domain.api.documentation;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.request.RequestDocumentation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import com.gerald.umaas.domain.entities.AppUser;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping.Priviledge;
import com.gerald.umaas.domain.repositories.UserRepository;


public class UserResource extends AbstractResource{
	@Autowired
	private UserRepository userRepository;

	//@Before
	public void initializeMapping(){
		createMapping(AppUser.class.getSimpleName(),"ALL",Priviledge.UPDATE);
	}
    @Test
    public void getAllUsers() throws Exception {
    	initializeMapping();
        this.mvc.perform(get("/domain/appUsers").accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isOk())
                .andDo(document("get-users-example", collectionLinks));
    }
    
    @Test
    public void getUsersByDomain() throws Exception {
    	initializeListOfUsers();
		createMapping(AppUser.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		this.mvc.perform(get("/domain/appUsers/search/findByDomain").accept(APPLICATION_HAL)
        		.headers(headers)
        		.param("domain", domain.getId()))
                .andExpect(status().isOk())
                .andDo(document("get-users-by-domain-example",selfLink));
    }
    
    @Test
    public void getUsersByDomainAndExternalId() throws Exception {
    	initializeListOfUsers();
		createMapping(AppUser.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		AppUser u = userRepository.findAll().get(0);
		this.mvc.perform(get("/domain/appUsers/search/findByDomainAndExternalId").accept(APPLICATION_HAL)
        		.headers(headers)
        		.param("domain", domain.getId())
        		.param("externalId", u.getExternalId()))
                .andExpect(status().isOk())
                .andDo(document("get-user-by-domain-and-external-id-example",selfLink.and(
                		linkWithRel("appUser").description("Link to the user"),
                		linkWithRel("domain").description("Link to the user's domain"))));
    }
    @Test
    public void getUser() throws Exception {
		createMapping(AppUser.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		initializeListOfUsers();
		AppUser u = userRepository.findAll().get(0);
		this.mvc.perform(RestDocumentationRequestBuilders.get("/domain/appUsers/{userId}", u.getId()).accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isOk())
                .andDo(document("get-user-example",pathParameters(
                		parameterWithName("userId").description("The user's id")) ));
    }
    
    @Test
    public void addUser() throws Exception {
		createMapping(AppUser.class.getSimpleName(),"ALL",Priviledge.ADD);
    	createField("nickname", "string");
    	Map<String,Object> user = new HashMap<>();
    	Map<String,Object> properties = new HashMap<>();
    	properties.put("nickname", "Jerry");
    	user.put("username", "gerald_oyudo");
    	user.put("password", "1234");
    	user.put("email", "gerald_oyudo@hotmail.com");
    	user.put("phoneNumber", "+2348078229930");
    	user.put("properties", properties);
    	user.put("domain", String.format("/domain/domains/%s", domain.getId()));
    	String json = mapper.writeValueAsString(user);  
    	this.mvc.perform( post("/domain/appUsers").accept(APPLICATION_HAL)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(json).headers(headers))
    	.andExpect(status().isCreated())
    	.andDo(document("add-user-example", responseFields(
    			fieldWithPath("username").description("the user's username"),
    			fieldWithPath("password").description("the user's password"),
    			fieldWithPath("email").description("the user's email"),
    			fieldWithPath("phoneNumber").description("the user's phoneNumber"),
    			fieldWithPath("properties").description("an object holding custom properties specified by the domain"),
    			fieldWithPath("meta").description("additional meta-data for the user"),
    			fieldWithPath("externalId").description("A fully customizable, unique field user id that can be used for search queries"),
    			fieldWithPath("roles").description("Domain defined roles that the user has."),
    			fieldWithPath("groups").description("Domain defined groups that the user is assigned to."),
    			fieldWithPath("emailVerified").description("Whether user's email has been verified"),
    			fieldWithPath("phoneNumberVerified").description("Whether user's phone number has been verified"),
    			fieldWithPath("_links").description("Resource links"))
    			, links(
    			linkWithRel("self").description("link to resource"),
    			linkWithRel("appUser").description("same as self"),
    			linkWithRel("domain").description("link to the user's domain"))));
    }
    
    private AppUser makeUser(){
    	long value = resourceCount++;
    	AppUser u = new AppUser();
    	u.setEmail(String.format("sample%s@email.com", value));
    	u.setPassword(String.format("password%s", value));
    	u.setPhoneNumber(String.format("+2348032332%s", value));
    	u.setUsername(String.format("sample%s", value));
    	u.setDomain(domain);
    	return userRepository.save(u);
    }
    
    private void initializeListOfUsers(){
    	for(int i=0; i<10; ++i){
    		makeUser();
    	}
    }
}
