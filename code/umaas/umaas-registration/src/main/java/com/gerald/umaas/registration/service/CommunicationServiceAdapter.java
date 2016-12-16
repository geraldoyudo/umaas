package com.gerald.umaas.registration.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;

import org.apache.commons.mail.util.MimeMessageParser;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.gerald.utils.communication.entities.SMSMessage;

@Aspect
@Component
public class CommunicationServiceAdapter {
	@Autowired
	private HttpSession session;
	@Value("${umaas.core}")
	private String coreUrl;
	@Autowired
	private RestTemplate restTemplate;
	private HttpHeaders headers;
	@Value("${umaas.accesscode.id:0000}")
	private String accessCodeId;
	@Value("${umaas.accesscode.value:0000}")
	private String accessCodeValue;
	@PostConstruct
	public void init(){
		headers = new HttpHeaders();
		headers.set(HttpHeaders.AUTHORIZATION, String.format("Basic %s",
				Base64Utils.encodeToString(String.format("%s:%s", accessCodeId, accessCodeValue).getBytes())));
	}
	@Around("target(com.gerald.utils.communication.ISMSSender) && bean(smsSender) && args(message)")
	public void onSendSMS(ProceedingJoinPoint pjp, SMSMessage message) throws Throwable{
		try{
			String domain = session.getAttribute("domainName").toString();
			if(domain == null) throw new NullPointerException();
			domain = getDomainId(domain);
			sendSMS(domain, message);
		}catch(NullPointerException ex){
			System.out.println("Request not available or No domain set");
			pjp.proceed();	
		}
	
		
	}
	
	@Around("target(com.gerald.utils.communication.IEmailSender) && bean(emailSender) && args(message)")
	public void onSendEmail(ProceedingJoinPoint pjp, MimeMessage message) throws Throwable{
		try{
			System.out.println(session.getId());
			String domain = session.getAttribute("domainName").toString();
			if(domain == null) throw new NullPointerException();
			System.out.println(domain);
			domain = getDomainId(domain);
			sendEmail(domain, message);
		}catch(NullPointerException ex){
			System.out.println("Request not available or No domain set");
			pjp.proceed();	
		}
	}
	
	private void sendSMS(String domainId,SMSMessage message){
		Map<String,Object> request = new HashMap<>();
		request.put("method", "send");
		Map<String,Object> input = new HashMap<>();
		input.put("id", message.getDestinationPhone());
		input.put("body", message.getMessage());
		input.put("subject","");
		request.put("input", input);
		String url= UriComponentsBuilder.fromHttpUrl(coreUrl)
				.path("/endpoint/com.gerald.umaas.domain.services.extensions.SMSService/")
				.path(domainId)
				.path("/execute")
				.build().toUriString();
		restTemplate.postForObject(url, new HttpEntity<>(request, headers), Map.class);	
	}
	private void sendEmail(String domainId,MimeMessage message) throws Exception{
            MimeMessageParser helper = new MimeMessageParser(message);
			Map<String,Object> request = new HashMap<>();
			request.put("method", "send");
			Map<String,Object> input = new HashMap<>();
			input.put("id", helper.getTo().get(0).toString());
			input.put("body", helper.parse().getHtmlContent());
			input.put("subject",helper.getSubject());
			request.put("input" , input);
			String url= UriComponentsBuilder.fromHttpUrl(coreUrl)
					.path("/endpoint/com.gerald.umaas.domain.services.extensions.EmailService/")
					.path(domainId)
					.path("/execute")
					.build().toUriString();
			restTemplate.postForObject(url,new HttpEntity<>(request, headers), Map.class);
			
	}
	private String getDomainId(String domainName){
		Map<String,Object> domain= restTemplate.exchange(
				UriComponentsBuilder.fromHttpUrl(coreUrl)
				.path("/domain/domains/search/findByName")
				.queryParam("name", domainName)
				.build().toUriString(),
				HttpMethod.GET,
				new HttpEntity(headers),
				Map.class).getBody();
		String url =  ((Map<String,Object>)((Map<String,Object>) domain.get("_links")).get("self")).get("href").toString();
		String[] segments = url.split("/");
		String id =  segments[segments.length - 1];
		System.out.println(id);
		return id;
	}
}
