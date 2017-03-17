package client;

import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * CSCI-201 Web Factory
 * FactoryNodes.java
 * Purpose: The Factory will contain an 2D array of FactoryNodes, 
 * which have an implementation of A* search. Nodes can also contain a single object
 * (such as a wall) that blocks traversal.
 * 
 * @version 2.0 
 * @since 01/11/2017
 */
public class FactoryNode extends FactoryObject {
	private static final long serialVersionUID = 1L;

	// position in the factory simulation table
//	private final int x;
//	private final int y;
	// Each node can own a FactoryObject, doing so will render it in its place
	private FactoryObject objectHeld;

	// This lock is used to lock the node or the object within the node
	private transient Lock nodeLock;
	// Used for path finding
	private transient ArrayList<FactoryNode> mNeighbors;

	// CONSTRUCTOR
	{
		objectHeld = null;
		nodeLock = new ReentrantLock();
		mNeighbors = new ArrayList<>();
	}
	public FactoryNode(int x, int y) {
		super(null, null, x, y);
//		this.x = x;
//		this.y = y;
	}

	// OBJECT
	FactoryObject getObject() {
		return objectHeld;
	}
	public void setObject(FactoryObject inFObject) {
		objectHeld = inFObject;
	}

	// POSITION
//	public int getX() {
//		return x;
//	}
//	public int getY() {
//		return y;
//	}

	// NEIGHBORS
	public void addNeighbor(FactoryNode neighbor) {
		mNeighbors.add(neighbor);
	}
	public ArrayList<FactoryNode> getNeighbors() {
		return mNeighbors;
	}

	// LOCKING
	boolean aquireNode() {
		return nodeLock.tryLock();
	}
	void releaseNode() {
		nodeLock.unlock();
	}

	/* ***** Below is the methods for path finding ***** */

	private int heuristicCostEstimate(FactoryNode factoryNode) {
		//Manhattan distance between the nodes
		//This method assumes we are path finding TO "this"
		return (Math.abs(this.getX() - factoryNode.getX()) + Math.abs(this.getY() - factoryNode.getY()));
	}

	private PathNode lowestFScore(ArrayList<PathNode> openList) {
		PathNode toReturn = null;
		int lowest = Integer.MAX_VALUE;
		for (PathNode pn : openList) {
			if (pn.fScore < lowest) {
				toReturn = pn;
				lowest = pn.fScore;
			}
		}
		return toReturn;
	}

	private Stack<FactoryNode> makePath(PathNode start, PathNode end) {
		Stack<FactoryNode> shortestPath = new Stack<>();
		PathNode current = end;
		shortestPath.add(end.fNode);
		while (current.fNode != start.fNode) {
			shortestPath.add(current.parent.fNode);
			current = current.parent;
		}
		return shortestPath;
	}

	private PathNode containsNode(ArrayList<PathNode> list, FactoryNode node) {
		for (PathNode pn : list) 
			if (pn.fNode == node) 
				return pn;
		return null;
	}

	/** 
	 * An implementation of A* path finding
	 * @param mDestinationNode
	 * @return shortest path as a Stack of FactoryNodes
	 */
	Stack<FactoryNode> findShortestPath(FactoryNode mDestinationNode) {
		ArrayList<PathNode> openList = new ArrayList<>();
		ArrayList<PathNode> closedList = new ArrayList<>();

		PathNode start = new PathNode(this, 0, 0, 0, null);
		openList.add(start);

		while (!openList.isEmpty()) {
			PathNode current = lowestFScore(openList);
			
			// base case
			if (current.fNode == mDestinationNode) return makePath(start, current);
			
			
			openList.remove(current);
			closedList.add(current);
			for (FactoryNode neighbor : current.fNode.mNeighbors) {
				// check if the the neighbor is blocked
				if (neighbor.getObject() != null)
					if (neighbor != mDestinationNode) continue;
				// check if the neighbor has been accessed by a shorter path already
				if (containsNode(closedList, neighbor) != null) continue;
				int temp_gScore = current.gScore + 1; //nodes always have distance 1 in our case
				PathNode neighborPathNode = containsNode(openList, neighbor);
				if (neighborPathNode == null || (temp_gScore < neighborPathNode.gScore)) {
					if (neighborPathNode == null) neighborPathNode = new PathNode();
					neighborPathNode.fNode = neighbor;
					neighborPathNode.parent = current;
					neighborPathNode.gScore = temp_gScore;
					neighborPathNode.hScore = heuristicCostEstimate(neighbor);
					neighborPathNode.fScore = neighborPathNode.gScore + neighborPathNode.hScore;
					if (containsNode(openList, neighbor) == null)
						openList.add(neighborPathNode);
				}
			}
		}
		return null; //no path exists
	}
	
	
	/**
	 * We need a wrapper class for path finding, otherwise
	 * two threads can't path-find at the same time.
	 */
	class PathNode {
		FactoryNode fNode; //the actual node
		int gScore; //cost from the start of the best known path
		int hScore; //Manhattan distance to the end
		int fScore; //g+h
		PathNode parent;
		
		PathNode() { /* do nothing */ }
		
		PathNode(FactoryNode factoryNode, int g, int h, int f, PathNode parent) {
			this.fNode = factoryNode;
			this.gScore = g;
			this.hScore = h;
			this.fScore = f;
			this.parent = parent;
		}
	}

}
