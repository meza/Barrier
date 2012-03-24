package hu.meza;

import hu.meza.cooldownstrategies.CoolDownStrategy;
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

	public RegulatedResponse execute(RegulatedCommand cmd) {
		if (coolDownStrategy.cool()) {
			open();
		}
		return handleCommand(cmd);
	}

	private synchronized void open() {
		currentState = State.OPEN;
	}

	private RegulatedResponse handleCommand(RegulatedCommand regulatedCommand) {

		if (currentState == State.CLOSED) {
			return new RegulatedResponse(false, new CircuitBrokenException());
		}

		try {
			regulatedCommand.execute();
			return new RegulatedResponse(true);
		} catch (Throwable e) {
			handleException(e);
			return new RegulatedResponse(false, e);
		}

	}

	private synchronized void handleException(Throwable e) {
		if (triggerStrategy.isBreaker(e)) {
			coolDownStrategy.trigger();
			currentState = State.CLOSED;
		}
	}
}

