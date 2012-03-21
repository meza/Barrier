package hu.meza;

import hu.meza.exceptions.CircuitBrokenException;

import java.net.ConnectException;

public class CircuitBreaker {
	private enum State {OPEN, CLOSED};

	private State currentState = State.OPEN;

	public RegulatedResponse execute(RegulatedCommand cmd) {

		RegulatedCommand regulatedCommand = cmd;
		handleCommand(regulatedCommand);
		return regulatedCommand.response();
	}

	private void handleCommand(RegulatedCommand regulatedCommand) {
		if (currentState == State.CLOSED) {
			regulatedCommand.response().receivedException(new CircuitBrokenException());
			return;
		}

		try {
			regulatedCommand.execute();
			regulatedCommand.response().succeed(true);
		} catch (Exception e) {
			regulatedCommand.response().succeed(false);
			if (ConnectException.class == e.getClass()) {
				currentState = State.CLOSED;
			}
			regulatedCommand.response().receivedException(e);
		}
	}
}
