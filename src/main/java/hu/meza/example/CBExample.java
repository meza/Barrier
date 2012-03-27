package hu.meza.example;

import hu.meza.CircuitBreaker;

public class CBExample {


	public static void main(String[] args) throws InterruptedException {
		Thread th1;
		Thread th2;
		Thread th3;
		Thread th4;

		CircuitBreaker cb = new CircuitBreaker(new ExampleCoolDownStrategy(), new ExampleTriggerStrategy());


		while (true) {
			th1 = new Thread(new TaskNode("A", cb));
			th2 = new Thread(new TaskNode("B", cb));
			th3 = new Thread(new TaskNode("C", cb));
			th4 = new Thread(new TaskNode("D", cb));

			th1.start();
			th2.start();
			th3.start();
			th4.start();

			th1.join();
			th2.join();
			th3.join();
			th4.join();

			Thread.sleep(800);
		}

	}
}
