package hu.meza;

public interface TriggerStrategy {
	public boolean isBreaker(Throwable throwable);
}