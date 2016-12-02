/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gerald.umaas.file.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gerald.umaas.file.entities.File;
import com.gerald.umaas.file.repositories.FileRepository;

/**
 *
 * @author Dev7
 */
@RestController
public class FileController {
	@Autowired
	private FileRepository fileRepository;
	
    private static final Logger logger = LoggerFactory
			.getLogger(FileController.class);
  
     // Download a file given its id
    @RequestMapping(value = "/files/{mode}/{fileId}",method = RequestMethod.GET)
    public ResponseEntity downloadFile(@PathVariable("fileId") String fileId, 
            @PathVariable(value = "mode")String mode) {
        File file = fileRepository.findOne(fileId);

        return downloadFile(mode, file);
    }
	
    @RequestMapping(value = "/files/upload/multiple",method = RequestMethod.POST)
    public ResponseEntity uploadFiles(@RequestParam("directory") String directory,
            @RequestBody MultipartFile[] files) {
        Map<String,String> fileMapping = new HashMap<>();
        File newFile;  
        try {
            for(MultipartFile file: files){     
                newFile = uploadFile(directory, file);
                fileMapping.put(newFile.getName(), newFile.getId());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("{}", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(fileMapping, HttpStatus.OK);
    }
   @RequestMapping(value = "/files/upload/single", method = RequestMethod.POST)
    public ResponseEntity uploadSingleFile(@RequestParam("directory") String directory,
            @RequestPart("file") MultipartFile file) {
        Map<String,String> fileMapping = new HashMap<>();
        File newFile;
        
        try {
                newFile = uploadFile(directory, file);
                fileMapping.put(newFile.getName(), newFile.getId());
            
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("{}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        System.out.println("Returning ");
        return new ResponseEntity<>(fileMapping, HttpStatus.OK);
    }

	private File uploadFile(String directory, MultipartFile file) throws IOException {
		System.out.println("uploading file");
		File newFile;
		String mimeType = file.getContentType();
		String filename = file.getOriginalFilename();
		byte[] bytes = file.getBytes();
		System.out.println("Creating file to " + directory);
		newFile = fileRepository.findByDirectoryAndName(directory, filename);
		if(newFile == null){
		    newFile = new File();
		    newFile.setDirectory(directory);
		    newFile.setName(filename);
		}
	    newFile.setFile(bytes);
	    newFile.setMimeType(mimeType);
	
		newFile = fileRepository.save(newFile);
		return newFile;
	}
    
     @RequestMapping(value = "/files/upload/{fileId}",
             method = RequestMethod.POST)
    public ResponseEntity uploadFileWithId(
            @PathVariable("fileId")String fileId,@RequestPart("file") MultipartFile file) {
    	 File appFile = fileRepository.findOne(fileId);
    	 if(appFile == null)  return new ResponseEntity<>("{}", HttpStatus.NOT_FOUND);
       
        try {      
            String mimeType = file.getContentType();
            byte[] bytes = file.getBytes();
            appFile.setFile(bytes);
            appFile.setMimeType(mimeType);
            fileRepository.save(appFile);
            
        }
        catch (Exception e) {
             logger.error(e.getMessage(),e);
            return new ResponseEntity<>("{}", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(fileId, HttpStatus.OK);
    }
    
     private ResponseEntity downloadFile(String mode, File file) {
 		// No file found based on the supplied filename
         if (file == null) {
             return new ResponseEntity<>("{}", HttpStatus.NOT_FOUND);
         }
         if(!mode.equals("download") && !mode.equals("view"))
             return new ResponseEntity<>("{}", HttpStatus.BAD_REQUEST);
         // Generate the http headers with the file properties
         HttpHeaders headers = new HttpHeaders();
         if(mode.equals("download"))
             headers.add("content-disposition", "attachment; filename=" + file.getName());

         // Split the mimeType into primary and sub types
         String primaryType, subType;
         String [] types = file.getMimeType().split("/");
         try {
             primaryType = types[0];
             if(types.length > 1)
                 subType = types[1];
             else{
                 subType = "*";
             }
         }
             catch (IndexOutOfBoundsException | NullPointerException ex) {
                 logger.error(ex.getMessage(),ex);
             return new ResponseEntity<>("{}", HttpStatus.INTERNAL_SERVER_ERROR);
         }

         headers.setContentType( new MediaType(primaryType, subType) );

         return new ResponseEntity<>(file.getFile(), headers, HttpStatus.OK);
 	}
    
     @RequestMapping(value = "/files/size", method = RequestMethod.GET)
     public long directorySize(@RequestParam("directory") String directory) {
    	 return fileRepository.sizeOfDirectory(directory);
     }
}
