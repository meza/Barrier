package hu.meza.tools.barrier;

public interface CoolDownStrategy {
	boolean isCool();

	void makeHot();

	void makeCold();
}
