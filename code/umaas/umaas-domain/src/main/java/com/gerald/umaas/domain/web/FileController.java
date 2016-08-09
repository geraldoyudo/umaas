package com.gerald.umaas.domain.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {
	private RestTemplate restTemplate = new RestTemplate();
	@Value("${umaas.file-repo.url}")
	private String fileRepoUrl;
	
	  // Download a file
    @RequestMapping(value = "/files/{mode}/{fileId}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadFile(@PathVariable("fileId") String fileId, 
            @PathVariable(value = "mode")String mode, HttpServletResponse response) {
    	String subPath = String.format("/files/%s/%s", mode, fileId);
    	return restTemplate.exchange(fileRepoUrl + subPath
    			, HttpMethod.GET, null, byte[].class);
    	
    }
    
    @RequestMapping(value = "/files/userPropertyUpload/{userId}/{fieldName}",
            method = RequestMethod.POST)
   public ResponseEntity uploadUserProperty(@PathVariable("userId") String userId,
           @PathVariable("fieldName")String fieldName,@RequestPart("file") MultipartFile file) throws IOException {
    	String subPath = String.format("/files/upload/single?directory=%s",
    			"folder/" + userId + "/" + fieldName);
    	Map<String, Object> map = new HashMap<>();
    	
    	
    	map.put("file", file.getBytes());
    	map.put("name", file.getOriginalFilename());
    	map.put("mimeType", file.getContentType());
    	map.put("directory", "folder/" + userId + "/" + fieldName);
    	System.out.println(subPath);
    	HttpEntity<Map<String,Object>> entity = new HttpEntity<>(map);
    	Map<String, Object> response = new HashMap<>();
    	response = restTemplate.postForObject(fileRepoUrl + "/files"
    			, map , Map.class);
    	Map<String,Object> link =(Map<String,Object>) response.get("_links");
    	Map<String,Object> self = (Map<String,Object>) link.get("self");
    	String href =(String) self.get("href");
    	Map<String,Object> resp = new HashMap<String,Object>();
    	String[] parts = href.split("/");
    	resp.put("id",parts[parts.length -1] );
    	return ResponseEntity.ok(resp);
    	
    
    }
    
}
