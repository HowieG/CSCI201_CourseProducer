package client;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * CSCI-201 Web Factory FactoryTaskBoard.java Purpose: FactoryTaskBoard manages the local database of products and tasks Uses locks to prevent race conditions.
 * 
 * @version 2.0
 * @since 01/12/2017
 */
public class FactoryTaskBoard extends FactoryObject {
	private static final long serialVersionUID = 1L;
	private int totalProducts;
	private int productsMade;

	// data table
	private Vector<Vector<Object>> workerTableDataVector;
	private Vector<Object> workerTableColumnNames;

	private transient Factory mFactory;
	// Lock for accessing the table
	private transient Lock mLock;
	// products the workers must build
	private transient Queue<FactoryProduct> mProducts;
	
	private transient Vector<FactoryProduct> completedProducts;

	//instance constructor
	{
		workerTableColumnNames = new Vector<>();
		workerTableDataVector = new Vector<>();
		mProducts = new LinkedList<>();
		mLock = new ReentrantLock();
	}

	public FactoryTaskBoard(Factory inFactory, Vector<FactoryProduct> inProducts, int x, int y) {
		super("Task Board", "Taskboard" + Constants.png, x, y);
		this.mFactory = inFactory;

		//Add the information to the task board
		for (FactoryProduct product : inProducts) {
			for (int i = 0; i < product.getQuantity(); i++) {
				mProducts.add(product);
			}
		}

		Collections.addAll(workerTableColumnNames, Constants.tableColumnNames);
		for (FactoryProduct product : inProducts) {
			Vector<Object> productRow = new Vector<>();
			synchronized (this) {
				productRow.add(product.name); //Name of product
				productRow.add(product.getQuantity()); //How many to make
				productRow.add(0); //None in progress
				productRow.add(0); //None completed
				workerTableDataVector.add(productRow);
			}
		}
		productsMade = 0;
		totalProducts = mProducts.size();
		
		completedProducts = new Vector<FactoryProduct>();
	}

	/**
	 * Gets task, updates table, and removes a FactoryProduct from its queue
	 * 
	 * @return a FactoryProduct that the FactoryWorker is meant to build
	 */
	FactoryProduct getTask() {
		mLock.lock();
		FactoryProduct toAssign;
		if (mProducts.isEmpty()) {
			mLock.unlock();
			return null;
		}
		toAssign = mProducts.remove();
		for (Vector<Object> vect : workerTableDataVector) {
			String name = (String) vect.get(Constants.productNameIndex);
			if (name.equals(toAssign.name)) {
				vect.setElementAt((int) vect.get(Constants.startedIndex) + 1, Constants.startedIndex);
				break;
			}
		}
		updateWorkerTable();
		mLock.unlock();
		return toAssign;
	}

	/**
	 * After FactoryWorker reports to TaskBoard and completes the task, increment the # of products made, and update hte table.
	 * 
	 * @param productMade
	 */
	void endTask(FactoryProduct productMade) {
		mLock.lock();
		productsMade++;
		for (Vector<Object> vect : workerTableDataVector) {
			String name = (String) vect.get(Constants.productNameIndex);
			if (name.equals(productMade.name)) {
				vect.setElementAt((int) vect.get(Constants.startedIndex) - 1, Constants.startedIndex);
				vect.setElementAt((int) vect.get(Constants.completedIndex) + 1, Constants.completedIndex);
				break;
			}
		}
		updateWorkerTable();
		
		//tracks each product as its completed by the worker
		completedProducts.add(productMade);

		//if simulation over, call report() for all FactoryReporters
		if (isDone()) {
			//send completedProducts vector for sendReports to loop through
			mFactory.sendReports(completedProducts);
		}

		mLock.unlock();
	}

	private synchronized void updateWorkerTable() {
		mLock.lock();
		// send to client
		mFactory.sendProductTable();
		mLock.unlock();
	}

	public boolean isDone() {
		return (productsMade == totalProducts);
	}

}
