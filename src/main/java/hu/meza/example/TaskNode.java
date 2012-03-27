package hu.meza.example;

import hu.meza.CircuitBreaker;
import hu.meza.Response;

class TaskNode implements Runnable {

	CircuitBreaker cb;
	String pref;

	public TaskNode(String prefix, CircuitBreaker cb) {
		this.cb = cb;
		this.pref = prefix;
	}

	@Override
	public void run() {
		for (int i = 0; i < 100; i++) {
			String param = this.pref + i;

			String s = "Calling: http://api.example.com/get_data?param=" + param + " ";
			Response rsp = cb.execute(new ExampleNetworkRequestCommand(param));
			if (rsp.success()) {
				System.out.println(s+"200 OK");
			} else {
				System.out.println(s+"Error: "+rsp.exception().getClass().getSimpleName());
			}
		}
	}
}
