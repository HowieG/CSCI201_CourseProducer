/*
 * WebSocketEndpoint.java
	•	The backend Java files are compiled when you run the project. Without getting into the details, @ServerEndpoint(value = "/ws") makes this WebSocketEndpoint.java connect on the project’s websocket, which WebSocketEndpoint.js connected to. 
	•	Therefore, it’s constantly listening for messages on this socket (because that’s how WebSockets work; note this is not how servlets work). Its onMessage function goes into its else statement because it does not yet have a factory. 
	•	“factories” is a HashMap so that all clients get the appropriate factory. If I connected 5 seconds ago, I would get a different instance of the factory from someone who connected 10 seconds ago because their workers, etc. should be 5 seconds ahead.
	•	We put a new Factory object into this HashMap, which we get by calling FactoryParser().factory. “new FactoryParser(session, this, is)” is an anonymous function; its constructor is called before moving onto the “.factory” part.
	
	•	Enters “if (factory != null)” because there is already a factory in the session.
	•	factory.listen(message): calls Factory.java’s listen() method
 */


package server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import client.Factory;

@ServerEndpoint(value = "/ws")

public class WebSocketEndpoint {
	private static final Logger logger = Logger.getLogger("BotEndpoint");
	private static final Map<String, Session> sessions = new HashMap<String, Session>();
	private static final Map<String, Factory> factories = new HashMap<>();
	private static Lock lock = new ReentrantLock();

	@OnOpen
	public void open(Session session) {
		lock.lock();
		logger.log(Level.INFO, "Connection opened. (id:)" + session.getId());
		sessions.put(session.getId(), session);
		lock.unlock();
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		lock.lock();
		Factory factory = factories.get(session.getId());
		if (factory != null) {
			// factory already created, listen
			factory.listen(message);
		} else {
			// factory not yet created, use message as text file
			InputStream is = new ByteArrayInputStream(message.getBytes());
			factories.put(session.getId(), new FactoryParser(session, this, is).factory);
		}
		lock.unlock();
	}

	@OnClose
	public void close(Session session) {
		lock.lock();
		logger.log(Level.INFO, "Connection closed. (id:)" + session.getId());
		sessions.remove(session.getId());
		if (factories.get(session.getId()) != null) {
			factories.get(session.getId()).killWorkers();
			factories.remove(session.getId());
		}
		lock.unlock();
	}

	@OnError
	public void onError(Throwable error) {
		error.printStackTrace();
	}

	public void sendToSession(Session session, String message) {
		lock.lock();
		try {
			session.getBasicRemote().sendText(message);
		} catch (IOException ex) {
			sessions.remove(session.getId());
			logger.log(Level.SEVERE, ex.getMessage(), ex.getStackTrace());
		}
		lock.unlock();
	}
}
