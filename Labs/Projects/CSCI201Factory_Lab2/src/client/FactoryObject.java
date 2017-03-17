package client;

import java.io.Serializable;

public abstract class FactoryObject implements Serializable {
	private static final long serialVersionUID = 1L;

	public String name;
	public String image;
	private int x,y;

	public FactoryObject(String name, String image, int x, int y) {
		this.name = name;
		this.image = image;
		this.x = x;
		this.y = y;
	}
	
	public FactoryObject(String name, String image) {
		this.name = name;
		this.image = image;
	}

	// POSITION
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}
