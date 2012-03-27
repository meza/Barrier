package hu.meza.example;

import hu.meza.Command;

import java.util.Random;

class ExampleNetworkRequestCommand implements Command {

	private String param;

	public ExampleNetworkRequestCommand(String parameter) {
		param = parameter;
	}

	@Override
	public Object execute() throws Throwable {
		
		Random random = new Random();
		if (random.nextInt(100) > 90) {
			throw new RuntimeException();
		}
		System.out.println(param);
		Thread.sleep(500);
		return null;
	}
}
