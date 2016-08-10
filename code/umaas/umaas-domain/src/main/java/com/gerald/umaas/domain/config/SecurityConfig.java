package com.gerald.umaas.domain.config;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.gerald.umaas.domain.web.utils.PostDataPersisterFilter;


@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private UserDetailsService userDetailsService;
	
	 @Bean
	    public FilterRegistrationBean domainSessionIdFilter(HttpSession session) {
	      FilterRegistrationBean registration = new FilterRegistrationBean();
	      registration.setFilter(new PostDataPersisterFilter());
	      registration.addUrlPatterns("/domain/*");
	      registration.setOrder(-100);
	      return registration;
	    }
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.csrf()
		.disable()
		.httpBasic()
		.and()
		.authorizeRequests()
		.antMatchers("/domain/**")
		.access("@apiSecurityChecker.check(authentication,request)")
		.antMatchers("/files/user/*/*/*")
		.access("@apiSecurityChecker.checkFilePropertyAccess(authentication,request)")
		.anyRequest()
		.permitAll();
	}
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
	}
	
}
