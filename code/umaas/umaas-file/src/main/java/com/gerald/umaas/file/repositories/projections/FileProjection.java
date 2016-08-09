/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gerald.umaas.file.repositories.projections;

import java.util.Map;

import org.springframework.data.rest.core.config.Projection;

import com.gerald.umaas.file.entities.File;

/**
 *
 * @author Dev7
 */
@Projection(name = "metaFile", types = { File.class })
public interface FileProjection {
    public String getName();
    public String getMimeType();
    public String getDirectory();
    public Map<String,Object> getMeta();
}
