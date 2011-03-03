package eu.soa4all.studio.luf.controller;

import java.util.Map;

import eu.soa4all.studio.luf.model.URI;

public interface Configuration {

	public String getHostName();

	public URI getServiceRepositoryServerUri();

	public String getServiceRepositoryName();

	public String getServiceLUFBaseUri();

	public Map<String,Map<String,String>> getOAuthCredentials();

}
