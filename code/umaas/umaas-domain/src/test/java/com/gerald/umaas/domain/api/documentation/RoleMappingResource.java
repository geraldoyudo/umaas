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
import com.gerald.umaas.domain.entities.Role;
import com.gerald.umaas.domain.entities.RoleMapping;
import com.gerald.umaas.domain.repositories.RoleMappingRepository;
import com.gerald.umaas.domain.utils.MapBuilder;


public class RoleMappingResource extends AbstractResource{
	@Autowired
	private RoleMappingRepository roleMappingRepository;
	
	Role role1, role2;
	Group group;
	AppUser user;
	@Override
	protected void beforeResourceTest() {
		role1 = createRole("admin");
		role2 = createRole("super-user");
		user = createUser();
		userRepository.save(user);
		group = createGroup("veterans");
		createRoleMapping(group, role1);
		createRoleMapping(user, role2);	
	}
	
	@Override
	protected void afterResourceTest() {
		System.out.println("After User Role Resource Test");
	}
	
   @Test
    public void getAllRoleMappings() throws Exception {
		createMapping(RoleMapping.class.getSimpleName(),"ALL",Priviledge.VIEW); 
        this.mvc.perform(get("/domain/roleMappings").accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isOk())
                .andDo(document("get-roleMappings-example", collectionLinks));
    }
   
   @Test
   public void getRoleMappingsByDomain() throws Exception {
		createMapping(RoleMapping.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		this.mvc.perform(get("/domain/roleMappings/search/findByDomain").accept(APPLICATION_HAL)
       		.headers(headers)
       		.param("domain", domain.getId()))
               .andExpect(status().isOk())
               .andDo(document("get-roleMappings-by-domain-example",selfLink));
   }
   
    
    @Test
    public void getRoleMappingsByKey() throws Exception {
		createMapping(RoleMapping.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		this.mvc.perform(get("/domain/roleMappings/search/findByDomainIdAndKey").accept(APPLICATION_HAL)
        		.headers(headers)
        		.param("domainId", domain.getId())
        		.param("key", user.key()))
                .andExpect(status().isOk())
                .andDo(document("get-roleMappings-by-user-example",selfLink));
    }
    
    
    @Test
    public void getRoleMappingByDomainAndExternalId() throws Exception {
		createMapping(RoleMapping.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		RoleMapping rm = roleMappingRepository.findByDomain(domain.getId()).get(0);
		this.mvc.perform(get("/domain/roleMappings/search/findByDomainAndExternalId").accept(APPLICATION_HAL)
        		.headers(headers)
        		.param("externalId", rm.getExternalId())
				.param("domain", domain.getId()))
                .andExpect(status().isOk())
                .andDo(document("get-roleMappings-by-domain-and-external-id-example",selfLink.and(
                		linkWithRel("role").description("Link to the role"),
                		linkWithRel("domain").description("Link to the domain"),
                		linkWithRel("roleMapping").description("Same as self"))));
    }
    @Test
    public void getRoleMapping() throws Exception {
		createMapping(RoleMapping.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		RoleMapping rm = roleMappingRepository.findAll().get(0);
		this.mvc.perform(RestDocumentationRequestBuilders.get("/domain/roleMappings/{roleMappingId}", rm.getId()).accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isOk())
                .andDo(document("get-roleMapping-example",pathParameters(
                		parameterWithName("roleMappingId").description("The role mapping id")) ));
    }
    
    @Test
    public void getRoleMappingByKeyAndRole() throws Exception {
    	createMapping(RoleMapping.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		RoleMapping rm = roleMappingRepository.findByDomain(domain.getId()).get(0);
		this.mvc.perform(get("/domain/roleMappings/search/findByKeyAndRoleId").accept(APPLICATION_HAL)
        		.headers(headers)
        		.param("key",rm.getKey())
				.param("roleId", rm.getRole().getId()))
                .andExpect(status().isOk())
                .andDo(document("get-roleMappings-by-user-and-role-example",selfLink.and(
                		linkWithRel("role").description("Link to the role"),
                		linkWithRel("domain").description("Link to the domain"),
                		linkWithRel("roleMapping").description("Same as self"))));
    }
    
    @Test
    public void addRoleMapping() throws Exception {
		createMapping(RoleMapping.class.getSimpleName(),"ALL",Priviledge.ADD);
    	Role f = createRole("kids");
    	Map<String,Object> roleMapping = new HashMap<>();
    	roleMapping.put("role", "/domain/roles/" + f.getId());
    	roleMapping.put("key", user.key());
    	roleMapping.put("domain", String.format("/domain/domains/%s", domain.getId()));
    	String json = mapper.writeValueAsString(roleMapping);  
    	this.mvc.perform( post("/domain/roleMappings").accept(APPLICATION_HAL)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(json).headers(headers))
    	.andExpect(status().isCreated())
    	.andDo(document("add-roleMappings-example", responseFields(
    			fieldWithPath("key").description("User or Group Id"),
    			fieldWithPath("type").description("Mapping type"),
    			fieldWithPath("meta").description("additional meta-data for the user"),
    			fieldWithPath("externalId").description("A fully customizable, unique role user id that can be used for search queries"),
    			fieldWithPath("_links").description("Resource links"))
    			, links(
    			linkWithRel("self").description("link to resource"),
    			linkWithRel("role").description("link to the role"),
    			linkWithRel("roleMapping").description("same as self"),
    			linkWithRel("domain").description("link to the domain of the role mapping entry"))));
    }
    
    @Test
    public void updateRoleMapping() throws Exception {
		createMapping(RoleMapping.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.UPDATE); 
		RoleMapping rm = roleMappingRepository.findAll().get(0);
		Map<String,Object> roleMapping = new HashMap<>();
    	roleMapping.put("role", "/domain/roles/" + rm.getRole().getId());
		this.mvc.perform(RestDocumentationRequestBuilders.patch("/domain/roleMappings/{roleMappingId}", rm.getId()).accept(APPLICATION_HAL)
        		.headers(headers)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(mapper.writeValueAsString(roleMapping)))
                .andExpect(status().isOk())
                .andDo(document("patch-roleMapping-example",pathParameters(
                		parameterWithName("roleMappingId").description("The roleMapping id"))
                		
                		));
    }
    
    @Test
    public void updateRoleMappingFull() throws Exception {
    	createMapping(RoleMapping.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.UPDATE); 
		RoleMapping rm = roleMappingRepository.findAll().get(0);
		Map<String,Object> roleMapping = new HashMap<>();
		roleMapping.put("externalId", "rm-101");
		roleMapping.put("meta", (new MapBuilder<String,Object>()).put("key", "random").build());
		roleMapping.put("role", "domain/roles/" +rm.getRole().getId());
		this.mvc.perform(RestDocumentationRequestBuilders.put("/domain/roleMappings/{roleMappingId}", rm.getId()).accept(APPLICATION_HAL)
        		.headers(headers)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(mapper.writeValueAsString(roleMapping)))
                .andExpect(status().isOk())
                .andDo(document("put-roleMapping-example",pathParameters(
                		parameterWithName("roleMappingId").description("The roleMapping id"))
                		
                		));
    }
        
    @Test
    public void deleteRoleMapping() throws Exception {
		createMapping(RoleMapping.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.DELETE); 
		RoleMapping rm = roleMappingRepository.findAll().get(0);
		this.mvc.perform(RestDocumentationRequestBuilders.delete("/domain/roleMappings/{roleMappingId}", rm.getId()).accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isNoContent())
                .andDo(document("delete-roleMapping-example",pathParameters(
                		parameterWithName("roleMappingId").description("The roleMapping id")) ));
    }
   
    
}
