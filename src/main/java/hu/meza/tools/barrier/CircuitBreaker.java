package hu.meza.tools.barrier;


import hu.meza.tools.barrier.exceptions.CircuitBrokenException;

public class CircuitBreaker {

	private CoolDownStrategy coolDownStrategy;
	private TriggerStrategy triggerStrategy;

	public CircuitBreaker(CoolDownStrategy secondsToCoolDown, TriggerStrategy triggerStrategy) {
		this.coolDownStrategy = secondsToCoolDown;
		this.triggerStrategy = triggerStrategy;
	}

	public Response execute(Command cmd) {
		return handleCommand(cmd);
	}

	private Response handleCommand(Command command) {
		if (!coolDownStrategy.isCool()) {
			return new Response(null, false, new CircuitBrokenException());
		}

		try {
			Object executionResult = command.execute();
			return new Response(executionResult, true);
		} catch (Throwable e) {
			handleException(e);
			return new Response(null, false, e);
		}
	}

	private void handleException(Throwable e) {
		if (coolDownStrategy.isCool()) {
			if (triggerStrategy.isBreaker(e)) {
				coolDownStrategy.makeHot();
			}
		}
	}
}