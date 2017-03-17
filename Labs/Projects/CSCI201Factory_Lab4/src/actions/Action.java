package actions;

import com.google.gson.JsonObject;

import client.Factory;

public abstract class Action {
	public abstract void execute(Factory factory, JsonObject msg);
}
