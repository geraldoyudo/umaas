package com.gerald.umaas.domain.repositories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.gerald.umaas.domain.entities.Affiliate;
import com.gerald.umaas.domain.entities.AppUser;
import com.gerald.umaas.domain.entities.Domain;
import com.gerald.umaas.domain.entities.DomainAccessCode;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping;
import com.gerald.umaas.domain.entities.DomainAccessCodeMapping.Priviledge;
import com.gerald.umaas.domain.entities.Field;
import com.gerald.umaas.domain.entities.Group;
import com.gerald.umaas.domain.entities.Role;
import com.gerald.umaas.domain.entities.RoleMapping;
import com.gerald.umaas.domain.entities.UserField;
import com.gerald.umaas.domain.entities.UserGroup;

@Component
@Profile("qa")
public class Populator {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private DomainRepository domainRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private UserGroupRepository userGroupRepository;
	@Autowired
	private RoleMappingRepository roleMappingRepository;
	@Autowired
	private FieldRepository fieldRepository;
	@Autowired
	private DomainAccessCodeRepository domainAccessCodeRepository;
	@Autowired
	private DomainAccessCodeMappingRepository codeMappingRepository;
	
	private static String[] entityTypes = new String[]{AppUser.class.getSimpleName(),
			Field.class.getSimpleName(),
			Role.class.getSimpleName(),
			Group.class.getSimpleName(),
			UserField.class.getSimpleName(),
			UserGroup.class.getSimpleName(),
			RoleMapping.class.getSimpleName(),
			DomainAccessCode.class.getSimpleName(),
			DomainAccessCodeMapping.class.getSimpleName()};
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Value("${umaas.security.admin-code:0000}")
	private String adminCode;
	@Value("${umaas.security.admin-id:0000}")
	private String adminId;
	
	@PostConstruct
	public void init(){
		mongoTemplate.getDb().dropDatabase();
		
		//initialize domains
		for(int i=0; i<10; ++i){
			createDomain(i);
		}
		
		//initialize domain entities,  in domain
		List<Domain> domains = domainRepository.findAll();
		int count =0;
		AppUser user;
		Role groupRole, userRole;
		Group group;
		for(Domain d: domains){
			count = (int)d.meta("count");
			ArrayList<Field> fieldList;
			for(int i=0; i<20; ++i){
				fieldList = new ArrayList<>();
				for(int j=0; j<5; ++j){
					fieldList.add(createField(d,j));
				}
				user = createUser(d,i, fieldList);
				userRole = createRole(d,i);
				groupRole = createRole(d, i+20);
				group = createGroup(d,i);
				createUserGroup(user, group);
				createRoleMapping(user, userRole);
				createRoleMapping(group,groupRole);
			}
		}
		
		DomainAccessCode code = new DomainAccessCode();
		code.setCode(adminCode);
		code.setId(adminId);
		createMapping(code, "ALL", "ALL",Priviledge.ALL);
		
		for(int i=0; i < domains.size(); ++i){
			for(int j=0; j<entityTypes.length; ++j){
				createMapping(createAccessCode(i), entityTypes[j], Arrays.asList(domains.get(i).getId()), Priviledge.ALL);
			}
			
		}
		
	}
	
	public Domain createDomain(int i){
		Domain d = new Domain();
		d.setName("domain-" + i);
		d.setCode("1234" + i);
		d.meta("count", i);
		return domainRepository.save(d);
	}
	private  Group createGroup(Domain domain, int index) {
		   Group  g = new Group();
			g.setDomain(domain);
			g.setName("group " + index);
			return groupRepository.save(g);
			
	}
	private Role createRole(Domain domain, int index) {
		   Role  r = new Role();
			r.setDomain(domain);
			r.setName("role " + index);
		return roleRepository.save(r);
			
	}
	
	private UserGroup createUserGroup(AppUser user, Group group){
		UserGroup userGroup = new UserGroup();
		userGroup.setGroup(group);
		userGroup.setUser(user);
		userGroup.setDomain(group.getDomain());
		return userGroupRepository.save(userGroup);
	}
	private RoleMapping createRoleMapping(Affiliate affiliate, Role role){
		RoleMapping rm = new RoleMapping();
		rm.setKey(affiliate.key());
		rm.setType(affiliate.type());
		rm.setRole(role);
		rm.setDomain(role.getDomain());;
		return roleMappingRepository.save(rm);
	}
	
	 protected DomainAccessCodeMapping createMapping(DomainAccessCode code, String entityType, 
				List<String> domains, Priviledge priviledge) {
	    	if(domains == null){
	    		domains = new  ArrayList<>();
	    	}
			DomainAccessCodeMapping m = new DomainAccessCodeMapping();
			m.setAccessCode(code);
			m.setEntityType(entityType);
			m.setEntityId("domain");
			m.setPriviledge(priviledge);
			m.meta("domains", domains);
			m = codeMappingRepository.save(m);
			return m;
		}
	 
	 protected DomainAccessCodeMapping createMapping(DomainAccessCode code, String entityType, 
			String entityId, Priviledge priviledge) {
	    	
			DomainAccessCodeMapping m = new DomainAccessCodeMapping();
			m.setAccessCode(code);
			m.setEntityType(entityType);
			m.setEntityId(entityId);
			m = codeMappingRepository.save(m);
			return m;
		}
	private AppUser createUser(Domain domain, int index, List<Field> fieldList){
		AppUser u = new AppUser();
		u.setDomain(domain);
		u.setEmail(String.format("sample%s@%s.com", index, domain.getName()));
		u.setUsername(String.format("sample%s", index));
		u.setPassword(String.format("password%s", index));
		u.setPhoneNumber(String.format("+234808323232%s", index));
		u.setEmailVerified(true);
		u.setPhoneNumberVerified(true);
		Map<String,Object> properties = new HashMap<>();
		for(Field f: fieldList){
			properties.put(f.getName(), f.getName() + index);
		}
		u.setProperties(properties);
		return userRepository.save(u);
	}
	
	private Field createField(Domain domain, int index){
		Field f = new Field();
		f.setDomain(domain);
		f.setName(String.format("%s-%s-%s",domain.getName(),"field" ,index ));
		f.setType("string");
		return fieldRepository.save(f);
	}
	
	private DomainAccessCode createAccessCode(int index){
		DomainAccessCode code = new DomainAccessCode();
		code.setCode("1234" + index);
		return domainAccessCodeRepository.save(code);
	}
}
