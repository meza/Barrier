package hu.meza.example;

import hu.meza.CoolDownStrategy;

class ExampleCoolDownStrategy implements CoolDownStrategy {

	private boolean on = false;

	@Override
	public boolean cool() {
		return !on;
	}

	@Override
	public synchronized void trigger() {
		if (!on) {
			System.err.println("Circuit off");
			on = true;
			new Thread(new ExampleTimeout(this)).start();
		}
	}
	
	public synchronized void off() {
		System.err.println("Circuit on");
		on = false;
	}
}
