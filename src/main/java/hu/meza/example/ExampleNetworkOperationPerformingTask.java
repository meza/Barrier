package hu.meza.example;

import hu.meza.CircuitBreaker;
import hu.meza.Command;
import hu.meza.Response;

class ExampleNetworkOperationPerformingTask implements Runnable {

	CircuitBreaker circuitBreaker;
	String prefix;

	public ExampleNetworkOperationPerformingTask(String prefix, CircuitBreaker circuitBreaker) {
		this.circuitBreaker = circuitBreaker;
		this.prefix = prefix;
	}

	@Override
	public void run() {

		for (int i = 0; i < 50; i++) {

			String param = this.prefix + i;

			Command commandToExecute = new ExampleNetworkRequestCommand(param);

			Response rsp = circuitBreaker.execute(commandToExecute);

			handleResponse(param, rsp);
		}

	}

	private void handleResponse(String param, Response rsp) {

		String url = "Called: http://api.example.com/get_data?param=" + param + " with result: ";
		if (rsp.success()) {
			System.out.println(url+"200 OK");
		} else {
			System.out.println(url+"Error: "+rsp.exception().getClass().getSimpleName());
		}
	}
}
