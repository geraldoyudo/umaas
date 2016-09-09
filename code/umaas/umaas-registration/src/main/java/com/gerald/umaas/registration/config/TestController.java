package com.gerald.umaas.registration.config;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
	
	@RequestMapping(path = "/sample")
	public String test(){
		return "Hello World!";
	}
}
