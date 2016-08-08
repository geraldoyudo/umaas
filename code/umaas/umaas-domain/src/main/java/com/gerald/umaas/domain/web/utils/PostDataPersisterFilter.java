package com.gerald.umaas.domain.web.utils;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.filter.GenericFilterBean;

public class PostDataPersisterFilter extends GenericFilterBean {

	public static final String POST_DATA = "__data__";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		try{
			System.out.println("Post data persist filter");
			MultiReadHttpServletRequest requestWrapper= new MultiReadHttpServletRequest((HttpServletRequest)request);
			
			String body = "";
			   BufferedReader bufferedReader = requestWrapper.getReader();           
			   String line;
			   while ((line = bufferedReader.readLine()) != null){
			       body += line;
			   }
			 System.out.println(body);
			 request.setAttribute(POST_DATA, body);
			 chain.doFilter(requestWrapper, response);
		}catch( IOException ex){
				System.out.println("Cannot read data");
				System.out.println(ex.getMessage());
				chain.doFilter(request, response);;
		}
		
		
	}

}
