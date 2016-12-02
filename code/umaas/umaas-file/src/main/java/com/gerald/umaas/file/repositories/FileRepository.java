package com.gerald.umaas.file.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.gerald.umaas.file.entities.File;
import com.gerald.umaas.file.repositories.projections.FileProjection;

@RepositoryRestResource(excerptProjection = FileProjection.class)
public interface FileRepository extends MongoRepository<File, String>{
	public Page<File> findByDirectoryStartsWith(@Param("directory") String directory, Pageable p);
	@RestResource(exported = false)
	public List<File> findByDirectoryStartsWith(String directory);
	public File findByDirectoryAndName(@Param("directory") String directory, @Param("name")String name);
	
	default long sizeOfDirectory(@Param("directory") String directory){
		long size = 0;
		for(File file: findByDirectoryStartsWith(directory)){
			size += file.getFile().length;
		}
		return size;
	}
}
