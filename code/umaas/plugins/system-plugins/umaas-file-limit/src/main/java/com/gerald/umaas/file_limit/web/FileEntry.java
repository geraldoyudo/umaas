package com.gerald.umaas.file_limit.web;

import lombok.Data;

@Data
public class FileEntry {
	private String directory;
	private long newSize;
	private long oldSize;
	private String id;
	private String name;
	private String baseDirectory;
}
