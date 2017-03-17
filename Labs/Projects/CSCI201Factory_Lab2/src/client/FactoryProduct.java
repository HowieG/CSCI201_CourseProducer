package client;

import java.util.Vector;
import utilities.Constants;

/**
 * CSCI-201 Web Factory
 * FactoryProduct.java
 * Purpose: FactoryProduct holds an array of Resources that are needed to build the product.
 * 
 * @version 2.0
 * @since 01/12/2017
 */
public class FactoryProduct extends FactoryObject {
	private static final long serialVersionUID = 1L;

	private int quantity;
	private Vector<FactoryResource> resourcesNeeded;

	// CONSTRUCTORS
	public FactoryProduct() {
		super("", null);
		setQuantity(0);
		resourcesNeeded = new Vector<>();
	}
	public FactoryProduct(String name, int quantity, Vector<FactoryResource> resourcesNeeded) {
		super(name, null);
		setQuantity(quantity);
		setResourcesNeeded(resourcesNeeded);
	}
	
	public void setName(String name) {
		this.name = name;
	}

	// QUANTITY
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		if (quantity < 0) {
			this.quantity = 0;
		}
		this.quantity = quantity;
	}

	// RESOURCES NEEDED
	public Vector<FactoryResource> getResourcesNeeded() {
		return resourcesNeeded;
	}
	public void setResourcesNeeded(Vector<FactoryResource> resourcesNeeded) {
		this.resourcesNeeded = new Vector<>();
		resourcesNeeded.forEach(this::addResourceNeeded);
	}
	public void addResourceNeeded(FactoryResource resource) {
		if (resourcesNeeded == null) resourcesNeeded = new Vector<>();
		resourcesNeeded.add(resource);
	}

	// UTILITIES
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(Constants.productString + ": ").append(name).append(" needs quantity ").append(quantity);
		for (FactoryResource resource : resourcesNeeded) {
			sb.append("\n");
			sb.append("\t\t").append(resource.toString());
		}
		return sb.toString();
	}

}
