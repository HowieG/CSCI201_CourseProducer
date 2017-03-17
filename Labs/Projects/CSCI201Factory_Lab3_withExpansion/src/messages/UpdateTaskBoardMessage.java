package messages;

import client.FactoryTaskBoard;

public class UpdateTaskBoardMessage extends Message {
	private static final long serialVersionUID = 1L;
	public FactoryTaskBoard taskBoard;
	
	public UpdateTaskBoardMessage(FactoryTaskBoard taskBoard) {
		this.action = "UpdateTaskBoard";
		this.taskBoard = taskBoard;
	}
}
