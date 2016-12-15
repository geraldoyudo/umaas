package com.gerald.umaas.domain.services.extensions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import com.gerald.umaas.domain.entities.AppUser;
import com.gerald.umaas.domain.entities.Group;
import com.gerald.umaas.domain.entities.Role;
import com.gerald.umaas.domain.entities.RoleMapping;
import com.gerald.umaas.domain.entities.UserGroup;
import com.gerald.umaas.domain.repositories.GroupRepository;
import com.gerald.umaas.domain.repositories.RoleMappingRepository;
import com.gerald.umaas.domain.repositories.RoleRepository;
import com.gerald.umaas.domain.repositories.UserGroupRepository;
import com.gerald.umaas.domain.repositories.UserRepository;
import com.gerald.umaas.extensionpoint.CustomDomainService;
import com.gerald.umaas.extensionpoint.Method;
import com.gerald.umaas.extensionpoint.TypeSpec;

public abstract class AbstractCommunicationService implements CustomDomainService{
	private static final String SEND_TO_ROLE = "sendToRole";
	private static final String SEND_TO_GROUP = "sendToGroup";
	private static final String SEND_TO_USER = "sendToUser";

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserGroupRepository userGroupRepository;
	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private RoleMappingRepository roleMappingRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	protected VelocityEngine velocityEngine;
	@Autowired
	private RestTemplate restTemplate;
	@Value("${umaas.communication-service}")
	private String serviceUrl;
	
	
	@Override
	public Set<Method> getMethods() {
		Set<Method> methods = new HashSet<>();
		TypeSpec id = new TypeSpec("id", "ID", String.class);
		TypeSpec message = new TypeSpec("body", "Message", String.class);
		TypeSpec subject = new TypeSpec("subject", "Subject", String.class);
		List<TypeSpec> specList = Arrays.asList(id, message,subject);
		Method sendToUser = new Method(SEND_TO_USER ,
				specList, Boolean.class);
		methods.add(sendToUser);
		Method sendToGroup = new Method(SEND_TO_GROUP ,
				specList, Boolean.class);
		methods.add(sendToGroup);
		Method sendToRole = new Method(SEND_TO_ROLE ,
				specList, Boolean.class);
		methods.add(sendToRole);
		return methods;
	}


	@Override
	public Object execute(String domainId, String methodName, Map<String, Object> inputParams,
			Map<String, Object> configuration) {
		switch(methodName){
			case SEND_TO_USER:{
				return doSendToUser(domainId, methodName,inputParams, configuration);
			}
			case SEND_TO_GROUP:{
				return doSendToGroup(domainId, methodName,inputParams, configuration);
			}
			case SEND_TO_ROLE:{
				return doSendToRole(domainId, methodName,inputParams, configuration);
			}
			
		}
		throw new IllegalArgumentException("Method name not supported");
	}

	protected abstract MessageData createMessage(Map<String,Object> input, Map<String,Object> configuration);
	protected abstract void processMessageData(Map<String, Object> inputParams, Map<String, Object> configuration, AppUser user,
			MessageData data);
	
	private Object doSendToRole(String domainId, String methodName, Map<String, Object> inputParams,
			Map<String, Object> configuration) {
		String id = inputParams.get("id").toString();
		Role role = roleRepository.findOne(id);
		if(role == null){
			throw new NullPointerException("Role not found");
		}
		List<RoleMapping> mappings = roleMappingRepository.findByRole(role);
		Set<AppUser> users = new TreeSet<>();
		for(RoleMapping m: mappings){
			if(m.getType().equals(RoleMapping.RoleMappingType.USER)){
				users.add(userRepository.findOne(m.getKey()));
			}else if(m.getType().equals(RoleMapping.RoleMappingType.GROUP)){
				Group g = groupRepository.findOne(m.getKey());
				List<UserGroup> ugs = userGroupRepository.findByGroup(g);
				for(UserGroup ug: ugs){
					users.add(ug.getUser());
				}
			}
		}
		for(AppUser user: users){
			sendMessageToUser(inputParams,configuration,user);
		}
		return true;
	}

	private Object doSendToGroup(String domainId, String methodName, Map<String, Object> inputParams,
			Map<String, Object> configuration) {
		String id = inputParams.get("id").toString();
		Group group = groupRepository.findOne(id);
		if(group == null)
			throw new NullPointerException("Group not found");
		List<UserGroup> userGroups = userGroupRepository.findByGroup(group);
		for(UserGroup userGroup: userGroups){
			sendMessageToUser(inputParams,configuration,userGroup.getUser());
		}
		return true;
	}

	private Object doSendToUser(String domainId, String methodName, Map<String, Object> inputParams,
			Map<String, Object> configuration) {
		String id = inputParams.get("id").toString();
		AppUser user = userRepository.findOne(id);
		sendMessageToUser(inputParams, configuration, user);
		return true;		
	}

	private void sendMessageToUser(Map<String, Object> inputParams, Map<String,Object> configuration, AppUser user) {
		if(user == null){
			throw new NullPointerException("User not found");
		}
		MessageData data = createMessage(inputParams, configuration);
		processMessageData(inputParams, configuration, user, data);
		sendMessage(data);
	}


	

	private void sendMessage(MessageData data) {
		restTemplate.postForObject(serviceUrl, data, String.class);
		
	}
}
