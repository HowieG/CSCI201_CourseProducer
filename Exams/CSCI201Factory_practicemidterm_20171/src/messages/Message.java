package messages;

import java.io.Serializable;

import javax.websocket.Session;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	public String sessionId;
	public String action;
}
