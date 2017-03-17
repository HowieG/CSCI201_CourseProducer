package client;

import java.awt.Point;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.Vector;

import javax.websocket.Session;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import actions.Action;
import actions.ActionFactory;
import messages.FactoryMessage;
import messages.Message;
import messages.ReportMessage;
import messages.UpdateResourcesMessage;
import messages.UpdateTaskBoardMessage;
import messages.WorkerMoveToPathMessage;
import server.WebSocketEndpoint;
import utilities.Constants;

/**
 * CSCI-201 Web Factory Factory.java Purpose: serves as the Model for the Factory simulation
 * 
 * @version 2.0
 * @since 01/12/2017
 */
public class Factory implements Serializable {
	public static final long serialVersionUID = 1;

	// server-side
	private transient Session mSession;
	private transient WebSocketEndpoint mWebSocketEndpoint;
	private transient Gson mGson = new Gson(); //Convenient library provided by Google to convert Java objects to JSON
	private transient ActionFactory mActionFactory = new ActionFactory();

	// serialized data members
	int width; // number of cells per row
	int height; // number of rows
	private String name;
	private int numberOfWorkers;
	private Vector<FactoryResource> resources;
	private Vector<FactoryProduct> products;
	private Vector<FactoryWall> walls;
	private Vector<FactoryWall2> walls2;
	private ArrayList<FactoryWorker> workers = new ArrayList<>();
	private FactoryTaskBoard taskBoard;

	// transient data members
	private transient ArrayList<FactoryObject> mFObjects = new ArrayList<>();
	private transient Map<String, FactoryNode> mFNodeMap = new HashMap<>();
	private transient FactoryNode mFNodes[][];
	private transient Point mTaskBoardLocation;

	/**
	 * Creates a default, empty factory
	 * 
	 * @param inSession
	 *            saves a reference to the session that created this factory
	 * @param inWSE
	 *            saves a reference to the WebSocketEndpoint to relay messages to the front-end
	 */
	public Factory(Session inSession, WebSocketEndpoint inWSE) {
		mSession = inSession;
		mWebSocketEndpoint = inWSE;

		name = "";
		numberOfWorkers = 0;
		width = 0;
		height = 0;
		resources = new Vector<FactoryResource>();
		products = new Vector<FactoryProduct>();
		walls = new Vector<FactoryWall>();
		walls2 = new Vector<FactoryWall2>();
	}

	/**
	 * Creates a filled factory
	 * 
	 * @param inSession
	 *            reference to the session that created this factory
	 * @param inWSE
	 *            to relay messages to the front-end
	 * @param name
	 *            of the factory
	 * @param numberOfWorkers
	 *            to generate this many workers
	 * @param width
	 *            number of cells per row
	 * @param height
	 *            number of rows in the factory
	 * @param resources
	 *            vector of FactoryResources (to be placed)
	 * @param products
	 *            vector of FactoryProducts (tasks to be fulfilled)
	 * @param taskBoardLocation
	 *            allows us to generate a FactoryTaskBoard
	 */
	public Factory(Session inSession, WebSocketEndpoint inWSE, String name, int numberOfWorkers, int width, int height,
			Vector<FactoryResource> resources, Vector<FactoryProduct> products, Point taskBoardLocation) {

		mSession = inSession;
		mWebSocketEndpoint = inWSE;

		// setup based on some information
		setName(name);
		setNumberOfWorkers(numberOfWorkers);
		setDimensions(width, height);
		setResources(resources);
		setProducts(products);

		// create the nodes of the factory
		createNodes();

		// send factory object to client
		sendFactory();
	}

	/* ***** WEBSOCKET COMMUNCATION METHODS ***** */

	/**
	 * Receives Strings from the WebSocketEndpoint and converts them into JsonObjects. From the JsonObject, we find and execute its respective action
	 * 
	 * @param rawJson
	 *            (String)
	 */
	public void listen(String rawJson) {
		JsonObject msg = mGson.fromJson(rawJson, JsonObject.class);
		Action action = mActionFactory.getAction(msg.get("action").getAsString());
		if (action != null)
			action.execute(this, msg);
	}

	/**
	 * Converts Message into a JSON String, and sends to current session via the WebSocketEndpoint
	 * 
	 * @param message
	 */
	private void send(Message message) {
		String json = mGson.toJson(message);
		mWebSocketEndpoint.sendToSession(mSession, json);
	}

	/**
	 * Sends entire Factory object to the client (use this only once!)
	 */
	public void sendFactory() {
		send(new FactoryMessage(this));
	}

	/**
	 * Sends the resources vector to the client, and its updates
	 */
	public void sendResources() {
		send(new UpdateResourcesMessage(this.resources));
	}

	/**
	 * Sends the taskboard to the client, primarily to update the ProductTable
	 */
	public void sendProductTable() {
		send(new UpdateTaskBoardMessage(this.taskBoard));
	}

	public void sendWorkerMoveToPath(FactoryWorker worker, Stack<FactoryNode> pathStack) {
		send(new WorkerMoveToPathMessage(worker, pathStack));
	}

	public void sendReports() {
		
		//loop through all instances of FactoryReporter to print report methods
		for (FactoryObject object : getObjects()) {
			if (object instanceof FactoryReporter) {
				send(new ReportMessage(((FactoryReporter) object).report()));
			}
		}
	}

	/* ***** GETTERS AND SETTERS (ENCAPSULATION) ***** */

	public void setName(String name) {
		this.name = name;
	}

	// WORKERS
	public void setNumberOfWorkers(int numberOfWorkers) {
		this.numberOfWorkers = numberOfWorkers;
	}

	public ArrayList<FactoryWorker> getWorkers() {
		return workers;
	}

	public FactoryWorker getWorker(int index) {
		return workers.get(index);
	}

	public void createWorkers() {
		//Create the workers, set their first task to look at the task board
		for (int i = 0; i < numberOfWorkers; i++) {
			//Start each worker at the first node (upper left corner)
			//Note, may change this, but all factories have an upper left corner(0,0) so it makes sense
			FactoryWorker fw = new FactoryWorker(i, mFNodes[0][0], this);
			mFObjects.add(fw);
			workers.add(fw);
		}
	}

	public void killWorkers() {
		for (FactoryWorker w : this.workers)
			w.kill();
	}

	public void createWalls() {

		for (int i = 0; i < 6; i++) {
			FactoryWall2 factoryWall = new FactoryWall2(i, 9);
			mFObjects.add(factoryWall);
			mFNodes[factoryWall.getX()][factoryWall.getY()].setObject(factoryWall);
			walls2.add(factoryWall);
		}

		InputStream is = getClass().getClassLoader().getResourceAsStream("walls");

		//File scanner
		Scanner reader = new Scanner(is);
		while (reader.hasNext()) {
			int x = reader.nextInt();
			int y = reader.nextInt();
			String file = reader.next();

			FactoryWall factoryWall = new FactoryWall(x, y, file);
			mFObjects.add(factoryWall);
			mFNodes[factoryWall.getX()][factoryWall.getY()].setObject(factoryWall);
			walls.add(factoryWall);
		}
		if (reader != null) {
			reader.close();
		}
	}

	// DIMENSIONS
	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	// NODES
	FactoryNode[][] getNodes() {
		return mFNodes;
	}

	public FactoryNode getNode(String key) {
		return mFNodeMap.get(key);
	}

	/**
	 * Generates the 2-D array of FactoryNodes, and sets up all the objects that will show up on the Factory simulation board
	 */
	public void createNodes() {
		mFNodes = new FactoryNode[width][height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				mFNodes[x][y] = new FactoryNode(x, y);
				mFObjects.add(mFNodes[x][y]);
			}
		}

		linkNeighbors();
		createResources();
		createTaskBoard(mTaskBoardLocation);
		createWalls();
		createWorkers();
	}

	/**
	 * Link nodes to its neighbors to make A* PathFinding easier
	 */
	public void linkNeighbors() {
		for (FactoryNode[] nodes : mFNodes) {
			for (FactoryNode node : nodes) {
				int x = node.getX();
				int y = node.getY();
				if (x != 0)
					node.addNeighbor(mFNodes[x - 1][y]);
				if (x != width - 1)
					node.addNeighbor(mFNodes[x + 1][y]);
				if (y != 0)
					node.addNeighbor(mFNodes[x][y - 1]);
				if (y != height - 1)
					node.addNeighbor(mFNodes[x][y + 1]);
			}
		}
	}

	// TASKBOARD
	public void setTaskBoardLocation(Point taskBoardLocation) {
		mTaskBoardLocation = taskBoardLocation;
	}

	public FactoryTaskBoard getTaskBoard() {
		return taskBoard;
	}

	/**
	 * Not only do we set the taskBoard to its location, we also pass in the products since the TaskBoard will maintain the database of product building progress
	 * 
	 * @param taskBoardLocation
	 */
	public void createTaskBoard(Point taskBoardLocation) {
		taskBoard = new FactoryTaskBoard(this, products, taskBoardLocation.x, taskBoardLocation.y);
		mFObjects.add(taskBoard);
		mFNodes[taskBoardLocation.x][taskBoardLocation.y].setObject(taskBoard);
		mFNodeMap.put("Task Board", mFNodes[taskBoardLocation.x][taskBoardLocation.y]);
	}

	// RESOURCE
	public void addResource(FactoryResource resource) {
		if (resources == null)
			resources = new Vector<>();
		resources.add(resource);
	}

	public void setResources(Vector<FactoryResource> resources) {
		this.resources = new Vector<>();
		resources.forEach(this::addResource);
	}

	public void createResources() {
		// create the resources
		for (FactoryResource resource : resources) {
			mFObjects.add(resource);
			mFNodes[resource.getX()][resource.getY()].setObject(resource);
			mFNodeMap.put(resource.getName(), mFNodes[resource.getX()][resource.getY()]);
		}
	}

	// PRODUCT
	public void addProduct(FactoryProduct product) {
		if (products == null)
			products = new Vector<>();
		products.add(product);
	}

	public void setProducts(Vector<FactoryProduct> products) {
		this.products = new Vector<>();
		products.forEach(this::addProduct);
	}

	// OBJECTS
	ArrayList<FactoryObject> getObjects() {
		return mFObjects;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(Constants.factoryNameString + ": ").append(name);
		for (FactoryResource resource : resources) {
			sb.append("\n");
			sb.append("\t").append(resource);
		}
		for (FactoryProduct product : products) {
			sb.append("\n");
			sb.append("\t").append(product);
		}
		return sb.toString();
	}

}
