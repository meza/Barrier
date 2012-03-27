package hu.meza;

import hu.meza.exceptions.CircuitBrokenException;

public class CircuitBreaker {

	private CoolDownStrategy coolDownStrategy;
	private TriggerStrategy triggerStrategy;

	public CircuitBreaker(CoolDownStrategy secondsToCoolDown, TriggerStrategy triggerStrategy) {
		this.coolDownStrategy = secondsToCoolDown;
		this.triggerStrategy = triggerStrategy;
	}

	private enum State {OPEN, CLOSED}

	private State currentState = State.OPEN;

	public Response execute(Command cmd) {
		if (coolDownStrategy.cool()) {
			open();
		}
		return handleCommand(cmd);
	}

	private synchronized void open() {
		currentState = State.OPEN;
	}

	private Response handleCommand(Command command) {

		if (currentState == State.CLOSED) {
			return new Response(null, false, new CircuitBrokenException());
		}

		try {
			return new Response(command.execute(), true);
		} catch (Throwable e) {
			handleException(e);
			return new Response(null, false, e);
		}

	}

	private synchronized void handleException(Throwable e) {
		if (triggerStrategy.isBreaker(e)) {
			coolDownStrategy.trigger();
			currentState = State.CLOSED;
		}
	}
}

