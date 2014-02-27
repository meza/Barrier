package hu.meza.tools.barrier;

public interface TriggerStrategy {
	boolean isBreaker(Throwable throwable);
}
