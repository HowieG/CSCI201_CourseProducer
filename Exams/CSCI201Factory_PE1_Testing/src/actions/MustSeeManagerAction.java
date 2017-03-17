package actions;

import com.google.gson.JsonObject;

import client.Factory;

//PE1
//triggers the specified worker's mustSeeManager bool to be true
//see: WorkerArrivedAtDesination.java for reference
public class MustSeeManagerAction extends Action {

	@Override
	public void execute(Factory factory, JsonObject msg) {
		//create an int workerIndex equal to workerNumber from the received msg
		int workerIndex = msg.get("workerNumber").getAsInt();

		//call the worker's mustSeeManager() method which sets its mustSeeManager bool to true
		factory.getWorker(workerIndex).mustSeeManager();
	}

}
