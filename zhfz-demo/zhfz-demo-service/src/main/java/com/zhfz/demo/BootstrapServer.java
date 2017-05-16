package com.zhfz.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.util.MotanSwitcherUtil;
import com.zhfz.demo.util.Constant;

public class BootstrapServer {

	private static Logger logger = LoggerFactory.getLogger(BootstrapServer.class);

	private static final String SERVER_NAME = "zhfz.server.name";
	private static final String SERVER_GROUP = "zhfz.server.group";
	private static final String SERVER_PORT = "zhfz.server.port";
	
	public static void main(String[] args) throws InterruptedException {
		long b = System.currentTimeMillis();
		String serverPort = null;
		String serverGroup = null;
		String coreConfigFile = null;
		if (args != null && args.length > 0) {
			int i = 1;
			for (String param : args) {
				if (i == 1) {
					serverPort = param;
				} else if (i == 2) {
					serverGroup = param;
				} else if (i == 3) {
					coreConfigFile = param;
				}
				i++;
			}
		}
		
		loadCoreProperties(coreConfigFile);
		
		if (StringUtils.isNotEmpty(serverPort)) {
			System.setProperty(SERVER_PORT, serverPort);
		}
		if (StringUtils.isNotEmpty(serverGroup)) {
			System.setProperty(SERVER_GROUP, serverGroup);
		}
		String serverName = System.getProperty(SERVER_NAME);
		logger.info("[{}] Server Starting...",serverName);
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				new String[] { "classpath:spring.xml", "classpath:zhfz-motan.xml" });
		MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, true);

		logger.info("---Zhfz Properties List---");
		Iterator<Entry<Object, Object>> iterator = System.getProperties().entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Object, Object> next = iterator.next();
			if(next.getKey().toString().startsWith("zhfz"))
				logger.info("{}={}", next.getKey(), next.getValue());
		}
		
		logger.info("[{}] Server startup in {} ms", serverName, (System.currentTimeMillis() - b));

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, false);
				logger.info("[{}] Server Shutdown", serverName);
			}
		}));
	}

	private static void loadCoreProperties(String realPath) {
		logger.debug("Load file:core.properties...");
		String confDir = BootstrapServer.class.getResource("/").getPath()+"conf"+Constant.FILE_SEPARATOR;
		boolean isExist = loadProperties(realPath);
		if(!isExist){
			logger.debug("Load classpath:core.properties...");
			realPath = confDir+"core.properties";
			loadProperties(realPath);
		}
		
		String propFile = System.getProperty("zhfz.app.properties");
		logger.debug("Load file:app.properties...");
		isExist = loadProperties(propFile);
		if(!isExist){
			logger.debug("Load file:app.properties...1");
			propFile = confDir+"app.properties";
			loadProperties(propFile);
		}
		
		logger.debug("Load file:jdbc.properties...");
		String jdbcPropFile = System.getProperty("zhfz.jdbc.properties");
		isExist = loadProperties(jdbcPropFile);
		if(!isExist){
			logger.debug("Load file:jdbc.properties...1");
			propFile = confDir+"jdbc.properties";
			loadProperties(propFile);
		}
	}

	private static boolean loadProperties(String propFile) {
		if (StringUtils.isNotEmpty(propFile)) {
			try {
				File file = new File(propFile);
				if (file.exists()) {
					InputStream inputStream = new FileInputStream(file);
					Properties p = new Properties();
					p.load(inputStream);
					Iterator<Entry<Object, Object>> iterator = p.entrySet().iterator();
					while (iterator.hasNext()) {
						Entry<Object, Object> next = iterator.next();
						logger.debug("{}={}", next.getKey(), next.getValue());
						System.setProperty(next.getKey().toString(), next.getValue().toString());
					}
					return true;
				} else {
					logger.debug("File not exists. {}", propFile);
					return false;
				}
			} catch (FileNotFoundException e) {
				logger.error("loadProperties Error:", e);
				return false;
			} catch (IOException e) {
				logger.error("loadProperties Error:", e);
				return false;
			}
		} else {
			logger.debug("Param is Empty.");
			return false;
		}
	}
}
