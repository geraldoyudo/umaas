package com.gerald.umaas.registration.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
	
	@RequestMapping("/register")
	public String home(){
		return "index";
	}
}
