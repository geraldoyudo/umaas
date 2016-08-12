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
import com.gerald.umaas.domain.entities.Group;
import com.gerald.umaas.domain.repositories.GroupRepository;


public class GroupResource extends AbstractResource{
	@Autowired
	private GroupRepository groupRepository;

	//@Before
	public void initializeMapping(){
		createMapping(Group.class.getSimpleName(),"ALL",Priviledge.VIEW);
	}
    @Test
    public void getAllGroups() throws Exception {
    	initializeMapping();
        this.mvc.perform(get("/domain/groups").accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isOk())
                .andDo(document("get-groups-example", collectionLinks));
    }
    
    @Test
    public void getGroupsByDomain() throws Exception {
    	initializeListOfGroups();
		createMapping(Group.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		this.mvc.perform(get("/domain/groups/search/findByDomain").accept(APPLICATION_HAL)
        		.headers(headers)
        		.param("domain", domain.getId()))
                .andExpect(status().isOk())
                .andDo(document("get-groups-by-domain-example",selfLink
                		));
    }
    
    @Test
    public void getGroupsByDomainAndExternalId() throws Exception {
    	initializeListOfGroups();
		createMapping(Group.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		Group g = groupRepository.findAll().get(0);
		this.mvc.perform(get("/domain/groups/search/findByDomainAndExternalId").accept(APPLICATION_HAL)
        		.headers(headers)
        		.param("domain", domain.getId())
        		.param("externalId", g.getExternalId()))
                .andExpect(status().isOk())
                .andDo(document("get-groups-by-domain-and-external-id-example",selfLink.and(
                		linkWithRel("group").description("Link to the group"),
                		linkWithRel("domain").description("Link to the group's domain"),
                		linkWithRel("parent").description("Link to the parent group"))));
    }
    @Test
    public void getgroup() throws Exception {
		createMapping(Group.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.VIEW); 
		initializeListOfGroups();
		Group g = groupRepository.findAll().get(0);
		this.mvc.perform(RestDocumentationRequestBuilders.get("/domain/groups/{groupId}", g.getId()).accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isOk())
                .andDo(document("get-group-example",pathParameters(
                		parameterWithName("groupId").description("The group id")) ));
    }
    
    @Test
    public void addgroup() throws Exception {
		createMapping(Group.class.getSimpleName(),"ALL",Priviledge.ADD);
    	
    	Map<String,Object> group = new HashMap<>(); 	
    	group.put("name", "admin");
    	group.put("domain", String.format("/domain/domains/%s", domain.getId()));
    	String json = mapper.writeValueAsString(group);  
    	this.mvc.perform( post("/domain/groups").accept(APPLICATION_HAL)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(json).headers(headers))
    	.andExpect(status().isCreated())
    	.andDo(document("add-group-example", responseFields(
    			fieldWithPath("name").description("the group name"),
    			fieldWithPath("meta").description("additional meta-data for the group"),
    			fieldWithPath("externalId").description("A fully customizable, unique field group id that can be used for search queries"),
    			fieldWithPath("roles").description("Domain defined roles that the group has."),
    			fieldWithPath("_links").description("Resource links"))
    			, links(
    			linkWithRel("self").description("link to resource"),
    			linkWithRel("group").description("same as self"),
    			linkWithRel("parent").description("link to the parent group"),
    			linkWithRel("domain").description("link to the group's domain"))));
    }
    
    @Test
    public void updateGroup() throws Exception {
		createMapping(Group.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.UPDATE); 
		initializeListOfGroups();
		Group u = groupRepository.findAll().get(0);
		Map<String,Object> group = new HashMap<>(); 	
    	group.put("name", "admin");
    	group.put("domain", String.format("/domain/domains/%s", domain.getId()));
		this.mvc.perform(RestDocumentationRequestBuilders.patch("/domain/groups/{groupId}", u.getId()).accept(APPLICATION_HAL)
        		.headers(headers)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(mapper.writeValueAsString(group)))
                .andExpect(status().isOk())
                .andDo(document("patch-group-example",pathParameters(
                		parameterWithName("groupId").description("The group id"))
                		
                		));
    }
    
    @Test
    public void updateGroupFull() throws Exception {
		createMapping(Group.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.UPDATE); 
		initializeListOfGroups();
		Group u = groupRepository.findAll().get(0);
		Map<String,Object> group = new HashMap<>(); 	
    	group.put("name", "admin");
    	group.put("domain", String.format("/domain/domains/%s", domain.getId()));
		this.mvc.perform(RestDocumentationRequestBuilders.put("/domain/groups/{groupId}", u.getId()).accept(APPLICATION_HAL)
        		.headers(headers)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(mapper.writeValueAsString(group)))
                .andExpect(status().isOk())
                .andDo(document("put-group-example",pathParameters(
                		parameterWithName("groupId").description("The group id"))
                		
                		));
    }
    
    @Test
    public void deleteGroup() throws Exception {
		createMapping(Group.class.getSimpleName(),Arrays.asList(domain.getId()),Priviledge.DELETE); 
		initializeListOfGroups();
		Group u = groupRepository.findAll().get(0);
		this.mvc.perform(RestDocumentationRequestBuilders.delete("/domain/groups/{groupId}", u.getId()).accept(APPLICATION_HAL)
        		.headers(headers))
                .andExpect(status().isNoContent())
                .andDo(document("delete-group-example",pathParameters(
                		parameterWithName("groupId").description("The group id")) ));
    }
    private Group makeGroup(){
    	long value = resourceCount++;
    	Group g = new Group();
    	g.setName("group " + value);
    	g.setDomain(domain);
    	return groupRepository.save(g);
    }
    
    private void initializeListOfGroups(){
    	for(int i=0; i<10; ++i){
    		makeGroup();
    	}
    }
}
