package com.gerald.umaas.domain.security;



import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;

import java.util.Arrays;

import com.gerald.umaas.domain.entities.AppUser;
import com.gerald.umaas.domain.entities.Domain;
import com.gerald.umaas.domain.entities.DomainAccessCode;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping.Priviledge;
import com.gerald.umaas.domain.repositories.DomainAccessCodeMappingRepository;
import com.gerald.umaas.domain.repositories.DomainAccessCodeRepository;
import com.gerald.umaas.domain.repositories.DomainRepository;
import com.gerald.umaas.domain.repositories.FieldRepository;
import com.gerald.umaas.domain.repositories.GroupRepository;
import com.gerald.umaas.domain.repositories.RoleMappingRepository;
import com.gerald.umaas.domain.repositories.RoleRepository;
import com.gerald.umaas.domain.repositories.UserFieldRepository;
import com.gerald.umaas.domain.repositories.UserGroupRepository;
import com.gerald.umaas.domain.repositories.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PermissionManagerTest {
	@Autowired
	private DomainAccessCodeRepository accessCodeRepository;
	@Autowired
	private DomainAccessCodeMappingRepository codeMappingRepository;
	@Autowired
	private DomainRepository domainRepository;
	private Domain domain;
	public static final String DOMAIN_CODE = "1234";
	private static final String ACCESS_CODE = "4212";
	@Autowired
	private FieldRepository fieldRepository;
	@Autowired
	private UserFieldRepository userFieldRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private UserGroupRepository userGroupsRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private RoleMappingRepository roleMappingRepository;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private PermissionManager permissionManager;
	private AppUser appUser;
	private DomainAccessCode code;
	private String codeId = "0000";
	
	@Before
	public void cleanUp(){
		mongoTemplate.getDb().dropDatabase();
		domain = new Domain();
		domain.setCode(DOMAIN_CODE);
		domain.setName("TEST");
		domainRepository.save(domain);
		appUser = createUser();
		code = createAccessCode();
	}
	@Test
	@WithMockUser(username = "0000", password = "4212")
	public void testExactMatch(){
		createMapping(AppUser.class.getSimpleName(), appUser.getId(), Priviledge.UPDATE);
		assertTrue(permissionManager.hasPermission(AppUser.class.getSimpleName(), appUser.getId(), Priviledge.UPDATE));
		assertTrue(permissionManager.hasPermission(AppUser.class.getSimpleName(), appUser.getId(), Priviledge.VIEW));
		assertFalse(permissionManager.hasPermission(AppUser.class.getSimpleName(), "ALL", Priviledge.VIEW));

	}
	@Test
	@WithMockUser(username = "0000", password = "4212")
	public void testDomainCollectionMatch(){
		DomainAccessCodeMapping mapping = createMapping(AppUser.class.getSimpleName(),"domain", Priviledge.UPDATE);
		mapping.meta("domains", Arrays.asList(domain.getId()));
		codeMappingRepository.save(mapping);
		assertTrue(permissionManager.hasPermission(AppUser.class.getSimpleName(), appUser.getId(), Priviledge.UPDATE));
		mapping.meta("domains", Arrays.asList("randomId"));
		codeMappingRepository.save(mapping);
		assertFalse(permissionManager.hasPermission(AppUser.class.getSimpleName(), appUser.getId(), Priviledge.UPDATE));
	}
	@Test
	@WithMockUser(username = "0000", password = "4212")
	public void testMatchByCollection(){
		createMapping(AppUser.class.getSimpleName(), "ALL", Priviledge.UPDATE);
		assertTrue(permissionManager.hasPermission(AppUser.class.getSimpleName(), appUser.getId(), Priviledge.UPDATE));
		assertTrue(permissionManager.hasPermission(AppUser.class.getSimpleName(), appUser.getId(), Priviledge.VIEW));
		assertTrue(permissionManager.hasPermission(AppUser.class.getSimpleName(), "ALL", Priviledge.VIEW));

	}
	@Test
	@WithMockUser(username = "0000", password = "4212")
	public void testMatchByDomain(){
		createMapping(Domain.class.getSimpleName(), domain.getId(), Priviledge.UPDATE);
		assertTrue(permissionManager.hasPermission(AppUser.class.getSimpleName(), appUser.getId(), Priviledge.UPDATE));
		assertTrue(permissionManager.hasPermission(AppUser.class.getSimpleName(), appUser.getId(), Priviledge.VIEW));
		assertFalse(permissionManager.hasPermission(AppUser.class.getSimpleName(), "ALL", Priviledge.VIEW));

	}
	@Test
	@WithMockUser(username = "0000", password = "4212")
	public void testByAllDomain(){
		createMapping(Domain.class.getSimpleName(), "ALL", Priviledge.UPDATE);
		assertTrue(permissionManager.hasPermission(AppUser.class.getSimpleName(), appUser.getId(), Priviledge.UPDATE));
		assertTrue(permissionManager.hasPermission(AppUser.class.getSimpleName(), appUser.getId(), Priviledge.VIEW));
		assertTrue(permissionManager.hasPermission(AppUser.class.getSimpleName(), "ALL", Priviledge.VIEW));

	}
	@Test
	@WithMockUser(username = "0000", password = "4212")
	public void testByAll(){
		createMapping("ALL", "ALL", Priviledge.UPDATE);
		assertTrue(permissionManager.hasPermission(AppUser.class.getSimpleName(), appUser.getId(), Priviledge.UPDATE));
		assertTrue(permissionManager.hasPermission(AppUser.class.getSimpleName(), appUser.getId(), Priviledge.VIEW));
		assertTrue(permissionManager.hasPermission(AppUser.class.getSimpleName(), "ALL", Priviledge.VIEW));

	}
	
	@Test
	@WithMockUser(username = "0000", password = "4212")
	public void testByAllExceptUser(){
		createMapping("ALL", "ALL", Priviledge.UPDATE);
		createMapping(AppUser.class.getSimpleName(), appUser.getId(), Priviledge.NONE);
		assertFalse(permissionManager.hasPermission(AppUser.class.getSimpleName(), appUser.getId(), Priviledge.UPDATE));
		assertFalse(permissionManager.hasPermission(AppUser.class.getSimpleName(), appUser.getId(), Priviledge.VIEW));
		assertTrue(permissionManager.hasPermission(AppUser.class.getSimpleName(), "ALL", Priviledge.VIEW));

	}
	private DomainAccessCodeMapping createMapping(String entityType ,
			String entityId, Priviledge priviledge) {
		DomainAccessCodeMapping m = new DomainAccessCodeMapping();
		m.setAccessCode(code);
		m.setEntityType(entityType);
		m.setEntityId(entityId);
		m.setPriviledge(priviledge);
		m = codeMappingRepository.save(m);
		return m;
	}
	
	private DomainAccessCode createAccessCode() {
		DomainAccessCode code = new DomainAccessCode();
		code.setId(codeId);
		code.setCode(ACCESS_CODE);
		code = accessCodeRepository.save(code);
		return code;
	}
	
	private AppUser createUser() {
		AppUser user = new AppUser();
		user.setEmail("sample@email.com");
		user.setPhoneNumber("+2348078229930");
		user.setPassword("2343");
		user.setUsername("test");
		user.setDomain(domain);
		user = userRepository.save(user);
		return user;
	}
	
}
