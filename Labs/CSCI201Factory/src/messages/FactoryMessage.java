package messages;

import client.Factory;

public class FactoryMessage extends Message {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private Factory factory;

	public FactoryMessage(Factory factory) {
		this.action = "Factory";
		this.factory = factory;
	}
}
