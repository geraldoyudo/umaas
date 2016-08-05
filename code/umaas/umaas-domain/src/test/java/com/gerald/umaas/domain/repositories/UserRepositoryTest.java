package com.gerald.umaas.domain.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import com.gerald.umaas.domain.entities.AppUser;
import com.gerald.umaas.domain.entities.Domain;
import com.gerald.umaas.domain.entities.Field;
import com.gerald.umaas.domain.entities.Group;
import com.gerald.umaas.domain.entities.UserField;
import com.gerald.umaas.domain.entities.UserGroup;
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTest {
	
	@Autowired
	private DomainRepository domainRepository;
	private Domain domain;
	public static final String DOMAIN_CODE = "1234";
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
	
	
	@Before
	public void cleanUp(){
		domainRepository.deleteAll();
		domain = new Domain();
		domain.setCode(DOMAIN_CODE);
		domain.setName("TEST");
		domainRepository.save(domain);
		userFieldRepository.deleteAll();
		userRepository.deleteAll();
		fieldRepository.deleteAll();
	}
	
	@Test
	public void testUserSave(){
		AppUser user = createUser();
		assertNotNull(user.getId());
	}
	
	@Test
	public void testUserQueryMethodsSave(){
		AppUser user = createUser();
		Field f = createField();
		UserField uf = createUserField(user, f);
		assertNotNull(uf);
		assertNotNull(user.getId());
		Group g = createGroup();
		UserGroup ug = createUserGroup(user, g);
		assertNotNull(ug);
		user = userRepository.findOne(user.getId());
		assertNotNull(user);
		System.out.println(user);
	}
	@Test
	public void testUserQueryMethodsSaveWithList(){
		AppUser user = createUser();
		Field f = createField();
		UserField uf = createUserField(user, f);
		assertNotNull(uf);
		assertNotNull(user.getId());
		Group g = createGroup();
		UserGroup ug = createUserGroup(user, g);
		assertNotNull(ug);
		List<AppUser> users = userRepository.findAll();
		assertThat(users.get(0).getProperties().keySet().size()).isEqualTo(1);
		assertThat(users.get(0).getGroups().size()).isEqualTo(1);
		System.out.println(users.get(0));
	}
	@Test
	public void testUserQueryMethodsSaveWithPage(){
		AppUser user = createUser();
		Field f = createField();
		UserField uf = createUserField(user, f);
		assertNotNull(uf);
		assertNotNull(user.getId());
		Group g = createGroup();
		UserGroup ug = createUserGroup(user, g);
		assertNotNull(ug);
		PageRequest p = new PageRequest(0, 10);
		Page<AppUser> users = userRepository.findAll(p);
		assertThat(users.getContent().get(0).getProperties().keySet().size()).isEqualTo(1);
		assertThat(users.getContent().get(0).getGroups().size()).isEqualTo(1);
		System.out.println(users.getContent().get(0));
	}
	
	@Test
	public void testAppUserSaveWithProperties(){
		Field f = createField();
		Group g = createGroup();
		AppUser user = new AppUser();
		user.setEmail("sample@email.com");
		user.setPhoneNumber("+2348078229930");
		user.setPassword("2343");
		user.setUsername("test");
		user.setDomain(domain);
		HashMap<String,Object> properties = new HashMap<String,Object>();
		properties.put("nickname", "My Orange");
		user.setProperties(properties);
		ArrayList<String> groups = new ArrayList<>();
		groups.add(g.getName());
		user.setGroups(groups);
		user = userRepository.save(user);
		UserField uf = userFieldRepository.findByUserAndField(user, f);
		UserGroup ug = userGroupsRepository.findByUserAndGroup(user, g);
		assertNotNull(uf);
		assertNotNull(ug);
		System.out.println(uf.getField().getName());
		System.out.println(ug.getGroup().getName());
	}

	private UserField createUserField(AppUser user, Field f) {
		UserField uf = new UserField();
		uf.setField(f);
		uf.setUser(user);
		uf.setValue("Dbanj");
		uf = userFieldRepository.save(uf);
		return uf;
	}
	private UserGroup createUserGroup(AppUser user, Group g) {
		UserGroup ug = new UserGroup();
		ug.setGroup(g);
		ug.setUser(user);
		return userGroupsRepository.save(ug);
	}
	private Field createField() {
		Field f = new Field();
		f.setDomain(domain);
		f.setType("string");
		f.setName("nickname");
		f = fieldRepository.save(f);
		return f;
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
	
	private Group createGroup() {
		Group g = new Group();
		g.setName("mad people");
		g.setDomain(domain);;
		return groupRepository.save(g);
		
	}
	
	
	
}
