/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gerald.umaas.domain.entities;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;

/**
 *
 * @author Dev7
 */
@CompoundIndexes({
    @CompoundIndex(name = "domain_externalId", def = "{'domain.$id' : 1, 'externalId' : 1}", unique = true)
})
public abstract class DomainResource extends Resource{
    /**
	 * 
	 */
	private static final long serialVersionUID = -5167126974325754916L;
	@DBRef
    protected Domain domain;

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }
}