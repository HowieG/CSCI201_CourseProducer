/*
 * FactoryWorker.java:
	•	createWorkers() only does three things, one of which is create a new FactoryWorker.
	•	The important thing here is the run() method. You will learn about threads, but basically each FactoryWorker is its own thread, which means they can all execute their run() methods at what appears to be the same time.
	•	Note: you will not be responsible for threading, however you should be able to manipulate the code inside thread-related code. For example, you have edited code inside of locks (myLock.lock() -> some code -> myLock.unlock(). Therefore you should be able to manipulate code inside of run() and similar methods, excluding atLocation.await(), Thread.sleep(1), etc.
 
 	run():
	•	Notice the try block is a big while loop. It may seem that the loop will execute very frequently, but there are conditions inside the loop that the worker will wait on (again, outside of your scope right now).
	•	Assigning the worker a task:
		o	The if statement ensures you only assign the worker a task if they don’t have a product to make.
		o	mDestinationNode…: assigns the task board as the destination node
		o	mShortestPath…: calls FactoryNode’s findShortestPath() method to find the shortest path to the task board
		o	mFactory.sendWorkerMoveToPath(): calls Factory’s sendWorkerMoveToPath() method to send a message to the front-end client that the worker has been assigned a path (see sendWorkerMoveToPath() below).
		o	atLocation.await(): the worker waits for a signal from the front-end client that it has finished animating/moving the worker to its destination. This happens in JavaScript, remember the MVC design pattern.
		o	while (!mDestinationNode.aquireNode()) { Thread.sleep(1); }: the worker waits in line for the task board to be free if there are other workers at the task board.
		o	mProductToMake…: since we have exited the above while loop, we know we are at the task board. The task board assigns the worker a task.
		o	Thread.sleep(1000): the task board takes one second to assign a task to a worker.
		o	mDestinationNode.releaseNode(): the task board is now free so the next worker can use it (outside of your scope).

	•	Building the product:
		o	A product is composed of multiple resources. Just like assigning a task, a path to each resource in the product is sent to the client (mDestinationNode() -> mShortestPath() -> mFactory. sendWorkerMoveToPath()).
		o	atLocation.await() ensures that the code will not continue until the worker receives a signal that it is at the desired resource.
		o	FactoryResource toTake…: gets resource at the destination node (which we know is a resource node)
		o	toTake.takeResource(): calls FactoryResource’s takeResource() method to decrement the number of that resource still available.
		o	mFactory.sendResources(): calls Factory’s sendResources() method (see sendWorkerMoveToPath()).
	•	Updating the task board table:
		o	mDestinationNode… -> mShortestPath -> mFactory. sendWorkerMoveToPath()  -> atLocation.await() tells the front-end to send the worker to the table and waits for a signal that this has happened.
		o	mFactory.getTaskBoard().endTask(mProductToMake): calls the FactoryTaskBoard’s endTask() method which updates
		o	mFactory.getTaskBoard().endTask(): worker no longer has a product to make, so we set it to null to prepare for the next loop of the while loop.
 
 	sendWorkerMoveToPath():
	•	All the send___() methods in the factory code all do the same thing, which is send a message to the client.
	•	send(new WorkerMoveToPathMessage(worker, pathStack)): calls Factory’s send() method, which just takes a Message.

 */

package client;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * CSCI-201 Web Factory
 * FactoryWorker.java
 * Each FactoryWorker instance must have a purpose (task) otherwise it will die.
 * It always reports to the TaskBoard whenever it
 * 	- completes a task
 * 	- needs a new task to begin
 * As it runs, it will ask to acquire resources necessary to build the FactoryProduct
 * At each stage, it will send data to the front-end and wait for the front-end to notify
 * it that an animation has completed.
 * 
 * @version 2.0 
 * @since 01/11/2017
 */
public class FactoryWorker extends FactoryObject implements Runnable {
	private static final long serialVersionUID = 1L;

	// serialized data member
	public final int number;

	// references
	private transient Factory mFactory;
	private transient FactoryProduct mProductToMake;

	// threading
	private transient Lock mLock;
	private transient Condition atLocation;
	private transient Thread thread;

	// path finding
	private FactoryNode currentNode;
	private transient FactoryNode mDestinationNode;
	private transient Stack<FactoryNode> mShortestPath;
	
	//PE1
	private transient boolean mustSeeManager = false;
	private transient static List<String> workerNames = Arrays.asList("Alana", "Bob", "Caleb", "Dana", "Edgar", "Frank", "Gary");

	// instance constructor
	{
		mLock = new ReentrantLock();
		atLocation = mLock.newCondition();
	}

	/**
	 * Create the FactoryWorker
	 * @param inNumber the index of the FactoryWorker
	 * @param startNode 
	 * @param inFactory
	 */
	public FactoryWorker(int inNumber, FactoryNode startNode, Factory inFactory) {
		super(workerNames.get(inNumber), "Worker" + Constants.png);
		number = inNumber;
		currentNode = startNode;
		mFactory = inFactory;
		thread = new Thread(this);
		thread.start();
	}
	
	//PE1
	//function just for encapsulation
	public void mustSeeManager() {
		this.mustSeeManager = true;
	}
	

	/** 
	 * Use a separate thread for expensive operations:
	 * - path finding
	 * - making objects
	 * - waiting
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		mLock.lock();
		try {
			while (true) {
				
				//PE1
				//sends worker to manager office once done with product if mustSeeManager is true 
				if(mustSeeManager) {
					//sets the destination node to the node named "Manager Office"
					mDestinationNode = mFactory.getNode("Manager Office");
					//gets the shortest path to the manager office node
					mShortestPath = currentNode.findShortestPath(mDestinationNode);
					//sends a message to the client to move this worker via the shortest path
					mFactory.sendWorkerMoveToPath(this, mShortestPath);
					//the following were given
					//backend waits for worker to reach manager office in frontend
					atLocation.await();
					//manager talks to worker for one second
					Thread.sleep(1000);
					//mustSeeManager set to false so worker won't repeatedly go to manager office after completing product
					this.mustSeeManager = false;
				}

				// if factoryWorker doesn't already have a purpose, let's assign it one
				if (mProductToMake == null) {
					// get an assignment from the table
					mDestinationNode = mFactory.getNode("Task Board");
					mShortestPath = currentNode.findShortestPath(mDestinationNode);
					// SEND SHORTEST PATH TO FRONT END
					mFactory.sendWorkerMoveToPath(this, mShortestPath);
					// all workers need to go to the Task Board in order to
					// acquire a task
					atLocation.await(); // waiting for animation
					// don't do anything if the destination node is currently
					// locked
					while (!mDestinationNode.aquireNode())
					Thread.sleep(1);
					// get the next task in the queue
					mProductToMake = mFactory.getTaskBoard().getTask();
					Thread.sleep(1000); // worker takes 1 second to learn task
					// unlock node for future workers to use
					mDestinationNode.releaseNode();
					if (mProductToMake == null)
					break; // No more tasks, end here
				}

				// build the product
				for (FactoryResource resource : mProductToMake.getResourcesNeeded()) {
					mDestinationNode = mFactory.getNode(resource.getName());
					mShortestPath = currentNode.findShortestPath(mDestinationNode);
					// SEND SHORTEST PATH TO FRONT END
					mFactory.sendWorkerMoveToPath(this, mShortestPath);
					atLocation.await(); // waiting for animation
					FactoryResource toTake = (FactoryResource) mDestinationNode.getObject();
					toTake.takeResource(resource.getQuantity());
					// SEND UPDATED RESOURCES TO FRONT END
					mFactory.sendResources();
				}

				// update JTable by going back to the task board and ending the task
				mDestinationNode = mFactory.getNode("Task Board");
				mShortestPath = currentNode.findShortestPath(mDestinationNode);
				// SEND SHORTEST PATH TO FRONT END
				mFactory.sendWorkerMoveToPath(this, mShortestPath);
				atLocation.await(); // waiting for animation
				mFactory.getTaskBoard().endTask(mProductToMake);
				mProductToMake = null;
			}
		} catch (InterruptedException e) {
			// e.printStackTrace();
			// Expected to be interrupted when websocket connection ends
			System.out.println("A worker has been interrupted");
		}
		mLock.unlock();
	}

	/**
	 * Interrupts the WorkerThread, which is necessary when the WebSocket session ends
	 */
	public void kill() {
		thread.interrupt();
	}

	/**
	 * Signifies that the FactoryWorker has arrived at its destination,
	 * so it should tell the Thread to continue
	 * @param x
	 * @param y
	 */
	public void atLocationSignal(int x, int y) {
		mLock.lock();
		currentNode = mFactory.getNodes()[x][y];
		atLocation.signal();
		mLock.unlock();
	}

}
