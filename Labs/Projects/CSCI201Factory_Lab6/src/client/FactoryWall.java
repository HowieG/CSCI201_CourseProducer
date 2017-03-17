package client;

public class FactoryWall extends FactoryObject {
	
	private static final long serialVersionUID = 1L;

	public FactoryWall(int x, int y) {
		super(Constants.wallString, "Wall" + Constants.png, x, y);
	}

	public FactoryWall(int x, int y, String file) {
		super(Constants.wallString, file, x, y);
	}
}
