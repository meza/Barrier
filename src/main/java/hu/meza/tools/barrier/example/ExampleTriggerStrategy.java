package hu.meza.tools.barrier.example;

import hu.meza.tools.barrier.TriggerStrategy;

class ExampleTriggerStrategy implements TriggerStrategy {
	@Override
	public boolean isBreaker(Throwable throwable) {
		return throwable.getClass().equals(RuntimeException.class);
	}
}
