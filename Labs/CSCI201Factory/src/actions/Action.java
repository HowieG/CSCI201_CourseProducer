/*
 * Action.java:
	•	Abstract class that just declares an abstract execute() method. Similar to the abstract Message.java class.
 */

package actions;

import com.google.gson.JsonObject;

import client.Factory;

public abstract class Action {
	public abstract void execute(Factory factory, JsonObject msg);
}
