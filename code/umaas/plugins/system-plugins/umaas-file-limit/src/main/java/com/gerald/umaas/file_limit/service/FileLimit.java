package com.gerald.umaas.file_limit.service;

import lombok.Data;

@Data
public class FileLimit {
	private long size = 0;
	private long limit = 100 * 1024*1024;
}
