package com.gerald.umaas.file.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.gerald.umaas.file.entities.File;
import com.gerald.umaas.file.repositories.projections.FileProjection;

@RepositoryRestResource(excerptProjection = FileProjection.class)
public interface FileRepository extends MongoRepository<File, String>{
	public Page<File> findByDirectory(String directory, Pageable p);
	public File findByDirectoryAndName(String directory, String name);
}
