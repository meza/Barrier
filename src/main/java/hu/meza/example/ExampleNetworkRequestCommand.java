package hu.meza.example;

import hu.meza.Command;

import java.util.Random;

class ExampleNetworkRequestCommand implements Command {

	private String parameter;

	public ExampleNetworkRequestCommand(String argument) {
		parameter = argument;
	}

	@Override
	public Object execute() throws Throwable {

		if (shouldFail()) {
			throw new RuntimeException();
		}

		simulateAction();

		return null;
	}

	private void simulateAction() throws InterruptedException {
		System.out.println(parameter);
		Thread.sleep(500);
	}

	private boolean shouldFail() {
		boolean fail = false;
		Random random = new Random();
		if (random.nextInt(100) > 90) {
			fail = true;
		}
		return fail;
	}

}
