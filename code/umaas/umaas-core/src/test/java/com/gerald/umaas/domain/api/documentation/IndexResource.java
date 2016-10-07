package com.gerald.umaas.domain.api.documentation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@AutoConfigureRestDocs("target/generated-snippets")
@SpringBootTest
@AutoConfigureMockMvc
public class IndexResource {
	@Autowired
    private MockMvc mvc;

    @Test
    public void index() throws Exception {
        this.mvc.perform(get("/domain").accept("application/hal+json"))
                .andExpect(status().isOk())
                .andDo(document("index", links(
                		linkWithRel("domains").description("Resource for accessing available domains"),
                		linkWithRel("appUsers").description("Resource for accessing users"),
                		linkWithRel("fields").description("Resource for accessing fields"),
                		linkWithRel("groups").description("Resource for accessing user groups"),
                		linkWithRel("userFields").description("Resource for accessing custom field values of users"),
                		linkWithRel("userGroups").description("Resource for accessing groups belonging to users"),
                		linkWithRel("roles").description("Resource for accessing roles"),
                		linkWithRel("roleMappings").description("Resource for accessing roles assigned to users and groups"),
                		linkWithRel("domainAccessCodes").description("Resource for accessing security codes for the api"),
                		linkWithRel("domainAccessCodeMappings").description("Resource for accessing api usage permissions"),
                		linkWithRel("profile").description("Resource for accessing api profile")
                		)));
    }
}
