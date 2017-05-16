package com.zhfz.demo.init;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.zhfz.demo.util.AppConfig;

@Component
@Scope("singleton")
public class InitWapper {

	private static Logger logger = LoggerFactory.getLogger(InitWapper.class);
	@Autowired
	private AppConfig appConfig;
	
	@PostConstruct
	public void init() {
		String rootLogger = appConfig.getRootLoggerLevel();
		if (StringUtils.isNotBlank(rootLogger)) {
			if ("INFO".equals(rootLogger.toUpperCase())) {
				LogManager.getRootLogger().setLevel(Level.INFO);
				logger.info("log4j.rootLogger.level:INFO");
			} else if ("DEBUG".equals(rootLogger.toUpperCase())) {
				LogManager.getRootLogger().setLevel(Level.DEBUG);
				logger.debug("log4j.rootLogger.level:DEBUG");
			} else if ("WARN".equals(rootLogger.toUpperCase())) {
				LogManager.getRootLogger().setLevel(Level.WARN);
				logger.warn("log4j.rootLogger.level:WARN");
			}
		}
	}
	
	@PreDestroy
	void destory() {
		logger.info("destory");
	}
}
