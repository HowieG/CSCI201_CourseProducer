package actions;

import com.google.gson.JsonObject;

import client.Factory;

public class ResetFactoryAction extends Action {

	@Override
	public void execute(Factory factory, JsonObject msg) {
		
		factory.reset();
	}
}
