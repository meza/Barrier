package hu.meza;

public interface CoolDownStrategy {
	public boolean cool();

	public void trigger();
}
