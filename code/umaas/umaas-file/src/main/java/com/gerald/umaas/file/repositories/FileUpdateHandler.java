package com.gerald.umaas.file.repositories;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.gerald.umaas.file.entities.File;

@Component
public class FileUpdateHandler extends AbstractMongoEventListener<File> {
	@Autowired
	private FileRepository fileRepository;
	@Value("${umaas.file.approval.url:http://localhost:8090/fileLimit/approveSave}")
	private String approvalUrl;
	@Value("${umaas.file.approval.base:umaas/domain/}")
	private String baseDirectory;
	private RestTemplate template = new RestTemplate();
	
	@Override
	public void onBeforeSave(BeforeSaveEvent<File> event) {
		File file = event.getSource();
		if(approvalUrl== null || approvalUrl.isEmpty() || baseDirectory == null ||
				file.getDirectory() == null || !file.getDirectory().startsWith(baseDirectory))
			return;
		boolean approved = true;
		String relativeDirectory = file.getDirectory().replaceFirst(baseDirectory, "" );
		if(file.getId() == null){
			// new
			approved = getApproval(relativeDirectory, file.getId(), file.getName(), 0, 
					file.getFile().length);
		}else{
			File oldFile = fileRepository.findOne(file.getId());
			approved = getApproval(relativeDirectory, file.getId(), file.getName(), 
					oldFile.getFile().length, 
					file.getFile().length);
		}
		if(!approved){
			throw new IllegalArgumentException("Request to save to this directory has been rejected");
		}
	}
	
	public boolean getApproval(String directory, String id, String name, long oldSize, long newSize){
		Map<String,Object> request = new HashMap<>();
		request.put("baseDirectory" , baseDirectory);
		request.put("directory" , directory);
		request.put("id" , id);
		request.put("name" , name);
		request.put("oldSize" , oldSize);
		request.put("newSize" , newSize);
		return template.postForObject(approvalUrl, request, Boolean.class);
	}
}
