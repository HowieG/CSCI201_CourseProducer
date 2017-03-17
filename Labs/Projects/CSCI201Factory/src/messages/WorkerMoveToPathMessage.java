/*
 * WorkerMoveToPathMessage:
	•	Like all Messages, its only method is its constructor, which takes in data as its parameters (in this case, a FactoryWorker and a Stack holding its path). All the constructor does is set the corresponding data members, so that when the message is serialized the receiver can access it.
 */

package messages;

import java.util.Stack;

import client.FactoryNode;
import client.FactoryWorker;

public class WorkerMoveToPathMessage extends Message {
	private static final long serialVersionUID = 1L;
	public FactoryWorker worker;
	public Stack<FactoryNode> shortestPathStack;

	public WorkerMoveToPathMessage(FactoryWorker worker, Stack<FactoryNode> shortestPath) {
		this.action = "WorkerMoveToPath";
		this.worker = worker;
		this.shortestPathStack = shortestPath;
	}
}
