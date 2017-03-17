/*
 * ActionFactory.java:
	•	Adds Actions to its actionMap, a HashMap of supported actions. Pretty straightforward especially since “WorkerArrivedAtDestination” is the only supported Action.
 */

package actions;

import java.util.HashMap;

/**
 * CSCI-201 Web Factory
 * ActionFactory.java
 * Purpose: Essentially acts like a database for Actions
 * 
 * @version 2.0 
 * @since 01/11/2017
 */
public class ActionFactory {

	private static HashMap<String, Action> actionMap;

	{
		if (actionMap == null) {
			actionMap = new HashMap<>();
			actionMap.put("WorkerArrivedAtDestination", new WorkerArrivedAtDestinationAction());
			//PE1
			//putting the MustSeeManagerAction() in the map of valid actions
			actionMap.put("MustSeeManager", new MustSeeManagerAction());
		}
	}

	/**
	 * @param messageClass (String), aka name of action
	 * @return Action attributed to the messageClass
	 */
	public Action getAction(String messageClass) {
		return actionMap.get(messageClass);
	}

}
