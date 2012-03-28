package hu.meza.example;

import hu.meza.TriggerStrategy;

class ExampleTriggerStrategy implements TriggerStrategy {
	@Override
	public boolean isBreaker(Throwable throwable) {
		return throwable.getClass().equals(RuntimeException.class);
	}
}