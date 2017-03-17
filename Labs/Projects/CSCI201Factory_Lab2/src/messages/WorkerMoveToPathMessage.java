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
