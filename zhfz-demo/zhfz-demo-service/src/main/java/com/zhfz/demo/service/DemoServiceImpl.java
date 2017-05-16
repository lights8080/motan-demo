package com.zhfz.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DemoServiceImpl implements DemoService {

	private static Logger logger = LoggerFactory.getLogger(DemoServiceImpl.class);

	@Override
	public String hello(String name) {
		String s = "Hello " + name + "!";
		logger.info(s);
		return s;
	}
	
}
