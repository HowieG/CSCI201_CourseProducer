package client;

public class FactoryWall2 extends FactoryObject {

	private static final long serialVersionUID = 1L;

	public FactoryWall2(int x, int y) {
		super(Constants.wallString, "Wall2" + Constants.png, x, y);
	}
}
