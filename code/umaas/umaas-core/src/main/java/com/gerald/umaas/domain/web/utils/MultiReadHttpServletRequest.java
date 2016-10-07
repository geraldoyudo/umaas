package com.gerald.umaas.domain.web.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class MultiReadHttpServletRequest extends HttpServletRequestWrapper {
 private String _body;
 
public MultiReadHttpServletRequest(HttpServletRequest request) throws IOException {
	 super(request);
	 _body = "";
	 BufferedReader bufferedReader = request.getReader();
	 if(bufferedReader == null){
		 return;
	 }
	 String line;
	 while ((line = bufferedReader.readLine()) != null){
	 _body += line;
	 }
 }

@Override
public ServletInputStream getInputStream() throws       IOException {
	return new CustomServletInputStream(_body.getBytes());
 }

@Override
public BufferedReader getReader() throws IOException {
 return new BufferedReader(new InputStreamReader(this.getInputStream()));
 }
private static class CustomServletInputStream extends ServletInputStream {

    private ByteArrayInputStream buffer;

    public CustomServletInputStream(byte[] contents) {
        this.buffer = new ByteArrayInputStream(contents);
    }

    @Override
    public int read() throws IOException {
        return buffer.read();
    }

    @Override
    public boolean isFinished() {
        return buffer.available() == 0;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener listener) {
        throw new RuntimeException("Not implemented");
    }
}
 }