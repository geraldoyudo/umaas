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
import com.gerald.umaas.domain.entities.Group;
import com.gerald.umaas.domain.entities.UserGroup;
import com.gerald.umaas.domain.repositories.UserGroupRepository;
import com.gerald.umaas.domain.utils.MapBuilder;


public class UserGroupResource extends AbstractResource{
	@Autowired
	private UserGroupRepository userGroupRepository;
	
	Group group1, group2;
	AppUser user;
	@Override
	protected void beforeResourceTest() {
		group1 = createGroup("admin");
		group2 = createGroup("veterans");
		user = createUser();
		user.setGroups(Arrays.asList("admin", "veterans"));
		userRepository.save(user);
	}
	
	@Override
	protected void afterResourceTest() {
		System.out.println("After User Group Resource Test");
	}
	@Test
	public void test(){
		
	}
   @Test
    public void getAllUserGroups() throws Exception {
		createMapping(UserGroup.class.getSimpleName(),"ALL",Priviledge.VIEW); 
        this.mvc.perform(get("/domain/userGroups").accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isOk())
                .andDo(document("get-userGroups-example", collectionLinks));
    }
   
   @Test
   public void getUserGroupsByDomain() throws Exception {
		createMapping(UserGroup.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		this.mvc.perform(get("/domain/userGroups/search/findByDomain").accept(APPLICATION_HAL)
       		.headers(headers)
       		.param("domain", domain.getId()))
               .andExpect(status().isOk())
               .andDo(document("get-userGroups-by-domain-example",selfLink));
   }
   
    
    @Test
    public void getUserGroupsByUser() throws Exception {
		createMapping(UserGroup.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		this.mvc.perform(get("/domain/userGroups/search/findByUserId").accept(APPLICATION_HAL)
        		.headers(headers)
        		.param("userId", user.getId()))
                .andExpect(status().isOk())
                .andDo(document("get-userGroups-by-user-example",selfLink));
    }
        
    @Test
    public void getUserGroupByDomainAndExternalId() throws Exception {
		createMapping(UserGroup.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		UserGroup ug = userGroupRepository.findByDomain(domain.getId()).get(0);
		this.mvc.perform(get("/domain/userGroups/search/findByDomainAndExternalId").accept(APPLICATION_HAL)
        		.headers(headers)
        		.param("externalId", ug.getExternalId())
				.param("domain", domain.getId()))
                .andExpect(status().isOk())
                .andDo(document("get-userGroups-by-domain-and-external-id-example",selfLink.and(
                		linkWithRel("user").description("Link to the user"),
                		linkWithRel("group").description("Link to the group"),
                		linkWithRel("domain").description("Link to the domain"),
                		linkWithRel("userGroup").description("Same as self"))));
    }
    @Test
    public void getUserGroup() throws Exception {
		createMapping(UserGroup.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		UserGroup ug = userGroupRepository.findAll().get(0);
		this.mvc.perform(RestDocumentationRequestBuilders.get("/domain/userGroups/{userGroupId}", ug.getId()).accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isOk())
                .andDo(document("get-userGroup-example",pathParameters(
                		parameterWithName("userGroupId").description("The user group id")) ));
    }
    
    @Test
    public void getUserGroupByUserAndGroup() throws Exception {
    	createMapping(UserGroup.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		UserGroup ug = userGroupRepository.findByDomain(domain.getId()).get(0);
		this.mvc.perform(get("/domain/userGroups/search/findByUserIdAndGroupId").accept(APPLICATION_HAL)
        		.headers(headers)
        		.param("userId",ug.getUser().getId())
				.param("groupId", ug.getGroup().getId()))
                .andExpect(status().isOk())
                .andDo(document("get-userGroups-by-user-and-group-example",selfLink.and(
                		linkWithRel("user").description("Link to the user"),
                		linkWithRel("group").description("Link to the group"),
                		linkWithRel("domain").description("Link to the domain"),
                		linkWithRel("userGroup").description("Same as self"))));
    }
    
    @Test
    public void addUserGroup() throws Exception {
		createMapping(UserGroup.class.getSimpleName(),"ALL",Priviledge.ADD);
    	Group f = createGroup("kids");
    	Map<String,Object> userGroup = new HashMap<>();
    	userGroup.put("group", "/domain/groups/" + f.getId());
    	userGroup.put("user", "/domain/users/" + user.getId());
    	userGroup.put("domain", String.format("/domain/domains/%s", domain.getId()));
    	String json = mapper.writeValueAsString(userGroup);  
    	this.mvc.perform( post("/domain/userGroups").accept(APPLICATION_HAL)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(json).headers(headers))
    	.andExpect(status().isCreated())
    	.andDo(document("add-userGroups-example", responseFields(
    			fieldWithPath("meta").description("additional meta-data for the user"),
    			fieldWithPath("externalId").description("A fully customizable, unique group user id that can be used for search queries"),
    			fieldWithPath("_links").description("Resource links"))
    			, links(
    			linkWithRel("self").description("link to resource"),
    			linkWithRel("user").description("link to user"),
    			linkWithRel("group").description("link to group"),
    			linkWithRel("userGroup").description("same as self"),
    			linkWithRel("domain").description("link to the domain of the user group entry"))));
    }
    
    @Test
    public void updateUserGroup() throws Exception {
		createMapping(UserGroup.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.UPDATE); 
		UserGroup ug = userGroupRepository.findAll().get(0);
		Map<String,Object> userGroup = new HashMap<>();
    	userGroup.put("group", "/domain/groups/" + ug.getGroup().getId());
		this.mvc.perform(RestDocumentationRequestBuilders.patch("/domain/userGroups/{userGroupId}", ug.getId()).accept(APPLICATION_HAL)
        		.headers(headers)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(mapper.writeValueAsString(userGroup)))
                .andExpect(status().isOk())
                .andDo(document("patch-userGroup-example",pathParameters(
                		parameterWithName("userGroupId").description("The userGroup id"))
                		
                		));
    }
    
    @Test
    public void updateUserGroupFull() throws Exception {
    	createMapping(UserGroup.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.UPDATE); 
		UserGroup ug = userGroupRepository.findAll().get(0);
		Map<String,Object> userGroup = new HashMap<>();
		userGroup.put("externalId", "ug-101");
		userGroup.put("meta", (new MapBuilder<String,Object>()).put("key", "random").build());
		this.mvc.perform(RestDocumentationRequestBuilders.put("/domain/userGroups/{userGroupId}", ug.getId()).accept(APPLICATION_HAL)
        		.headers(headers)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(mapper.writeValueAsString(userGroup)))
                .andExpect(status().isOk())
                .andDo(document("put-userGroup-example",pathParameters(
                		parameterWithName("userGroupId").description("The userGroup id"))
                		
                		));
    }
        
    @Test
    public void deleteUserGroup() throws Exception {
		createMapping(UserGroup.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.DELETE); 
		UserGroup ug = userGroupRepository.findAll().get(0);
		this.mvc.perform(RestDocumentationRequestBuilders.delete("/domain/userGroups/{userGroupId}", ug.getId()).accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isNoContent())
                .andDo(document("delete-userGroup-example",pathParameters(
                		parameterWithName("userGroupId").description("The userGroup id")) ));
    }
   
    
}
