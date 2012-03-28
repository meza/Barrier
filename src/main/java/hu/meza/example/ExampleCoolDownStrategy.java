package hu.meza.example;

import hu.meza.CoolDownStrategy;

class ExampleCoolDownStrategy implements CoolDownStrategy {

	private boolean cool = true;

	@Override
	public boolean isCool() {
		return cool;
	}

	@Override
	public synchronized void makeHot() {
		if (cool) {
			System.err.println("Circuit off");
			cool = false;
			new Thread(new ExampleTimeout(this)).start();
		}
	}
	
	public synchronized void makeCold() {
		System.err.println("Circuit on");
		cool = true;
	}
}