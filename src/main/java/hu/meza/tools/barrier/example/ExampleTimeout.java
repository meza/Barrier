package hu.meza.tools.barrier.example;

import hu.meza.tools.barrier.CoolDownStrategy;

class ExampleTimeout implements Runnable {

	private CoolDownStrategy cs;

	public ExampleTimeout(CoolDownStrategy cs) {
		this.cs = cs;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(5000);
			cs.makeCold();
		} catch (InterruptedException e) {
			// do something
		}
	}
}
