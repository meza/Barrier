package hu.meza.tools.barrier;

public interface Command<T> {
	T execute() throws Throwable;
}
