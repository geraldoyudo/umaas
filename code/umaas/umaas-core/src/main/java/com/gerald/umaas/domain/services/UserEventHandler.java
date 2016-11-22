package com.gerald.umaas.domain.services;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.gerald.umaas.domain.entities.AppUser;
import com.gerald.umaas.domain.entities.Domain;

@Component
@RepositoryEventHandler
public class UserEventHandler {
	private static final Logger log = LoggerFactory
			.getLogger(UserEventHandler.class);
	
	private static final String USER_LISTENER_URL_PROPERTY = "userListenerUrl";
	private RestTemplate restTemplate = new RestTemplate();
	private enum Action {ADD, UPDATE, DELETE};
	
	@HandleAfterDelete
	public void onDelete(AppUser user){
		handleChange(Action.DELETE, user );
	}
	
	@HandleAfterSave
	public void onUpdate(AppUser user){
		handleChange(Action.UPDATE, user );
	}
	
	@HandleAfterCreate
	public void onAdd(AppUser user){
		handleChange(Action.ADD, user );
	}
	
	private void handleChange(Action action, AppUser user){
		Domain d = null;
		try{
			d = user.getDomain();
			if (d == null) return;
			String listenerUrl = d.getProperties().get(USER_LISTENER_URL_PROPERTY).toString();
			if(listenerUrl == null || listenerUrl.isEmpty()) return;
			Map<String, Object> values = new HashMap<>();
			values.put("user", user);
			values.put("action", action.name());
			String resp = restTemplate.postForObject(listenerUrl, values, String.class);
			log.info("Successfully called listener", resp);
		}catch(NullPointerException e){
			// do nothing
		}catch (HttpClientErrorException ex){
			log.info("Error executing callback for domain " + d.getName());
			log.info("Http Client Error exception -- Status", ex.getStatusCode());
			log.info("Http Client Error exception -- Message", ex.getMessage());
		}
	}
}
