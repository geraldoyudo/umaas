package com.gerald.umaas.domain_admin.security;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.Data;

@Component
@Data
@Primary
public class AccessCodeDetailsService implements UserDetailsService {
	@Value("${umaas.accesscode.id:0000}")
	private String accessCodeId;
	@Value("${umaas.accesscode.value:0000}")
	private String accessCodeValue;
	@Value("${umaas.core}")
	private String coreUrl;
	@Value("${umaas.core.contextPath:}")
	private String contextPath;
	@Autowired
	private RestTemplate restTemplate;
	private HttpHeaders headers;
	
	@PostConstruct
	public void init(){
		headers = new HttpHeaders();
		headers.set(HttpHeaders.AUTHORIZATION, String.format("Basic %s",
				Base64Utils.encodeToString(String.format("%s:%s", accessCodeId, accessCodeValue).getBytes())));
	}
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if(username == null){
			throw new UsernameNotFoundException("Null username");
		}
		String url = UriComponentsBuilder.fromHttpUrl(coreUrl)
				.path(contextPath)
				.path("/domain/domainAccessCodes/")
				.path(username).build().toUriString();
		try{
			AccessCode accessCode= restTemplate.exchange(url, HttpMethod.GET, 
					new HttpEntity<>(headers), AccessCode.class).getBody();
			accessCode.setId(username);
			return accessCode;
		}catch(HttpClientErrorException ex){
			if(ex.getStatusCode().equals(HttpStatus.NOT_FOUND)){
				throw new UsernameNotFoundException("Access code id not found");
			}else{
				throw ex;
			}
		}
	}
	

}
