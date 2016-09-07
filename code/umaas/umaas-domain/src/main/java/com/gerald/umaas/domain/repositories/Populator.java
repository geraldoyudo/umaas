package com.gerald.umaas.domain.repositories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
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
	Random random = new Random(System.currentTimeMillis());
	private static String[] entityTypes = new String[]{AppUser.class.getSimpleName(),
			Field.class.getSimpleName(),
			Role.class.getSimpleName(),
			Group.class.getSimpleName(),
			UserField.class.getSimpleName(),
			UserGroup.class.getSimpleName(),
			RoleMapping.class.getSimpleName(),
			DomainAccessCode.class.getSimpleName(),
			DomainAccessCodeMapping.class.getSimpleName()};
	
	@Value("${umaas.security.admin-code:0000}")
	private String adminCode;
	@Value("${umaas.security.admin-id:0000}")
	private String adminId;
	
	@PostConstruct
	public void init(){
		if(domainRepository.findAll().isEmpty()){
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
		ArrayList<Field> fieldList;
		for(Domain d: domains){
			count = (int)d.meta("count");
			fieldList = new ArrayList<>();
			for(int j=0; j<10; ++j){
				fieldList.add(createField(d,j));
			}
			for(int i=0; i<20; ++i){
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
				createMapping(createAccessCode(10*i + j), entityTypes[j], Arrays.asList(domains.get(i).getId()), Priviledge.ALL);
			}
			
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
		int modIndex = index %10;
		switch(modIndex){
			case 0:{
				f.setType("file");
				f.set("accept", "image/*");
				break;
			}
			case 1:{
				f.setType("string");
				break;
			}
			case 2:{
				f.setType("string");
				f.set("pattern", String.format("[A-Za-z]{%d}", random.nextInt(16)+1));
				break;
			}
			case 3:{
				f.setType("date");
				f.set("minimum", System.currentTimeMillis() - Math.abs(random.nextInt(100)*365*24*60*60*1000));
				f.set("maximum", System.currentTimeMillis() + Math.abs(random.nextInt(100)*365*24*60*60*1000));
				break;
			}
			case 4:{
				f.setType("date");
				f.set("minimum", System.currentTimeMillis());
				f.set("maximum", System.currentTimeMillis() + Math.abs(random.nextInt(100)*365*24*60*60*1000));
				break;
			}
			case 5:{
				f.setType("email");
				break;
			}
			case 6:{
				f.setType("select");
				f.set("options", Arrays.asList("option-1", "option-2", "option-3"));
				f.set("labels", Arrays.asList("label-1", "label-2", "label-3"));
				break;
			}
			case 7:{
				f.setType("select");
				f.set("options", Arrays.asList("option-1", "option-2", "option-3"));
				break;
			}
			case 8:{
				f.setType("integer");
				break;
			}
			case 9:{
				f.setType("integer");
				int min = random.nextInt(100);
				f.set("minimum", min);
				f.set("maximum", min +random.nextInt(1000));
				break;
			}
		}
		System.out.println(f);
		return fieldRepository.save(f);
	}
	
	private DomainAccessCode createAccessCode(int index){
		DomainAccessCode code = new DomainAccessCode();
		code.setCode("1234" + index);
		return domainAccessCodeRepository.save(code);
	}
	
}
