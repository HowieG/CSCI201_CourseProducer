package client;

//PE1
//see Lab 2: FactoryWall.java for reference
public class ManagerOffice extends FactoryObject {
	
	private static final long serialVersionUID = 1L;

	//set ManagerOffice name, image, and location using FactoryObject constructor
	public ManagerOffice(int x, int y) {
		super("Manager Office", "ManagerOffice" + Constants.png, x, y);
	}

	public String getName() {
		return name;
	}
}
