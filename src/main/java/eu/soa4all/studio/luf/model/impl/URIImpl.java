package eu.soa4all.studio.luf.model.impl;

import eu.soa4all.studio.luf.model.URI;

public class URIImpl implements URI {

	private static final long serialVersionUID = 6851415228104134895L;

	private String uriString;

	public URIImpl() {
		uriString = "";
	}

	public URIImpl(String uriString) {
		this.uriString = uriString;
	}

	public String getNameSpace() {
		if ( uriString.endsWith("/") ) {
			return uriString;
		}
		int localNameIdx = getLocalNameIndex(uriString);
		if ( localNameIdx >= uriString.length() ) {
			return uriString;
		}
		return uriString.substring(0, localNameIdx);
	}

	public String getLocalName() {
		if ( uriString.endsWith("/") ) {
			return "";
		}
		int localNameIdx = getLocalNameIndex(uriString);
		if ( localNameIdx >= uriString.length() ) {
			return uriString;
		}
		return uriString.substring(localNameIdx);
	}

	@Override
	public String toString() {
		return uriString;
	}

	public String toSPARQL() {
		return "<" + uriString + ">";
	}

	private int getLocalNameIndex(String uri) {
		int separatorIdx = uri.indexOf('#');

		if (separatorIdx < 0) {
			separatorIdx = uri.lastIndexOf('/');
		}

		if (separatorIdx < 0) {
			separatorIdx = uri.lastIndexOf(':');
		}

		if (separatorIdx < 0) {
			throw new IllegalArgumentException("No separator character found in URI: " + uri);
		}

		return separatorIdx + 1;
	}

}
