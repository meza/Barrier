package hu.meza;

public interface CoolDownStrategy {
	public boolean isCool();

	public void makeHot();

	public void makeCold();
}