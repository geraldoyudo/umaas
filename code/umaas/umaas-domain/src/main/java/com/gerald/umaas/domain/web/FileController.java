package com.gerald.umaas.domain.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.gerald.umaas.domain.entities.AppUser;
import com.gerald.umaas.domain.entities.Field;
import com.gerald.umaas.domain.entities.UserField;
import com.gerald.umaas.domain.repositories.FieldRepository;
import com.gerald.umaas.domain.repositories.UserFieldRepository;
import com.gerald.umaas.domain.repositories.UserRepository;

@RestController
public class FileController {
	private RestTemplate restTemplate = new RestTemplate();
	@Value("${umaas.file-repo.url}")
	private String fileRepoUrl;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private FieldRepository fieldRepository;
	@Autowired
	private UserFieldRepository userFieldRepository;
	private static final String DIRECTORY_FORMAT = "umaas/domain/%s/user/%s/field";
	
	
	  // Download a file
    @RequestMapping(value = "/files/user/{mode}/{userId}/{fieldId}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadFile(@PathVariable("userId") String userId,
    		@PathVariable("fieldId") String fieldId, 
            @PathVariable(value = "mode")String mode, HttpServletResponse response) {
    	AppUser user = userRepository.findOne(userId);
    	Field field = fieldRepository.findOne(fieldId);
    	if(!field.getType().equals("file")){
    		throw new IllegalArgumentException("Field is not a binary field");
    	}
    	if(!user.getDomain().equals(field.getDomain())){
    		throw new IllegalArgumentException("Field and user does not belong to the same domain");
    	}
    	UserField userField = userFieldRepository.findByUserAndField(user, field);
    	String fileId = null;
    	if(userField == null){
    		userField = new UserField();
    		userField.setUser(user);
    		userField.setField(field);
    	}else{
    		fileId =(String) userField.getValue();
    	}
    	if(fileId == null){
    		return new ResponseEntity<>(new byte[]{},HttpStatus.NO_CONTENT);
    	}
    	String subPath = String.format("/files/%s/%s", mode, fileId);
    	return restTemplate.exchange(fileRepoUrl + subPath
    			, HttpMethod.GET, null, byte[].class);
    	
    }
    
    @RequestMapping(value = "/files/user/upload/{userId}/{fieldId}",
            method = RequestMethod.POST)
   public ResponseEntity uploadUserProperty(@PathVariable("userId") String userId,
           @PathVariable("fieldId")String fieldId,@RequestPart("file") MultipartFile file) throws IOException {
    	AppUser user = userRepository.findOne(userId);	
    	Field field = fieldRepository.findOne(fieldId);
    	if(!field.getType().equals("file")){
    		throw new IllegalArgumentException("Field is not a binary field");
    	}
    	if(!user.getDomain().equals(field.getDomain())){
    		Map<String,String> ret = new HashMap<>();
    		ret.put("message", "field does not belong to the user's domain");
    		return new ResponseEntity<Map<String,String>>(ret, HttpStatus.BAD_REQUEST);
    	}
    	UserField userField = userFieldRepository.findByUserAndField(user, field);
    	String fileId = null;
    	if(userField == null){
    		userField = new UserField();
    		userField.setUser(user);
    		userField.setField(field);
    	}else{
    		fileId =(String) userField.getValue();
    	}
    	boolean exists = fileExists(fileId);
    	Map<String, Object> map = new HashMap<>();
    	
		map.put("file", file.getBytes());
    	map.put("name", field.getId());
    	map.put("mimeType", file.getContentType());    	
    	map.put("directory", String.format(DIRECTORY_FORMAT, user.getDomain().getId(),
    			user.getId()));
    	Map response = new HashMap<>();
    	HttpEntity<Map<String,Object>> entity = new HttpEntity<>(map);
    	if(exists){
    		response = restTemplate.exchange(fileRepoUrl + "/files/" + fileId
        			,HttpMethod.PUT, entity , Map.class).getBody();
    	}else{
    		response = restTemplate.postForObject(fileRepoUrl + "/files"
        			, map , Map.class);
    	}
    	Map<String,Object> link =(Map<String,Object>) response.get("_links");
    	Map<String,Object> self = (Map<String,Object>) link.get("self");
    	String href =(String) self.get("href");
    	Map<String,Object> resp = new HashMap<String,Object>();
    	String[] parts = href.split("/");
    	resp.put("id",parts[parts.length -1] );
    	userField.setValue(parts[parts.length -1]);
    	userFieldRepository.save(userField);
    	return ResponseEntity.ok(resp);
    }

    @RequestMapping(value = "/files/user/upload/{userId}",
            method = RequestMethod.POST)
   public ResponseEntity uploadUserProperties(@PathVariable("userId") String userId,MultipartRequest request) throws IOException {
    	AppUser user = userRepository.findOne(userId);	
    
    	Map<String, MultipartFile> fileMap = request.getFileMap();
    
    	List<Map<String,Object>> rets = new ArrayList<>();
    	for(String fieldIdKey: fileMap.keySet()){
    		MultipartFile file = fileMap.get(fieldIdKey);
    		Field field = fieldRepository.findOne(fieldIdKey);
        	if(!field.getType().equals("file")){
        		throw new IllegalArgumentException("Field is not a binary field");
        	}
        	if(!user.getDomain().equals(field.getDomain())){
        		Map<String,String> ret = new HashMap<>();
        		ret.put("message", "field does not belong to the user's domain");
        		return new ResponseEntity<Map<String,String>>(ret, HttpStatus.BAD_REQUEST);
        	}
        	UserField userField = userFieldRepository.findByUserAndField(user, field);
        	String fileId = null;
        	if(userField == null){
        		userField = new UserField();
        		userField.setUser(user);
        		userField.setField(field);
        	}else{
        		fileId =(String) userField.getValue();
        	}
        	boolean exists = fileExists(fileId);
        	Map<String, Object> map = new HashMap<>();
        	
    		map.put("file", file.getBytes());
        	map.put("name", field.getId());
        	map.put("mimeType", file.getContentType());    	
        	map.put("directory", String.format(DIRECTORY_FORMAT, user.getDomain().getId(),
        			user.getId()));
        	Map response = new HashMap<>();
        	HttpEntity<Map<String,Object>> entity = new HttpEntity<>(map);
        	if(exists){
        		response = restTemplate.exchange(fileRepoUrl + "/files/" + fileId
            			,HttpMethod.PUT, entity , Map.class).getBody();
        	}else{
        		response = restTemplate.postForObject(fileRepoUrl + "/files"
            			, map , Map.class);
        	}
        	Map<String,Object> link =(Map<String,Object>) response.get("_links");
        	Map<String,Object> self = (Map<String,Object>) link.get("self");
        	String href =(String) self.get("href");
        	Map<String,Object> resp = new HashMap<String,Object>();
        	String[] parts = href.split("/");
        	resp.put("id",parts[parts.length -1] );
        	userField.setValue(parts[parts.length -1]);
        	userFieldRepository.save(userField);
        	rets.add(resp);
    	}
    	
    	
    	return ResponseEntity.ok(rets);
    }

	private boolean fileExists(String fileId) {
		try{
			return restTemplate.getForEntity(fileRepoUrl + "/files/" + fileId, String.class).getStatusCode().equals(HttpStatus.OK);
		}catch(HttpClientErrorException ex){
				return false;
		}
	}
    
}
