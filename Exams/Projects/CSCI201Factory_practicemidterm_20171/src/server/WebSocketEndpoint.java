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

	public static Factory getFactory(String sessionId) {
		return factories.get(sessionId);
	}
	
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
