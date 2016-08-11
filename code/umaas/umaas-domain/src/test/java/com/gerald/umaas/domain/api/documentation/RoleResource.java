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

import com.gerald.umaas.domain.entities.DomainAccessCodeMapping.Priviledge;
import com.gerald.umaas.domain.entities.Role;
import com.gerald.umaas.domain.repositories.RoleRepository;


public class RoleResource extends AbstractResource{
	@Autowired
	private RoleRepository roleRepository;

	//@Before
	public void initializeMapping(){
		createMapping(Role.class.getSimpleName(),"ALL",Priviledge.VIEW);
	}
    @Test
    public void getAllRoles() throws Exception {
    	initializeMapping();
        this.mvc.perform(get("/domain/roles").accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isOk())
                .andDo(document("get-roles-example", collectionLinks));
    }
    
    @Test
    public void getRolesByDomain() throws Exception {
    	initializeListOfRoles();
		createMapping(Role.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		this.mvc.perform(get("/domain/roles/search/findByDomain").accept(APPLICATION_HAL)
        		.headers(headers)
        		.param("domain", domain.getId()))
                .andExpect(status().isOk())
                .andDo(document("get-roles-by-domain-example",selfLink
                		));
    }
    
    @Test
    public void getRolesByDomainAndExternalId() throws Exception {
    	initializeListOfRoles();
		createMapping(Role.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		Role g = roleRepository.findAll().get(0);
		this.mvc.perform(get("/domain/roles/search/findByDomainAndExternalId").accept(APPLICATION_HAL)
        		.headers(headers)
        		.param("domain", domain.getId())
        		.param("externalId", g.getExternalId()))
                .andExpect(status().isOk())
                .andDo(document("get-roles-by-domain-and-external-id-example",selfLink.and(
                		linkWithRel("role").description("Link to the role"),
                		linkWithRel("domain").description("Link to the role's domain"))));
    }
    @Test
    public void getrole() throws Exception {
		createMapping(Role.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		initializeListOfRoles();
		Role g = roleRepository.findAll().get(0);
		this.mvc.perform(RestDocumentationRequestBuilders.get("/domain/roles/{roleId}", g.getId()).accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isOk())
                .andDo(document("get-role-example",pathParameters(
                		parameterWithName("roleId").description("The role id")) ));
    }
    
    @Test
    public void addrole() throws Exception {
		createMapping(Role.class.getSimpleName(),"ALL",Priviledge.ADD);
    	
    	Map<String,Object> role = new HashMap<>(); 	
    	role.put("name", "admin");
    	role.put("domain", String.format("/domain/domains/%s", domain.getId()));
    	String json = mapper.writeValueAsString(role);  
    	this.mvc.perform( post("/domain/roles").accept(APPLICATION_HAL)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(json).headers(headers))
    	.andExpect(status().isCreated())
    	.andDo(document("add-role-example", responseFields(
    			fieldWithPath("name").description("the role name"),
    			fieldWithPath("meta").description("additional meta-data for the role"),
    			fieldWithPath("externalId").description("A fully customizable, unique field role id that can be used for search queries"),
    			fieldWithPath("_links").description("Resource links"))
    			, links(
    			linkWithRel("self").description("link to resource"),
    			linkWithRel("role").description("same as self"),
    			linkWithRel("domain").description("link to the role's domain"))));
    }
    
    @Test
    public void updateRole() throws Exception {
		createMapping(Role.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.UPDATE); 
		initializeListOfRoles();
		Role u = roleRepository.findAll().get(0);
		Map<String,Object> role = new HashMap<>(); 	
    	role.put("name", "admin");
    	role.put("domain", String.format("/domain/domains/%s", domain.getId()));
		this.mvc.perform(RestDocumentationRequestBuilders.patch("/domain/roles/{roleId}", u.getId()).accept(APPLICATION_HAL)
        		.headers(headers)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(mapper.writeValueAsString(role)))
                .andExpect(status().isOk())
                .andDo(document("patch-role-example",pathParameters(
                		parameterWithName("roleId").description("The role id"))
                		
                		));
    }
    
    @Test
    public void updateRoleFull() throws Exception {
		createMapping(Role.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.UPDATE); 
		initializeListOfRoles();
		Role u = roleRepository.findAll().get(0);
		Map<String,Object> role = new HashMap<>(); 	
    	role.put("name", "admin");
    	role.put("domain", String.format("/domain/domains/%s", domain.getId()));
		this.mvc.perform(RestDocumentationRequestBuilders.put("/domain/roles/{roleId}", u.getId()).accept(APPLICATION_HAL)
        		.headers(headers)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(mapper.writeValueAsString(role)))
                .andExpect(status().isOk())
                .andDo(document("put-role-example",pathParameters(
                		parameterWithName("roleId").description("The role id"))
                		
                		));
    }
    
    private Role makeRole(){
    	long value = resourceCount++;
    	Role g = new Role();
    	g.setName("role " + value);
    	g.setDomain(domain);
    	return roleRepository.save(g);
    }
    
    private void initializeListOfRoles(){
    	for(int i=0; i<10; ++i){
    		makeRole();
    	}
    }
}
