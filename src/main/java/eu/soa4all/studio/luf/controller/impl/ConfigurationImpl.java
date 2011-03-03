package eu.soa4all.studio.luf.controller.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import eu.soa4all.studio.luf.controller.Configuration;
import eu.soa4all.studio.luf.controller.exceptions.ConfigurationException;
import eu.soa4all.studio.luf.model.URI;
import eu.soa4all.studio.luf.model.impl.URIImpl;

public class ConfigurationImpl implements Configuration {

	private Properties props;

	public ConfigurationImpl() throws ConfigurationException {
		props = new Properties();
		try {
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("config.properties");
			props.load(in);
		} catch (FileNotFoundException e) {
			throw new ConfigurationException(e);
		} catch (IOException e) {
			throw new ConfigurationException(e);
		}
	}

	public String getHostName() {
		if ( props != null )
			return props.getProperty ("serverName");
		return null;
	}

	public URI getServiceRepositoryServerUri() {
		if ( props != null )
			return new URIImpl(props.getProperty ("sesameServerURL"));
		return null;
	}

	public String getServiceRepositoryName() {
		if ( props != null )
			return props.getProperty ("repoName");
		return null;
	}

	public String getServiceLUFBaseUri() {
		if ( props != null )
			return props.getProperty ("LUFBaseUri");
		return null;
	}

	public Map<String,Map<String,String>> getOAuthCredentials() {
		Map<String,Map<String,String>> credentials = new HashMap<String,Map<String,String>>();
		if ( props != null ) {
			int i = 1;
			String credentialsNum = "oauthCredentials."+Integer.toString(i);
			while(props.getProperty(credentialsNum)!=null) {
				String[] credentialStrings = props.getProperty(credentialsNum).split(",");
				Map<String,String> c = new HashMap<String,String>();
				c.put("consumerKey", credentialStrings[0]);
				c.put("token", credentialStrings[1]);
				c.put("consumerSecret", credentialStrings[2]);
				c.put("tokenSecret", credentialStrings[3]);
				c.put("origin", credentialStrings[4]);
				credentials.put(credentialStrings[0], c);
				i++;
				credentialsNum = "oauthCredentials."+Integer.toString(i);
			}
			return credentials;
		}
		return null;
	}

}
