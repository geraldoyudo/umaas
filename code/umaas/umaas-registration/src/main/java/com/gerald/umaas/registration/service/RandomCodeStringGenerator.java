/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gerald.umaas.registration.service;

import java.util.Random;

import org.springframework.stereotype.Component;

/**
 *
 * @author Gerald
 */
@Component
public class RandomCodeStringGenerator implements CodeGenerator<String>{
    private Random random;
    private int charLength = 6;

    public RandomCodeStringGenerator() {
        random = new Random(System.currentTimeMillis());
    }
    
    
    @Override
    public String generateCode() {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< charLength; ++i){
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
    
    public void setSeed(long seed){
        random.setSeed(seed);
    }
    
    public void setCharLength(int length){
         charLength = length;
    }

    public int getCharLength() {
        return charLength;
    }
    
    
}
