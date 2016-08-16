package com.gerald.umaas.domain.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.gerald.umaas.domain.entities.AppUser;
public interface UserRepository extends DomainResourceRepository<AppUser, String>{
	@RestResource(exported = false)
    public List<AppUser> findByUsername(String username);
    @Query("{ 'username' : ?0, 'domain.id': '?1' }")
    @RestResource(exported = false)
    public List<AppUser> findByUsernameAndDomain(String username, String domain);
    @RestResource(exported = false)
    public List<AppUser> findByEmailAndDomain(String email, String domain);
    @RestResource(exported = false)
    public List<AppUser> findByPhoneNumberAndDomain(String phoneNumber, String domain);
    @RestResource(path =  "/findAllInDomainContainingUsername")
    public List<AppUser> findByDomainAndUsernameContaining(
            @Param("domain")String domain,@Param("username") String username);
    public Page<AppUser> findByDomainAndUsernameContaining(
            @Param("domain")String domain,@Param("username") String username, Pageable p);
    public Page<AppUser> findByUsername(@Param("username") String username, Pageable p );
    @Query("{ 'username' : ?0, 'domain.id': '?1'}")
    public Page<AppUser> findByUsernameAndDomain(@Param("username") String username, @Param("domain") String domain, Pageable p );
    @RestResource(exported = false)
    public Page<AppUser> findByEmailAndDomain(String email, String domain, Pageable p);
    @RestResource(exported = false)
    public Page<AppUser> findByPhoneNumberAndDomain(String phoneNumber, String domain, Pageable p);
}
