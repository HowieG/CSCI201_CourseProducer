package client;

import java.util.Stack;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import java.sql.Timestamp;

/**
 * CSCI-201 Web Factory FactoryWorker.java Each FactoryWorker instance must have a purpose (task) otherwise it will die. It always reports to the TaskBoard whenever it - completes a task - needs a new
 * task to begin As it runs, it will ask to acquire resources necessary to build the FactoryProduct At each stage, it will send data to the front-end and wait for the front-end to notify it that an
 * animation has completed.
 * 
 * @version 2.0
 * @since 01/11/2017
 */
public class FactoryWorker extends FactoryObject implements Runnable, FactoryReporter {
	private static final long serialVersionUID = 1L;

	// serialized data member
	public final int number;

	// references
	private transient Factory mFactory;
	private transient FactoryProduct mProductToMake;
	private transient FactoryNode mStartNode;

	// threading
	private transient Lock mLock;
	private transient Condition atLocation;
	private transient Thread thread;

	// path finding
	private FactoryNode currentNode;
	private transient FactoryNode mDestinationNode;
	private transient Stack<FactoryNode> mShortestPath;
	
	private Timestamp finished;
	
	{
		mLock = new ReentrantLock();
		atLocation = mLock.newCondition();
	}

	/**
	 * Create the FactoryWorker
	 * 
	 * @param inNumber
	 *            the index of the FactoryWorker
	 * @param startNode
	 * @param inFactory
	 */
	public FactoryWorker(int inNumber, FactoryNode startNode, Factory inFactory) {
		super(Constants.workerString + String.valueOf(inNumber), "Worker" + Constants.png);
		mStartNode = startNode;
		number = inNumber;
		mFactory = inFactory;
		this.start();
	}
	
	public FactoryWorker(FactoryWorker toClone) {
		super(Constants.workerString + String.valueOf(toClone.number), "Worker" + Constants.png);
		mStartNode = toClone.mStartNode;
		number = toClone.number;
		mFactory = toClone.mFactory;
		this.start();
	}
	
	public void start() {
		currentNode = mStartNode;
		mProductToMake = null;
		mDestinationNode = null;
		mShortestPath = null;
		thread = new Thread(this);
		thread.start();
	}

	/**
	 * Use a separate thread for expensive operations: - path finding - making objects - waiting
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		mLock.lock();
		try {
			while (true) {

				// if factoryWorker doesn't already have a purpose, let's assign it one
				if (mProductToMake == null) {
					// get an assignment from the table
					mDestinationNode = mFactory.getNode("Task Board");
					mShortestPath = currentNode.findShortestPath(mDestinationNode);
					// SEND SHORTEST PATH TO FRONT END
					mFactory.sendWorkerMoveToPath(this, mShortestPath);
					// all workers need to go to the Task Board in order to acquire a task
					atLocation.await(); // waiting for animation
					// don't do anything if the destination node is currently locked
					while (!mDestinationNode.aquireNode()) {
						Thread.sleep(1);
					}
					// get the next task in the queue
					mProductToMake = mFactory.getTaskBoard().getTask();
					Thread.sleep(1000); // worker takes 1 second to learn task
					// unlock node for future workers to use
					mDestinationNode.releaseNode();
					if (mProductToMake == null) {
						break; // No more tasks, end here
					}
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

				// update table by going back to the task board and ending the task
				mDestinationNode = mFactory.getNode("Task Board");
				mShortestPath = currentNode.findShortestPath(mDestinationNode);
				// SEND SHORTEST PATH TO FRONT END
				mFactory.sendWorkerMoveToPath(this, mShortestPath);
				atLocation.await(); // waiting for animation
				finished = new Timestamp(System.currentTimeMillis());
				mFactory.getTaskBoard().endTask(mProductToMake);
				mProductToMake = null;
			}
		} catch (InterruptedException e) {
			// e.printStackTrace();
			// Expected to be interrupted when websocket connection ends
			System.out.println("Worker " + number + " has been interrupted");
		}
		mLock.unlock();
	}

	/**
	 * Interrupts the WorkerThread, which is necessary when the WebSocket session ends
	 */
	public void kill() {
		atLocation = mLock.newCondition();
		thread.interrupt();
	}

	/**
	 * Signifies that the FactoryWorker has arrived at its destination, so it should tell the Thread to continue
	 * 
	 * @param x
	 * @param y
	 */
	public void atLocationSignal(int x, int y) {
		mLock.lock();
		currentNode = mFactory.getNodes()[x][y];
		atLocation.signal();
		mLock.unlock();
	}

	@Override
	public String report() {	
		String report = "Worker " + number + " finished at " + finished;
		System.out.println(report);
		return report;}
}
