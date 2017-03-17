package messages;

import java.util.Vector;

import client.FactoryResource;

public class UpdateResourcesMessage extends Message {
	private static final long serialVersionUID = 1L;
	public Vector<FactoryResource> resources;

	public UpdateResourcesMessage(Vector<FactoryResource> resources) {
		this.action = "UpdateResources";
		this.resources = resources;
	}
}
