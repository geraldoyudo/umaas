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

import com.gerald.umaas.domain.entities.AppUser;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping.Priviledge;


public class UserResource extends AbstractResource{

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
    public void getUserByUsernameAndDomain() throws Exception {
    	initializeListOfUsers();
		createMapping(AppUser.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		AppUser u = userRepository.findAll().get(0);
		this.mvc.perform(get("/domain/appUsers/search/findByUsernameAndDomain").accept(APPLICATION_HAL)
        		.headers(headers)
        		.param("domain", domain.getId())
        		.param("username", u.getUsername()))
                .andExpect(status().isOk())
                .andDo(document("get-user-by-username-and-domain-example",selfLink.and(
                		linkWithRel("appUser").description("Link to the user"),
                		linkWithRel("domain").description("Link to the user's domain"))));
    }
    
    @Test
    public void getUserByEmailAndDomain() throws Exception {
    	initializeListOfUsers();
		createMapping(AppUser.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		AppUser u = userRepository.findAll().get(0);
		this.mvc.perform(get("/domain/appUsers/search/findByEmailAndDomain").accept(APPLICATION_HAL)
        		.headers(headers)
        		.param("domain", domain.getId())
        		.param("email", u.getEmail()))
                .andExpect(status().isOk())
                .andDo(document("get-user-by-email-and-domain-example",selfLink.and(
                		linkWithRel("appUser").description("Link to the user"),
                		linkWithRel("domain").description("Link to the user's domain"))));
    }
    
    @Test
    public void getUserByPhoneNumberAndDomain() throws Exception {
    	initializeListOfUsers();
		createMapping(AppUser.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		AppUser u = userRepository.findAll().get(0);
		this.mvc.perform(get("/domain/appUsers/search/findByPhoneNumberAndDomain").accept(APPLICATION_HAL)
        		.headers(headers)
        		.param("domain", domain.getId())
        		.param("phoneNumber", u.getPhoneNumber()))
                .andExpect(status().isOk())
                .andDo(document("get-user-by-phoneNumber-and-domain-example",selfLink.and(
                		linkWithRel("appUser").description("Link to the user"),
                		linkWithRel("domain").description("Link to the user's domain"))));
    }
    
    @Test
    public void getUserByPhoneNumberAndDomain2() throws Exception {
    	initializeListOfUsers();
		createMapping("ALL","ALL",Priviledge.VIEW); 
		AppUser u = userRepository.findAll().get(0);
		this.mvc.perform(get("/domain/appUsers/search/findByPhoneNumberAndDomain").accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isNotFound());
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
    			fieldWithPath("disabled").description("If the user's account has been disabled"),
    			fieldWithPath("locked").description("If the user's account has been locked"),
    			fieldWithPath("credentialsExpired").description("If the user's login credentials have expired"),
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
    
    @Test
    public void addUserWithAdmin() throws Exception {
		createMapping("ALL","ALL",Priviledge.ALL);
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
    	.andExpect(status().isCreated());
    }
    @Test
    public void updateUser() throws Exception {
		createMapping(AppUser.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.UPDATE); 
		initializeListOfUsers();
		createField("nickname", "string");
		AppUser u = userRepository.findAll().get(0);
		Map<String,Object> user = new HashMap<>();
    	Map<String,Object> properties = new HashMap<>();
    	properties.put("nickname", "micky");
    	user.put("username", "smith_o");
    	user.put("password", "4321");
    	user.put("email", "smith_0@mail.com");
    	user.put("phoneNumber", "+2348078229931");    
    	user.put("properties", properties);
    	user.put("domain", "/domain/domains/" + domain.getId());   
		this.mvc.perform(RestDocumentationRequestBuilders.patch("/domain/appUsers/{userId}", u.getId()).accept(APPLICATION_HAL)
        		.headers(headers)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andDo(document("patch-user-example",pathParameters(
                		parameterWithName("userId").description("The user's id"))
                		
                		));
    }
    
    @Test
    public void updateUserFull() throws Exception {
		createMapping(AppUser.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.UPDATE); 
		initializeListOfUsers();
		createField("nickname", "string");
		AppUser u = userRepository.findAll().get(0);
		Map<String,Object> user = new HashMap<>();
    	Map<String,Object> properties = new HashMap<>();
    	properties.put("nickname", "micky");
    	user.put("externalId", "myCustomId0000");
    	user.put("username", "smith_o");
    	user.put("password", "4321");
    	user.put("email", "smith_0@mail.com");
    	user.put("phoneNumber", "+2348078229931");  
    	user.put("emailVerified", "true");  
    	user.put("phoneNumberVerified", "true");  
    	Map<String,Object> meta = new HashMap<>();
    	meta.put("serialNo", 000);
    	user.put("meta", meta);
    	user.put("properties", properties);
    	user.put("domain", "/domain/domains/" + domain.getId());   
		this.mvc.perform(RestDocumentationRequestBuilders.put("/domain/appUsers/{userId}", u.getId()).accept(APPLICATION_HAL)
        		.headers(headers)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andDo(document("put-user-example",pathParameters(
                		parameterWithName("userId").description("The user's id"))
                		
                		));
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
    
    @Test
    public void deleteUser() throws Exception {
		createMapping(AppUser.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.DELETE); 
		initializeListOfUsers();
		AppUser u = userRepository.findAll().get(0);
		this.mvc.perform(RestDocumentationRequestBuilders.delete("/domain/appUsers/{userId}", u.getId()).accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isNoContent())
                .andDo(document("delete-user-example",pathParameters(
                		parameterWithName("userId").description("The user's id")) ));
    }
    private void initializeListOfUsers(){
    	for(int i=0; i<10; ++i){
    		makeUser();
    	}
    }
}
