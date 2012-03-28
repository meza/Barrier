package hu.meza.example;

import hu.meza.CircuitBreaker;

public class CBExample {


	public static void main(String[] args) throws InterruptedException {
		Thread th1;
		Thread th2;
		Thread th3;
		Thread th4;

		CircuitBreaker cb = new CircuitBreaker(new ExampleCoolDownStrategy(), new ExampleTriggerStrategy());

		 for (int i = 0; i < 10; i++) {
			th1 = new Thread(new ExampleNetworkOperationPerformingTask("A", cb));
			th2 = new Thread(new ExampleNetworkOperationPerformingTask("B", cb));
			th3 = new Thread(new ExampleNetworkOperationPerformingTask("C", cb));
			th4 = new Thread(new ExampleNetworkOperationPerformingTask("D", cb));

			th1.start();
			th2.start();
			th3.start();
			th4.start();

			th1.join();
			th2.join();
			th3.join();
			th4.join();
		}

	}
}