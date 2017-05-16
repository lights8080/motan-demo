package com.zhfz.demo.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class AppConfig {

	@Value("${zhfz.log4j.rootLogger:INFO}")
	private String rootLoggerLevel;
	
	@Value("${zhfz.sabre.api.url:https://webservices3.sabre.com}")
	private String sabreApiUrl;
	
	@Value("${zhfz.sabre.api.datapath:}")
	private String sabreApiDatapath;
	
	@Value("${zhfz.ipcc.sessions:}")
	private String ipccSessions;
	
	@Value("${zhfz.sessions.memadds:}")
	private String sessionMemAdds;
	

	public String getRootLoggerLevel() {
		return rootLoggerLevel;
	}

	public String getSabreApiUrl() {
		return sabreApiUrl;
	}

	public String getIpccSessions() {
		return ipccSessions;
	}

	public String getSabreApiDatapath() {
		if(sabreApiDatapath==null || sabreApiDatapath.length()==0){
			return AppConfig.class.getResource("/").getPath()+"data";
		}else{
			return sabreApiDatapath;
		}
	}

	public String getSessionMemAdds() {
		return sessionMemAdds;
	}
	
}


