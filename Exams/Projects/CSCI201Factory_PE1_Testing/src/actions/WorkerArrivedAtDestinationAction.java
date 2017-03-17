/*
 * WorkerArrivedAtDestinationAction.java:
	•	Defines its abstract execute() method.
	•	int workerIndex…: gets the index of the worker that sent the message. Recall that the FactoryWorker.js sent its corresponding FactoryWorker.java object (from factoryData.workers) in the message, and FactoryWorker.java has a data member “public final int number.”
	•	int x…: gets the x value of the sent currentNode which is a FactoryNode.java.
	•	factory.getWorker(workerIndex).atLocationSignal(x, y); signals the FactoryWorker that it has arrived at its destination, which if you recall allows the while loop in their run() method to continue and get a new destination.
 */

package actions;

import com.google.gson.JsonObject;

import client.Factory;

/**
 * CSCI-201 Web Factory
 * WorkerArrivedAtDestinationAction.java
 * Purpose: When the FactoryWorker on the front-end arrives at its destination
 * we will notify the FactoryWorker on the back-end to send us the next task/path
 * 
 * @version 2.0 
 * @since 01/12/2017
 */
public class WorkerArrivedAtDestinationAction extends Action {

	@Override
	public void execute(Factory factory, JsonObject msg) {
		int workerIndex = msg.get("worker").getAsJsonObject().get("number").getAsInt();
		int x = msg.get("currentNode").getAsJsonObject().get("x").getAsInt();
		int y = msg.get("currentNode").getAsJsonObject().get("y").getAsInt();

		// signal thread
		factory.getWorker(workerIndex).atLocationSignal(x, y);
	}

}
