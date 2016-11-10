package com.gerald.umaas.domain.extension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.gerald.umaas.domain.services.CustomDomainServiceProxy;
import com.gerald.umaas.extensionpoint.CustomDomainService;
import com.gerald.umaas.extensionpoint.Method;

@RunWith(SpringRunner.class)
public class TestCustomServiceProxy {
	private static final String DO_METHOD = "do";
	final static String SERVICE_ID = "test";
	final static String DOMAIN_ID = "0000";
	@MockBean
	CustomDomainService mockService;
	
	CustomDomainServiceProxy proxy;
	
	@Before
	public void setUp(){
		given(mockService.getId()).willReturn(SERVICE_ID);
		Method m = new Method();
		m.setName(DO_METHOD);
		m.setOutput(Integer.class);
		Map<String,Class<?>> input = new HashMap<>();
		input.put("param1", String.class);
		m.setInput(input);
		given(mockService.getMethods()).willReturn(new HashSet<>(Arrays.asList(m)));
		given(mockService.getConfigurationSpecification()).willReturn(input);
		proxy = new CustomDomainServiceProxy(Arrays.asList(mockService));
	}
	
	@Test
	public void testGetName() {
		String name = "name";
		given(mockService.getName()).willReturn(name);
		assertThat(proxy.getName(SERVICE_ID)).isEqualTo(name);
	}

	@Test
	public void testGetMethods() {
		assertThat(proxy.getMethods(SERVICE_ID).size()).isEqualTo(1);
	}
	
	@Test
	public void testEnabled() {
		given(mockService.isEnabled(DOMAIN_ID)).willReturn(true);
		assertThat(proxy.isEnabled(SERVICE_ID, DOMAIN_ID)).isTrue();
	}

	@Test
	public void testGetConfigurationSpecification(){
		assertThat(proxy.getConfigurationSpecification(SERVICE_ID).size()).isEqualTo(1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetConfigurationWithInvalidConfiguarion(){
		Map<String, Object> input = new HashMap<>();
		input.put("param1", 3);
		proxy.setConfiguration(SERVICE_ID, DOMAIN_ID, input);
	}
	
	@Test
	public void testSetConfiguration(){
		Map<String, Object> input = new HashMap<>();
		input.put("param1", "three");
		proxy.setConfiguration(SERVICE_ID, DOMAIN_ID, input);
	}
	
	@Test
	public void testExecute(){
		Map<String, Object> input = new HashMap<>();
		input.put("param1", "three");
		given(mockService.execute(DOMAIN_ID, DO_METHOD, input)).willReturn(1);
		assertThat(proxy.execute(SERVICE_ID, DOMAIN_ID,
				DO_METHOD, input)).isEqualTo(1);
	}
	@Test(expected = IllegalArgumentException.class)
	public void testExecuteWithInvalidInput(){
		Map<String, Object> input = new HashMap<>();
		input.put("param1", 3);
		given(mockService.execute(DOMAIN_ID, DO_METHOD, input)).willReturn(1);
		proxy.execute(SERVICE_ID, DOMAIN_ID,
				DO_METHOD, input);
	}
	
}

