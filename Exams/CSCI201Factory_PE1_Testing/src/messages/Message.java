/*
 * Message.java:
	•	Serializable abstract class (it should be abstract in the base code, either way it is never directly instantiated). Its sole purpose is to hold data.
	•	Holds “String action,” an identifier which will act as a key when we send the message to the client. For example WebSocketEndpoint.js will receive this message and if (action == ‘WorkerMoveToPath’), it will do move the worker elements, but if (action == 'UpdateResources') it will update the resource elements. Note: the JavaScript cannot interact with any Java except indirectly either through a Servlet or by sending messages over the WebSocket.
 */

package messages;

import java.io.Serializable;

public abstract class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	public String action;
}
