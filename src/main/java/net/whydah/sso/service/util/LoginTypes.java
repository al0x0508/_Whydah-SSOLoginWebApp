package net.whydah.sso.service.util;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginTypes {
	
	private final static Logger logger = LoggerFactory.getLogger(LoginTypes.class);
	
	private static final String ENABLED = "enabled";
	
	private final boolean facebookLoginEnabled;
	private final boolean openIdLoginEnabled;
	private final boolean omniLoginEnabled;
	private final boolean userpasswordLoginEnabled;
	
	public LoginTypes(Properties properties) {
		facebookLoginEnabled = ENABLED.equals(properties.getProperty("logintype.facebook"));
		openIdLoginEnabled = ENABLED.equals(properties.getProperty("logintype.openid"));
		omniLoginEnabled = ENABLED.equals(properties.getProperty("logintype.omni"));
		userpasswordLoginEnabled = ENABLED.equals(properties.getProperty("logintype.userpassword"));
		
		logger.debug(String.format("Fabook Sign on is %1s, OpenId Sign on is %2s, Omni Sign on is %3s, User/Password Sign on is %4s." 
									, properties.getProperty("logintype.facebook")
									, properties.getProperty("logintype.openid")
									, properties.getProperty("logintype.omni")
									, properties.getProperty("logintype.userpassword")));
	}

	public boolean isFacebookLoginEnabled() {
		return facebookLoginEnabled;
	}

	public boolean isOpenIdLoginEnabled() {
		return openIdLoginEnabled;
	}

	public boolean isOmniLoginEnabled() {
		return omniLoginEnabled;
	}

	public boolean isUserpasswordLoginEnabled() {
		return userpasswordLoginEnabled;
	}

}
