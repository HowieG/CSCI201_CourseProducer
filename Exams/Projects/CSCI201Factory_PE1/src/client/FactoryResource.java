package client;

/**
 * CSCI-201 Web Factory
 * FactoryResource.java
 * Purpose: FactoryResource holds information about its quantity
 * 
 * @version 2.0
 * @since 01/12/2017
 */
public class FactoryResource extends FactoryObject {
	private static final long serialVersionUID = 1L;
	
	private int quantity;

	// CONSTRUCTORS
	public FactoryResource(Factory inFactory, String name, int quantity, int x, int y) {
		super(name, name + Constants.png, x, y);
		setName(name);
		setQuantity(quantity);
	}
	public FactoryResource(String name, int quantity) {
		super(name, name + Constants.png);
		setQuantity(quantity);
	}

	// NAME
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	// QUANTITY
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	void takeResource(int amount) {
		setQuantity(this.quantity - amount);
	}

	// UTILITIES
	public String toString() {
		return utilities.Constants.resourceString + ": " + name + " has quantity " + quantity;
	}
	public boolean equals(FactoryResource resource) {
		return (getName().equals(resource.getName()));
	}
}
