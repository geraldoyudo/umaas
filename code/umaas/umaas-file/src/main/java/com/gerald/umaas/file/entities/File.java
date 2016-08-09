package com.gerald.umaas.file.entities;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document
@CompoundIndexes({
    @CompoundIndex(name = "unique_path", def = "{'directory' : 1, 'name' : 1}", unique = true)
})
@Data
public class File {
	@Id
	private String id;
	private String directory = "";
	@NotNull
	private String name;
	private byte[] file;
    @NotNull
    private String mimeType;
    private Map<String, Object> meta = new HashMap<>();
    
    public void meta(String key, Object value){
        meta.put(key, value);
    }
    
    public Object meta(String key){
        return meta.get(key);
    }
}
