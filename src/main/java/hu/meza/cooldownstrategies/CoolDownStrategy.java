package hu.meza.cooldownstrategies;

public interface CoolDownStrategy {
	public boolean cool();

	public void trigger();
}
