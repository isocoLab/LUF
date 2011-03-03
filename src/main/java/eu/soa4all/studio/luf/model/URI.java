package eu.soa4all.studio.luf.model;

import java.io.Serializable;

public interface URI extends Serializable {

	public String getNameSpace();

	public String getLocalName();

	public String toSPARQL();
}
