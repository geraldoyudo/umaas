package com.gerald.umaas.domain.repositories;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.gerald.umaas.domain.entities.Domain;
import static org.assertj.core.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class DomainRepositoryTest {
	@Autowired
	private DomainRepository domainRepository;
	@Before
	public void clearRepository(){
		domainRepository.deleteAll();
	}

	@Test
	public void testCreationAndSave() {
		Domain d = new Domain();
		assertNull(d.getId());
		d = domainRepository.save(d);
		assertNotNull(d.getId());
		long size = domainRepository.count();
		assertThat(size).isEqualTo(1);
	}

}
