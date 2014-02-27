package hu.meza.tools.barrier;


import hu.meza.tools.barrier.exceptions.CircuitBrokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CircuitBreaker {

	private CoolDownStrategy coolDownStrategy;
	private TriggerStrategy triggerStrategy;
	private Logger logger = LoggerFactory.getLogger(CircuitBreaker.class);

	public CircuitBreaker(CoolDownStrategy secondsToCoolDown, TriggerStrategy triggerStrategy) {
		this.coolDownStrategy = secondsToCoolDown;
		this.triggerStrategy = triggerStrategy;
	}

	public Response execute(Command cmd) {
		return handleCommand(cmd);
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
			} else {
				logger.debug("Not breaking circuit");
			}
		}
	}
}
