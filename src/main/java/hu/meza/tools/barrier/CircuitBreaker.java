package hu.meza.tools.barrier;


import hu.meza.tools.barrier.exceptions.CircuitBrokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class CircuitBreaker {

	private CoolDownStrategy coolDownStrategy;
	private TriggerStrategy triggerStrategy;
	private Logger logger = LoggerFactory.getLogger(CircuitBreaker.class);
	private Set<CircuitMonitor> listeners = new HashSet<CircuitMonitor>() {
	};

	public CircuitBreaker(CoolDownStrategy secondsToCoolDown, TriggerStrategy triggerStrategy) {
		this.coolDownStrategy = secondsToCoolDown;
		this.triggerStrategy = triggerStrategy;
	}

	public Response execute(Command cmd) {
		return handleCommand(cmd);
	}

	public void addListener(CircuitMonitor listener) {
		listeners.add(listener);
	}

	public void removeListener(CircuitMonitor listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}

	private Response handleCommand(Command command) {
		logger.debug(String.format("Attemting to perform task %s", command.getClass().getCanonicalName()));
		if (!coolDownStrategy.isCool()) {
			logger.debug("Circuit breaker is hot, not doing it");
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
			logger.debug(String.format("Got an %s, checking if the circuit should be breaked",
				e.getClass().getCanonicalName()));
			if (triggerStrategy.isBreaker(e)) {
				logger.debug("Breaking circuit");
				coolDownStrategy.makeHot();
				for (CircuitMonitor listener : listeners) {
					listener.circuitBroken();
				}
			} else {
				logger.debug("Not breaking circuit");
			}
		}
	}
}
